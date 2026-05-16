package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * A data generator that simulates continuous blood oxygen saturation (SpO2) levels for patients.
 * <p>
 * This class maintains the state of each patient's saturation level across multiple generation 
 * cycles. It initializes each patient with a healthy baseline saturation (between 95% and 100%) 
 * and applies small random fluctuations (-1%, 0%, or +1%) during each generation step, 
 * ensuring the values realistically stay within the bounds of 90% and 100%.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Constructs a new {@code BloodSaturationDataGenerator} and initializes baseline 
     * saturation levels for the specified number of patients.
     *
     * @param patientCount The total number of simulated patients. The internal state arrays 
     * are sized to accommodate patient IDs starting from 1 up to this count.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a new blood oxygen saturation reading for the specified patient and outputs it.
     * <p>
     * The new value is calculated by applying a minor random fluctuation to the patient's 
     * previous reading. The result is strictly bounded between 90% and 100% and then passed 
     * to the provided output strategy formatted as a percentage string (e.g., "98.0%").
     *
     * @param patientId      The unique identifier of the patient.
     * @param outputStrategy The output strategy used to route the generated saturation data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
