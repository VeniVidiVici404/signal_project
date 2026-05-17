package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public Alert checkAlert(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        AlertFactory factory = new AlertFactory(); // Using the Factory
        
        double sum = 0;
        int count = 0;
        double windowAverage = 0;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG")) {
                double value = record.getMeasurementValue();
                sum += value;
                count++;
                windowAverage = sum / count;

                if (count > 5 && value > (windowAverage * 1.5)) {
                    return factory.createAlert(Integer.toString(record.getPatientId()), "Abnormal ECG Peak", record.getTimestamp());
                }
            }
        }
        return null;
    }
}