// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of the
// WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.AlgaeShooterConstants;
import frc.robot.Constants.ZoneConstants;
import frc.robot.commands.CommandFactory;
import frc.robot.subsystems.ButtonBox;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.TargetClass;
import frc.robot.subsystems.Algae.AlgaeArm;
import frc.robot.subsystems.Algae.AlgaeShooter;
import frc.robot.subsystems.Coral.Shooter;
import frc.robot.subsystems.Coral.ShooterArm;
import frc.robot.subsystems.Coral.ShooterPivot;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.util.Elastic;

import java.io.File;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import swervelib.SwerveInputStream;
import frc.robot.Constants.SpeedConstants;


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{
  // Variables for velocity/acceleration smoothing
  private double prevTransVel = 0;
  private double prevTransAccel = 0;
  private double prevRotVel = 0;
  private double prevRotAccel = 0;


    // Add timer variables for debouncing target reached detection
    private double positionReachedTimestamp = 0;
    private double rotationReachedTimestamp = 0;
    private double targetReachedTimestamp = 0;
    private boolean positionTimerActive = false;
    private boolean rotationTimerActive = false;
    private boolean targetTimerActive = false;



  // Replace with CommandPS4Controller or CommandJoystick if needed
  final CommandXboxController driverXbox = new CommandXboxController(0);

  private final CommandJoystick buttonBox1 = new CommandJoystick(1);
  private final CommandJoystick buttonBox2 = new CommandJoystick(2);
  final CommandXboxController opXbox = new CommandXboxController(3);

  // Add variables for smooth speed transition
  private float targetDriveSpeed = 0;
  private float actualDriveSpeed = 0;

  private boolean isClose = false;
  private boolean isVeryClose = false;
  private boolean isApproaching = false;
  private boolean isLinedUp = false;

  // Add zone status tracking
  private boolean isInReefZone = false;
  private boolean isInCoralStationLeftZone = false;  // Consistent naming
  private boolean isInCoralStationRightZone = false; // Consistent naming

  
  DoubleSupplier headingXAng = () -> -driverXbox.getRightX() * .8;
  DoubleSupplier angSpeed;

  DoubleSupplier driveX;
  DoubleSupplier driveY;
  DoubleSupplier headingX = () -> -driverXbox.getRightX();
  DoubleSupplier headingY = () -> -driverXbox.getRightY();

  DoubleSupplier elevatorUpDown = () -> opXbox.getRightY();
  //DoubleSupplier algaeArmTriggerUp = () -> opXbox.getLeftTriggerAxis();
  //DoubleSupplier algaeArmTriggerDown = () -> opXbox.getLeftTriggerAxis();
  DoubleSupplier shooterArmUpDown = () -> opXbox.getLeftY();
  DoubleSupplier shooterPivotUpDown = () -> opXbox.getLeftX(); //Questionable Name Practices... Shooter Pivot UP DOWN not Left Right??

  DoubleSupplier climberUpDown = () -> opXbox.getRightX();
  
  // Add suppliers for algae shooter triggers
  DoubleSupplier algaeShooterIntake = () -> driverXbox.getLeftTriggerAxis();
  DoubleSupplier algaeShooterOutake = () -> driverXbox.getRightTriggerAxis();

  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem       drivebase  = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                                "swerve/neo"));
  
  
  private final AlgaeArm algaeArm = new AlgaeArm();
  private final AlgaeShooter algaeShooter = new AlgaeShooter();
  //private final Scope scope = new Scope();
  private final Shooter shooter = new Shooter();
  private final ShooterArm shooterArm = new ShooterArm();
  private final ShooterPivot shooterPivot = new ShooterPivot();
  private final Climber climber = new Climber();
  private final Elevator elevator = new Elevator(this);

  //private final Funnel funnel = new Funnel();
  //private final LED LED = new LED();
  private final ButtonBox buttonBox = new ButtonBox();

  // Triggers for proximity detection
  public Trigger approachingTrigger = new Trigger(() -> isApproaching);
  public Trigger closeTrigger = new Trigger(() -> isClose);
  public Trigger veryCloseTrigger = new Trigger(() -> isVeryClose);
  public Trigger linedUpTrigger = new Trigger(() -> isLinedUp);
  
  // Triggers for zone detection
  public Trigger reefZoneTrigger(){
    return new Trigger(() -> isInReefZone);
  }
  
  public Trigger coralStationLeftTrigger(){
    return new Trigger(() -> isInCoralStationLeftZone);
  }
  public Trigger coralStationRightTrigger(){
    return new Trigger(() -> isInCoralStationRightZone);
  }

  public Trigger anyZoneTrigger(){
    return new Trigger(() -> isInReefZone || isInCoralStationLeftZone || isInCoralStationRightZone);
  }

  // Track positioning accuracy
  private boolean isAtTargetPosition = false;
  private boolean isAtTargetRotation = false;
  private boolean isAtTarget = false;
  
  
  // Add back tolerance variables that were accidentally removed
  private double positionTolerance = Constants.DriveToPoseConstants.POSITION_TOLERANCE; 
  private double rotationTolerance = Constants.DriveToPoseConstants.ROTATION_TOLERANCE;
  
  // Flag to track if we've removed visualization after reaching a target
  private boolean hasReachedAndClearedTarget = false;
  
  // Add auto-cancel functionality when target is reached
  private boolean autoCancel = true;
  private boolean hasAutoCanceled = false;
  
  // Add flag to track if we've processed the current target
  private boolean hasProcessedCurrentTarget = false;
  
  // Add flag to control auto-advance to next target
  private boolean autoAdvanceTargets = true;
  
  // Trigger that fires when target is reached (can be used elsewhere)
  public Trigger targetReachedTrigger(){
    return new Trigger(() -> isAtTarget && isDriveToPoseActive());
  }
  public Trigger targetPositionReachedTrigger(){
    return new Trigger(() -> isAtTargetPosition && isDriveToPoseActive());
  }
  public Trigger targetRotationReachedTrigger(){
    return new Trigger(() -> isAtTargetRotation && isDriveToPoseActive());
  }

  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
  () -> driveY.getAsDouble(),
  () -> driveX.getAsDouble())
.withControllerRotationAxis(() -> angSpeed.getAsDouble())
.deadband(Constants.DEADBAND)
.scaleTranslation(1)
.allianceRelativeControl(true);

/**
* Clone's the angular velocity input stream and converts it to a fieldRelative input stream.
*/
SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(headingX,
                               headingY).headingWhile(true);

/**
* Clone's the angular velocity input stream and converts it to a robotRelative input stream.
*/
SwerveInputStream driveRobotOriented = driveAngularVelocity.copy().robotRelative(true)
.allianceRelativeControl(false);

SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(drivebase.getSwerveDrive(),
          () -> -driverXbox.getLeftY(),
          () -> -driverXbox.getLeftX())
      .withControllerRotationAxis(() -> driverXbox.getRawAxis(
          2))
      .deadband(Constants.DEADBAND)
      .scaleTranslation(0.8)
      .allianceRelativeControl(true);
// Derive the heading axis with math!
SwerveInputStream driveDirectAngleKeyboard     = driveAngularVelocityKeyboard.copy()
                 .withControllerHeadingAxis(() ->
                                                Math.sin(
                                                    driverXbox.getRawAxis(
                                                        2) *
                                                    Math.PI) *
                                                (Math.PI *
                                                 2),
                                            () ->
                                                Math.cos(
                                                    driverXbox.getRawAxis(
                                                        2) *
                                                    Math.PI) *
                                                (Math.PI *
                                                 2))
                 .headingWhile(true);

/*
// Create an input stream using values provided by the ButtonBox.
SwerveInputStream driveButtonBoxInput =
    SwerveInputStream.of(drivebase.getSwerveDrive(),
        // For x and y inputs, get values from ButtonBox suppliers.
        () -> buttonBox.getFieldOrientedSuppliers().x.getAsDouble(),
        () -> buttonBox.getFieldOrientedSuppliers().y.getAsDouble())
    // For rotation, use the rotation suppliers from ButtonBox.
    .withControllerHeadingAxis(
        () -> buttonBox.getFieldOrientedSuppliers().rotationX.getAsDouble(),
        () -> buttonBox.getFieldOrientedSuppliers().rotationY.getAsDouble())
    .deadband(Constants.DEADBAND)
    .scaleTranslation(1.0)
    .allianceRelativeControl(true)
    // Optionally, if you want to drive with a heading-control mode:
    .headingWhile(true);
  */
  /*  SwerveInputStream driveAngularVelocityDriveToPose = SwerveInputStream.of(drivebase.getSwerveDrive(),
  () -> driverXbox.getLeftY() * -1,
  () -> driverXbox.getLeftX() * -1)
.withControllerRotationAxis(headingXAng)
.deadband(Constants.DEADBAND)
.scaleTranslation(driveSpeed)
.allianceRelativeControl(true)
.driveToPose(TargetClass.toPose2dSupplier(buttonBox), DriveToPoseConstants.xProfiledPID, DriveToPoseConstants.yProfiledPID)
.driveToPoseEnabled(driveToPoseEnabled);
  */

  // Track distance to target for PID adjustment
  private double distanceToTarget = Double.POSITIVE_INFINITY;
  private double angleToTarget = Double.POSITIVE_INFINITY;
  
  // Keep track of when controllers need to be updated
  private boolean pidControllersNeedUpdate = true;
  private ProfiledPIDController currentXController;
  private ProfiledPIDController currentRotController;
  
  // Dynamic PID controller suppliers
  private Supplier<ProfiledPIDController> driveToPoseXControllerSupplier = () -> {
      // Create or update the translation controller
      if (currentXController == null || pidControllersNeedUpdate) {
          TargetClass target = buttonBox.peekNextTarget();
          
          // Update distance to target for constraint calculation
          Pose2d robotPose = drivebase.getPose();
          distanceToTarget = Double.POSITIVE_INFINITY;
          
          // Default constraints (zero velocity/acceleration for safety)
          Constraints constraints = new Constraints(0, 0);
          
          if (target != null) {
              Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
              Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
              
              // Only use normal constraints if target is valid
              if (!isNearOrigin(allianceAdjustedPose)) {
                  distanceToTarget = robotPose.getTranslation().getDistance(allianceAdjustedPose.getTranslation());
                  constraints = getTranslationConstraintsForDistance(distanceToTarget);
              } else {
                  Elastic.sendNotification(
                    new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                    "Zero Constraints", 
                    "Using ZERO translation constraints for invalid target near origin"));
              }
              
              SmartDashboard.putString("Target Name", target.getName());
              SmartDashboard.putBoolean("Target Available", true);
          } else {
              SmartDashboard.putString("Target Name", "None");
              SmartDashboard.putBoolean("Target Available", false);
              Elastic.sendNotification(
                new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                "Zero Constraints", 
                "Using ZERO translation constraints (no target available)"));
          }

          // Always create controller with proper PID values, but use constraints based on target validity
          currentXController = new ProfiledPIDController(
              Constants.DriveToPoseConstants.TRANSLATION_P,
              Constants.DriveToPoseConstants.TRANSLATION_I,
              Constants.DriveToPoseConstants.TRANSLATION_D,
              constraints
          );
          currentXController.setTolerance(0.05);
          SmartDashboard.putNumber("X Controller P", Constants.DriveToPoseConstants.TRANSLATION_P);
      }
      return currentXController;
  };
  
  private Supplier<ProfiledPIDController> driveToPoseRotControllerSupplier = () -> {
      // Create or update the rotation controller
      if (currentRotController == null || pidControllersNeedUpdate) {
          TargetClass target = buttonBox.peekNextTarget();
          
          // Update distance to target for constraint calculation
          Pose2d robotPose = drivebase.getPose();
          distanceToTarget = Double.POSITIVE_INFINITY;
          angleToTarget = 0;
          
          // Default constraints (zero velocity/acceleration for safety)
          Constraints constraints = new Constraints(0, 0);
          
          if (target != null) {
              Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
              Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
              
              // Only use normal constraints if target is valid
              if (!isNearOrigin(allianceAdjustedPose)) {
                  distanceToTarget = robotPose.getTranslation().getDistance(allianceAdjustedPose.getTranslation());
                  
                  // Calculate angle to target
                  angleToTarget = Math.atan2(
                      allianceAdjustedPose.getY() - robotPose.getY(),
                      allianceAdjustedPose.getX() - robotPose.getX()
                  ) - robotPose.getRotation().getRadians();
                  // Normalize angle
                  angleToTarget = Math.atan2(Math.sin(angleToTarget), Math.cos(angleToTarget));
                  
                  constraints = getRotationConstraintsForDistance(distanceToTarget);
              } else {
                  Elastic.sendNotification(
                    new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                    "Zero Constraints", 
                    "Using ZERO rotation constraints for invalid target near origin"));
              }
          } else {
              Elastic.sendNotification(
                new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                "Zero Constraints", 
                "Using ZERO rotation constraints (no target available)"));
          }

          // Always create controller with proper PID values, but use constraints based on target validity
          currentRotController = new ProfiledPIDController(
              Constants.DriveToPoseConstants.ROTATION_P,
              Constants.DriveToPoseConstants.ROTATION_I,
              Constants.DriveToPoseConstants.ROTATION_D,
              constraints
          );
          currentRotController.setTolerance(Units.degreesToRadians(2));
          currentRotController.enableContinuousInput(-Math.PI, Math.PI);
          SmartDashboard.putNumber("Rot Controller P", Constants.DriveToPoseConstants.ROTATION_P);
      }
      return currentRotController;
  };

  // Create a stream that includes drive-to-pose capability with dynamic controllers
  public SwerveInputStream driveToPoseStream = driveDirectAngle.copy().driveToPose(
      () -> {
          // Flag that controllers should be updated
          pidControllersNeedUpdate = true;
          
          // Get the target pose and update distance metrics
          TargetClass target = buttonBox.peekNextTarget();
          
          if (target != null) {
              Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
              Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
              
              // SAFETY CHECK: Verify target location isn't at/near origin
              if (isNearOrigin(allianceAdjustedPose)) {
                  Elastic.sendNotification(
                    new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                    "Target Near Origin", 
                    "Target position near origin detected: " + allianceAdjustedPose + " - Using current pose instead"));
                  return drivebase.getPose(); // Return current pose as failsafe
              }
              
              // Visualize the target pose
              drivebase.visualizeTargetPose(allianceAdjustedPose);
              
              // Get current robot pose
              Pose2d robotPose = drivebase.getPose();
              
              // Update distance and angle metrics
              distanceToTarget = robotPose.getTranslation().getDistance(allianceAdjustedPose.getTranslation());
              angleToTarget = Math.atan2(
                  allianceAdjustedPose.getY() - robotPose.getY(),
                  allianceAdjustedPose.getX() - robotPose.getX()
              ) - robotPose.getRotation().getRadians();
              // Normalize angle
              angleToTarget = Math.atan2(Math.sin(angleToTarget), Math.cos(angleToTarget));
              
              SmartDashboard.putNumber("Distance To Target", distanceToTarget);
              SmartDashboard.putNumber("Angle To Target (deg)", Units.radiansToDegrees(angleToTarget));
              SmartDashboard.putString("Target Name", target.getName());
              SmartDashboard.putBoolean("Target Available", true);
              
              return allianceAdjustedPose;
          } else {
              SmartDashboard.putString("Target Name", "None");
              SmartDashboard.putBoolean("Target Available", false);
              
              // Reset distance values when no target
              distanceToTarget = Double.POSITIVE_INFINITY;
              angleToTarget = 0;
              
              // Force controllers to update with zero values next cycle
              pidControllersNeedUpdate = true;
              
              // CRITICAL FIX: Return current pose instead of potentially (0,0,0)
              // This prevents the robot from trying to drive to the origin
              Elastic.sendNotification(
                new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                "No Target Available", 
                "No target available - Using current pose instead of (0,0,0)"));
              return drivebase.getPose();
          }
      },
      driveToPoseXControllerSupplier.get(),
      driveToPoseRotControllerSupplier.get()
  );

  // Create drive commands
  public Command driveFieldOrientedDirectAngle = drivebase.driveFieldOriented(driveDirectAngle);
  public Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);
  public Command driveFieldOrientedDriveToPose = drivebase.driveFieldOriented(driveToPoseStream);


// Simplified enableDriveToPoseCommand: just schedule tempDriveToPoseCommand
public Command enableDriveToPoseCommand() {
    return Commands.runOnce(() -> {
        // Only schedule if not already running
        if (!tempDriveToPoseCommand.isScheduled()) {
          
            targetTimerActive = false;
            positionReachedTimestamp = 0;
            rotationReachedTimestamp = 0;
            targetReachedTimestamp = 0;
            tempDriveToPoseCommand.schedule();
        }
    });
}

// Simplified disableDriveToPoseCommand: just cancel tempDriveToPoseCommand
public Command disableDriveToPoseCommand() {
    return Commands.runOnce(() -> {
        // Only cancel if currently running
        if (tempDriveToPoseCommand.isScheduled()) {
            tempDriveToPoseCommand.cancel();
        }
    });
}



  // Create a command to temporarily run drive-to-pose without canceling default command
  private Command tempDriveToPoseCommand = Commands.sequence(
    // First check if a target is available
    Commands.runOnce(() -> {
        // Check if we have a target
        TargetClass target = buttonBox.peekNextTarget();
        
        if (target == null) {
            // No target available - show warning and don't enable drive-to-pose
            SmartDashboard.putString("Drive Status", "ERROR: No Target Available!");
            // Flash warning on dashboard (will be visible)
            SmartDashboard.putBoolean("No Target Warning", true);
            
        } else {
            // Check if target is near origin (extra safety)
            Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
            Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
            
            
            // Validate the target is not at/near origin (0,0,0)
            if (isNearOrigin(allianceAdjustedPose)) {
                Elastic.sendNotification(
                  new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
                  "Target Near Origin", 
                  "Target position near origin detected: " + allianceAdjustedPose));
                SmartDashboard.putString("Drive Status", "ERROR: Invalid Target Position!");
                SmartDashboard.putBoolean("Invalid Target Warning", true);
            }
        }
    }),
    
    // Only proceed if a target is available and valid - check condition before enabling
    Commands.runOnce(() -> {
        TargetClass target = buttonBox.peekNextTarget();
        
        if (target != null) {
            // We have a target - get its pose for validation
            Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
            Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
            
            // Only enable if the target is valid (not near origin)
            if (!isNearOrigin(allianceAdjustedPose)) {
                // We have a valid target - enable drive-to-pose
                pidControllersNeedUpdate = true; // Force controller update
                currentXController = null; // Reset controllers to ensure they're recreated
                currentRotController = null;
                driveToPoseStream.driveToPoseEnabled(true);
                
                // Reset target position status
                isAtTargetPosition = false;
                isAtTargetRotation = false;
                isAtTarget = false;
                hasReachedAndClearedTarget = false;
                hasProcessedCurrentTarget = false;
                
                SmartDashboard.putBoolean("Drive To Pose Active", true);
                SmartDashboard.putBoolean("At Target Position", false);
                SmartDashboard.putBoolean("At Target Rotation", false);
                SmartDashboard.putBoolean("At Target", false);
                SmartDashboard.putBoolean("No Target Warning", false);
                SmartDashboard.putBoolean("Invalid Target Warning", false);
                
                SmartDashboard.putString("Drive Status", "Target Found - Driving to " + target.getName());
            }
        }
    }).unless(() -> {
        // Skip if no target or invalid target location
        TargetClass target = buttonBox.peekNextTarget();
        if (target == null) return true;
        
        // Check if target pose is invalid
        Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
        Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
        return isNearOrigin(allianceAdjustedPose);
    }),
    
    // Only run drive-to-pose command if a target is available AND valid
    Commands.either(
        // Add auto-cancellation when target is reached
        Commands.sequence(
            driveFieldOrientedDriveToPose.until(() -> isAtTarget && autoCancel),
            Commands.runOnce(() -> {
                if (!hasAutoCanceled) {
                    Elastic.sendNotification(
                      new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                      "Auto-Cancel", 
                      "AUTO-CANCELING drive-to-pose - target reached!"));
                    hasAutoCanceled = true;
                    SmartDashboard.putString("Drive Status", "Auto-Canceled - Target Reached");
                }
            })
        ),
        Commands.none(), // Do nothing if no target
        () -> {
            TargetClass target = buttonBox.peekNextTarget();
            if (target == null) return false;
            
            // Additional check to prevent driving to invalid locations
            Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
            Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
            return !isNearOrigin(allianceAdjustedPose);
        }
    ),
    
    // Disable drive-to-pose when done (this runs even if interrupted)
    Commands.runOnce(() -> {
        driveToPoseStream.driveToPoseEnabled(false);
        SmartDashboard.putBoolean("Drive To Pose Active", false);
        SmartDashboard.putString("Drive Status", "Normal Driving");
        SmartDashboard.putBoolean("No Target Warning", false);
        SmartDashboard.putBoolean("Invalid Target Warning", false);
        
        // Reset target position status and auto-cancel flag
        isAtTargetPosition = false;
        isAtTargetRotation = false;
        isAtTarget = false;
        hasAutoCanceled = false;
        hasReachedAndClearedTarget = false;
        hasProcessedCurrentTarget = false;
        
        SmartDashboard.putBoolean("At Target Position", false);
        SmartDashboard.putBoolean("At Target Rotation", false);
        SmartDashboard.putBoolean("At Target", false);
    })
  ).withInterruptBehavior(Command.InterruptionBehavior.kCancelSelf);

  /**
   * Linear interpolation helper method
   */
  private double lerp(double a, double b, double t) {
    return a + (b - a) * t;
  }

  public Command leftAuto = CommandFactory.LeftAutonCommand(shooter, shooterArm, shooterPivot, elevator, buttonBox, drivebase, this);
  public Command rightAuto = CommandFactory.RightAutonCommand(shooter, shooterArm, shooterPivot, elevator, buttonBox, drivebase, this);


  SendableChooser<Command> chooser = new SendableChooser<>();

  /**
* The container for the robot. Contains subsystems, OI devices, and commands.
*/
  public RobotContainer()
  {
    // Configure the trigger bindings
    configureBindings();
    
    // Set default drive command
    drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    
    // Initialize drive to pose command for debugging
    SmartDashboard.putBoolean("Drive To Pose Enabled", false);
    
    // Initialize auto-cancel feature (can be toggled in SmartDashboard if needed)
    SmartDashboard.putBoolean("Auto-Cancel Enabled", autoCancel);
    
    // Initialize auto-advance feature (can be toggled in SmartDashboard if needed)
    SmartDashboard.putBoolean("Auto-Advance Targets", autoAdvanceTargets);
  }

   /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */

  private void configureBindings()
  {
    
    elevator.setDefaultCommand(new RunCommand(() -> elevator.moveAmount(elevatorUpDown.getAsDouble()), elevator));
    //algaeArm.setDefaultCommand(new RunCommand(() -> algaeArm.moveTrigger(algaeArmTrigger.getAsDouble()), algaeArm)); // Updated to use moveTrigger
    shooterArm.setDefaultCommand(new RunCommand(() -> shooterArm.moveAmount(shooterArmUpDown.getAsDouble()), shooterArm));
    shooterPivot.setDefaultCommand(new RunCommand(() -> shooterPivot.moveAmount(shooterPivotUpDown.getAsDouble()), shooterPivot));

    //climber.setDefaultCommand(new RunCommand(() -> climber.moveAmount(elevatorUpDown.getAsDouble()), climber));

    
    drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);

    // Updated default command for AlgaeShooter using suppliers
    algaeShooter.setDefaultCommand(new RunCommand(() -> {
        // Get trigger values from the suppliers
        double leftTrigger = algaeShooterIntake.getAsDouble();
        double rightTrigger = algaeShooterOutake.getAsDouble();
        
        // Control logic for the algae shooter based on triggers
        if (leftTrigger > AlgaeShooterConstants.triggerThreshold) {
            // Left trigger controls intake (forward) at variable speed
            double speed = leftTrigger * AlgaeShooterConstants.maxTriggerIntake;
            algaeShooter.setSpeed(speed);
        } 
        else if (rightTrigger > AlgaeShooterConstants.triggerThreshold) {
            // Right trigger controls outake (reverse) at variable speed
            double speed = rightTrigger * AlgaeShooterConstants.maxTriggerOutake;
            algaeShooter.setSpeed(speed);
        }
        else {
            // If both triggers are below threshold, stop the motor
            algaeShooter.setSpeed(0);
        }
    }, algaeShooter));
    

      buttonBox1.button(3).onTrue(new InstantCommand(() -> buttonBox.deleteFirstTarget()));
      buttonBox1.button(2).onTrue(new InstantCommand(() -> buttonBox.clearTargets()));
      buttonBox1.button(1).onTrue(new InstantCommand(() -> buttonBox.deleteLastTarget()));

      buttonBox1.button(9).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C400")));
      buttonBox1.button(9).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C401")));
      buttonBox1.button(9).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C410")));
      buttonBox1.button(9).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C411")));
      buttonBox1.button(9).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C420")));
      buttonBox1.button(9).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C421")));
      buttonBox1.button(9).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C430")));
      buttonBox1.button(9).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C431")));
      buttonBox1.button(10).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C300")));
      buttonBox1.button(10).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C301")));
      buttonBox1.button(10).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C310")));
      buttonBox1.button(10).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C311")));
      buttonBox1.button(10).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C320")));
      buttonBox1.button(10).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C321")));
      buttonBox1.button(10).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C330")));
      buttonBox1.button(10).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C331")));
      buttonBox1.button(11).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C200")));
      buttonBox1.button(11).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C201")));
      buttonBox1.button(11).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C210")));
      buttonBox1.button(11).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C211")));
      buttonBox1.button(11).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C220")));
      buttonBox1.button(11).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C221")));
      buttonBox1.button(11).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C230")));
      buttonBox1.button(11).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C231")));
      buttonBox1.button(6).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C100")));
      buttonBox1.button(6).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C101")));
      buttonBox1.button(6).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C110")));
      buttonBox1.button(6).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C111")));
      buttonBox1.button(6).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C120")));
      buttonBox1.button(6).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C121")));
      buttonBox1.button(6).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C130")));
      buttonBox1.button(6).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C131")));
      buttonBox1.button(7).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C600")));
      buttonBox1.button(7).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C601")));
      buttonBox1.button(7).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C610")));
      buttonBox1.button(7).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C611")));
      buttonBox1.button(7).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C620")));
      buttonBox1.button(7).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C621")));
      buttonBox1.button(7).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C630")));
      buttonBox1.button(7).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C631")));
      buttonBox1.button(8).and(buttonBox2.button(5)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C500")));
      buttonBox1.button(8).and(buttonBox2.button(1)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C501")));
      buttonBox1.button(8).and(buttonBox2.button(6)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C510")));
      buttonBox1.button(8).and(buttonBox2.button(2)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C511")));
      buttonBox1.button(8).and(buttonBox2.button(7)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C520")));
      buttonBox1.button(8).and(buttonBox2.button(3)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C521")));
      buttonBox1.button(8).and(buttonBox2.button(8)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C530")));
      buttonBox1.button(8).and(buttonBox2.button(4)).onTrue(new InstantCommand(() -> buttonBox.addTarget("C531")));

      
      buttonBox1.button(4).onTrue(new InstantCommand(() -> buttonBox.requeueLastTarget()));
      //buttonBox1.button(4).onTrue(new InstantCommand(() -> buttonBox.addTarget("SR")));

    driverXbox.rightBumper().whileTrue(CommandFactory.scoreBasedOnQueueCommandDriveAutoNOSHOOT(shooter, shooterArm, shooterPivot, elevator, buttonBox, drivebase, this));
    driverXbox.leftBumper().onTrue(CommandFactory.setIntakeCommand(shooter, shooterArm, shooterPivot, elevator));
  
    driverXbox.x().onTrue(shooter.shooterIntakeCommand());
    driverXbox.x().onFalse(shooter.shooterZeroSpeedCommand());
    driverXbox.y().onTrue(shooter.shooterOutakeCommand());
    driverXbox.y().onFalse(shooter.shooterZeroSpeedCommand());


    driverXbox.a().onTrue(CommandFactory.scoreBasedOnQueueCommand(shooter, shooterArm, shooterPivot, elevator, buttonBox));
    driverXbox.b().onTrue(CommandFactory.setElevatorZero(shooter, shooterArm, shooterPivot, elevator));

    driverXbox.pov(0).onTrue(CommandFactory.pullOffHighBall(shooter, shooterArm, shooterPivot, elevator));
    driverXbox.pov(180).onTrue(CommandFactory.pullOffLowBall(shooter, shooterArm, shooterPivot, elevator));
    
    driverXbox.pov(90).onTrue(CommandFactory.setAlgaeIntakeCommand(algaeArm, algaeShooter));
    driverXbox.pov(270).onTrue(CommandFactory.algaeStowCommand(algaeArm, algaeShooter));





    /*
    // Hold back button to temporarily use drive-to-pose
    driverXbox.back().whileTrue(tempDriveToPoseCommand);
    
    // Toggle drive-to-pose with start button
    driverXbox.start().onTrue(
        Commands.either(
            Commands.runOnce(() -> tempDriveToPoseCommand.cancel()),
            Commands.runOnce(() -> tempDriveToPoseCommand.schedule()),
            () -> tempDriveToPoseCommand.isScheduled()
        )
    );
    */

    // Cancel drive-to-pose when driver provides manual input
    driverXbox.axisMagnitudeGreaterThan(0, 0.1)
        .or(driverXbox.axisMagnitudeGreaterThan(1, .1))
        .or(driverXbox.axisMagnitudeGreaterThan(4, .1))
        .or(driverXbox.axisMagnitudeGreaterThan(5, .1))
        .onTrue(Commands.runOnce(() -> {
            drivebase.setCancel(true);
            if (tempDriveToPoseCommand.isScheduled()) {
                tempDriveToPoseCommand.cancel();
            }
        }));

    driverXbox.x().onTrue(new InstantCommand(() -> buttonBox.addTarget("C531")));
    driverXbox.y().onTrue(
        Commands.either(
            Commands.runOnce(() -> tempDriveToPoseCommand.cancel()),
            Commands.runOnce(() -> tempDriveToPoseCommand.schedule()),
            () -> tempDriveToPoseCommand.isScheduled()
        )
    );





    opXbox.pov(180).onTrue(CommandFactory.setClimbPosition(algaeArm, shooter, shooterArm, shooterPivot, elevator));
    opXbox.pov(90).onTrue(algaeArm.algaeArmStraightOutCommand());

    opXbox.a().onTrue(algaeShooter.algaeShooterIntakeCommand());
    opXbox.a().onFalse(algaeShooter.algaeShooterZeroSpeedCommand());

    opXbox.b().onTrue(algaeShooter.algaeShooterOutakeCommand());
    opXbox.b().onFalse(algaeShooter.algaeShooterZeroSpeedCommand());
    
    opXbox.x().onTrue(climber.climberExtendCommand());
    opXbox.x().onFalse(climber.climberStopCommand());

    opXbox.y().onTrue(climber.climberRetractCommand());
    opXbox.y().onFalse(climber.climberStopCommand());

    opXbox.rightBumper().onTrue(algaeArm.algaeArmGroundIntakeCommand());
    opXbox.leftBumper().onTrue(algaeArm.algaeArmStowUpCommand());

    opXbox.pov(0).onTrue(new InstantCommand(() -> buttonBox.addTarget("C531")));


    chooser.setDefaultOption("Right", rightAuto);
    chooser.addOption("Left", leftAuto);
    SmartDashboard.putData(chooser);
  }

  public Command changeDriveSpeedCommand(float speed)
  {
    return new InstantCommand(() -> targetDriveSpeed = speed);
  }

  public void setDriveSpeedBasedOnElevatorAndCloseness()
  {    
    // Determine drive speed based on elevator height category using the new constants
    Elevator.ElevatorHeight heightCategory = elevator.getElevatorHeightCategory();
    
    // Set target speed based on elevator height
    switch (heightCategory) {
      case FULLY_RAISED:
        targetDriveSpeed = SpeedConstants.elevatorFullyRaisedSpeed;
        break;
      case MID_RAISED:
        targetDriveSpeed = SpeedConstants.elevatorMidRaisedSpeed;
        break;
      case PARTIALLY_RAISED:
        targetDriveSpeed = SpeedConstants.elevatorPartiallyRaisedSpeed;
        break;
      case SLIGHTLY_RAISED:
        targetDriveSpeed = SpeedConstants.elevatorRaisedSpeed;
        break;
      case LOWERED:
      default:
        targetDriveSpeed = SpeedConstants.elevatorLoweredSpeed;
        break;
    }
    
    // Update SmartDashboard with current elevator height state
    SmartDashboard.putString("Elevator Height State", heightCategory.toString());
    
    // Update zone statuses
    Pose2d currentPose = drivebase.getPose();
    isInReefZone = isInReefZone(currentPose);
    isInCoralStationLeftZone = isInCoralStationLeft(currentPose);
    isInCoralStationRightZone = isInCoralStationRight(currentPose);

    // Apply zone-based speed modifier
    float zoneModifier = getZoneSpeedMultiplier();
    targetDriveSpeed = Math.min(targetDriveSpeed, targetDriveSpeed * zoneModifier);
    
    reefZoneTrigger();
    coralStationLeftTrigger();
    coralStationRightTrigger();

    // Smooth the speed transition
    smoothDriveSpeed();

    // Update dashboard with speed values
    SmartDashboard.putNumber("Target Drive Speed", targetDriveSpeed);
    SmartDashboard.putNumber("Actual Drive Speed", actualDriveSpeed);
    SmartDashboard.putNumber("Drive Speed", targetDriveSpeed);
    SmartDashboard.putNumber("Zone Modifier", zoneModifier);
    
    // Update zone status on dashboard
    SmartDashboard.putBoolean("In Reef Zone", isInReefZone);
    SmartDashboard.putBoolean("In Coral Station Left", isInCoralStationLeftZone);
    SmartDashboard.putBoolean("In Coral Station Right", isInCoralStationRightZone);

    // Update drive suppliers with new speed
    driveY = () -> -driverXbox.getLeftY() * targetDriveSpeed;
    driveX = () -> -driverXbox.getLeftX() * targetDriveSpeed;
    angSpeed = () -> -driverXbox.getRightX() * targetDriveSpeed;

    // Handle drive-to-pose status updates
    // If drive-to-pose is active, update target position status
    if (tempDriveToPoseCommand.isScheduled()) {
      TargetClass target = buttonBox.peekNextTarget();
      
      if (target != null) {
          // Get current robot pose and target pose
          Pose2d robotPose = drivebase.getPose();
          Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
          Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
          
          // Calculate position error (distance between points)
          double positionError = robotPose.getTranslation().getDistance(allianceAdjustedPose.getTranslation());
          
          // Calculate rotation error with proper normalization
          // Get the difference between current and target rotation
          double rotationDiff = robotPose.getRotation().minus(allianceAdjustedPose.getRotation()).getRadians();
          // Normalize to [-π, π]
          double rotationError = Math.abs(Math.atan2(Math.sin(rotationDiff), Math.cos(rotationDiff)));
          
          // Determine instantaneous status based on errors
          boolean instantAtPosition = positionError <= positionTolerance;
          boolean instantAtRotation = rotationError <= rotationTolerance;
          boolean instantAtTarget = instantAtPosition && instantAtRotation;
          
          // Current time for timestamp comparison
          double currentTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
          
          // Apply time-based debounce logic for position
          if (instantAtPosition) {
              // Start or continue the timer
              if (!positionTimerActive) {
                  positionReachedTimestamp = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
                  positionTimerActive = true;
              }
              // Check if we've been at position long enough
              isAtTargetPosition = (currentTime - positionReachedTimestamp) >= 
                  Constants.DriveToPoseConstants.TARGET_REACHED_DEBOUNCE_TIME;
          } else {
              // Reset the timer
              positionTimerActive = false;
              positionReachedTimestamp = 0;
              isAtTargetPosition = false;
          }
          
          // Apply time-based debounce logic for rotation
          if (instantAtRotation) {
              // Start or continue the timer
              if (!rotationTimerActive) {
                  rotationReachedTimestamp = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
                  rotationTimerActive = true;
              }
              // Check if we've been at rotation long enough
              isAtTargetRotation = (currentTime - rotationReachedTimestamp) >= 
                  Constants.DriveToPoseConstants.TARGET_REACHED_DEBOUNCE_TIME;
          } else {
              // Reset the timer
              rotationTimerActive = false;
              rotationReachedTimestamp = 0;
              isAtTargetRotation = false;
          }
          
          // Apply time-based debounce logic for full target
          if (instantAtTarget) {
              // Start or continue the timer
              if (!targetTimerActive) {
                  targetReachedTimestamp = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
                  targetTimerActive = true;
              }
              // Check if we've been at target long enough
              isAtTarget = (currentTime - targetReachedTimestamp) >= 
                  Constants.DriveToPoseConstants.TARGET_REACHED_DEBOUNCE_TIME;
          } else {
              // Reset the timer
              targetTimerActive = false;
              targetReachedTimestamp = 0;
              isAtTarget = false;
          }
          
          // Display status about auto-cancel feature
          SmartDashboard.putBoolean("Has Auto-Canceled", hasAutoCanceled);
          
          // Display timer values on dashboard for debugging
          SmartDashboard.putNumber("Position Time Elapsed", positionTimerActive ? currentTime - positionReachedTimestamp : 0);
          SmartDashboard.putNumber("Rotation Time Elapsed", rotationTimerActive ? currentTime - rotationReachedTimestamp : 0);
          SmartDashboard.putNumber("Target Time Elapsed", targetTimerActive ? currentTime - targetReachedTimestamp : 0);
          SmartDashboard.putNumber("Target Debounce Time", Constants.DriveToPoseConstants.TARGET_REACHED_DEBOUNCE_TIME);
          
          // Clear target visualization when we reach the target position
          // Only do this once when the target is first reached
          if (isAtTarget && !hasReachedAndClearedTarget) {
              // Clear the target visualization marker from the field
              drivebase.clearTargetVisualization();
              hasReachedAndClearedTarget = true;
              
              targetPositionReachedTrigger();
              targetRotationReachedTrigger();
              targetReachedTrigger();
              
              Elastic.sendNotification(
                new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                "Target Reached", 
                "TARGET REACHED: " + target.getName()));
              
              // Process the target - get the next one if auto-advance is enabled
              if (autoAdvanceTargets && !hasProcessedCurrentTarget) {
                  hasProcessedCurrentTarget = true;
                  Elastic.sendNotification(
                    new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                    "Auto-Advance", 
                    "Auto-advancing to next target"));
                  
                  // Get the current target (which we've reached)
                  buttonBox.getNextTarget();
                  
                  // If there are more targets, prepare for the next one
                  if (buttonBox.hasQueue()) {
                      TargetClass nextTarget = buttonBox.peekNextTarget();
                      if (nextTarget != null) {
                          Elastic.sendNotification(
                            new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                            "Next Target", 
                            "Next target: " + nextTarget.getName()));
                          
                          // Reset flags to handle the next target
                          hasReachedAndClearedTarget = false;
                          hasProcessedCurrentTarget = false;
                          hasAutoCanceled = false;
                          
                          // No need to reset isAtTarget flags or disable drive-to-pose
                          // We'll let the next target get processed naturally
                      }
                  } else {
                      // No more targets in queue
                      Elastic.sendNotification(
                        new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
                        "Queue Empty", 
                        "No more targets in queue"));
                  }
              }
          } else if (!isAtTarget) {
              // Reset flags when we're not at the target
              hasReachedAndClearedTarget = false;
              hasProcessedCurrentTarget = false;
          }
          
          // Display on dashboard
          SmartDashboard.putNumber("Position Error", positionError);
          SmartDashboard.putNumber("Position Tolerance", positionTolerance);
          SmartDashboard.putNumber("Rotation Error (deg)", Units.radiansToDegrees(rotationError));
          SmartDashboard.putNumber("Rotation Tolerance (deg)", Units.radiansToDegrees(rotationTolerance));
          SmartDashboard.putBoolean("At Target Position", isAtTargetPosition);
          SmartDashboard.putBoolean("At Target Rotation", isAtTargetRotation);
          SmartDashboard.putBoolean("At Target", isAtTarget);
          
          // Update drive status message
          if (isAtTarget) {
              SmartDashboard.putString("Drive Status", "Target Reached!");
          } else if (isAtTargetPosition) {
              SmartDashboard.putString("Drive Status", "Position Reached - Aligning Rotation");
          } else if (isAtTargetRotation) {
              SmartDashboard.putString("Drive Status", "Rotation Aligned - Moving to Position");
          } else {
              SmartDashboard.putString("Drive Status", "Driving to Target");
          }
      } else {
          // No target available, reset all flags to false
          isAtTargetPosition = false;
          isAtTargetRotation = false;
          isAtTarget = false;
          hasReachedAndClearedTarget = false;
          hasAutoCanceled = false;
          hasProcessedCurrentTarget = false;
          
          // Reset all timers
          positionTimerActive = false;
          rotationTimerActive = false;
          targetTimerActive = false;
          positionReachedTimestamp = 0;
          rotationReachedTimestamp = 0;
          targetReachedTimestamp = 0;
          
          SmartDashboard.putBoolean("At Target Position", false);
          SmartDashboard.putBoolean("At Target Rotation", false);
          SmartDashboard.putBoolean("At Target", false);
          SmartDashboard.putString("Drive Status", "No Target Selected");
      }
    } else {
      // Drive-to-pose not active, reset all flags to false
      isAtTargetPosition = false;
      isAtTargetRotation = false;
      isAtTarget = false;
      hasReachedAndClearedTarget = false;
      hasAutoCanceled = false;
      hasProcessedCurrentTarget = false;
      
      // Reset all timers
      positionTimerActive = false;
      rotationTimerActive = false;
      targetTimerActive = false;
      positionReachedTimestamp = 0;
      rotationReachedTimestamp = 0;
      targetReachedTimestamp = 0;
      
      SmartDashboard.putBoolean("At Target Position", false);
      SmartDashboard.putBoolean("At Target Rotation", false);
      SmartDashboard.putBoolean("At Target", false);
      
    }
  }

  /**
   * Check if robot is inside the reef zone (circle)
   */
  private boolean isInReefZone(Pose2d robotPose) {
    // Get alliance-relative reef center
    Pose2d reefCenter = TargetClass.toPose2d(new Pose2d(
        ZoneConstants.reefCenterX, 
        ZoneConstants.reefCenterY, 
        new Rotation2d(0)));
    
    // Calculate distance from center of circle
    double distance = Math.sqrt(
        Math.pow(robotPose.getX() - reefCenter.getX(), 2) + 
        Math.pow(robotPose.getY() - reefCenter.getY(), 2));
    
    // Inside if distance is less than radius
    return distance < ZoneConstants.reefZoneRadius;
  }

  /**
   * Check if robot is in left coral station
   */
  private boolean isInCoralStationLeft(Pose2d robotPose) {
    // Get alliance-relative coordinates for coral station left
    Pose2d minCorner = TargetClass.toPose2d(new Pose2d(
        ZoneConstants.LCoralStationMinX, 
        ZoneConstants.LCoralStationMinY,
        new Rotation2d(0)));
    
    Pose2d maxCorner = TargetClass.toPose2d(new Pose2d(
        ZoneConstants.LCoralStationMaxX, 
        ZoneConstants.LCoralStationMaxY,
        new Rotation2d(0)));
    
    return isInRectangularZone(robotPose, 
        Math.min(minCorner.getX(), maxCorner.getX()),
        Math.max(minCorner.getX(), maxCorner.getX()),
        Math.min(minCorner.getY(), maxCorner.getY()),
        Math.max(minCorner.getY(), maxCorner.getY()));
  }

  /**
   * Check if robot is in right coral station
   */
  private boolean isInCoralStationRight(Pose2d robotPose) {
    // Get alliance-relative coordinates for coral station right
    Pose2d minCorner = TargetClass.toPose2d(new Pose2d(
        ZoneConstants.RCoralStationMinX, 
        ZoneConstants.RCoralStationMinY,
        new Rotation2d(0)));
    
    Pose2d maxCorner = TargetClass.toPose2d(new Pose2d(
        ZoneConstants.RCoralStationMaxX, 
        ZoneConstants.RCoralStationMaxY,
        new Rotation2d(0)));
    
    return isInRectangularZone(robotPose, 
        Math.min(minCorner.getX(), maxCorner.getX()),
        Math.max(minCorner.getX(), maxCorner.getX()),
        Math.min(minCorner.getY(), maxCorner.getY()),
        Math.max(minCorner.getY(), maxCorner.getY()));
  }

  /**
   * Helper method to check if a point is inside a rectangle
   */
  private boolean isInRectangularZone(Pose2d pose, double minX, double maxX, double minY, double maxY) {
    return (pose.getX() >= minX && pose.getX() <= maxX && 
            pose.getY() >= minY && pose.getY() <= maxY);
  }

  /**
   * Get speed multiplier based on what zone the robot is in
   */
  private float getZoneSpeedMultiplier() {
    if (isInReefZone) {
      return ZoneConstants.reefSpeedMultiplier;
    } else if (isInCoralStationLeftZone || isInCoralStationRightZone) {
      return ZoneConstants.coralStationMultiplier;
    }
    return 1.0f; // No speed reduction if not in any special zone
  }

  /**
   * Apply smooth interpolation to drive speed changes to prevent jerky movement
   */
  private void smoothDriveSpeed() {
    // Calculate the difference between target and actual speed
    float speedDifference = targetDriveSpeed - actualDriveSpeed;
    
    // Apply the smoothing factor to gradually adjust the actual speed
    actualDriveSpeed += speedDifference * ZoneConstants.speedSmoothingFactor;
    
    // Set the drive speed to the smoothed value
    targetDriveSpeed = actualDriveSpeed;
  }

/**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    // Make sure drive-to-pose is inactive at start of auto mode
    driveToPoseStream.driveToPoseEnabled(false);
    
    // Set auto-cancel to true for autonomous
    autoCancel = true;
    SmartDashboard.putBoolean("Auto-Cancel Enabled", autoCancel);
    
    // Reset all drive-to-pose controllers
    resetDriveToPoseForAuto();
    
    // Return the selected autonomous command
    return chooser.getSelected();
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }

  /**
   * Get dynamic translation constraints based on distance to target with smoothing,
   * now also considering elevator height
   */
  private Constraints getTranslationConstraintsForDistance(double distance) {
    // Special case - if we're checking for a target at/near the origin, return zero constraints
    if (distance <= 0.0) {
        Elastic.sendNotification(
          new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
          "Zero Constraints", 
          "ZERO constraints: Invalid distance value: " + distance));
        return new Constraints(0, 0); // Zero velocity and acceleration
    }
    
    // Calculate base target velocity and acceleration based on distance
    double targetVel;
    double targetAccel;
    
    // Use default "far" constraints if no target or infinite distance
    if (Double.isInfinite(distance)) {
        // Use moderate default values that allow movement but not too fast
        targetVel = Constants.DriveToPoseConstants.FAR_MAX_VEL * 0.5;
        targetAccel = Constants.DriveToPoseConstants.FAR_MAX_ACCEL * 0.5;
    }
    else if (distance < Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE) {
        targetVel = Constants.DriveToPoseConstants.VERY_CLOSE_MAX_VEL;
        targetAccel = Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ACCEL;
    } else if (distance < Constants.DriveToPoseConstants.CLOSE_DISTANCE) {
        // Interpolate between CLOSE and VERY_CLOSE constraints
        double t = (distance - Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE) / 
                   (Constants.DriveToPoseConstants.CLOSE_DISTANCE - Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE);
        targetVel = lerp(Constants.DriveToPoseConstants.VERY_CLOSE_MAX_VEL, 
                         Constants.DriveToPoseConstants.CLOSE_MAX_VEL, t);
        targetAccel = lerp(Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ACCEL, 
                           Constants.DriveToPoseConstants.CLOSE_MAX_ACCEL, t);
    } else if (distance < Constants.DriveToPoseConstants.MID_DISTANCE) {
        // Interpolate between MID and CLOSE constraints
        double t = (distance - Constants.DriveToPoseConstants.CLOSE_DISTANCE) / 
                   (Constants.DriveToPoseConstants.MID_DISTANCE - Constants.DriveToPoseConstants.CLOSE_DISTANCE);
        targetVel = lerp(Constants.DriveToPoseConstants.CLOSE_MAX_VEL, 
                         Constants.DriveToPoseConstants.MID_MAX_VEL, t);
        targetAccel = lerp(Constants.DriveToPoseConstants.CLOSE_MAX_ACCEL, 
                           Constants.DriveToPoseConstants.MID_MAX_ACCEL, t);
    } else {
        // Interpolate between FAR and MID constraints
        double t = Math.min(1.0, (distance - Constants.DriveToPoseConstants.MID_DISTANCE) / 
                   (Constants.DriveToPoseConstants.FAR_DISTANCE - Constants.DriveToPoseConstants.MID_DISTANCE));
        targetVel = lerp(Constants.DriveToPoseConstants.MID_MAX_VEL, 
                         Constants.DriveToPoseConstants.FAR_MAX_VEL, t);
        targetAccel = lerp(Constants.DriveToPoseConstants.MID_MAX_ACCEL, 
                           Constants.DriveToPoseConstants.FAR_MAX_ACCEL, t);
    }
    
    // Now apply elevator height-based modifiers
    double elevatorVelMultiplier;
    double elevatorAccelMultiplier;
    
    // Get elevator height category
    Elevator.ElevatorHeight heightCategory = elevator.getElevatorHeightCategory();
    
    // Set constraint multipliers based on elevator height
    switch (heightCategory) {
        case FULLY_RAISED:
            elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_HIGH_VEL_MULTIPLIER;
            elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_HIGH_ACCEL_MULTIPLIER;
            break;
        case MID_RAISED:
            elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_MID_VEL_MULTIPLIER;
            elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_MID_ACCEL_MULTIPLIER;
            break;
        case PARTIALLY_RAISED:
            elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_PARTIAL_VEL_MULTIPLIER;
            elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_PARTIAL_ACCEL_MULTIPLIER;
            break;
        case SLIGHTLY_RAISED:
        case LOWERED:
        default:
            elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_LOW_VEL_MULTIPLIER;
            elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_LOW_ACCEL_MULTIPLIER;
            break;
    }
    
    // Apply elevator based modifiers
    targetVel *= elevatorVelMultiplier;
    targetAccel *= elevatorAccelMultiplier;
    
    // Display multipliers to dashboard
    SmartDashboard.putNumber("Elevator Velocity Multiplier", elevatorVelMultiplier);
    SmartDashboard.putNumber("Elevator Accel Multiplier", elevatorAccelMultiplier);
    
    // Apply smoothing between previous and target values
    double smoothedVel = lerp(prevTransVel, targetVel, Constants.DriveToPoseConstants.CONSTRAINT_SMOOTHING_FACTOR);
    double smoothedAccel = lerp(prevTransAccel, targetAccel, Constants.DriveToPoseConstants.CONSTRAINT_SMOOTHING_FACTOR);
    
    // Save current values for next cycle
    prevTransVel = smoothedVel;
    prevTransAccel = smoothedAccel;
    
    // Log the constraint values
    SmartDashboard.putNumber("Translation Vel Constraint", smoothedVel);
    SmartDashboard.putNumber("Translation Accel Constraint", smoothedAccel);
    
    return new Constraints(smoothedVel, smoothedAccel);
  }
  
  /**
   * Get dynamic rotation constraints based on distance to target with smoothing,
   * now also considering elevator height
   */
  private Constraints getRotationConstraintsForDistance(double distance) {
      // Special case - if we're checking for a target at/near the origin, return zero constraints
      if (distance <= 0.0) {
          Elastic.sendNotification(
            new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
            "Zero Constraints", 
            "ZERO constraints: Invalid distance value: " + distance));
          return new Constraints(0, 0); // Zero velocity and acceleration
      }
      
      // Calculate base target velocity and acceleration based on distance
      double targetVel;
      double targetAccel;
      
      // Use default "far" constraints if no target or infinite distance
      if (Double.isInfinite(distance)) {
          // Use moderate default values that allow rotation but not too fast
          targetVel = Constants.DriveToPoseConstants.FAR_MAX_ROT_VEL * 0.5;
          targetAccel = Constants.DriveToPoseConstants.FAR_MAX_ROT_ACCEL * 0.5;
      }
      else if (distance < Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE) {
          targetVel = Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ROT_VEL;
          targetAccel = Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ROT_ACCEL;
      } else if (distance < Constants.DriveToPoseConstants.CLOSE_DISTANCE) {
          // Interpolate between CLOSE and VERY_CLOSE constraints
          double t = (distance - Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE) / 
                     (Constants.DriveToPoseConstants.CLOSE_DISTANCE - Constants.DriveToPoseConstants.VERY_CLOSE_DISTANCE);
          targetVel = lerp(Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ROT_VEL, 
                           Constants.DriveToPoseConstants.CLOSE_MAX_ROT_VEL, t);
          targetAccel = lerp(Constants.DriveToPoseConstants.VERY_CLOSE_MAX_ROT_ACCEL, 
                             Constants.DriveToPoseConstants.CLOSE_MAX_ROT_ACCEL, t);
      } else if (distance < Constants.DriveToPoseConstants.MID_DISTANCE) {
          // Interpolate between MID and CLOSE constraints
          double t = (distance - Constants.DriveToPoseConstants.CLOSE_DISTANCE) / 
                     (Constants.DriveToPoseConstants.MID_DISTANCE - Constants.DriveToPoseConstants.CLOSE_DISTANCE);
          targetVel = lerp(Constants.DriveToPoseConstants.CLOSE_MAX_ROT_VEL, 
                           Constants.DriveToPoseConstants.MID_MAX_ROT_VEL, t);
          targetAccel = lerp(Constants.DriveToPoseConstants.CLOSE_MAX_ROT_ACCEL, 
                             Constants.DriveToPoseConstants.MID_MAX_ROT_ACCEL, t);
      } else {
          // Interpolate between FAR and MID constraints
          double t = Math.min(1.0, (distance - Constants.DriveToPoseConstants.MID_DISTANCE) / 
                     (Constants.DriveToPoseConstants.FAR_DISTANCE - Constants.DriveToPoseConstants.MID_DISTANCE));
          targetVel = lerp(Constants.DriveToPoseConstants.MID_MAX_ROT_VEL, 
                           Constants.DriveToPoseConstants.FAR_MAX_ROT_VEL, t);
          targetAccel = lerp(Constants.DriveToPoseConstants.MID_MAX_ROT_ACCEL, 
                             Constants.DriveToPoseConstants.FAR_MAX_ROT_ACCEL, t);
      }
      
      // Apply elevator height-based modifiers to rotation constraints as well
      double elevatorVelMultiplier;
      double elevatorAccelMultiplier;
      
      // Get elevator height category
      Elevator.ElevatorHeight heightCategory = elevator.getElevatorHeightCategory();
      
      // Set constraint multipliers based on elevator height
      switch (heightCategory) {
          case FULLY_RAISED:
              elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_HIGH_VEL_MULTIPLIER;
              elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_HIGH_ACCEL_MULTIPLIER;
              break;
          case MID_RAISED:
              elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_MID_VEL_MULTIPLIER;
              elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_MID_ACCEL_MULTIPLIER;
              break;
          case PARTIALLY_RAISED:
              elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_PARTIAL_VEL_MULTIPLIER;
              elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_PARTIAL_ACCEL_MULTIPLIER;
              break;
          case SLIGHTLY_RAISED:
          case LOWERED:
          default:
              elevatorVelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_LOW_VEL_MULTIPLIER;
              elevatorAccelMultiplier = Constants.DriveToPoseConstants.ELEVATOR_LOW_ACCEL_MULTIPLIER;
              break;
      }
      
      // Apply elevator based modifiers
      targetVel *= elevatorVelMultiplier;
      targetAccel *= elevatorAccelMultiplier;
      
      // Apply smoothing between previous and target values
      double smoothedVel = lerp(prevRotVel, targetVel, Constants.DriveToPoseConstants.CONSTRAINT_SMOOTHING_FACTOR);
      double smoothedAccel = lerp(prevRotAccel, targetAccel, Constants.DriveToPoseConstants.CONSTRAINT_SMOOTHING_FACTOR);
      
      // Save current values for next cycle
      prevRotVel = smoothedVel;
      prevRotAccel = smoothedAccel;
      
      // Log the constraint values
      SmartDashboard.putNumber("Rotation Vel Constraint", smoothedVel);
      SmartDashboard.putNumber("Rotation Accel Constraint", smoothedAccel);
      
      return new Constraints(smoothedVel, smoothedAccel);
  }
  
  // Remove this method as we don't need it anymore
  // public void updatePeriodicCounter() {
  //   periodicCounter++;
  // }

  // Add a method to check if drive-to-pose is active
  public boolean isDriveToPoseActive() {
    return tempDriveToPoseCommand.isScheduled();
  }

  // Method to check if the robot has reached the target - fix by using direct field access
  public boolean isAtTargetPosition() {
    return isAtTargetPosition && isDriveToPoseActive();
  }
  
  public boolean isAtTargetRotation() {
    return isAtTargetRotation && isDriveToPoseActive();
  }
  
  public boolean isAtTarget() {
    return isAtTarget && isDriveToPoseActive();
  }

  // Add new helper method to check if a pose is at or near the origin
  private boolean isNearOrigin(Pose2d pose) {
      if (pose == null) {
          return true; // Null poses are treated as "near origin" for safety
      }
      
      // Consider "near origin" if within 0.5 meters of (0,0) or if pose is exactly at origin
      double distanceFromOrigin = Math.sqrt(pose.getX() * pose.getX() + pose.getY() * pose.getY());
      boolean nearOrigin = distanceFromOrigin < 0.5 || 
                          (Math.abs(pose.getX()) < 0.001 && Math.abs(pose.getY()) < 0.001);
      
      if (nearOrigin) {
          Elastic.sendNotification(
            new Elastic.Notification(Elastic.Notification.NotificationLevel.WARNING, 
            "Invalid Position", 
            "Position too close to origin: " + pose.getX() + ", " + pose.getY() + 
            " (distance: " + distanceFromOrigin + ")"));
      }
      
      return nearOrigin;
  }

  // Add a method to immediately cancel drive-to-pose (callable from other commands)
  public void cancelDriveToPose() {
    if (tempDriveToPoseCommand.isScheduled()) {
      tempDriveToPoseCommand.cancel();
      Elastic.sendNotification(
        new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
        "Drive-to-pose Canceled", 
        "Drive-to-pose manually canceled by external request"));
    }
  }
  
  // Command to cancel drive-to-pose (can be used in command groups)
  public Command cancelDriveToPoseCommand = Commands.runOnce(this::cancelDriveToPose);
  
  // Method to check if the nearest target is reached with custom tolerance
  public boolean isNearTarget(double customPositionTolerance) {
    if (!isDriveToPoseActive() || buttonBox.peekNextTarget() == null) {
      return false;
    }
    
    TargetClass target = buttonBox.peekNextTarget();
    Pose2d robotPose = drivebase.getPose();
    Pose2d targetPose = new Pose2d(target.getX(), target.getY(), new Rotation2d(target.getZ()));
    Pose2d allianceAdjustedPose = TargetClass.toPose2d(targetPose);
    
    double positionError = robotPose.getTranslation().getDistance(allianceAdjustedPose.getTranslation());
    return positionError <= customPositionTolerance;
  }
  
  // Get custom triggers for external use (with different tolerances)
  public Trigger createCustomDistanceTrigger(double customTolerance) {
    return new Trigger(() -> isNearTarget(customTolerance));
  }

  
  /**
   * Disable drive-to-pose specifically for autonomous mode
   */
  public void disableDriveToPoseForAuto() {
    driveToPoseStream.driveToPoseEnabled(false);
    
    SmartDashboard.putBoolean("Drive To Pose Enabled", false);
    SmartDashboard.putString("Drive Status", "Normal Driving");
    
    // Reset target position status
    isAtTargetPosition = false;
    isAtTargetRotation = false;
    isAtTarget = false;
  }
  
  /**
   * Reset the drive-to-pose system for autonomous
   */
  public void resetDriveToPoseForAuto() {
    disableDriveToPoseForAuto();
    pidControllersNeedUpdate = true;
    currentXController = null;
    currentRotController = null;
    prevTransVel = 0;
    prevTransAccel = 0;
    prevRotVel = 0;
    prevRotAccel = 0;
    
    // Reset timers for autonomous
    positionTimerActive = false;
    rotationTimerActive = false;
    targetTimerActive = false;
    positionReachedTimestamp = 0;
    rotationReachedTimestamp = 0;
    targetReachedTimestamp = 0;
  }
  
  // Add a method to toggle auto-target advancement
  public void setAutoAdvanceTargets(boolean enabled) {
    this.autoAdvanceTargets = enabled;
    SmartDashboard.putBoolean("Auto-Advance Targets", enabled);
  }
  
  // Command to toggle auto-target advancement
  public Command toggleAutoAdvanceTargetsCommand() {
    return Commands.runOnce(() -> {
      setAutoAdvanceTargets(!autoAdvanceTargets);
      Elastic.sendNotification(
        new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
        "Auto-Advance", 
        "Auto-advance targets " + (autoAdvanceTargets ? "enabled" : "disabled")));
    });
  }

  // Add this method to allow for explicit advancement to next target
  public Command advanceToNextTargetCommand() {
    return Commands.runOnce(() -> {
      if (buttonBox.hasQueue()) {
        buttonBox.getNextTarget();
        
        // Reset flags for the next target
        hasReachedAndClearedTarget = false;
        hasProcessedCurrentTarget = false;
        
        Elastic.sendNotification(
          new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
          "Target Advanced", 
          "Manually advanced to next target"));
        
        // Clear visualization of previous target
        drivebase.clearTargetVisualization();
        
        // If there are still targets in the queue, show status
        if (buttonBox.hasQueue()) {
          TargetClass nextTarget = buttonBox.peekNextTarget();
          if (nextTarget != null) {
            Elastic.sendNotification(
              new Elastic.Notification(Elastic.Notification.NotificationLevel.INFO, 
              "Next Target", 
              "Next target: " + nextTarget.getName()));
          }
        }
      }
    });
  }
}