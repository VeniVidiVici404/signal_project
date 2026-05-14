package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the contract for all patient data generators within the Cardiovascular Health Monitoring System.
 * <p>
 * Classes implementing this interface are responsible for simulating specific types of 
 * health data (such as ECG readings, blood pressure, or oxygen saturation) and routing 
 * that data to a specified output mechanism.
 */
public interface PatientDataGenerator {

    /**
     * Generates a simulated health data reading for a specific patient and outputs it using 
     * the provided output strategy.
     *
     * @param patientId      The unique identifier of the patient for whom the data is being generated.
     * @param outputStrategy The {@code OutputStrategy} defining how and where the generated data 
     * should be routed (e.g., to a console, a file, or over a network).
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
