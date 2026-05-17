package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());

        checkBloodPressure(patient, records);
        checkBloodSaturation(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkECG(patient, records);
        checkManualAlerts(patient, records);
    }

    /**
     * Checks for blood pressure anomalies including critical thresholds and rapid trends.
     */
    private void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        double prevSys = -1, prevPrevSys = -1;
        double prevDia = -1, prevPrevDia = -1;

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();

            // 1. Critical Threshold Alerts
            if (type.equals("SystolicBloodPressure")) {
                if (value > 180 || value < 90) {
                    triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Critical Systolic BP", record.getTimestamp()));
                }
                
                // 2. Trend Alerts (Systolic)
                if (prevSys != -1 && prevPrevSys != -1) {
                    if ((value - prevSys > 10 && prevSys - prevPrevSys > 10) || 
                        (prevSys - value > 10 && prevPrevSys - prevSys > 10)) {
                        triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Systolic BP Trend Alert", record.getTimestamp()));
                    }
                }
                prevPrevSys = prevSys;
                prevSys = value;
            } 
            else if (type.equals("DiastolicBloodPressure")) {
                if (value > 120 || value < 60) {
                    triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Critical Diastolic BP", record.getTimestamp()));
                }

                // 2. Trend Alerts (Diastolic)
                if (prevDia != -1 && prevPrevDia != -1) {
                    if ((value - prevDia > 10 && prevDia - prevPrevDia > 10) || 
                        (prevDia - value > 10 && prevPrevDia - prevDia > 10)) {
                        triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Diastolic BP Trend Alert", record.getTimestamp()));
                    }
                }
                prevPrevDia = prevDia;
                prevDia = value;
            }
        }
    }

    /**
     * Checks for low blood oxygen saturation and rapid drops.
     */
    private void checkBloodSaturation(Patient patient, List<PatientRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            if (!current.getRecordType().equals("Saturation")) continue;

            double currentValue = current.getMeasurementValue();

            // Low Saturation Alert
            if (currentValue < 92) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Low Blood Saturation", current.getTimestamp()));
            }

            // Rapid Drop Alert (5% drop within 10 minutes)
            long tenMinutesInMillis = 10 * 60 * 1000;
            for (int j = i + 1; j < records.size(); j++) {
                PatientRecord future = records.get(j);
                if (!future.getRecordType().equals("Saturation")) continue;

                if (future.getTimestamp() - current.getTimestamp() <= tenMinutesInMillis) {
                    if (currentValue - future.getMeasurementValue() >= 5) {
                        triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Rapid Saturation Drop", future.getTimestamp()));
                        break; // Trigger once per drop
                    }
                } else {
                    break; // Outside the 10-minute window, stop looking ahead
                }
            }
        }
    }

    /**
     * Checks for the combined condition of Hypotensive Hypoxemia.
     */
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        // We need to find if a systolic drop < 90 and a saturation drop < 92 happen around the same time.
        for (PatientRecord sysRecord : records) {
            if (sysRecord.getRecordType().equals("SystolicBloodPressure") && sysRecord.getMeasurementValue() < 90) {
                
                // If we find a low systolic, check nearby records for low saturation
                for (PatientRecord satRecord : records) {
                    if (satRecord.getRecordType().equals("Saturation") && satRecord.getMeasurementValue() < 92) {
                        // Assuming "same time" means within a 1-minute window
                        if (Math.abs(sysRecord.getTimestamp() - satRecord.getTimestamp()) <= 60000) {
                            triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Hypotensive Hypoxemia", sysRecord.getTimestamp()));
                            break; 
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks for abnormal ECG peaks using a simple sliding window average.
     */
    private void checkECG(Patient patient, List<PatientRecord> records) {
        double sum = 0;
        int count = 0;
        double windowAverage = 0;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG")) {
                double value = record.getMeasurementValue();
                sum += value;
                count++;
                windowAverage = sum / count;

                // If the peak is wildly abnormal compared to the current average (e.g., a 50% spike)
                if (count > 5 && value > (windowAverage * 1.5)) {
                    triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Abnormal ECG Peak", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Checks if a manual alert button was triggered.
     */
    private void checkManualAlerts(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Alert")) {
                // Let's assume a triggered manual alert is logged with a value of 1.0 (or similar)
                // You may need to adjust this depending on how the generator saves the text "triggered"
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Manual Alert Triggered", record.getTimestamp()));
            }
        }
    }
    
    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
}
