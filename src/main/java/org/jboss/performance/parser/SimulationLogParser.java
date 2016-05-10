package org.jboss.performance.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by johara on 10/05/16.
 */
public class SimulationLogParser {

    private static final String SEPARATOR = "\t";
    public static final String REQUEST_FIELD = "REQUEST";
    private final String simulationLogFile;

    public SimulationLogParser(String simulationLogFile) {
        this.simulationLogFile = simulationLogFile;
    }

    public void parseLogFile() {

        if (simulationLogFile == null) {
            throw new SimulationLogFileNotDefinedException();
        }

        try (Stream<String> stream = Files.lines(Paths.get(simulationLogFile))) {
            OptionalDouble meanResponseTime = stream
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .filter(list -> list.get(2).equals(REQUEST_FIELD))
                    .mapToDouble(item -> Double.parseDouble(item.get(8)) - Double.parseDouble(item.get(5)))
                    .average();

            System.out.println("mean: " + meanResponseTime.getAsDouble());

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> stream = Files.lines(Paths.get(simulationLogFile))) {

            OptionalDouble maxResponseTime = stream
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .filter(list -> list.get(2).equals(REQUEST_FIELD))
                    .mapToDouble(item -> Double.parseDouble(item.get(8)) - Double.parseDouble(item.get(5)))
                    .max();

            System.out.println("max: " + maxResponseTime.getAsDouble());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
