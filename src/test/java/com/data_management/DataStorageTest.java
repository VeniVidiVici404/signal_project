package com.data_management;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;


class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        // Add two records at specific timestamps
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        // Retrieve records exactly within that timeframe
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void testGetRecordsOutsideTimeRange() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);

        // Try to retrieve records from a time range in the future
        List<PatientRecord> records = storage.getRecords(1, 1714376789060L, 1714376789070L);
        
        // The list should be completely empty
        assertTrue(records.isEmpty()); 
    }

    @Test
    void testMockDataReader() throws IOException {
        DataStorage storage = DataStorage.getInstance();
        
        // Implementing a quick mock DataReader specifically for this test
        DataReader mockReader = new DataReader() {
            @Override
            public void readData(DataStorage dataStorage) throws IOException {
                dataStorage.addPatientData(2, 98.0, "Saturation", 1000L);
                dataStorage.addPatientData(2, 95.0, "Saturation", 2000L);
            }
        };

        // Use the mock reader to push data into storage
        mockReader.readData(storage);
        
        // Verify the storage properly saved the mocked data
        List<PatientRecord> records = storage.getRecords(2, 500L, 2500L);
        assertEquals(2, records.size());
        assertEquals(98.0, records.get(0).getMeasurementValue());
    }
}