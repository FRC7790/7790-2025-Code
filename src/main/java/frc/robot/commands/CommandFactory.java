package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Algae.AlgaeArm;
import frc.robot.subsystems.Coral.Shooter;
import frc.robot.subsystems.Coral.ShooterArm;
import frc.robot.subsystems.Coral.ShooterPivot;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class CommandFactory {

public static Command scoreL1AutomaticCommand(SwerveSubsystem drivebase){
        Command command = drivebase.pathfindThenFollowPath("Right to 0");
        //.andThen(pivot.setAmpScoreCommand())
        //.andThen(new WaitCommand(1.5))
        //.andThen(shooter.startAmpShooterCommand())
       // .andThen(new WaitCommand(1))
       // .andThen(shooter.shootCommand())
       // .andThen(new WaitCommand(1))
       // .andThen(shooter.stopShooterCommand())
       // .andThen(shooter.indexStopCommand());

        command.addRequirements(drivebase);

        return command;
    }

   // public static Command scoreL1Command(AlgaeArm algaeArm, Shooter shooter, ShooterArm shooterArm, ShooterPivot shooterPivot, SwerveSubsystem drivebase, Elevator elevator){


       // command.addRequirements(drivebase, algaeArm, shooter, shooterArm, shooterPivot, elevator);

    //    return command;
   // }

      //public static Command scoreL2Command(AlgaeArm algaeArm, Shooter shooter, ShooterArm shooterArm, ShooterPivot shooterPivot, SwerveSubsystem drivebase, Elevator elevator){

       // Command command = algaeArm.algaeArmStraightOutCommand()

      //  command.addRequirements(drivebase, algaeArm, shooter, shooterArm, shooterPivot, elevator);

      //  return command;
      
   // public static Command leftAuto(AlgaeArm algaeArm, Shooter shooter, ShooterArm shooterArm, ShooterPivot shooterPivot, SwerveSubsystem drivebase, Elevator elevator){

        //These will be predefined locations. The other commands will take info from queue to determine which face to go to
        //For left source
        //pathfind to face
        //run placement command
        //retract and drive to station
        //Coninue list of commands
        

      //  return command;
  //  }
  //  public static Command rightAuto(AlgaeArm algaeArm, Shooter shooter, ShooterArm shooterArm, ShooterPivot shooterPivot, SwerveSubsystem drivebase, Elevator elevator){

        //These will be predefined locations. The other commands will take info from queue to determine which face to go to
        //For right source
        //pathfind to face
        //run placement command
        //retract and drive to station
        //Coninue list of commands
        
   //     return command;
  //  }
}









//}
