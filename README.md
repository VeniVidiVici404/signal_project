# Cardio Data Simulator

The Cardio Data Simulator is a Java-based application designed to simulate real-time cardiovascular data for multiple patients. This tool is particularly useful for educational purposes, enabling students to interact with real-time data streams of ECG, blood pressure, blood saturation, and other cardiovascular signals.

## Features

- Simulate real-time ECG, blood pressure, blood saturation, and blood levels data.
- Supports multiple output strategies:
  - Console output for direct observation.
  - File output for data persistence.
  - WebSocket and TCP output for networked data streaming.
- Configurable patient count and data generation rate.
- Randomized patient ID assignment for simulated data diversity.

## Getting Started

### Prerequisites

- Java JDK 11 or newer.
- Maven for managing dependencies and compiling the application.

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/tpepels/signal_project.git
   ```

2. Navigate to the project directory:

   ```sh
   cd signal_project
   ```

3. Compile and package the application using Maven:
   ```sh
   mvn clean package
   ```
   This step compiles the source code and packages the application into an executable JAR file located in the `target/` directory.

### Running the Simulator

After packaging, you can run the simulator directly from the executable JAR:

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar
```

To run with specific options (e.g., to set the patient count and choose an output strategy):

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar --patient-count 100 --output file:./output
```

### Supported Output Options

- `console`: Directly prints the simulated data to the console.
- `file:<directory>`: Saves the simulated data to files within the specified directory.
- `websocket:<port>`: Streams the simulated data to WebSocket clients connected to the specified port.
- `tcp:<port>`: Streams the simulated data to TCP clients connected to the specified port.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Week 2: UML Modeling
For Project Part 2, four subsystems of the Cardiovascular Health Monitoring System were modeled to establish a clean, modular architecture. 

You can view the diagrams and read the design rationale for each system in the [UML Models folder](./uml_models).

## Week 3: Testing and Code Coverage
For Project Part 3, unit tests were implemented for the data management and alert generation systems using JUnit. Below is the JaCoCo code coverage report verifying the tests.

![JaCoCo Coverage Report](./reports/jacoco_report.png)

**Code Coverage Explanation:**
As shown in the JaCoCo report, the `com.alerts` and `com.data_management` packages have high coverage because unit tests were thoroughly implemented for the `AlertGenerator`, `DataStorage`, and `FileDataReader` classes. The `com.cardio_generator` packages currently show 0% coverage. These were intentionally left untested because they belong to the Week 1 simulator logic, and the scope of Project Part 3 was strictly limited to testing the new patient storage and alert generation systems.

## Project Members

- Student ID: 6346179
