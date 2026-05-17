package com.alerts;

import com.data_management.Patient;

/**
 * The strategy interface for different types of health alerts.
 */
public interface AlertStrategy {
    /**
     * Checks the patient's data to determine if an alert should be triggered.
     * * @param patient The patient to evaluate.
     * @return An Alert object if the condition is met, or null if everything is healthy.
     */
    Alert checkAlert(Patient patient);
}