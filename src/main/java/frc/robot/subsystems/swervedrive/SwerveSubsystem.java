// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of the
// WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.swervedrive;

import static edu.wpi.first.units.Units.Meter;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.FollowPathCommand;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.commands.PathfindingCommand;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Config;
import frc.robot.Constants;
import frc.robot.Constants.DriveToPoseConstants;
import frc.robot.commands.FastProfileToPose;
import frc.robot.commands.ProfileToPose;
import frc.robot.commands.SlowProfileToPose;
import frc.robot.subsystems.ButtonBox;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.TargetClass;

import java.io.File;
import java.util.function.Supplier;
import swervelib.SwerveController;
import swervelib.SwerveDrive;
import swervelib.SwerveDriveTest;
import swervelib.parser.SwerveControllerConfiguration;
import swervelib.parser.SwerveDriveConfiguration;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;
public class SwerveSubsystem extends SubsystemBase
{

  /**
   * Swerve drive object.
   */
  private final SwerveDrive         swerveDrive;

  private boolean pathCanceled = false;

  private boolean isShaking = false;
  private double shakeStartTime = 0;
  private Command currentShakeCommand = null; // Track the current shake command
  
  private boolean isUsingQuest;



  private QuestNavVision questNavVision = new QuestNavVision();

  private Vision vision = new Vision();


  /**
   * Initialize {@link SwerveDrive} with the directory provided.
   *
   * @param directory Directory of swerve drive config files.
   */
  public SwerveSubsystem(File directory)
  {
    // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
    try
    {
      swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.MAX_SPEED,
                                                                  new Pose2d(new Translation2d(Meter.of(1),
                                                                                               Meter.of(4)),
                                                                             Rotation2d.fromDegrees(0)));
      // Alternative method if you don't want to supply the conversion factor via JSON files.
      // swerveDrive = new SwerveParser(directory).createSwerveDrive(maximumSpeed, angleConversionFactor, driveConversionFactor);
    } catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    swerveDrive.setHeadingCorrection(false); // Heading correction should only be used while controlling the robot via angle.
    swerveDrive.setCosineCompensator(false);//!SwerveDriveTelemetry.isSimulation); // Disables cosine compensation for simulations since it causes discrepancies not seen in real life.
    swerveDrive.setAngularVelocityCompensation(true,
                                               true,
                                               0.1); //Correct for skew that gets worse as angular velocity increases. Start with a coefficient of 0.1.
    swerveDrive.setModuleEncoderAutoSynchronize(false,
                                                1); // Enable if you want to resynchronize your absolute encoders and motor encoders periodically when they are not moving.
//    swerveDrive.pushOffsetsToEncoders(); // Set the absolute encoder to be used over the internal encoder and push the offsets onto it. Throws warning if not possible
    setupPathPlanner();
  }

  /**
   * Construct the swerve drive.
   *
   * @param driveCfg      SwerveDriveConfiguration for the swerve.
   * @param controllerCfdrig Swerve Controller.
   */
  public SwerveSubsystem(SwerveDriveConfiguration driveCfg, SwerveControllerConfiguration controllerCfg)
  {
    swerveDrive = new SwerveDrive(driveCfg,
                                  controllerCfg,
                                  Constants.MAX_SPEED,
                                  new Pose2d(new Translation2d(Meter.of(2), Meter.of(0)),
                                             Rotation2d.fromDegrees(0)));
  }

  @Override
  public void periodic() {
    // Process vision measurements from both cameras separately
    addLeftCameraVisionMeasurement();
    addRightCameraVisionMeasurement();

    if(isUsingQuest){
      addQuestVisionMeasurement();
    }

    SmartDashboard.putNumber("Battery Voltage", RobotController.getBatteryVoltage());
    SmartDashboard.putNumber("Match Time", DriverStation.getMatchTime());

    // Log shake status to SmartDashboard
    SmartDashboard.putBoolean("Shake Mode Active", isShaking);
    if (isShaking) {
      SmartDashboard.putNumber("Shake Time Running", Timer.getFPGATimestamp() - shakeStartTime);
    }
  }

  @Override
  public void simulationPeriodic()
  {
    vision.simulationPeriodic(getPose());
  }

  /**
   * Setup AutoBuilder for PathPlanner.
   */
  public void setupPathPlanner()
  {
    // Load the RobotConfig from the GUI settings. You should probably
    // store this in your Constants file
    RobotConfig config;
    try
    {
      config = RobotConfig.fromGUISettings();

      final boolean enableFeedforward = true;
      // Configure AutoBuilder last
      AutoBuilder.configure(
          this::getPose,
          // Robot pose supplier
          this::resetOdometry,
          // Method to reset odometry (will be called if your auto has a starting pose)
          this::getRobotVelocity,
          // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
          (speedsRobotRelative, moduleFeedForwards) -> {
            if (enableFeedforward)
            {
              swerveDrive.drive(
                  speedsRobotRelative,
                  swerveDrive.kinematics.toSwerveModuleStates(speedsRobotRelative),
                  moduleFeedForwards.linearForces()
                               );
            } else
            {
              swerveDrive.setChassisSpeeds(speedsRobotRelative);
            }
          },
          // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
          new PPHolonomicDriveController(
              // PPHolonomicController is the built in path following controller for holonomic drive trains
              new PIDConstants(5.0, 0.0, 0.0),
              // Translation PID constants
              new PIDConstants(5.15, 0.0, 0.0)
              // Rotation PID constants
          ),
          config,
          // The robot configuration
          () -> {
            // Boolean supplier that controls when the path will be mirrored for the red alliance
            // This will flip the path being followed to the red side of the field.
            // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent())
            {
              return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          },
          this
          // Reference to this subsystem to set requirements
                           );

    } catch (Exception e)
    {
      // Handle exception as needed
      e.printStackTrace();
    }

    //Preload PathPlanner Path finding
    // IF USING CUSTOM PATHFINDER ADD BEFORE THIS LINE
    PathfindingCommand.warmupCommand().schedule();
    FollowPathCommand.warmupCommand().schedule();
  }


  /**
   * Get the path follower with events.
   *
   * @param pathName PathPlanner path name.
   * @return {@link AutoBuilder#followPath(PathPlannerPath)} path command.
   */
  public Command getAutonomousCommand(String pathName)
  {
    // Create a path following command using AutoBuilder. This will also trigger event markers.
    return new PathPlannerAuto(pathName);
  }


  

  public Command pathfindThenFollowPath(String pathName) {
    try{
        // Load the path you want to follow using its name in the GUI
        PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);

        /*
        PathConstraints constraints = new PathConstraints(
        swerveDrive.getMaximumChassisVelocity(), 4.0,
        swerveDrive.getMaximumChassisAngularVelocity(), Units.degreesToRadians(720));
        
        */

        PathConstraints constraints = new PathConstraints(1.0, 1.0, Units.degreesToRadians(120), Units.degreesToRadians(120)); //For testing
        // Create a path following command using AutoBuilder. This will also trigger event markers.
        return AutoBuilder.pathfindThenFollowPath(path, constraints);
    } catch (Exception e) {
        DriverStation.reportError("Big oops: " + e.getMessage(), e.getStackTrace());
        return Commands.none();
    }
  }

  public void setCancel(boolean value) {
    this.pathCanceled = value;
  }
  
  public boolean getCancel() {
    return this.pathCanceled;
  }
  
  
  public Command driveToPosePATHPLANNER(ButtonBox buttonBox, Elevator elevator) {
    return new Command() {
        private Command pathCommand;
        private PathConstraints lastConstraints = null;
        private TargetClass lastTarget = null;
        
        @Override
        public void initialize() {
            // Reset cancel flag once at the very start
            setCancel(false);
        }
        
        @Override
        public void execute() {
            // Get current target from the supplier
            TargetClass target = buttonBox.currentTargetClassSupplier.get();
            if (target == null) {
                target = TargetClass.GetTargetByName("C100");
            }
            
            // Compute target pose from the target
            Pose2d targetPose = new Pose2d(
                new Translation2d(target.getX(), target.getY()),
                Rotation2d.fromRadians(target.getZ())
            );
            Pose2d finalTargetPose = TargetClass.toPose2d(targetPose);
            
            // Create the PathConstraints with the computed values
            PathConstraints currentConstraints = new PathConstraints(
                3.0,
                2.0,
                DriveToPoseConstants.THETA_MAX_VELOCITY,
                DriveToPoseConstants.THETA_MAX_ACCELERATION
            );
            
            // Only update when either constraints or target have changed
            if (!currentConstraints.equals(lastConstraints) || !target.equals(lastTarget)) {
                if (pathCommand != null && !pathCommand.isFinished()) {
                    pathCommand.cancel();
                }
                
                // Create and schedule the new pathfinding command using the computed constraints
                pathCommand = AutoBuilder.pathfindToPose(finalTargetPose, currentConstraints,
                    edu.wpi.first.units.Units.MetersPerSecond.of(0));
                pathCommand.schedule();
                
                // Cache new values
                lastConstraints = currentConstraints;
                lastTarget = target;
            }
            
            // Check cancellation flag during this cycle
            if (getCancel() && pathCommand != null && !pathCommand.isFinished()) {
                pathCommand.cancel();
            }
        }
        
        @Override
        public boolean isFinished() {
            return (pathCommand != null && pathCommand.isFinished()) || getCancel();
        }
        
        @Override
        public void end(boolean interrupted) {
            if (pathCommand != null) {
                pathCommand.cancel();
            }
            setCancel(false);
        }
    };
}


  public Command driveToPoseProfiled(ButtonBox buttonBox) {
    // Simply pass the ButtonBox to the ProfileToPose command
    // Let the command handle null targets internally
    return new ProfileToPose(this, buttonBox);
  }

  /**
   * Command to characterize the robot drive motors using SysId
   *
   * @return SysId Drive Command
   */
  public Command sysIdDriveMotorCommand()
  {
    return SwerveDriveTest.generateSysIdCommand(
        SwerveDriveTest.setDriveSysIdRoutine(
            new Config(),
            this, swerveDrive, 12, true),
        3.0, 5.0, 3.0);
  }

  /**
   * Command to characterize the robot angle motors using SysId
   *
   * @return SysId Angle Command
   */
  public Command sysIdAngleMotorCommand()
  {
    return SwerveDriveTest.generateSysIdCommand(
        SwerveDriveTest.setAngleSysIdRoutine(
            new Config(),
            this, swerveDrive),
        3.0, 5.0, 3.0);
  }

  /**
   * The primary method for controlling the drivebase.  Takes a {@link Translation2d} and a rotation rate, and
   * calculates and commands module states accordingly.  Can use either open-loop or closed-loop velocity control for
   * the wheel velocities.  Also has field- and robot-relative modes, which affect how the translation vector is used.
   *
   * @param translation   {@link Translation2d} that is the commanded linear velocity of the robot, in meters per
   *                      second. In robot-relative mode, positive x is torwards the bow (front) and positive y is
   *                      torwards port (left).  In field-relative mode, positive x is away from the alliance wall
   *                      (field North) and positive y is torwards the left wall when looking through the driver station
   *                      glass (field West).
   * @param rotation      Robot angular rate, in radians per second. CCW positive.  Unaffected by field/robot
   *                      relativity.
   * @param fieldRelative Drive mode.  True for field-relative, false for robot-relative.
   */
  public void drive(Translation2d translation, double rotation, boolean fieldRelative)
  {
    swerveDrive.drive(translation,
                      rotation,
                      fieldRelative,
                      false); // Open loop is disabled since it shouldn't be used most of the time.
  }

  /**
   * Drive the robot given a chassis field oriented velocity.
   *
   * @param velocity Velocity according to the field.
   */
  public void driveFieldOriented(ChassisSpeeds velocity)
  {
    swerveDrive.driveFieldOriented(velocity);
  }

  /**
   * Drive the robot given a chassis field oriented velocity.
   *
   * @param velocity Velocity according to the field.
   */
  public Command driveFieldOriented(Supplier<ChassisSpeeds> velocity)
  {
    return run(() -> {
      swerveDrive.driveFieldOriented(velocity.get());
    });
  }

  /**
   * Drive according to the chassis robot oriented velocity.
   *
   * @param velocity Robot oriented {@link ChassisSpeeds}
   */
  public void drive(ChassisSpeeds velocity)
  {
    swerveDrive.drive(velocity);
  }


  /**
   * Get the swerve drive kinematics object.
   *
   * @return {@link SwerveDriveKinematics} of the swerve drive.
   */
  public SwerveDriveKinematics getKinematics()
  {
    return swerveDrive.kinematics;
  }

  /**
   * Resets odometry to the given pose. Gyro angle and module positions do not need to be reset when calling this
   * method.  However, if either gyro angle or module position is reset, this must be called in order for odometry to
   * keep working.
   *
   * @param initialHolonomicPose The pose to set the odometry to
   */
  public void resetOdometry(Pose2d initialHolonomicPose)
  {
    swerveDrive.resetOdometry(initialHolonomicPose);
  }

  /**
   * Gets the current pose (position and rotation) of the robot, as reported by odometry.
   *
   * @return The robot's pose
   */
  public Pose2d getPose()
  {
    return swerveDrive.getPose();
  }

  /**
   * Set chassis speeds with closed-loop velocity control.
   *
   * @param chassisSpeeds Chassis Speeds to set.
   */
  public void setChassisSpeeds(ChassisSpeeds chassisSpeeds)
  {
    swerveDrive.setChassisSpeeds(chassisSpeeds);
  }

  /**
   * Post the trajectory to the field.
   *
   * @param trajectory The trajectory to post.
   */
  public void postTrajectory(Trajectory trajectory)
  {
    swerveDrive.postTrajectory(trajectory);
  }

  /**
   * Resets the gyro angle to zero and resets odometry to the same position, but facing toward 0.
   */
  public void zeroGyro()
  {
    swerveDrive.zeroGyro();
  }

  /**
   * Checks if the alliance is red, defaults to false if alliance isn't available.
   *
   * @return true if the red alliance, false if blue. Defaults to false if none is available.
   */
  private boolean isRedAlliance()
  {
    var alliance = DriverStation.getAlliance();
    return alliance.isPresent() ? alliance.get() == DriverStation.Alliance.Red : false;
  }

  /**
   * This will zero (calibrate) the robot to assume the current position is facing forward
   * <p>
   * If red alliance rotate the robot 180 after the drviebase zero command
   */
  public void zeroGyroWithAlliance()
  {
    if (isRedAlliance())
    {
      zeroGyro();
      //Set the pose 180 degrees
      resetOdometry(new Pose2d(getPose().getTranslation(), Rotation2d.fromDegrees(180)));
    } else
    {
      zeroGyro();
    }
  }

  /**
   * Sets the drive motors to brake/coast mode.
   *
   * @param brake True to set motors to brake mode, false for coast.
   */
  public void setMotorBrake(boolean brake)
  {
    swerveDrive.setMotorIdleMode(brake);
  }

  /**
   * Gets the current yaw angle of the robot, as reported by the swerve pose estimator in the underlying drivebase.
   * Note, this is not the raw gyro reading, this may be corrected from calls to resetOdometry().
   *
   * @return The yaw angle
   */
  public Rotation2d getHeading()
  {
    return getPose().getRotation();
  }

  /**
   * Get the chassis speeds based on controller input of 2 joysticks. One for speeds in which direction. The other for
   * the angle of the robot.
   *
   * @param xInput   X joystick input for the robot to move in the X direction.
   * @param yInput   Y joystick input for the robot to move in the Y direction.
   * @param headingX X joystick which controls the angle of the robot.
   * @param headingY Y joystick which controls the angle of the robot.
   * @return {@link ChassisSpeeds} which can be sent to the Swerve Drive.
   
  public ChassisSpeeds getTargetSpeeds(double xInput, double yInput, double headingX, double headingY)
  {
    Translation2d scaledInputs = SwerveMath.cubeTranslation(new Translation2d(xInput, yInput));
    return swerveDrive.swerveController.getTargetSpeeds(scaledInputs.getX(),
                                                        scaledInputs.getY(),
                                                        headingX,
                                                        headingY,
                                                        getHeading().getRadians(),
                                                        Constants.MAX_SPEED);
  }

  
   * Get the chassis speeds based on controller input of 1 joystick and one angle. Control the robot at an offset of
   * 90deg.
   *
   * @param xInput X joystick input for the robot to move in the X direction.
   * @param yInput Y joystick input for the robot to move in the Y direction.
   * @param angle  The angle in as a {@link Rotation2d}.
   * @return {@link ChassisSpeeds} which can be sent to the Swerve Drive.
    public ChassisSpeeds getTargetSpeeds(double xInput, double yInput, Rotation2d angle)
  {
    Translation2d scaledInputs = SwerveMath.cubeTranslation(new Translation2d(xInput, yInput));

    return swerveDrive.swerveController.getTargetSpeeds(scaledInputs.getX(),
                                                        scaledInputs.getY(),
                                                        angle.getRadians(),
                                                        getHeading().getRadians(),
                                                        Constants.MAX_SPEED);
  }
*/
  /**
   * Gets the current field-relative velocity (x, y and omega) of the robot
   *
   * @return A ChassisSpeeds object of the current field-relative velocity
   */
  public ChassisSpeeds getFieldVelocity()
  {
    return swerveDrive.getFieldVelocity();
  }

  /**
   * Gets the current velocity (x, y and omega) of the robot
   *
   * @return A {@link ChassisSpeeds} object of the current velocity
   */
  public ChassisSpeeds getRobotVelocity()
  {
    return swerveDrive.getRobotVelocity();
  }

  /**
   * Get the {@link SwerveController} in the swerve drive.
   *
   * @return {@link SwerveController} from the {@link SwerveDrive}.
   */
  public SwerveController getSwerveController()
  {
    return swerveDrive.swerveController;
  }

  /**
   * Get the {@link SwerveDriveConfiguration} object.
   *
   * @return The {@link SwerveDriveConfiguration} fpr the current drive.
   */
  public SwerveDriveConfiguration getSwerveDriveConfiguration()
  {
    return swerveDrive.swerveDriveConfiguration;
  }

  /**
   * Lock the swerve drive to prevent it from moving.
   */
  public void lock()
  {
    swerveDrive.lockPose();
  }

  /**
   * Gets the current pitch angle of the robot, as reported by the imu.
   *
   * @return The heading as a {@link Rotation2d} angle
   */
  public Rotation2d getPitch()
  {
    return swerveDrive.getPitch();
  }

  /**
   * Gets the swerve drive object.
   *
   * @return {@link SwerveDrive}
   */
  public SwerveDrive getSwerveDrive()
  {
    return swerveDrive;
  }


  /**
   * Add vision measurement from the left camera only
   */
  public void addLeftCameraVisionMeasurement() {
    // Correct pose estimate with left camera vision measurements
    var visionEst = vision.getEstimatedGlobalPoseLeftCamera();
    visionEst.ifPresent(
        est -> {
            // Change our trust in the measurement based on the tags we can see
            var estStdDevs = vision.getLeftEstimationStdDevs();
            swerveDrive.addVisionMeasurement(est.estimatedPose.toPose2d(), est.timestampSeconds, estStdDevs);
        }
    );
  }

  /**
   * Add vision measurement from the right camera only
   */
  public void addRightCameraVisionMeasurement() {
    // Correct pose estimate with right camera vision measurements
    var visionEst = vision.getEstimatedGlobalPoseRightCamera();
    visionEst.ifPresent(
        est -> {
            // Change our trust in the measurement based on the tags we can see
            var estStdDevs = vision.getRightEstimationStdDevs();
            swerveDrive.addVisionMeasurement(est.estimatedPose.toPose2d(), est.timestampSeconds, estStdDevs);
        }
    );
  }

  public void addQuestVisionMeasurement() {
    
    var questDetection = questNavVision.getPose();

    if(isUsingQuest){
    swerveDrive.addVisionMeasurement(questDetection.getFirst(), Timer.getFPGATimestamp());
  }
  }


    /**
     * Visualize the target pose on the field
     * @param targetPose The pose to visualize
     */
    public void visualizeTargetPose(Pose2d targetPose) {
        swerveDrive.field.getObject("targetPose").setPose(targetPose);
    }
    
    /**
     * Clear the target visualization from the field
     * This removes the target marker without affecting the queue
     */
    public void clearTargetVisualization() {
        // Set an invisible pose far away to effectively hide it
        // Using a position outside the field boundary keeps it hidden
        swerveDrive.field.getObject("targetPose").setPose(
            new Pose2d(-100, -100, new Rotation2d(0))
        );
    }

    /**
     * Starts the drive to pose command and returns immediately.
     * The drive command will continue running in the background.
     * You can use your triggers (veryCloseTrigger, etc.) to determine when the robot is at the target.
     * 
     * @param buttonBox The button box containing target information
     * @return A command that starts the drive process and completes immediately
     */
    public Command startDriveToPose(ButtonBox buttonBox, Elevator elevator) {
        return ProfileToPose.startAndReturnCommand(this, buttonBox);
    }

  /**
   * Generates shake motion with direct speed control for more aggressive shaking
   * 
   * @param xShake Whether to shake along X axis
   * @param yShake Whether to shake along Y axis
   * @param rotationShake Whether to perform rotational shaking
   * @return ChassisSpeeds with appropriate oscillating values
   */
  private ChassisSpeeds getShakeMotion(boolean xShake, boolean yShake, boolean rotationShake) {
    double time = Timer.getFPGATimestamp() - shakeStartTime;
    
    // Calculate X velocity if enabled (using traditional sine)
    double xVelocity = xShake ? 
        Constants.ShakeModeConstants.SHAKE_AMPLITUDE_X * 
        Constants.ShakeModeConstants.SHAKE_FREQUENCY * 
        Math.sin(2 * Math.PI * Constants.ShakeModeConstants.SHAKE_FREQUENCY * time) : 0;
    
    // Use simpler square wave with direct speed values for Y
    // This creates a more aggressive back-and-forth motion
    double yDirection = Math.sin(2 * Math.PI * Constants.ShakeModeConstants.SHAKE_FREQUENCY * time) >= 0 ? 1.0 : -1.0;
    double yVelocity = yShake ? yDirection * Constants.ShakeModeConstants.SHAKE_SPEED_Y : 0;
    
    // Calculate rotation velocity if enabled
    double rotationalVelocity = rotationShake ? 
        Constants.ShakeModeConstants.ANGULAR_SHAKE_AMPLITUDE * 
        Constants.ShakeModeConstants.SHAKE_FREQUENCY * 
        Math.sin(2 * Math.PI * Constants.ShakeModeConstants.SHAKE_FREQUENCY * time + 
                Constants.ShakeModeConstants.ROTATION_PHASE_SHIFT) : 0;

    // Log shake values to SmartDashboard for debugging
    SmartDashboard.putNumber("Shake X Velocity", xVelocity);
    SmartDashboard.putNumber("Shake Y Velocity", yVelocity);
    SmartDashboard.putNumber("Shake Direction", yDirection);
    SmartDashboard.putNumber("Shake Rotation Velocity", rotationalVelocity);
    
    return new ChassisSpeeds(xVelocity, yVelocity, rotationalVelocity);
  }

  /**
   * Command to shake the robot to dislodge stuck coral
   * 
   * @param xShake Enable shaking along the X axis
   * @param yShake Enable shaking along the Y axis
   * @param rotationShake Enable rotational shaking
   * @return Command that shakes the robot until stopped
   */
  public Command shakeRobotCommand(boolean xShake, boolean yShake, boolean rotationShake) {
    return Commands.sequence(
      // First, initialize the shake state
      Commands.runOnce(() -> {
        isShaking = true;
        shakeStartTime = Timer.getFPGATimestamp();
      }),
      
      // Run the shake continuously until command is manually canceled
      Commands.run(() -> {
        // Get shake motion speeds and apply them
        ChassisSpeeds shakeMotion = getShakeMotion(xShake, yShake, rotationShake);
        
        // Use direct module states for more aggressive shaking if configured
        if (Constants.ShakeModeConstants.USE_OPEN_LOOP) {
          swerveDrive.drive(
            new Translation2d(shakeMotion.vxMetersPerSecond, shakeMotion.vyMetersPerSecond),
            shakeMotion.omegaRadiansPerSecond, 
            false,  // Robot-relative mode
            true    // Open-loop control
          );
        } else {
          setChassisSpeeds(shakeMotion);
        }
      }),
      
      // Always stop when command is canceled or ended
      Commands.runOnce(this::stopShaking)
    );
  }

  /**
   * Default shake command using all axes
   * 
   * @return Command that shakes the robot until stopped
   */
  public Command shakeRobotCommand() {
    Command shakeCmd = shakeRobotCommand(
      Constants.ShakeModeConstants.DEFAULT_SHAKE_X,
      Constants.ShakeModeConstants.DEFAULT_SHAKE_Y,
      Constants.ShakeModeConstants.DEFAULT_SHAKE_ROTATION
    );
    
    // Store the command reference for later cancellation
    currentShakeCommand = shakeCmd;
    return shakeCmd;
  }


  /**
   * Immediately stops any shake motion and sets chassis speeds to zero
   */
  public void stopShaking() {
    if (isShaking) {
      isShaking = false;
      setChassisSpeeds(new ChassisSpeeds(0, 0, 0));
      
      // Cancel any ongoing shake command (important!)
      if (currentShakeCommand != null) {
        currentShakeCommand.cancel();
        currentShakeCommand = null;
      }
    }
  }

  /**
   * Command to stop robot shake motion 
   * 
   * @return Command that stops robot shaking
   */
  public Command stopShakeCommand() {
    return Commands.runOnce(() -> {
      
      // Set chassis speeds to zero
      setChassisSpeeds(new ChassisSpeeds(0, 0, 0));
      
      // Cancel the current shake command if it exists
      if (currentShakeCommand != null) {
        currentShakeCommand.cancel();
        currentShakeCommand = null;
      }
      
      // Always reset the shake state
      isShaking = false;
    });
  }

  /**
   * Check if the robot is currently executing a shake motion
   * 
   * @return true if shaking, false otherwise
   */
  public boolean isShaking() {
    return isShaking;
  }

  /**
   * Creates a ProfileToPose command with rotation delay
   * This prevents the robot from rotating during the initial movement period in autonomous
   * 
   * @param buttonBox The button box containing target information
   * @return A command that drives to the target pose with rotation delay
   */
  public Command driveToPoseProfiledWithRotationDelay(ButtonBox buttonBox) {
    return new ProfileToPose(this, buttonBox).withRotationDelay();
  }

  /**
   * Creates a ProfileToPose command with custom rotation delay time
   * 
   * @param buttonBox The button box containing target information
   * @param delaySeconds Custom delay time in seconds
   * @return A command that drives to the target pose with rotation delay
   */
  public Command driveToPoseProfiledWithRotationDelay(ButtonBox buttonBox, double delaySeconds) {
    return new ProfileToPose(this, buttonBox).withRotationDelay(delaySeconds);
  }

  /**
   * Starts the drive to pose command with rotation delay and returns immediately.
   * The drive command will continue running in the background.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @return A command that starts the drive process with rotation delay and completes immediately
   */
  public Command startDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator) {
    return ProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox);
  }

  /**
   * Starts the drive to pose command with custom rotation delay and returns immediately.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @param delaySeconds Custom delay time in seconds
   * @return A command that starts the drive process with rotation delay and completes immediately
   */
  public Command startDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator, double delaySeconds) {
    return ProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox, delaySeconds);
  }

  /**
   * Public method to explicitly trigger vision measurement
   * Used for cycling measurements during initialization
   */
  public void addVisionMeasurementCommand() {
    // Call individual camera measurements instead of the combined one
    addLeftCameraVisionMeasurement();
    addRightCameraVisionMeasurement();
  }

  public void setIsUsingQuest(boolean bool){
    isUsingQuest = bool;
  }

  /**
   * Creates a FastProfileToPose command for high-speed driving
   * This doesn't slow down near target - useful for fast autonomous movements like to source stations
   * 
   * @param buttonBox The button box containing target information
   * @return A command that drives to the target pose at full speed
   */
  public Command fastDriveToPoseProfiled(ButtonBox buttonBox) {
    return new FastProfileToPose(this, buttonBox);
  }

  /**
   * Creates a FastProfileToPose command with rotation delay for high-speed driving
   * 
   * @param buttonBox The button box containing target information
   * @return A high-speed command that drives to the target pose with rotation delay
   */
  public Command fastDriveToPoseProfiledWithRotationDelay(ButtonBox buttonBox) {
    return new FastProfileToPose(this, buttonBox).withRotationDelay();
  }

  /**
   * Creates a FastProfileToPose command with custom rotation delay for high-speed driving
   * 
   * @param buttonBox The button box containing target information
   * @param delaySeconds Custom delay time in seconds
   * @return A high-speed command that drives to the target pose with rotation delay
   */
  public Command fastDriveToPoseProfiledWithRotationDelay(ButtonBox buttonBox, double delaySeconds) {
    return new FastProfileToPose(this, buttonBox).withRotationDelay(delaySeconds);
  }

  /**
   * Starts a fast drive to pose command and returns immediately.
   * The drive command will continue running in the background at maximum speed.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @return A command that starts the fast drive process and completes immediately
   */
  public Command startFastDriveToPose(ButtonBox buttonBox, Elevator elevator) {
    return FastProfileToPose.startAndReturnCommand(this, buttonBox);
  }

  /**
   * Starts a fast drive to pose command with rotation delay and returns immediately.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @return A command that starts the fast drive process with rotation delay and completes immediately
   */
  public Command startFastDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator) {
    return FastProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox);
  }

  /**
   * Starts a fast drive to pose command with custom rotation delay and returns immediately.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @param delaySeconds Custom delay time in seconds
   * @return A command that starts the fast drive process with custom rotation delay and completes immediately
   */
  public Command startFastDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator, double delaySeconds) {
    return FastProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox, delaySeconds);
  }

  /**
   * Command to stop any active drive-to-pose commands
   * This sets the cancel flag to true and immediately stops the robot's movement
   * 
   * @return A command that cancels drive-to-pose
   */
  public Command stopDriveToPoseCommand() {
    return Commands.sequence(
      // Set the cancel flag to true
      Commands.runOnce(() -> setCancel(true)),
      
      // Stop motion immediately
      Commands.runOnce(() -> setChassisSpeeds(new ChassisSpeeds(0, 0, 0))),
      
      // Delay very briefly to allow cancelation to propagate
      Commands.waitSeconds(0.05),
      
      // Reset the cancel flag for future commands
      Commands.runOnce(() -> setCancel(false))
    );
  }

  /**
   * Immediately stops any active drive-to-pose command
   * This is a convenience method for direct calls from code
   */
  public void stopDriveToPose() {
    setCancel(true);
    setChassisSpeeds(new ChassisSpeeds(0, 0, 0));
    
    // Schedule a command to reset the cancel flag after a short delay
    Commands.runOnce(() -> setCancel(false), this)
            .withTimeout(0.05)
            .schedule();
  }

  /**
   * Starts a PathPlanner-based drive-to-pose command and returns immediately.
   * The drive command will continue running in the background.
   * This variant uses PathPlanner's pathfinding for more complex navigation.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @return A command that starts the PathPlanner drive process and completes immediately
   */
  public Command startDriveToPosePATHPLANNER(ButtonBox buttonBox, Elevator elevator) {
    return Commands.runOnce(() -> {
      // Reset cancel flag at start
      setCancel(false);
      
      // Create but don't return the actual drive command
      Command driveCommand = driveToPosePATHPLANNER(buttonBox, elevator);
      
      // Schedule it so it runs in the background
      driveCommand.schedule();
    });
  }

  /**
     * Starts the drive to pose command and returns immediately.
     * The drive command will continue running in the background.
     * You can use your triggers (veryCloseTrigger, etc.) to determine when the robot is at the target.
     * 
     * @param buttonBox The button box containing target information
     * @return A command that starts the drive process and completes immediately
     */
    public Command startSlowDriveToPose(ButtonBox buttonBox, Elevator elevator) {
      return SlowProfileToPose.startAndReturnCommand(this, buttonBox);
  }

  /**
   * Creates a ProfileToPose command with rotation delay
   * This prevents the robot from rotating during the initial movement period in autonomous
   * 
   * @param buttonBox The button box containing target information
   * @return A command that drives to the target pose with rotation delay
   */
  public Command slowDriveToPoseProfiledWithRotationDelay(ButtonBox buttonBox) {
    return new SlowProfileToPose(this, buttonBox).withRotationDelay();
  }

  /**
   * Creates a ProfileToPose command with custom rotation delay time
   * 
   * @param buttonBox The button box containing target information
   * @param delaySeconds Custom delay time in seconds
   * @return A command that drives to the target pose with rotation delay
   */
  public Command slowDriveToPoseProfiledWithRotationDelay(ButtonBox buttonBox, double delaySeconds) {
    return new SlowProfileToPose(this, buttonBox).withRotationDelay(delaySeconds);
  }

  /**
   * Starts the drive to pose command with rotation delay and returns immediately.
   * The drive command will continue running in the background.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @return A command that starts the drive process with rotation delay and completes immediately
   */
  public Command startSlowDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator) {
    return SlowProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox);
  }

  /**
   * Starts the drive to pose command with custom rotation delay and returns immediately.
   * 
   * @param buttonBox The button box containing target information
   * @param elevator The elevator subsystem (used for reference)
   * @param delaySeconds Custom delay time in seconds
   * @return A command that starts the drive process with rotation delay and completes immediately
   */
  public Command startSlowDriveToPoseWithRotationDelay(ButtonBox buttonBox, Elevator elevator, double delaySeconds) {
    return SlowProfileToPose.startAndReturnCommandWithRotationDelay(this, buttonBox, delaySeconds);
  }
}