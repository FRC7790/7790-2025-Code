package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Configs;
import frc.robot.Constants.ClimberConstants;
import frc.robot.util.Elastic;

public class Climber extends SubsystemBase {

    private SparkMax climberMotor = new SparkMax(ClimberConstants.ID, MotorType.kBrushless);
    
    // Add encoder and closed loop controller for position control
    private RelativeEncoder m_encoder;
    private SparkClosedLoopController climberClosedLoopController;
    
    // Add profile for smooth motion control
    private final TrapezoidProfile m_profile = new TrapezoidProfile(
        new TrapezoidProfile.Constraints(ClimberConstants.maxVelocity, ClimberConstants.maxAcceleration));
    private TrapezoidProfile.State m_goal = new TrapezoidProfile.State();
    private TrapezoidProfile.State m_setpoint = new TrapezoidProfile.State();
    
    // Position control variables
    public float climberDesiredPosition = 0;
    private double kDt = 0.02; // 20ms periodic time
    private boolean isInManualMode = true; // Default to manual mode
    private boolean isInitialized = false;

    private boolean cameraOn = false;
    
    // Track manual control input
    private double manualPower = 0;

    public Climber() {
        climberMotor.configure(
                Configs.Climber.climberConfig,
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
        
        // Get encoder and closed loop controller references
        m_encoder = climberMotor.getEncoder();
        climberClosedLoopController = climberMotor.getClosedLoopController();
        
        // Reset encoder position
        m_encoder.setPosition(0);
    }

    // Position control methods
    public void setFullRetract() {
        climberDesiredPosition = ClimberConstants.retractedPosition;
        isInManualMode = false; // Switch to position control
        manualPower = 0; // Clear manual power
    }


    public void setFullExtend() {

        if(!cameraOn){
            UsbCamera camera = CameraServer.startAutomaticCapture();
            camera.setResolution(240, 240);
            camera.setFPS(20);
            cameraOn = true;
            Elastic.selectTab("Climber");
        }

        climberDesiredPosition = ClimberConstants.extendedPosition;
        isInManualMode = false; // Switch to position control
        manualPower = 0; // Clear manual power
    }

    // Original manual control methods - these should switch to manual mode
    private void setZero() {
        climberMotor.set(0);
        manualPower = 0;
        isInManualMode = true; // Switch to manual control
    }


    public void moveWithPower(DoubleSupplier power) {
        double climbPower = -power.getAsDouble();

        if(Math.abs(climbPower) <= 0.1) {
            // Do nothing if power is close to zero - don't change modes or set power
            climbPower = 0;
        } else {
            climbPower = MathUtil.clamp(climbPower, -1, 1);
            isInManualMode = true; // Switch to manual control only with significant input
        }
        // Only set motor power directly when in manual mode
        if (isInManualMode) {
            climberMotor.set(climbPower);
        }
        manualPower = climbPower;
    }

    // Commands for position control (will switch to automatic mode)
    public Command climberFullRetractCommand() {
        return new InstantCommand(() -> setFullRetract());
    }


    public Command climberFullExtendCommand() {
        return new InstantCommand(() -> setFullExtend());
    }

    public Command climberStopCommand() {
        return new InstantCommand(() -> setZero());
    }
    
    // Position feedback methods
    public Trigger isAtSetpoint() {
        return new Trigger(() -> {
            boolean atSetpoint = m_encoder.getPosition() >= climberDesiredPosition - ClimberConstants.SETPOINT_TOLERANCE &&
                               m_encoder.getPosition() <= climberDesiredPosition + ClimberConstants.SETPOINT_TOLERANCE;
            SmartDashboard.putBoolean("Climber At Setpoint", atSetpoint);
            return atSetpoint;
        });
    }
    
    public boolean isAtSetpointBoolean() {
        return m_encoder.getPosition() >= climberDesiredPosition - ClimberConstants.SETPOINT_TOLERANCE &&
               m_encoder.getPosition() <= climberDesiredPosition + ClimberConstants.SETPOINT_TOLERANCE;
    }
    
    public Trigger isFullyRetracted() {
        return new Trigger(() -> {
            boolean retracted = m_encoder.getPosition() >= ClimberConstants.retractedPosition - ClimberConstants.SETPOINT_TOLERANCE &&
                               m_encoder.getPosition() <= ClimberConstants.retractedPosition + ClimberConstants.SETPOINT_TOLERANCE;
            SmartDashboard.putBoolean("Climber Fully Retracted", retracted);
            return retracted;
        });
    }
    
    public Trigger isFullyExtended() {
        return new Trigger(() -> {
            boolean extended = m_encoder.getPosition() >= ClimberConstants.extendedPosition - ClimberConstants.SETPOINT_TOLERANCE &&
                              m_encoder.getPosition() <= ClimberConstants.extendedPosition + ClimberConstants.SETPOINT_TOLERANCE;
            SmartDashboard.putBoolean("Climber Fully Extended", extended);
            return extended;
        });
    }

    @Override
    public void periodic() {
        // Initialize on first run
        if (!isInitialized) {
            climberDesiredPosition = (float) m_encoder.getPosition();
            isInitialized = true;
            isInManualMode = true; // Default to manual mode on startup
        }
        
        // Dashboard updates
        SmartDashboard.putNumber("Climber Current Position", m_encoder.getPosition());
        SmartDashboard.putNumber("Climber Desired Position", climberDesiredPosition);
        SmartDashboard.putBoolean("Climber Manual Mode", isInManualMode);
        SmartDashboard.putNumber("Climber Manual Power", manualPower);

        SmartDashboard.putNumber("Climber OutputPower", climberMotor.getOutputCurrent());
        
        // Only use position control when not in manual mode
        if (!isInManualMode) {
            float desiredPosition = (float) MathUtil.clamp(climberDesiredPosition, 
                                                         ClimberConstants.min, 
                                                         ClimberConstants.max);
            
            // Use trapezoid profile for smooth motion
            m_goal = new TrapezoidProfile.State(desiredPosition, 0);
            m_setpoint = m_profile.calculate(kDt, m_setpoint, m_goal);
            

            // Set the motor to the profile setpoint
            climberClosedLoopController.setReference(m_setpoint.position, 
                                                  ControlType.kPosition, 
                                                  ClosedLoopSlot.kSlot0);
        }
    }
}