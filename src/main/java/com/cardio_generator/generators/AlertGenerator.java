package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * A data generator that simulates unpredictable, stateful patient alerts.
 * <p>
 * This class models sporadic events, such as a patient pressing a call button or a medical 
 * monitor detecting an anomaly. It maintains the current alert state (triggered or resolved) 
 * for each patient. If an alert is currently active, there is a high probability it will be 
 * resolved in the next cycle. If no alert is active, there is a calculated probability 
 * (based on a Poisson distribution) that a new alert will be triggered.
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random RANDOM_GENERATOR = new Random();
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs a new {@code AlertGenerator} and initializes the alert states for all patients.
     * <p>
     * All patients start in a "resolved" (false) alert state by default when the array is initialized.
     *
     * @param patientCount The total number of simulated patients. The internal state array 
     * is sized to accommodate patient IDs starting from 1 up to this count.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Evaluates and updates the alert state for a specific patient, outputting any changes.
     * <p>
     * The method checks the patient's current state:
     * <ul>
     * <li>If an alert is active, there is a 90% chance it resolves during this generation step.</li>
     * <li>If no alert is active, it calculates the probability of a new alert triggering based 
     * on an average rate (lambda = 0.1).</li>
     * </ul>
     * Any state changes (either "resolved" or "triggered") are routed to the provided output strategy.
     *
     * @param patientId      The unique identifier of the patient.
     * @param outputStrategy The strategy used to route the generated alert data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
