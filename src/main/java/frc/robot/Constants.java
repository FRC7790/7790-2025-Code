// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

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

  public static final float DEADBAND = 0.1f;

  public static final float armLength = (float)Units.inchesToMeters(0);




  
  public static final class AlgaeArmConstants{
    public static final int ID = 14;

    public static final float stowedUpAngle = 0.74f;
    public static final float straightOutAngle = 0.5f;
    public static final float groundIntakeAngle = 0.5f; //Tenative could move arm up and then angle farther down
    public static final float upperCrashLimit = 0.75f; // This is absolute max, there are two sub states where the max is either equal to this or slightly less due to the placement mechanism being in the way
    public static final float lowerCrashLimit = 0.45f; // Slightly ahead of actually limit for safety
    public static final float clearanceAngle = 0.5f;

    public static final float angleOffset = 0.39f;

    public static final float max = 0.5f;
    public static final float min = 0.31f;

    public static final float manualMultiplier = 0.005f;


    public static final float P = 0.5f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    public static final float maxVelocity = 1000;
    public static final float maxAcceleration = 1000;
    public static final float allowedClosedLoopError = .05f;
  }

  public static final class AlgaeShooterConstants{
    public static final int ID = 16;
    public static final float intake = -0.3f;
    public static final float outake = 0.3f;
  }
  public static final class ShooterConstants{
    public static final int ID = 19;
    public static final float intake = 0.15f;
    public static final float outake = -1f;
  }

  public static final class ShooterArmConstants{
    public static final int ID = 15;

    public static final float angleOffset = .5f;

    public static final float manualMultiplier = 0.005f;

    public static final float min = 0.253f;
    public static final float max = 0.698f;

    public static final float scoreAngleLOW = 0.698f;
    public static final float scoreAngleHIGH = 0.698f;
    public static final float loadAngle = 0.253f;
    public static final float L1Angle = 0.5f;

    public static final float P = 1f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    public static final float maxVelocity = 1000;
    public static final float maxAcceleration = 1000;
    public static final float allowedClosedLoopError = .05f;
  }

  public static final class ShooterPivotConstants{

    public static final int ID = 17;

    public static final float leftAngleInitial = 0.0f;
    public static final float rightAngleInitial = 0.0f;
    public static final float centerAngle = .49f;

    public static final float angleOffset = 0.48f;

    public static final float max = 0.73f;
    public static final float min = 0.255f;

    public static final float manualMultiplier = 0.005f;

    public static final float P = 1f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    public static final float maxVelocity = 1000;
    public static final float maxAcceleration = 1000;
    public static final float allowedClosedLoopError = .05f;
  }

  public static final class AimingConstants{

  }

  public static final class ClimberConstants{

    public static final int ID = 18;

    public static final float extendSpeed = 1f;
    public static final float retractSpeed = -1f;
  }
  
  public static final class ElevatorConstants{

    public static final int ID = 13;
    
    public static final float manualMultiplier = 1f;

    public static final float min = -272.0f;
    public static final float max = 0.0f;

    public static final float L4Pose = -272.0f;
    public static final float L3Pose = -143.5f;
    public static final float L2Pose = -36.0f;
    public static final float L1Pose = 0.0f;
    public static final float pickupPose = -119.5f;

    public static final float P = 0.2f;
    public static final float I = 0.0f;
    public static final float D = 0.0f;
    public static final float maxVelocity = 5200;
    public static final float maxAcceleration = 6000;
    public static final float allowedClosedLoopError = .5f;
  }

  public static final class FunnelConstants{
    
  }

  public static final class LEDConstants{
    
    public static final int port = 0;

    public static final int length = 25;

  }
  public static final class ScopeConstants{
    
  }
  public static final class AprilTagVisionConstants{

    public static final float rightCamXOffset = 0.0f;
    public static final float rightCamYOffset = 0.0f;
    public static final float rightCamZOffset = 0.0f;

    public static final float rightCamRoll = 0.0f;
    public static final float rightCamPitch = 0.0f;
    public static final float rightCamYaw = 0.0f;

    
    public static final float leftCamXOffset = 0.0f;
    public static final float leftCamYOffset = 0.0f;
    public static final float leftCamZOffset = 0.0f;

    public static final float leftCamRoll = 0.0f;
    public static final float leftCamPitch = 0.0f;
    public static final float leftCamYaw = 0.0f;

    public static final float limelightXOffset = 0.0f;
    public static final float limelightYOffset = 0.0f;
    public static final float limelightZOffset = 0.0f;

    public static final float limelightRoll = 0.0f;
    public static final float limelightPitch = 0.0f;
    public static final float limelightYaw = 0.0f;
  }

  public static final class AlignmentConstants{

    public static final double driveP = 0.2;
  
    public static final double maxSpeed = .25; // or whichever maximum speed is appropriate
  }
  public static final class ButtonBoxConstants{
    public static final double allowableError = 0.05;
    public static final boolean invertX = true;
    public static final boolean invertY = true;




    public static final float xValueC0XX = 0.0f;
    public static final float yValueC0XX = 0.0f;
    public static final float zValueC0XX = 0.0f;
    public static final int faceValueC0XX = 0;
    
    public static final float xValueC1XX = 0.0f;
    public static final float yValueC1XX = 0.0f;
    public static final float zValueC1XX = 0.0f;
    public static final int faceValueC1XX = 1;

    public static final float xValueC2XX = 0.0f;
    public static final float yValueC2XX = 0.0f;
    public static final float zValueC2XX = 0.0f;
    public static final int faceValueC2XX = 2;

    public static final float xValueC3XX = 0.0f;
    public static final float yValueC3XX = 0.0f;
    public static final float zValueC3XX = 0.0f;
    public static final int faceValueC3XX = 3;

    public static final float xValueC4XX = 0.0f;
    public static final float yValueC4XX = 0.0f;
    public static final float zValueC4XX = 0.0f;
    public static final int faceValueC4XX = 4;

    public static final float xValueC5XX = 0.0f;
    public static final float yValueC5XX = 0.0f;
    public static final float zValueC5XX = 0.0f;
    public static final int faceValueC5XX = 5;

    public static final int heightCX0X = 0;
    public static final int heightCX1X = 1;
    public static final int heightCX2X = 2;
    public static final int heightCX3X = 3;

    public static final boolean setLeftCXX0 = true;
    public static final boolean setLeftCXX1 = false;
}
}