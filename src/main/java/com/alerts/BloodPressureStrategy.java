package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Strategy for monitoring blood pressure.
 * Uses the Factory Pattern to create alerts and the Decorator Pattern
 * to wrap critical alerts as Priority alerts.
 */
public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public Alert checkAlert(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        AlertFactory factory = new AlertFactory(); // Using the Factory
        
        double prevSys = -1, prevPrevSys = -1;
        double prevDia = -1, prevPrevDia = -1;

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();

            if (type.equals("SystolicBloodPressure")) {
                // 1. Critical Threshold Alerts (Wrapped as Priority!)
                if (value > 180 || value < 90) {
                    Alert baseAlert = factory.createAlert(Integer.toString(record.getPatientId()), "Critical Systolic BP", record.getTimestamp());
                    return new PriorityAlertDecorator(baseAlert);
                }
                
                // 2. Trend Alerts (Systolic) - Standard Alert
                if (prevSys != -1 && prevPrevSys != -1) {
                    if ((value - prevSys > 10 && prevSys - prevPrevSys > 10) || 
                        (prevSys - value > 10 && prevPrevSys - prevSys > 10)) {
                        return factory.createAlert(Integer.toString(record.getPatientId()), "Systolic BP Trend Alert", record.getTimestamp());
                    }
                }
                prevPrevSys = prevSys;
                prevSys = value;
            } 
            else if (type.equals("DiastolicBloodPressure")) {
                // 1. Critical Threshold Alerts (Wrapped as Priority!)
                if (value > 120 || value < 60) {
                    Alert baseAlert = factory.createAlert(Integer.toString(record.getPatientId()), "Critical Diastolic BP", record.getTimestamp());
                    return new PriorityAlertDecorator(baseAlert);
                }

                // 2. Trend Alerts (Diastolic) - Standard Alert
                if (prevDia != -1 && prevPrevDia != -1) {
                    if ((value - prevDia > 10 && prevDia - prevPrevDia > 10) || 
                        (prevDia - value > 10 && prevPrevDia - prevDia > 10)) {
                        return factory.createAlert(Integer.toString(record.getPatientId()), "Diastolic BP Trend Alert", record.getTimestamp());
                    }
                }
                prevPrevDia = prevDia;
                prevDia = value;
            }
        }
        
        return null; // Return null if no alert is triggered
    }
}