package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();
    private List<AlertStrategy> activeStrategies;
    
    // NEW: Add the factory to the generator
    private AlertFactory alertFactory;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.activeStrategies = new ArrayList<>();
        this.alertFactory = new AlertFactory(); // Initialize the factory
        
        this.activeStrategies.add(new BloodPressureStrategy());
        this.activeStrategies.add(new OxygenSaturationStrategy());
        this.activeStrategies.add(new HeartRateStrategy());
    }

    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : activeStrategies) {
            Alert potentialAlert = strategy.checkAlert(patient);
            if (potentialAlert != null) {
                triggerAlert(potentialAlert);
            }
        }

        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        checkHypotensiveHypoxemia(patient, records);
        checkManualAlerts(patient, records);
    }

    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        for (PatientRecord sysRecord : records) {
            if (sysRecord.getRecordType().equals("SystolicBloodPressure") && sysRecord.getMeasurementValue() < 90) {
                for (PatientRecord satRecord : records) {
                    if (satRecord.getRecordType().equals("Saturation") && satRecord.getMeasurementValue() < 92) {
                        if (Math.abs(sysRecord.getTimestamp() - satRecord.getTimestamp()) <= 60000) {
                            // Using the Factory here!
                            triggerAlert(alertFactory.createAlert(Integer.toString(sysRecord.getPatientId()), "Hypotensive Hypoxemia", sysRecord.getTimestamp()));
                            break; 
                        }
                    }
                }
            }
        }
    }


    private void checkManualAlerts(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Alert")) {
                // Using the Factory here!
                triggerAlert(alertFactory.createAlert(Integer.toString(record.getPatientId()), "Manual Alert Triggered", record.getTimestamp()));
            }
        }
    }

    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
}
