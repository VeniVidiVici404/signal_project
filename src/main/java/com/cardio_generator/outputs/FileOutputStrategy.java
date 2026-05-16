package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An output strategy that writes generated health data to a file system[cite: 524, 567].
 * <p>
 * This class organizes data by writing it into separate text files within a specified 
 * base directory. Each file is named according to the data's {@code label} (e.g., "ECG.txt"). 
 * It utilizes a {@link ConcurrentHashMap} to cache file paths for efficient, thread-safe access 
 * during continuous data generation.
 */
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;

    /**
     * A thread-safe map that caches the absolute file paths associated with each data label.
     */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@code FileOutputStrategy} with the specified base directory.
     *
     * @param baseDirectory The path to the directory where the output text files will be stored[cite: 524].
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs the generated health data by appending it to the appropriate text file.
     * <p>
     * If the base directory or the specific file for the given label does not exist, 
     * they will be created automatically. The data is appended to the file in a formatted string.
     *
     * @param patientId The unique identifier of the patient.
     * @param timestamp The time the data was generated, in milliseconds.
     * @param label     The category of the data (e.g., "ECG", "Alert"), which dictates the output file's name.
     * @param data      The actual data value or message to be recorded.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        
        // Set the filePath variable
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
