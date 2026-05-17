package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        // This runs before every single test to give us a fresh slate
        storage = new DataStorage();
        alertGenerator = new AlertGenerator(storage);
        testPatient = new Patient(1);
    }

    @Test
    void testCriticalSystolicBloodPressureAlert() {
        // Give the patient a dangerously high blood pressure
        testPatient.addRecord(185.0, "SystolicBloodPressure", 1000L);
        
        alertGenerator.evaluateData(testPatient);
        
        // We expect exactly 1 alert to be generated
        assertEquals(1, alertGenerator.getTriggeredAlerts().size());
        assertEquals("Critical Systolic BP", alertGenerator.getTriggeredAlerts().get(0).getCondition());
    }

    @Test
    void testHealthyBloodPressureNoAlert() {
        // Give the patient a healthy blood pressure
        testPatient.addRecord(115.0, "SystolicBloodPressure", 1000L);
        testPatient.addRecord(75.0, "DiastolicBloodPressure", 1000L);
        
        alertGenerator.evaluateData(testPatient);
        
        // We expect 0 alerts
        assertTrue(alertGenerator.getTriggeredAlerts().isEmpty());
    }

    @Test
    void testLowBloodSaturationAlert() {
        // Give the patient dangerously low oxygen
        testPatient.addRecord(90.0, "Saturation", 1000L);
        
        alertGenerator.evaluateData(testPatient);
        
        assertEquals(1, alertGenerator.getTriggeredAlerts().size());
        assertEquals("Low Blood Saturation", alertGenerator.getTriggeredAlerts().get(0).getCondition());
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        // Combine low BP and low oxygen at the exact same time
        testPatient.addRecord(85.0, "SystolicBloodPressure", 1000L);
        testPatient.addRecord(90.0, "Saturation", 1000L);
        
        alertGenerator.evaluateData(testPatient);
        
        // It should trigger the critical threshold alerts AND the combined condition alert
        boolean hasCombinedAlert = alertGenerator.getTriggeredAlerts().stream()
            .anyMatch(a -> a.getCondition().equals("Hypotensive Hypoxemia"));
            
        assertTrue(hasCombinedAlert);
    }

    @Test
    void testManualAlert() {
        // Simulate a nurse pressing the button
        testPatient.addRecord(1.0, "Alert", 1000L);
        
        alertGenerator.evaluateData(testPatient);
        
        assertEquals(1, alertGenerator.getTriggeredAlerts().size());
        assertEquals("Manual Alert Triggered", alertGenerator.getTriggeredAlerts().get(0).getCondition());
    }
}