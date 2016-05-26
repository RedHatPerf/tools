package org.jboss.performance.parser;

import org.jboss.performance.parser.printer.StatsPrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by johara on 10/05/16.
 */
public class SimulationLogParser {

    private static final String SEPARATOR = "\t";
    public static final String REQUEST = "REQUEST";
    public static final String RUN = "RUN";
    public static final String USER = "USER";
    public static final String START = "START";
    public static final String END = "END";

    public static final int TYPE_FIELD = 2;
    public static final int COMPLETE_TIMESTAMP_FIELD = 8;
    public static final int REQUEST_TIMESTAMP_FIELD = 5;

    private final String simulationLogFile;
    private List<Double> responseTimeList;

    public SimulationLogParser(String simulationLogFile) {
        this.simulationLogFile = simulationLogFile;
    }

    public void parseLogFile() {

        if (simulationLogFile == null) {
            throw new SimulationLogFileNotDefinedException();
        }
        this.responseTimeList = parseFile();
    }

    public SimulationInfo parseInfo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(simulationLogFile))) {
            String line;
            SimulationInfo info = new SimulationInfo(simulationLogFile);
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(SEPARATOR);
                if (REQUEST.equals(columns[TYPE_FIELD])) {
                    info.firstRequestStart = Math.min(Long.parseLong(columns[REQUEST_TIMESTAMP_FIELD]), info.firstRequestStart);
                    info.lastRequestComplete = Math.max(Long.parseLong(columns[COMPLETE_TIMESTAMP_FIELD]), info.lastRequestComplete);
                } else if (RUN.equals(columns[TYPE_FIELD])) {
                    info.clazz = columns[0];
                    info.name = columns[1];
                    info.startTime = Long.parseLong(columns[3]);
                }
            }
            return info;
        } catch (IOException e) {
            e.printStackTrace();  // TODO: Customise this generated block
            return null;
        }
    }

    public void printStats(List<StatsPrinter> printers) {
        //Print stats
        for (StatsPrinter printer : printers) {
            printer.printStats(this.responseTimeList);
        }
    }

    private List<Double> parseFile() {

        try (Stream<String> stream = Files.lines(Paths.get(simulationLogFile))) {
            return stream
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .filter(list -> list.get(TYPE_FIELD).equals(REQUEST))
                    .map(item -> Double.parseDouble(item.get(COMPLETE_TIMESTAMP_FIELD)) - Double.parseDouble(item.get(REQUEST_TIMESTAMP_FIELD)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void rewrite(SimulationInfo info, BufferedWriter writer, List<Filter> filters) {
        try (BufferedReader reader = new BufferedReader(new FileReader(simulationLogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(SEPARATOR);
                for (Filter filter : filters) {
                    columns = filter.apply(info, columns, cs -> {
                        try {
                            addLine(writer, cs);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    if (columns == null) break;
                }
                if (columns != null) {
                    addLine(writer, columns);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // TODO: Customise this generated block
        }
    }

    public void addLine(BufferedWriter writer, String[] columns) throws IOException {
        writer.write(columns[0]);
        for (int i = 1; i < columns.length; ++i) {
            writer.write(SEPARATOR);
            writer.write(columns[i]);
        }
        writer.write('\n');
    }

    interface Filter {
        String[] apply(SimulationInfo info, String[] columns, Consumer<String[]> addLine);
    }

    /** Skip first X ms */
    static class SkipFilter implements Filter {
        private final long ms;

        SkipFilter(long ms) {
            this.ms = ms;
        }

        @Override
        public String[] apply(SimulationInfo info, String[] columns, Consumer<String[]> addLine) {
            long newStart = info.startTime + ms;
            if (REQUEST.equals(columns[TYPE_FIELD])) {
                if (Long.parseLong(columns[REQUEST_TIMESTAMP_FIELD]) < newStart) {
                    return null;
                }
            } else if (USER.equals(columns[TYPE_FIELD])) {
                long start = Long.parseLong(columns[4]);
                long end = Long.parseLong(columns[5]);
                if (START.equals(columns[3]) && start < newStart) {
                    return null;
                } else if (END.equals(columns[3]) && start < newStart) {
                    if (end < newStart) {
                        return null;
                    } else {
                        columns[4] = String.valueOf(newStart);
                        String[] startColumns = Arrays.copyOf(columns, columns.length);
                        startColumns[3] = START;
                        startColumns[4] = String.valueOf(newStart);
                        startColumns[5] = "0";
                        addLine.accept(startColumns);
                    }
                }
            } else if (RUN.equals(columns[TYPE_FIELD])) {
                columns[3] = String.valueOf(newStart);
            }
            return columns;
        }
    }

    /** Skip last X ms */
    static class TruncateFilter implements Filter {
        private final long ms;

        TruncateFilter(long ms) {
            this.ms = ms;
        }

        @Override
        public String[] apply(SimulationInfo info, String[] columns, Consumer<String[]> addLine) {
            if (REQUEST.equals(columns[TYPE_FIELD])) {
                if (Long.parseLong(columns[COMPLETE_TIMESTAMP_FIELD]) >= info.lastRequestComplete - ms) {
                    return null;
                }
            } else if (USER.equals(columns[TYPE_FIELD])) {
                if (Long.parseLong(columns[4]) >= info.lastRequestComplete - ms || Long.parseLong(columns[5]) >= info.lastRequestComplete - ms) {
                    return null;
                }
            }
            return columns;
        }
    }
}
