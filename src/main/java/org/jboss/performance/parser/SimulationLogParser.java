package org.jboss.performance.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by johara on 10/05/16.
 */
public class SimulationLogParser {

    private static final String SEPARATOR = "\t";
    public static final String REQUEST_FIELD = "REQUEST";

    public static final int TYPE_FIELD = 2;
    public static final int COMPLETE_TIMESTAMP_FIELD = 8;
    public static final int REQUEST_TIMESTAMP_FIELD = 5;

    private final String simulationLogFile;

    public SimulationLogParser(String simulationLogFile) {
        this.simulationLogFile = simulationLogFile;
    }

    public void parseLogFile() {

        if (simulationLogFile == null) {
            throw new SimulationLogFileNotDefinedException();
        }
        List<Double> responseTimeList = null;

        try (Stream<String> stream = Files.lines(Paths.get(simulationLogFile))) {
            responseTimeList = stream
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .filter(list -> list.get(TYPE_FIELD).equals(REQUEST_FIELD))
                    .map(item -> Double.parseDouble(item.get(COMPLETE_TIMESTAMP_FIELD)) - Double.parseDouble(item.get(REQUEST_TIMESTAMP_FIELD)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stream<Double> timesStream = responseTimeList.stream();

        OptionalDouble maxResponseTime = timesStream.mapToDouble(Double::doubleValue).max();

        timesStream = responseTimeList.stream();

        OptionalDouble meanResponseTime = timesStream.mapToDouble(Double::doubleValue).average();

        timesStream = responseTimeList.stream();
        OptionalDouble minResponseTime = timesStream.mapToDouble(Double::doubleValue).min();

        System.out.println("mean: " + meanResponseTime.getAsDouble());
        System.out.println("max: " + maxResponseTime.getAsDouble());
        System.out.println("min: " + minResponseTime.getAsDouble());
    }




}
