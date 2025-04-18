package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.WaitTimeConstants;

/**
 * Utility class for managing dynamically configurable wait times.
 * Wait times are stored in SmartDashboard for easy tuning and can be
 * accessed at runtime by name.
 */
public class DynamicWait {
    

    public static int autoCounter;


    /**
     * Initializes all wait times with their default values.
     * Call this method in robotInit() to ensure all wait times are properly set.
     */
    public DynamicWait() {
        // Simply initialize all wait times with default values
        SmartDashboard.putNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.INITIAL_PLACEMENT_TIME, 
                                WaitTimeConstants.DEFAULT_INITIAL_PLACEMENT_TIME);
        SmartDashboard.putNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.FIRST_BALL_TIME, 
                                WaitTimeConstants.DEFAULT_FIRST_BALL_TIME);
        SmartDashboard.putNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.SECOND_BALL_TIME, 
                                WaitTimeConstants.DEFAULT_SECOND_BALL_TIME);
        SmartDashboard.putNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.THIRD_BALL_TIME, 
                                WaitTimeConstants.DEFAULT_THIRD_BALL_TIME);


        autoCounter = 0;
        
    }
    
    /**
     * Creates a wait command that will wait for the duration specified by the named wait time.
     * 
     * @param waitTimeName The name of the wait time to use
     * @return A command that will wait for the specified duration
     */
    public static Command waitCommand(String waitTimeName) {
        // Read directly from SmartDashboard when creating the command
        String key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + waitTimeName;
        double waitTime = SmartDashboard.getNumber(key, 0.0);
        return new WaitCommand(waitTime);
    }
    


    public static double waitTimeWithIncrementAUTALGAEOONLY(){

        double waitTime;

        autoCounter++;

        if(autoCounter == 1){
            waitTime = SmartDashboard.getNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.INITIAL_PLACEMENT_TIME, 0.0);
        }
        else if(autoCounter == 2){
            waitTime = SmartDashboard.getNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.FIRST_BALL_TIME, 0.0);
        }
        else if(autoCounter == 3){
            waitTime = SmartDashboard.getNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.SECOND_BALL_TIME, 0.0);
        }
        else if(autoCounter == 4){
            waitTime = SmartDashboard.getNumber(WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.THIRD_BALL_TIME, 0.0);
        }
        else{
            autoCounter = 0;
            waitTime = 0;
        }
        return waitTime;
    }


    public static void resetAutoCounter(){
        autoCounter = 0;
    }
    
    /**
     * Creates a dynamic wait command that will evaluate the wait time when it executes.
     * 
     * @param waitTimeName The name of the wait time to use
     * @return A command that gets the wait time when executed
     */
    public static Command dynamicWaitCommand(String waitTimeName) {
        return new Command() {
            private double endTime;
            
            @Override
            public void initialize() {
                // Read directly from SmartDashboard at execution time
                String key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + waitTimeName;
                double waitTime = SmartDashboard.getNumber(key, 0.0);
                endTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp() + waitTime;
            }
            
            @Override
            public boolean isFinished() {
                return edu.wpi.first.wpilibj.Timer.getFPGATimestamp() >= endTime;
            }
        };
    }

    /**
     * Creates a dynamic wait command that uses auto counter to determine which wait time to use.
     * The wait time is read from SmartDashboard when the command executes.
     * 
     * @return A command that waits for the duration specified by the current auto counter
     */
    public static Command dynamicIncrementWaitCommand() {
        return new Command() {
            private double endTime;
            private String key;
            
            @Override
            public void initialize() {
                // Increment counter when command starts
                autoCounter++;
                
                // Get the correct key based on counter
                if (autoCounter == 1) {
                    key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.INITIAL_PLACEMENT_TIME;
                } else if (autoCounter == 2) {
                    key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.FIRST_BALL_TIME;
                } else if (autoCounter == 3) {
                    key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.SECOND_BALL_TIME;
                } else if (autoCounter == 4) {
                    key = WaitTimeConstants.WAIT_TIMES_KEY_PREFIX + WaitTimeConstants.THIRD_BALL_TIME;
                } else {
                    autoCounter = 0;
                    key = "";
                }
                
                // Read wait time from SmartDashboard at execution time
                double waitTime = 0.0;
                if (!key.isEmpty()) {
                    waitTime = SmartDashboard.getNumber(key, 0.0);
                }
                
                endTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp() + waitTime;
            }
            
            @Override
            public boolean isFinished() {
                return edu.wpi.first.wpilibj.Timer.getFPGATimestamp() >= endTime;
            }
        };
    }
}
