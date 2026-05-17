package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public Alert checkAlert(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        AlertFactory factory = new AlertFactory(); // Using the Factory

        for (int i = 0; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            if (!current.getRecordType().equals("Saturation")) continue;

            double currentValue = current.getMeasurementValue();

            if (currentValue < 92) {
                return factory.createAlert(Integer.toString(current.getPatientId()), "Low Blood Saturation", current.getTimestamp());
            }

            long tenMinutesInMillis = 10 * 60 * 1000;
            for (int j = i + 1; j < records.size(); j++) {
                PatientRecord future = records.get(j);
                if (!future.getRecordType().equals("Saturation")) continue;

                if (future.getTimestamp() - current.getTimestamp() <= tenMinutesInMillis) {
                    if (currentValue - future.getMeasurementValue() >= 5) {
                        return factory.createAlert(Integer.toString(future.getPatientId()), "Rapid Saturation Drop", future.getTimestamp());
                    }
                } else {
                    break; 
                }
            }
        }
        return null;
    }
}