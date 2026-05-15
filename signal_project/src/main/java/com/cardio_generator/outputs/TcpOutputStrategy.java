package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * An output strategy that streams generated health data over a network using TCP sockets.
 * <p>
 * This class acts as a simple TCP server. Upon initialization, it listens on a specified port 
 * for an incoming client connection. The connection acceptance process is handled in a separate 
 * background thread to ensure that the main data generation loop is not blocked while waiting 
 * for a client to connect. Once connected, data is transmitted as a comma-separated string.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a new {@code TcpOutputStrategy} and starts a TCP server on the given port.
     * <p>
     * A background thread is spawned immediately to wait for and accept a client connection.
     *
     * @param port The port number on which the TCP server will listen for incoming client connections.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs the generated health data by sending it to the connected TCP client.
     * <p>
     * If no client is currently connected, the data is simply discarded. When a client is connected, 
     * the data is formatted into a comma-separated string (patientId,timestamp,label,data) and sent 
     * over the network.
     *
     * @param patientId The unique identifier of the patient.
     * @param timestamp The exact time the data was generated, in milliseconds.
     * @param label     The category of the data (e.g., "ECG", "BloodPressure").
     * @param data      The actual data value or message.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
