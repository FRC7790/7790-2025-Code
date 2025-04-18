// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of the
// WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{

  public static final float ROBOT_MASS = (float) Units.lbsToKilograms(115); 
  public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final float LOOP_TIME  = 0.13f; //s, 20ms + 110ms sprk max velocity lag
  public static final float MAX_SPEED  = (float) Units.feetToMeters(22.1);
  public static final float WHEEL_LOCK_TIME = 10;
  // Maximum speed of the robot in meters per second, used to limit acceleration.

  public static final float slowSpeedClamp = 0.1f;
  public static final float mediumSpeedClamp = 0.2f;

  public static final float DEADBAND = 0.075f;

  public static final float armLength = (float)Units.inchesToMeters(0);
    


  public static final class AlgaeArmConstants{
    public static final int ID = 14;

    public static final float stowedUpAngle = 0.305f;
    public static final float straightOutAngle = 0.535f;
    public static final float groundIntakeAngle = 0.45f;

    public static final float holdAngle = 0.34f;

    // Current threshold for detecting algae
    public static final float currentThreshold = 15.0f;

    public static final float angleOffset = 0.75f;

    public static final float max = 0.535f;
    public static final float min = 0.28f;

    public static final float manualMultiplier = 0.01f;


    public static final float P = 2.0f;
    //public static final float P = 0.0f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    public static final float maxVelocity = 50;
    public static final float maxAcceleration = 50;
    public static final float allowedClosedLoopError = .005f;

    public static final float L1ScoreAngle = 0.33f;
    
    // New feedforward constants
    public static final float kS = 0.0f;  // Static friction compensation
    public static final float kG = 0.3f;  // Gravity compensation (tune based on arm mass)
    public static final float kV = 0.0f;  // Velocity feedforward
    public static final float kA = 0.0f;  // Acceleration feedforward
    public static final float feedforwardOffset = 0.75f; // Offset for zero position
  }

    public static final class AlgaeShooterConstants{
    public static final int ID = 16;
    public static final float intake = 0.4f;
    
    public static final float outake = -0.4f;
    public static final float currentThreshold = 10f; // Current threshold for detecting algae
    
    // Trigger control constants
    public static final float triggerThreshold = 0.25f;    // Minimum trigger value to activate
    public static final float maxTriggerIntake = 0.5f;    // Maximum intake speed with trigger (left)
    public static final float maxTriggerOutake = -0.6f;   // Maximum outake speed with trigger (right)
    
    public static final float DEBOUNCE_TIME = 1.0f; // Time to debounce current threshold (seconds)
  }
  public static final class ShooterConstants{
    public static final int ID = 19;
    public static final float intake = 0.50f;
    public static final float outake = -.50f;
    public static final float currentThreshold = 15;
    public static final float DEBOUNCE_TIME = 0.075f; // Time to debounce current threshold (seconds)
  }

  public static final class ShooterArmConstants{
    public static final int ID = 15;

    public static final float angleOffset = .83f;

    public static final float manualMultiplier = -0.005f;

    public static final float min = 0.252f;
    //public static final float max = .9f;
    public static final float max = 0.698f;

    

    public static final float maxManual = 0.698f;
    //public static final float maxManual = .9f;
    
    public static final float climbAngle = 0.698f; //Larger than this in reality

    public static final float scoreAngleLOW = 0.698f;
    public static final float scoreAngleHIGH = 0.661367297f;
    // Minimum angle allowed in reef zone (prevents arm from going too low in reef)
    public static final float reefZoneMinimumAngle = 0.643f; // This should be between ballAngle and scoreAngleHIGH
    // Debounce time when exiting reef zone (seconds)
    public static final float reefZoneExitDebounceTime = 0.5f;

    public static final float loadAngle = 0.252f;
    public static final float outLoadAngle = 0.3f;

    
    // Ball position angle
    public static final float preBallAngle = 0.64974045753479f
    ;
    public static final float preLowBallAngle = 0.64974045753479f;
    public static final float ballAngle = 0.64974045753479f;
    public static final float preBallBelowAngle = 0.64974045753479f;
    public static final float L1Angle = 0.5f;

    public static final float realL1Angle = 0.252f;

    //public static final float P = 7.0f;
    //public static final float I = 0.0f;
    //public static final float D = 0.5f;

    public static final float P = 2.5f;
    //public static final float P = 1.75f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;

    // Uncomment profile constants
    public static final float maxVelocity = 3;
    public static final float maxAcceleration = 3;

    public static final float allowedClosedLoopError = 0.005f;
    
    // New feedforward constants
    public static final float kS = 0.0f;  // Static friction compensation
    public static final float kG = .6f;  // Gravity compensation (tune this based on arm mass)
    public static final float kV = 0.0f;  // Velocity feedforward
    public static final float kA = 0.0f;  // Acceleration feedforward
    
    public static final float feedforwardOffset = 0.25f;

    // PID position tolerance
    public static final float positionTolerance = 0.01f;
  }

  public static final class ClimberConstants{

    public static final int ID = 18;

    public static final float extendSpeed = 1;
    public static final float retractSpeed = -1f;
    
    // Position control constants
    public static final float P = 1.0f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    
    // Motion profile parameters
    public static final float maxVelocity = 1000;
    public static final float maxAcceleration = 1000;
    
    
    public static final float min = 0;  // Max extended position
    public static final float max = 119.07172393798828f;    // Fully retracted position
    
    // Predefined positions (reversed from previous values to match Neo motor behavior)
    public static final float retractedPosition = 0f;      // Fully retracted
    public static final float extendedPosition = 109f;    // Fully extended
    
    // Tolerance for position control
    public static final float SETPOINT_TOLERANCE = 2.0f;
  }
  
  public static final class ElevatorConstants{
    
    public static final int ID = 13;

    public static final int slaveID = 17;
    
    public static final float manualMultiplier = .5f;
    //public static final float manualMultiplier = .2f; //For Testing

    //Gray Spools
    //public static final float min = -44f;
    public static final float min = -28.87722f;
    public static final float max = 0.0f;

    // elevator set positions
    public static final float L4Pose = -28.87722f;
    public static final float L3LPose = -15.066f;
    public static final float L3RPose = -15.066f;

    public static final float L2RPose = -6.552f;
    public static final float L2LPose = -6.552f;
    public static final float pickupPose = -15.373566627502441f
    ;
    public static final float climbPose = -0;

    //-6.296857f WHEN TO DISABLE FEED FORWARD
    
    public static final float downPosition = -4.2f;
    
    // Ball pickup positions
     public static final float highBallPose = -16.913700103759766f;
     public static final float highBallBelowPose = -16.913700103759766f;
     public static final float lowBallPose = -7.9168572425842285f;
    
    // New elevator height thresholds for speed control
    public static final float SLIGHTLY_RAISED_THRESHOLD = -6f;
    public static final float PARTIALLY_RAISED_THRESHOLD = -12f;
    public static final float MID_RAISED_THRESHOLD = -18f;
    public static final float FULLY_RAISED_THRESHOLD = -24f;
    
    // Elevator setpoint tolerance constants
    public static final float STANDARD_SETPOINT_TOLERANCE = 1.2f;
    public static final float NEAR_SETPOINT_TOLERANCE = 3.0f;
    public static final float APPROACHING_SETPOINT_TOLERANCE = 4.8f;
    
    // Home position threshold
    public static final float HOME_POSITION_THRESHOLD = 3.0f;
    
    // Intake clearance thresholds
    public static final float INTAKE_CLEARANCE_MARGIN = 3.0f;
    
    public static final float P = 1.5f;
    public static final float I = 0.0f;
    public static final float D = 0.1f;
    
    public static final float maxVelocity = 500;
    public static final float maxAcceleration = 70;

    public static final float L1ScoreHeight = -18.782352447509766f;
    
    // Feedforward constants for ElevatorFeedforward
    public static final float kS = 0.0f;
    //public static final float kG = 0.04130375012755394f;   // Gravity compensation (voltage)
    public static final float kG = 0;   // Gravity compensation (voltage)
    public static final float kV = 0.0f;   // Velocity feedforward (voltage per unit/s)  
    public static final float kA = 0.0f;  // Acceleration feedforward (voltage per unit/s²)
  }

  public static final class FunnelConstants {
    public static final int ID = 20;
    
    // Position constants
    public static final float homePosition = .54f;
    public static final float fullUpPosition = .24f;
    
    // Angle offset for absolute encoder
    public static final float angleOffset = 0.13f; // Adjust this based on mechanical setup
    
    // Min and max position limits
    public static final float min = .24f;
    public static final float max = .56f;
    
    // Safety threshold - minimum algae arm position that allows funnel movement
    public static final float SAFE_ALGAE_ARM_POSITION = 0.3f;
    
    // Manual control multiplier
    public static final float manualMultiplier = 0.005f;
    
    // Position tolerance for "at position" detection
    public static final float positionTolerance = 0.05f;
    
    // PID Constants
    public static final float P = 2.5f;
    //public static final float P = 0.0f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    
    // Motion profile parameters
    public static final float maxVelocity = 6;
    public static final float maxAcceleration = 6;
    public static final float allowedClosedLoopError = 0.005f;
    
    // New feedforward constants
    public static final float kS = 0.0f;  // Static friction compensation
    public static final float kG = 1.0f;  // Gravity compensation
    public static final float kV = 0.0f;  // Velocity feedforward
    public static final float kA = 0.0f;  // Acceleration feedforward
    public static final float feedforwardOffset = 0.0f; // Offset for zero position
    
    // Pre-intake position for coral detection
    public static final float preIntakePosition = 0.42f; // Slightly raised from home position
    
    
    // New current detection parameters
    public static final float currentThreshold = 3.0f;  // Current spike threshold (amps)
    public static final float currentNormalLevel = 0.25f; // Normal operating current (for comparison)
    public static final float currentSpikeDuration = 0.05f; // How long a spike needs to last (seconds)
    
    // Shaking parameters
    public static final float shakingAmplitude = 0.03f; // How far to move when shaking
    public static final float shakingFrequency = 3f; // Increased oscillations per second
    public static final float shakingDuration = 15f; // Extended max duration (will stop early when coral loaded)
    
    // Multiple detection methods
    public static final boolean USE_VELOCITY_DETECTION = true;
    public static final boolean USE_CURRENT_DETECTION = true;

    public static final float L1Pose = 0.504f;

    public static final float L1Dump = 0.425033f;
  }

  public static final class LEDConstants{
    
    public static final int port = 0;
    public static final int length = 104;

    // Add flash pattern constants
    public static final double FLASH_ON_DURATION = 0.05;     // Time flash stays on (seconds)
    public static final double FLASH_OFF_DURATION = 0.05;    // Time flash stays off (seconds)
    public static final int DEFAULT_FLASH_COUNT = 3;        // Default number of flashes
    public static final double FLASH_COMPLETE_DELAY = 0.1;  // Delay after flashing completes before returning to normal pattern

    public static final int linedUpFlashCount = 5;

    // LED layout constants
    public static final int LEFT_LEDS = 43;
    public static final int TOP_LEDS = 18;
    public static final int RIGHT_LEDS = 43;
    public static final int TOTAL_LEDS = LEFT_LEDS + TOP_LEDS + RIGHT_LEDS;
    
    // WS2812B LED power specifications
    public static final double MILLIAMPS_PER_LED_FULL_WHITE = 60.0; // mA per LED at full white
    public static final double MILLIAMPS_PER_RED = 20.0;   // mA for full red
    public static final double MILLIAMPS_PER_GREEN = 20.0; // mA for full green
    public static final double MILLIAMPS_PER_BLUE = 20.0;  // mA for full blue
    
    // Power constraint - maximum allowed current in amps
    public static final double MAX_AMPERAGE = 1.0; // Maximum current draw in amps
    
    // Default starting brightness if not using dynamic calculation
    public static final double DEFAULT_BRIGHTNESS = 0.1;
    
    // Power headroom to prevent hitting exact power limit (0-1)
    public static final double POWER_SAFETY_MARGIN = 0.9;
    
    // Calculate max brightness based on power constraints
    // MAX_BRIGHTNESS = MAX_AMPERAGE / (number of LEDs * current per LED at full brightness)
    public static final double MAX_BRIGHTNESS = MAX_AMPERAGE / (length * (MILLIAMPS_PER_LED_FULL_WHITE / 1000.0));  // Convert mA to A

    public static final int ANIMATION_CYCLE_MODULO = 20;
    public static final int FLAME_UPDATE_INTERVAL = 3;
    public static final double NEW_TARGET_PROBABILITY = 0.1;
    public static final double MIN_FLAME_INTENSITY = 0.5;
    public static final double MAX_FLAME_INTENSITY = 1.0;
    public static final double MIN_TARGET_INTENSITY = 0.4;
    public static final double MAX_TARGET_INTENSITY = 1.0;
    public static final double MIN_CHANGE_SPEED = 0.05;
    public static final double MAX_CHANGE_SPEED = 0.2;
    public static final double MIN_BRIGHTNESS_LIMIT = 0.2;
    public static final double MAX_BRIGHTNESS_LIMIT = 1.0;
    
    // Left side flame constants
    public static final double LEFT_SINE_AMPLITUDE = 0.3;
    public static final double LEFT_SINE_OFFSET = 0.7;
    public static final double LEFT_SINE_FREQUENCY = 10.0;
    public static final double LEFT_BOTTOM_INTENSITY = 0.3;
    public static final double LEFT_BOTTOM_OFFSET = 0.7;
    
    // Top flame constants
    public static final double TOP_COSINE_AMPLITUDE = 0.2;
    public static final double TOP_COSINE_OFFSET = 0.8;
    public static final double TOP_COSINE_FREQUENCY = 5.0;
    public static final double TOP_EDGE_INTENSITY = 0.3;
    public static final double TOP_EDGE_OFFSET = 0.7;
    
    // Right side flame constants
    public static final double RIGHT_SINE_AMPLITUDE = 0.3;
    public static final double RIGHT_SINE_OFFSET = 0.7;
    public static final double RIGHT_SINE_FREQUENCY = 10.0;
    public static final double RIGHT_BOTTOM_INTENSITY = 0.3;
    public static final double RIGHT_BOTTOM_OFFSET = 0.7;
    
    // Color variation constants
    public static final double RED_GREEN_MULTIPLIER_BASE = 0.3;
    public static final double RED_GREEN_MULTIPLIER_SCALE = 0.4;
    public static final double BLUE_GREEN_MULTIPLIER = 0.7;
    public static final double BLUE_RED_BRIGHTNESS_MULTIPLIER = 0.1;

    // Breathing pattern constants
    public static final double BREATHING_CYCLE_PERIOD = 1.5; // Full breath cycle in seconds
    public static final double BREATHING_MIN_INTENSITY = 0.05; // Minimum brightness in breath cycle
    public static final double BREATHING_MAX_INTENSITY = 0.8; // Maximum brightness in breath cycle
    public static final int BREATHING_STEPS = 50; // Number of steps per breath (smoother = more steps)
    
    // New constants for distance-based breathing mode
    public static final double DISTANCE_BREATHE_MIN_DISTANCE = 0.2;  // Minimum distance (meters)
    public static final double DISTANCE_BREATHE_MAX_DISTANCE = 5.0;  // Maximum distance (meters)
    public static final double DISTANCE_BREATHE_MIN_PERIOD   = .25; // Fast cycle for close targets (seconds)
    public static final double DISTANCE_BREATHE_MAX_PERIOD   = 2.0;  // Slow cycle for far targets (seconds)
  }
  
  public static final class ScopeConstants{
    
  }
  public static final class AprilTagVisionConstants{


    public static final float rightCamXOffset = (float)Units.inchesToMeters(4.814);
    public static final float rightCamYOffset = (float)Units.inchesToMeters(-8.771);
    public static final float rightCamZOffset = (float)Units.inchesToMeters(16.194);

    public static final float rightCamRoll = (float)Units.degreesToRadians(0.0);
    public static final float rightCamPitch = (float)Units.degreesToRadians(15.0);
    public static final float rightCamYaw = (float)Units.degreesToRadians(15.0);

    public static final float leftCamXOffset = (float)Units.inchesToMeters(5.672);
    public static final float leftCamYOffset = (float)Units.inchesToMeters(-7.956);
    public static final float leftCamZOffset = (float)Units.inchesToMeters(34.184);

    public static final float leftCamRoll = (float)Units.degreesToRadians(0.0);
    public static final float leftCamPitch = (float)Units.degreesToRadians(30.0);
    public static final float leftCamYaw = (float)Units.degreesToRadians(15.0);

    public static final float limelightXOffset = (float)Units.inchesToMeters(-1.588);
    public static final float limelightYOffset = (float)Units.inchesToMeters(2.137);
    public static final float limelightZOffset = (float)Units.inchesToMeters(36.273);

    public static final float limelightRoll = (float)Units.degreesToRadians(0.0);
    public static final float limelightPitch = (float)Units.degreesToRadians(-30.0);
    public static final float limelightYaw = (float)Units.degreesToRadians(180);
    

  }
  public static final class QuestNavVisionConstants {
    public static final Transform2d QUEST_NAV_TO_ROBOT = new Transform2d(
            new Translation2d(Units.inchesToMeters(2.048), Units.inchesToMeters(8.92)),
            new Rotation2d(Units.degreesToRadians(0)));
  }
  public static final class TargetClassConstants{

    public static final float LeftStartX = (float)Units.inchesToMeters(297.5);
    public static final float LeftStartY = (float)Units.inchesToMeters(242.0);
    public static final float LeftStartZ = (float)Units.degreesToRadians(240.0);
    public static final float CenterStartX = (float)Units.inchesToMeters(297.5);
    public static final float CenterStartY = (float)Units.inchesToMeters(158.5);
    public static final float CenterStartZ = (float)Units.degreesToRadians(180);
    public static final float RightStartX = (float)Units.inchesToMeters(297.5);
    public static final float RightStartY = (float)Units.inchesToMeters(75.0);
    public static final float RightStartZ = (float)Units.degreesToRadians(120.0);

    public static final float SLPositionX = (float)Units.inchesToMeters(43.5);
    public static final float SLPositionY = (float)Units.inchesToMeters(278.52);
    public static final float SLPositionZ = (float)Units.degreesToRadians(306);

    public static final float SRPositionX = (float)Units.inchesToMeters(43.5);
    public static final float SRPositionY = (float)Units.inchesToMeters(38.48);
    public static final float SRPositionZ = (float)Units.degreesToRadians(54);

    public static final float CLPositionX = (float)Units.inchesToMeters(297.5);
    public static final float CLPositionY = (float)Units.inchesToMeters(286.75);
    public static final float CLPositionZ = (float)Units.degreesToRadians(240);

    public static final float CCPositionX = (float)Units.inchesToMeters(297.5);
    public static final float CCPositionY = (float)Units.inchesToMeters(243.75);
    public static final float CCPositionZ = (float)Units.degreesToRadians(215);

    public static final float CRPositionX = (float)Units.inchesToMeters(297.5);
    public static final float CRPositionY = (float)Units.inchesToMeters(200.75);
    public static final float CRPositionZ = (float)Units.degreesToRadians(190);
    
    public static final float ProcessorPositionX = (float)Units.inchesToMeters(234);
    public static final float ProcessorPositionY = (float)Units.inchesToMeters(26);
    public static final float ProcessorPositionZ = (float)Units.degreesToRadians(270);

    // Hexagon center and dimensions
    public static final float hexagonCenterX = (float)Units.inchesToMeters(176.745);
    public static final float hexagonCenterY = (float)Units.inchesToMeters(158.5);
    public static final float hexagonRadius = (float)Units.inchesToMeters(50.0);
    
    // Offset constants
    public static final float LeftOffset = (float)Units.inchesToMeters(7.5);
    public static final float RightOffset = (float)Units.inchesToMeters(7.5);
    public static final float BackOffset = (float)Units.inchesToMeters(-4.0);
    public static final float BackOffsetAuto = (float)Units.inchesToMeters(-5.5);
    public static final float BackOffsetAutoBack = (float)Units.inchesToMeters(-4.5);
    public static final float AlgaeInset = (float)Units.inchesToMeters(8.0);
    public static final float AlgaeOffset = (float)Units.inchesToMeters(12.0);

    // Face 1 (0 degrees)
    public static final float zValueC1XX = (float)Units.degreesToRadians(0);
    public static final float zValueC10X = (float)Units.degreesToRadians(180);
    
    // Flip right/left offset directions
    public static final float xValueC1X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC1XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueC1X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC1XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC1XX + Math.PI);

    public static final float xValueC1X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC1XX + Math.PI) - RightOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueC1X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC1XX + Math.PI) + RightOffset * (float)Math.cos(zValueC1XX + Math.PI);
    
    public static final float xValueC130 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC1XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueC130 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC1XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC1XX + Math.PI);

    public static final float xValueC131 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC1XX + Math.PI) - RightOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueC131 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC1XX + Math.PI) + RightOffset * (float)Math.cos(zValueC1XX + Math.PI);

    public static final float xValueA1X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC1XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueA1X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC1XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC1XX + Math.PI);
    public static final float xValueA1X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC1XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueA1X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC1XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC1XX + Math.PI);
    public static final boolean isHighAlgaeA1XX = true;

    public static final int faceValueC1XX = 1;

    // Face 2 (60 degrees)
    public static final float zValueC2XX = (float)Units.degreesToRadians(60);
    public static final float zValueC20X = (float)Units.degreesToRadians(240);
    
    public static final float xValueC2X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC2XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueC2X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC2XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC2XX + Math.PI);

    public static final float xValueC2X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC2XX + Math.PI) - RightOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueC2X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC2XX + Math.PI) + RightOffset * (float)Math.cos(zValueC2XX + Math.PI);
    
    public static final float xValueC230 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC2XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueC230 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC2XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC2XX + Math.PI);

    public static final float xValueC231 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC2XX + Math.PI) - RightOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueC231 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC2XX + Math.PI) + RightOffset * (float)Math.cos(zValueC2XX + Math.PI);

    public static final float xValueA2X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC2XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueA2X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC2XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC2XX + Math.PI);
    public static final float xValueA2X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC2XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueA2X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC2XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC2XX + Math.PI);
    public static final boolean isHighAlgaeA2XX = false;

    public static final int faceValueC2XX = 2;

    // Face 3 (120 degrees)
    public static final float zValueC3XX = (float)Units.degreesToRadians(120);
    public static final float zValueC30X = (float)Units.degreesToRadians(300);
    
    public static final float xValueC3X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC3XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueC3X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC3XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC3XX + Math.PI);

    public static final float xValueC3X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC3XX + Math.PI) - RightOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueC3X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC3XX + Math.PI) + RightOffset * (float)Math.cos(zValueC3XX + Math.PI);
    
    public static final float xValueC330 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC3XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueC330 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC3XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC3XX + Math.PI);

    public static final float xValueC331 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC3XX + Math.PI) - RightOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueC331 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC3XX + Math.PI) + RightOffset * (float)Math.cos(zValueC3XX + Math.PI);
    
    public static final float xValueS330 = hexagonCenterX + (hexagonRadius - BackOffsetAuto) * (float)Math.cos(zValueC3XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueS330 = hexagonCenterY + (hexagonRadius - BackOffsetAuto) * (float)Math.sin(zValueC3XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC3XX + Math.PI);

    public static final float xValueS331 = hexagonCenterX + (hexagonRadius - BackOffsetAuto) * (float)Math.cos(zValueC3XX + Math.PI) - RightOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueS331 = hexagonCenterY + (hexagonRadius - BackOffsetAuto) * (float)Math.sin(zValueC3XX + Math.PI) + RightOffset * (float)Math.cos(zValueC3XX + Math.PI);

    public static final float xValueA3X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC3XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueA3X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC3XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC3XX + Math.PI);
    public static final float xValueA3X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC3XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueA3X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC3XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC3XX + Math.PI);
    public static final boolean isHighAlgaeA3XX = true;

    public static final int faceValueC3XX = 3;

    // Face 4 (180 degrees)
    public static final float zValueC4XX = (float)Units.degreesToRadians(180);
    public static final float zValueC40X = (float)Units.degreesToRadians(0);
    
    public static final float xValueC4X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC4XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueC4X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC4XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC4XX + Math.PI);

    public static final float xValueC4X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC4XX + Math.PI) - RightOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueC4X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC4XX + Math.PI) + RightOffset * (float)Math.cos(zValueC4XX + Math.PI);
    
    public static final float xValueC430 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC4XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueC430 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC4XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC4XX + Math.PI);

    public static final float xValueC431 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC4XX + Math.PI) - RightOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueC431 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC4XX + Math.PI) + RightOffset * (float)Math.cos(zValueC4XX + Math.PI);
    
    public static final float xValueS430 = hexagonCenterX + (hexagonRadius - BackOffsetAutoBack) * (float)Math.cos(zValueC4XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueS430 = hexagonCenterY + (hexagonRadius - BackOffsetAutoBack) * (float)Math.sin(zValueC4XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC4XX + Math.PI);

    public static final float xValueS431 = hexagonCenterX + (hexagonRadius - BackOffsetAutoBack) * (float)Math.cos(zValueC4XX + Math.PI) - RightOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueS431 = hexagonCenterY + (hexagonRadius - BackOffsetAutoBack) * (float)Math.sin(zValueC4XX + Math.PI) + RightOffset * (float)Math.cos(zValueC4XX + Math.PI);

    public static final float xValueA4X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC4XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueA4X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC4XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC4XX + Math.PI);
    public static final float xValueA4X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC4XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueA4X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC4XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC4XX + Math.PI);
    public static final boolean isHighAlgaeA4XX = false;

    public static final int faceValueC4XX = 4;

    // Face 5 (240 degrees)
    public static final float zValueC5XX = (float)Units.degreesToRadians(240);
    public static final float zValueC50X = (float)Units.degreesToRadians(60);
    
    public static final float xValueC5X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC5XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueC5X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC5XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC5XX + Math.PI);

    public static final float xValueC5X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC5XX + Math.PI) - RightOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueC5X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC5XX + Math.PI) + RightOffset * (float)Math.cos(zValueC5XX + Math.PI);
    
    public static final float xValueC530 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC5XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueC530 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC5XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC5XX + Math.PI);

    public static final float xValueC531 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC5XX + Math.PI) - RightOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueC531 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC5XX + Math.PI) + RightOffset * (float)Math.cos(zValueC5XX + Math.PI);
    
    public static final float xValueS530 = hexagonCenterX + (hexagonRadius - BackOffsetAuto) * (float)Math.cos(zValueC5XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueS530 = hexagonCenterY + (hexagonRadius - BackOffsetAuto) * (float)Math.sin(zValueC5XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC5XX + Math.PI);

    public static final float xValueS531 = hexagonCenterX + (hexagonRadius - BackOffsetAuto) * (float)Math.cos(zValueC5XX + Math.PI) - RightOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueS531 = hexagonCenterY + (hexagonRadius - BackOffsetAuto) * (float)Math.sin(zValueC5XX + Math.PI) + RightOffset * (float)Math.cos(zValueC5XX + Math.PI);

    public static final float xValueA5X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC5XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueA5X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC5XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC5XX + Math.PI);
    public static final float xValueA5X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC5XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueA5X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC5XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC5XX + Math.PI);
    public static final boolean isHighAlgaeA5XX = true;

    public static final int faceValueC5XX = 5;

    // Face 6 (300 degrees)
    public static final float zValueC6XX = (float)Units.degreesToRadians(300);
    public static final float zValueC60X = (float)Units.degreesToRadians(120);
    
    public static final float xValueC6X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC6XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueC6X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC6XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC6XX + Math.PI);

    public static final float xValueC6X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC6XX + Math.PI) - RightOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueC6X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC6XX + Math.PI) + RightOffset * (float)Math.cos(zValueC6XX + Math.PI);
    
    public static final float xValueC630 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC6XX + Math.PI) + LeftOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueC630 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC6XX + Math.PI) - LeftOffset * (float)Math.cos(zValueC6XX + Math.PI);
    
    public static final float xValueC631 = hexagonCenterX + (hexagonRadius - BackOffset) * (float)Math.cos(zValueC6XX + Math.PI) - RightOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueC631 = hexagonCenterY + (hexagonRadius - BackOffset) * (float)Math.sin(zValueC6XX + Math.PI) + RightOffset * (float)Math.cos(zValueC6XX + Math.PI);

    public static final float xValueA6X0 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC6XX + Math.PI) - AlgaeInset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueA6X0 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC6XX + Math.PI) + AlgaeInset * (float)Math.cos(zValueC6XX + Math.PI);
    public static final float xValueA6X1 = hexagonCenterX + hexagonRadius * (float)Math.cos(zValueC6XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueA6X1 = hexagonCenterY + hexagonRadius * (float)Math.sin(zValueC6XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC6XX + Math.PI);
    public static final boolean isHighAlgaeA6XX = false;

    public static final int faceValueC6XX = 6;

    public static final int heightCX0X = 0;
    public static final int heightCX1X = 1;
    public static final int heightCX2X = 2;
    public static final int heightCX3X = 3;

    public static final boolean setLeftCXX0 = true;
    public static final boolean setLeftCXX1 = false;

    
    public static final float BackupOffset = (float)Units.inchesToMeters(20);
    
    // Face 1 Algae with backup offset (0 degrees)
    public static final float xValueA1X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC1XX + Math.PI);
    public static final float yValueA1X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float xValueA1X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC1XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC1XX + Math.PI);
    public static final float yValueA1X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC1XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC1XX + Math.PI);

    // Face 2 Algae with backup offset (60 degrees)
    public static final float xValueA2X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC2XX + Math.PI);
    public static final float yValueA2X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float xValueA2X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC2XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC2XX + Math.PI);
    public static final float yValueA2X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC2XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC2XX + Math.PI);

    // Face 3 Algae with backup offset (120 degrees)
    public static final float xValueA3X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC3XX + Math.PI);
    public static final float yValueA3X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float xValueA3X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC3XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC3XX + Math.PI);
    public static final float yValueA3X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC3XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC3XX + Math.PI);

    // Face 4 Algae with backup offset (180 degrees)
    public static final float xValueA4X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC4XX + Math.PI);
    public static final float yValueA4X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float xValueA4X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC4XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC4XX + Math.PI);
    public static final float yValueA4X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC4XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC4XX + Math.PI);

    // Face 5 Algae with backup offset (240 degrees)
    public static final float xValueA5X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC5XX + Math.PI);
    public static final float yValueA5X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float xValueA5X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC5XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC5XX + Math.PI);
    public static final float yValueA5X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC5XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC5XX + Math.PI);

    // Face 6 Algae with backup offset (300 degrees)
    public static final float xValueA6X0Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC6XX + Math.PI);
    public static final float yValueA6X0Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float xValueA6X1Backup = hexagonCenterX + (hexagonRadius + BackupOffset) * (float)Math.cos(zValueC6XX + Math.PI) - AlgaeOffset * (float)Math.sin(zValueC6XX + Math.PI);
    public static final float yValueA6X1Backup = hexagonCenterY + (hexagonRadius + BackupOffset) * (float)Math.sin(zValueC6XX + Math.PI) + AlgaeOffset * (float)Math.cos(zValueC6XX + Math.PI);
  }

  public static final class ZoneConstants {

    public static final float reefCenterX = (float)Units.inchesToMeters(176.745);
    public static final float reefCenterY = (float)Units.inchesToMeters(158.5);
    public static final float reefZoneRadius = (float)Units.inchesToMeters(76);
    
    // Buffer distance to keep outside reef zone until elevator is ready
    public static final float REEF_ZONE_ENTRY_BUFFER = 0.3f;
    

    // Adding convenience values for min/max coordinates to make boundary checks easier
    public static final float RCoralStationMinX = (float)Units.inchesToMeters(0);
    public static final float RCoralStationMaxX = (float)Units.inchesToMeters(86.99);
    public static final float RCoralStationMinY = (float)Units.inchesToMeters(0);
    public static final float RCoralStationMaxY = (float)Units.inchesToMeters(76.95);
    
    public static final float LCoralStationMinX = (float)Units.inchesToMeters(0);
    public static final float LCoralStationMaxX = (float)Units.inchesToMeters(86.99);
    public static final float LCoralStationMinY = (float)Units.inchesToMeters(235.95);
    public static final float LCoralStationMaxY = (float)Units.inchesToMeters(312.91);

    public static final float BargeMinX = (float)Units.inchesToMeters(297.5);
    public static final float BargeMaxX = (float)Units.inchesToMeters(383.5);
    public static final float BargeMinY = (float)Units.inchesToMeters(0);
    public static final float BargeMaxY = (float)Units.inchesToMeters(320);
    
    // Speed multipliers for each zone
    public static final float reefSpeedMultiplier = 0.5f;
    public static final float coralStationMultiplier = 0.6f;
    public static final float bargeMultiplier = 0.3f;

    public static final float speedSmoothingFactor = 0.1f; // Controls how quickly speed changes (0.0-1.0)
  }

  /**
   * Speed control constants for the robot
   */
  public static final class SpeedConstants {
    
    // Default speed when elevator is lowered
    public static final float elevatorLoweredSpeed = 1.0f;
    
    // New speed constants for different elevator heights

    //public static final float elevatorPartiallyRaisedSpeed = 0.5f;

    public static final float elevatorPartiallyRaisedSpeed = 0.4f;

    //public static final float elevatorMidRaisedSpeed = 0.3f;

    public static final float elevatorMidRaisedSpeed = 0.3f;
    public static final float elevatorFullyRaisedSpeed = 0.2f;
    
    // Full speed when at intake position
    public static final float intakePositionSpeed = 1.0f;

    public static float fullSpeedOverride = 1.0f;
  }

  /**
   * Constants for drive-to-pose behavior
   */
  public static final class DriveToPoseConstants {
    // PID values for drive controller
    public static final double DRIVE_KP = 1.5;
    public static final double DRIVE_KI = 0.0;
    public static final double DRIVE_KD = 0.01;
    
    // PID values for theta (rotation) controller
    public static final double THETA_KP = 5.0;
    public static final double THETA_KI = 0.0;
    public static final double THETA_KD = 0.0;
    
    // Motion profile constraints
    public static final double DRIVE_MAX_VELOCITY = 0.7;
    public static final double DRIVE_MAX_ACCELERATION = 1.0;
    public static final double THETA_MAX_VELOCITY = Units.degreesToRadians(720);
    public static final double THETA_MAX_ACCELERATION = Units.degreesToRadians(720);
    
    // Position and angle tolerances
    public static final double DRIVE_TOLERANCE = 0.01;
    public static final double THETA_TOLERANCE = Units.degreesToRadians(1.0);
    
    // Distance thresholds in meters
    public static final double APPROACHING_DISTANCE_THRESHOLD = 2.0; // meters
    public static final double CLOSE_DISTANCE_THRESHOLD = 0.4; // meters
    public static final double VERY_CLOSE_DISTANCE_THRESHOLD = 0.2; // meters
    
    // Visualization settings
    public static final double TARGET_MARKER_SIZE = 0.3; // size of visualization marker

    // Speed profile constants for different distances
    // Using values consistent with the original ProfileToPose
    public static final double APPROACHING_MAX_VEL = 3.0; // Same as DRIVE_MAX_VELOCITY
    public static final double APPROACHING_MAX_ACCEL = 2.0; // Same as DRIVE_MAX_ACCELERATION
    
    public static final double CLOSE_MAX_VEL = 3.0; // Slower for closer approach
    public static final double CLOSE_MAX_ACCEL = 1.5; // Less acceleration when closer
    
    public static final double VERY_CLOSE_MAX_VEL = 0.7; // Slowest for final approach 
    public static final double VERY_CLOSE_MAX_ACCEL = 1.0; // Least acceleration for precision

    // Rotation delay for autonomous mode (seconds)
    public static final double AUTON_ROTATION_DELAY = 0.5;
  }

  public static final class ShakeModeConstants {
    // How far to oscillate in each direction (meters)
    public static final double SHAKE_AMPLITUDE_X = 0.1;  // Keeping small for stability
    public static final double SHAKE_AMPLITUDE_Y = 0.1;   // More aggressive Y-axis shake
    
    // Direct speed control (meters per second) - MUCH higher for more aggressive motion
    public static final double SHAKE_SPEED_Y = .3;   // Very aggressive Y-axis speed
    
    // Frequency of shake oscillation (Hz) - Controls how quickly direction changes
    public static final double SHAKE_FREQUENCY = 3.0;
    
    // Rotation shake parameters (radians)
    public static final double ANGULAR_SHAKE_AMPLITUDE = Math.toRadians(10.0);
    
    // Phase shifts to create more complex motion
    public static final double Y_PHASE_SHIFT = 0;
    public static final double ROTATION_PHASE_SHIFT = 0;
    
    // Pattern selection - FOCUS on Y-axis only
    public static final boolean DEFAULT_SHAKE_X = false;    // Disabled X-axis
    public static final boolean DEFAULT_SHAKE_Y = true;     // Enabled Y-axis
    public static final boolean DEFAULT_SHAKE_ROTATION = false; // Disabled rotation
    
    // Use open-loop control for maximum power and direct motor response
    public static final boolean USE_OPEN_LOOP = true;
  }

  /**
   * Constants for dynamic wait times used throughout the robot.
   * These are the keys used with SmartDashboard/Shuffleboard to store
   * configurable wait durations.
   */
  public static final class WaitTimeConstants {
    // Common wait times prefix for organization in dashboard
    public static final String WAIT_TIMES_KEY_PREFIX = "WaitTimes/";
    
    // Game piece placement timing
    public static final String INITIAL_PLACEMENT_TIME = "InitialPlacementTime";
    public static final String FIRST_BALL_TIME = "FirstBallTime";
    public static final String SECOND_BALL_TIME = "SecondBallTime";
    public static final String THIRD_BALL_TIME = "ThirdBallTime";
    
    // Default values for all wait times (in seconds)
    public static final double DEFAULT_INITIAL_PLACEMENT_TIME = 0.0;
    public static final double DEFAULT_FIRST_BALL_TIME = 0.0;
    public static final double DEFAULT_SECOND_BALL_TIME = 0.0;
    public static final double DEFAULT_THIRD_BALL_TIME = 0.0;
  }
}