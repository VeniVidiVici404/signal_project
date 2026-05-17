package com.alerts;

/**
 * Factory class for creating Alert objects.
 * This abstracts the instantiation logic, allowing for future expansion
 * (e.g., creating specific subclasses like BloodPressureAlert or ECGAlert)
 * without modifying the core strategy logic.
 */
public class AlertFactory {

    /**
     * Creates and returns a new Alert object.
     * * @param patientId The ID of the patient.
     * @param condition The medical condition triggering the alert.
     * @param timestamp The time the alert was triggered.
     * @return A newly constructed Alert.
     */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}