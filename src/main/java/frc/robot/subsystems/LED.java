package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LEDConstants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;

import java.util.HashMap;
import java.util.Map;

public class LED extends SubsystemBase {
  
    private final AddressableLED led;
    private final AddressableLEDBuffer buffer;
    private final AddressableLEDBuffer tempBuffer; // For power calculations

    private final Color reefColor = new Color(196, 97, 140);
    private final Color algaeColor = new Color(0, 255, 222);

    // Map to store patterns by name
    private final Map<String, LEDPattern> patternMap = new HashMap<>();

    // LED patterns
    private final LEDPattern solidRed;
    private final LEDPattern solidBlue;
    private final LEDPattern solidReef;
    private final LEDPattern solidHoldingAlgae;

    private final LEDPattern blueBreathing;
    private final LEDPattern redBreathing;
    private final LEDPattern redBlueGradient; // New gradient pattern
    
    // Add default flash patterns
    private final LEDPattern linedUpFlash;
    
    private LEDPattern currentPattern;
    private LEDPattern normalPattern; // Pattern to return to after flashing
    
    private double currentBrightness = LEDConstants.DEFAULT_BRIGHTNESS;

    // Define our own LEDPattern interface since it's not standard in WPILib
    private interface LEDPattern {
        void applyTo(AddressableLEDBuffer buffer);
        
        // Helper method to create solid color pattern
        static LEDPattern solid(Color color) {
            return buffer -> {
                // Apply current dynamic brightness to solid colors
                for (int i = 0; i < buffer.getLength(); i++) {
                    buffer.setLED(i, color);
                }
            };
        }
    }

    public LED() {

      led = new AddressableLED(LEDConstants.port);
      buffer = new AddressableLEDBuffer(LEDConstants.length);
      tempBuffer = new AddressableLEDBuffer(LEDConstants.length); // For calculations
      led.setLength(LEDConstants.length);
      led.start();
      

      // Create solid alliance color patterns
      solidRed = LEDPattern.solid(Color.kRed);
      solidBlue = LEDPattern.solid(Color.kBlue);
      solidHoldingAlgae = LEDPattern.solid(algaeColor);
      solidReef = LEDPattern.solid(reefColor);
      
      // Create breathing patterns
      blueBreathing = createBreathingPattern(Color.kBlue);
      redBreathing = createBreathingPattern(Color.kRed);
      
  
      // Create red-blue gradient pattern
      redBlueGradient = createRedBlueGradientPattern();
      
      // Initialize flash patterns
      linedUpFlash = createFlashPattern(Color.kGreen, 3);
      
      // Initialize pattern map with all available patterns
      patternMap.put("SOLID_RED", solidRed);
      patternMap.put("SOLID_BLUE", solidBlue);
      patternMap.put("SOLID_REEF", solidReef);
      patternMap.put("SOLID_ALGAE", solidHoldingAlgae);
      patternMap.put("BLUE_BREATHING", blueBreathing);
      patternMap.put("RED_BREATHING", redBreathing);
      patternMap.put("RED_BLUE_GRADIENT", redBlueGradient);
      patternMap.put("LINED_UP_FLASH", linedUpFlash);
      
      // Start with gradient pattern by default until alliance is known
      currentPattern = redBlueGradient;
      
      // Apply initial pattern immediately to ensure LEDs light up even without computer
      currentPattern.applyTo(buffer);
      led.setData(buffer);
    }
  
    @Override
    public void periodic() {
      // Check if robot is disabled
      boolean currentlyDisabled = DriverStation.isDisabled();
      
      // Check if alliance information is available
      var allianceOption = DriverStation.getAlliance();
      
      // Select the appropriate pattern based on conditions
      if (normalPattern == null) { // Only change pattern if not currently flashing
        if (allianceOption.isPresent()) {
          Alliance alliance = allianceOption.get();
          
          if (currentlyDisabled) {
            // Robot is disabled - use breathing patterns
            currentPattern = (alliance == Alliance.Blue) ? blueBreathing : redBreathing;
          } else {
            // Robot is enabled - use solid alliance colors
            currentPattern = (alliance == Alliance.Blue) ? solidBlue : solidRed;
          }
        } else {
          // No alliance information available, use gradient pattern
          currentPattern = redBlueGradient;
        }
      }
      
      // Apply current pattern to temp buffer for power calculations
      currentPattern.applyTo(tempBuffer);
      
      // Calculate power and adjust brightness if needed
      adjustBrightnessForPower();
      
      // Apply the current pattern with adjusted brightness
      applyPatternWithBrightness();
      
      // Write the data to the LED strip
      led.setData(buffer);
    }
    
    /**
     * Creates a gradient pattern that transitions from red to blue
     * 
     * @return A red-blue gradient LEDPattern
     */
    private LEDPattern createRedBlueGradientPattern() {
      return new LEDPattern() {
        private final Timer animationTimer = new Timer();
        
        {
          // Initialize timer immediately
          animationTimer.start();
        }
        
        @Override
        public void applyTo(AddressableLEDBuffer buffer) {
          // Make the gradient shift over time for a dynamic effect
          double time = animationTimer.get() * 0.5; // Controls the speed of the animation
          double cycle = (time % 1.0);
          
          int length = buffer.getLength();
          
          // Generate gradient
          for (int i = 0; i < length; i++) {
            // Calculate position in the gradient (0 to 1)
            double position = ((double)i / length + cycle) % 1.0;
            
            // Create color based on position
            // First half transitions from red to purple
            // Second half transitions from purple to blue
            double red, blue;
            
            if (position < 0.5) {
              // 0.0 to 0.5: Red to Purple
              red = 1.0;
              blue = position * 2.0; // 0.0 to 1.0
            } else {
              // 0.5 to 1.0: Purple to Blue
              red = 1.0 - ((position - 0.5) * 2.0); // 1.0 to 0.0
              blue = 1.0;
            }
            
            // Apply a pulse/wave effect
            double brightness = 0.5 + 0.5 * Math.sin(time * 2 * Math.PI + i * Math.PI / 20);
            
            // Create the color with gradient and wave effect
            Color gradientColor = new Color(red * brightness, 0, blue * brightness);
            buffer.setLED(i, gradientColor);
          }
        }
      };
    }
        
    /**
     * Calculates current draw based on RGB values and adjusts brightness if needed
     */
    private void adjustBrightnessForPower() {
      double totalCurrentDraw = 0;
      
      // Calculate potential power consumption based on temp buffer
      for (int i = 0; i < tempBuffer.getLength(); i++) {
          double red = tempBuffer.getLED(i).red;
          double green = tempBuffer.getLED(i).green;
          double blue = tempBuffer.getLED(i).blue;
          
          // Calculate current for this LED (in mA)
          double ledCurrent = (red * LEDConstants.MILLIAMPS_PER_RED) +
                              (green * LEDConstants.MILLIAMPS_PER_GREEN) +
                              (blue * LEDConstants.MILLIAMPS_PER_BLUE);
          
          totalCurrentDraw += ledCurrent;
      }
      
      // Convert mA to A
      totalCurrentDraw /= 1000.0;
      
      // Calculate max safe brightness based on current power draw
      if (totalCurrentDraw > 0) {
          // How much we can scale up (or need to scale down) our brightness
          double maxSafeBrightness = (LEDConstants.MAX_AMPERAGE * LEDConstants.POWER_SAFETY_MARGIN) / totalCurrentDraw;
          
          // Gradually adjust brightness for smooth transitions
          if (maxSafeBrightness < currentBrightness) {
              // Need to reduce brightness immediately to stay within power budget
              currentBrightness = maxSafeBrightness;
          } else if (maxSafeBrightness > currentBrightness) {
              // Can increase brightness, but do it gradually
              currentBrightness += (maxSafeBrightness - currentBrightness) * 0.1;
          }
          
          // Ensure brightness is within reasonable limits
          currentBrightness = Math.max(0.05, Math.min(1.0, currentBrightness));
      }
  }
  
  /**
   * Applies the current pattern with adjusted brightness
   */
  private void applyPatternWithBrightness() {
      // No need to apply the pattern again, it's already in tempBuffer
      
      // Copy to main buffer with brightness adjustment
      for (int i = 0; i < buffer.getLength(); i++) {
          Color originalColor = tempBuffer.getLED(i);
          Color adjustedColor = new Color(
              originalColor.red * currentBrightness,
              originalColor.green * currentBrightness,
              originalColor.blue * currentBrightness
          );
          buffer.setLED(i, adjustedColor);
      }
  }
  
    /**
     * Creates a breathing pattern in the specified color
     * 
     * @param baseColor The base color for the breathing pattern
     * @return A breathing pattern LEDPattern
     */
    private LEDPattern createBreathingPattern(Color baseColor) {
      return new LEDPattern() {
        private final Timer breathingTimer = new Timer();
        private boolean initialized = false;
        
        {
          // Initialize timer
          breathingTimer.start();
        }
        
        @Override
        public void applyTo(AddressableLEDBuffer buffer) {
          if (!initialized) {
            breathingTimer.reset();
            initialized = true;
          }
          
          // Calculate the phase of the breathing cycle (0 to 1)
          double time = breathingTimer.get();
          double cyclePosition = (time % LEDConstants.BREATHING_CYCLE_PERIOD) / LEDConstants.BREATHING_CYCLE_PERIOD;
          
          // Calculate brightness using a sine wave for smooth transitions
          // sin function oscillates between -1 and 1, so we adjust to 0-1 range
          double breathIntensity = (Math.sin(2 * Math.PI * cyclePosition) + 1) / 2;
          
          // Map to our desired min/max range
          breathIntensity = LEDConstants.BREATHING_MIN_INTENSITY + 
              breathIntensity * (LEDConstants.BREATHING_MAX_INTENSITY - LEDConstants.BREATHING_MIN_INTENSITY);
          
          // Apply the breathing effect to each LED
          for (int i = 0; i < buffer.getLength(); i++) {
            Color breathColor = new Color(
                baseColor.red * breathIntensity,
                baseColor.green * breathIntensity,
                baseColor.blue * breathIntensity
            );
            buffer.setLED(i, breathColor);
          }
        }
      };
    }
  
    /**
     * Create a flashing pattern that alternates between a color and black
     */
    private LEDPattern createFlashPattern(Color color, int flashCount) {
        return new LEDPattern() {
            private final Timer flashTimer = new Timer();
            private boolean initialized = false;
            private boolean isComplete = false;
            
            {
                // Initialize timer
                flashTimer.start();
            }
            
            @Override
            public void applyTo(AddressableLEDBuffer buffer) {
                if (!initialized) {
                    flashTimer.reset();
                    isComplete = false;
                    initialized = true;
                }
                
                // If we've completed all flashes plus the final delay, return to normal pattern
                if (isComplete) {
                    // Flash sequence is done, switch back to normal pattern
                    if (normalPattern != null) {
                        currentPattern = normalPattern;
                        normalPattern = null;
                    }
                    return;
                }
                
                double cycleTime = LEDConstants.FLASH_ON_DURATION + LEDConstants.FLASH_OFF_DURATION;
                double elapsedTime = flashTimer.get();
                int cycleCount = (int)(elapsedTime / cycleTime);
                
                // Check if we've completed all flashes
                if (cycleCount >= flashCount) {
                    // Add a delay before returning to normal pattern
                    if (elapsedTime >= (flashCount * cycleTime) + LEDConstants.FLASH_COMPLETE_DELAY) {
                        isComplete = true;
                    }
                    // During the delay, show black
                    for (int i = 0; i < buffer.getLength(); i++) {
                        buffer.setLED(i, Color.kBlack);
                    }
                    return;
                }
                
                // Calculate position within the current cycle
                double cyclePosition = elapsedTime % cycleTime;
                boolean isOn = cyclePosition < LEDConstants.FLASH_ON_DURATION;
                
                // Set all LEDs to either the flash color or black
                for (int i = 0; i < buffer.getLength(); i++) {
                    buffer.setLED(i, isOn ? color : Color.kBlack);
                }
            }
        };
    }

    
    /**
     * Gets a pattern by its name
     * @param patternName The name of the pattern
     * @return The LEDPattern, or null if not found
     */
    public LEDPattern getPatternByName(String patternName) {
        return patternMap.getOrDefault(patternName, null);
    }
    
    /**
     * Creates a command that runs a pattern on the entire LED strip using the pattern name.
     *
     * @param patternName the name of the LED pattern to run
     * @return Command that sets the LED pattern
     */
    public Command runPattern(String patternName) {
        LEDPattern pattern = getPatternByName(patternName);
        if (pattern == null) {
            // Pattern not found, return a command that does nothing
            return runOnce(() -> {});
        }
        return runOnce(() -> {
            currentPattern = pattern;
            normalPattern = null;
        });
    }

    /**
     * Creates a flash pattern command that temporarily shows a pattern and then returns to normal
     *
     * @param patternName the name of the flash pattern to run
     * @return Command that runs the flash pattern
     */
    public Command runFlashPattern(String patternName) {
        LEDPattern pattern = getPatternByName(patternName);
        if (pattern == null) {
            // Pattern not found, return a command that does nothing
            return runOnce(() -> {});
        }
        return runOnce(() -> {
            normalPattern = currentPattern;
            currentPattern = pattern;
        });
    }
    
    /**
     * Gets all available pattern names
     * @return Array of pattern names
     */
    public String[] getAvailablePatterns() {
        return patternMap.keySet().toArray(new String[0]);
    }
    
    /**
     * Creates a command that runs a pattern on the entire LED strip.
     * This is kept for compatibility with existing code.
     *
     * @param pattern the LED pattern to run
     */
    public Command runPattern(LEDPattern pattern) {
        return runOnce(() -> {
            currentPattern = pattern;
            normalPattern = null;
        });
    }

    /**
     * Creates a flash pattern command that temporarily shows a pattern and then returns to normal.
     * This is kept for compatibility with existing code.
     * 
     * @param pattern the LED pattern to flash
     */
    public Command runFlashPattern(LEDPattern pattern) {
        return runOnce(() -> {
            normalPattern = currentPattern;
            currentPattern = pattern;
        });
    }
    
    /**
     * Gets the current estimated power consumption in amps
     * @return Current power consumption in amps
     */
    public double getCurrentPowerConsumption() {
        double totalCurrentDraw = 0;
        
        for (int i = 0; i < buffer.getLength(); i++) {
            Color color = buffer.getLED(i);
            totalCurrentDraw += (color.red * LEDConstants.MILLIAMPS_PER_RED) +
                               (color.green * LEDConstants.MILLIAMPS_PER_GREEN) +
                               (color.blue * LEDConstants.MILLIAMPS_PER_BLUE);
        }
        
        return totalCurrentDraw / 1000.0; // Convert mA to A
    }
}