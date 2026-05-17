package com.data_management;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class FileDataReaderTest {

    @Test
    void testReadData() throws IOException {
        // 1. Create a temporary folder and test file
        File tempDir = new File("temp_test_data");
        tempDir.mkdir();
        File tempFile = new File(tempDir, "test_data.txt");
        
        // 2. Write dummy patient data into the file (including the % sign to test our parsing)
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1, 1700000000000, HeartRate, 75.0\n");
            writer.write("1, 1700000001000, Saturation, 98.0%\n");
        }

        // 3. Set up our storage and reader pointing to the temporary folder
        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader("temp_test_data");

        // 4. Execute the read operation
        reader.readData(storage);

        // 5. Verify the data was parsed and stored correctly
        List<PatientRecord> records = storage.getRecords(1, 1600000000000L, 1800000000000L);
        
        assertEquals(2, records.size()); // It should have found both lines
        
        // Verify the first line (HeartRate)
        assertEquals(75.0, records.get(0).getMeasurementValue());
        assertEquals("HeartRate", records.get(0).getRecordType());
        
        // Verify the second line (Saturation parsed correctly without the %)
        assertEquals(98.0, records.get(1).getMeasurementValue());
        assertEquals("Saturation", records.get(1).getRecordType());

        // 6. Clean up the temporary files so we don't clutter the computer
        tempFile.delete();
        tempDir.delete();
    }
}