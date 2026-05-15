package com.cardio_generator.outputs;

/**
 * Defines the contract for outputting generated health data within the Cardiovascular Health Monitoring System.
 * <p>
 * Implementations of this interface determine the specific destination and format of the 
 * simulated data, such as printing to the console, writing to a file, or transmitting over a network 
 * (e.g., via TCP or WebSockets).
 */
public interface OutputStrategy {

    /**
     * Outputs the specified health data reading for a given patient.
     *
     * @param patientId The unique identifier of the patient associated with the data.
     * @param timestamp The exact time the data was generated, typically represented in milliseconds since the Unix epoch.
     * @param label     A string categorizing the type of data being output (e.g., "ECG", "BloodPressure", "Alert").
     * @param data      The actual generated value or message formatted as a string.
     */
    void output(int patientId, long timestamp, String label, String data);
}
