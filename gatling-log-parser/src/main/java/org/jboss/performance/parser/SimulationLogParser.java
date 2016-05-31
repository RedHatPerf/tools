package org.jboss.performance.parser;

import org.jboss.performance.parser.format.FileFormat;
import org.jboss.performance.parser.format.FileVersionA;
import org.jboss.performance.parser.format.FileVersionB;
import org.jboss.performance.parser.format.UnknownFileFormatException;
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

    public final FileFormat fileFormat;

    private final String simulationLogFile;
    private List<Double> responseTimeList;

    public SimulationLogParser(String simulationLogFile) {
        this.simulationLogFile = simulationLogFile;
        this.fileFormat  = getFileFormat();
    }

    private FileFormat getFileFormat() {
        String header = readFile();
        FileFormat fileFormat;

        fileFormat = new FileVersionA();

        if(fileFormat.validateFormat(header))
            return fileFormat;

        fileFormat = new FileVersionB();

        if(fileFormat.validateFormat(header))
            return fileFormat;

        fileFormat = null;

        throw new UnknownFileFormatException();
    }

    private String readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(simulationLogFile))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();  // TODO: Customise this generated block
            return null;
        }
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
                String[] columns = line.split(fileFormat.getSEPARATOR());
                if (fileFormat.getREQUEST().equals(columns[fileFormat.getTypeField()])) {
                    info.firstRequestStart = Math.min(Long.parseLong(columns[fileFormat.getRequestTimestampField()]), info.firstRequestStart);
                    info.lastRequestComplete = Math.max(Long.parseLong(columns[fileFormat.getCompleteTimestampField()]), info.lastRequestComplete);
                } else if (fileFormat.getRUN().equals(columns[fileFormat.getTypeField()])) {
                    info.clazz = columns[fileFormat.getClassTypeField()];
                    info.name = columns[fileFormat.getIdField()];
                    info.startTime = Long.parseLong(columns[fileFormat.getHeaderStartTimeField()]);
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
                    .map(line -> Arrays.asList(line.split(fileFormat.getSEPARATOR())))
                    .filter(list -> list.get(fileFormat.getTypeField()).equals(fileFormat.getREQUEST()))
                    .map(item -> Double.parseDouble(item.get(fileFormat.getCompleteTimestampField())) - Double.parseDouble(item.get(fileFormat.getRequestTimestampField())))
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
                String[] columns = line.split(fileFormat.getSEPARATOR());
                for (Filter filter : filters) {
                    columns = filter.apply(fileFormat, info, columns,  cs-> {
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
            writer.write(fileFormat.getSEPARATOR());
            writer.write(columns[i]);
        }
        writer.write('\n');
    }

    interface Filter {
        String[] apply(FileFormat fileFormat, SimulationInfo info, String[] columns, Consumer<String[]> addLine);
    }

    /** Skip first X ms */
    static class SkipFilter implements Filter {
        private final long ms;

        SkipFilter(long ms) {
            this.ms = ms;
        }

        @Override
        public String[] apply(FileFormat fileFormat, SimulationInfo info, String[] columns, Consumer<String[]> addLine) {
            long newStart = info.startTime + ms;
            if (fileFormat.getREQUEST().equals(columns[fileFormat.getTypeField()])) {
                if (Long.parseLong(columns[fileFormat.getRequestTimestampField()]) < newStart) {
                    return null;
                }
            } else if (fileFormat.getUSER().equals(columns[fileFormat.getTypeField()])) {
                long start = Long.parseLong(columns[4]);
                long end = Long.parseLong(columns[5]);
                if (fileFormat.getSTART().equals(columns[3]) && start < newStart) {
                    return null;
                } else if (fileFormat.getEND().equals(columns[3]) && start < newStart) {
                    if (end < newStart) {
                        return null;
                    } else {
                        columns[4] = String.valueOf(newStart);
                        String[] startColumns = Arrays.copyOf(columns, columns.length);
                        startColumns[3] = fileFormat.getSTART();
                        startColumns[4] = String.valueOf(newStart);
                        startColumns[5] = "0";
                        addLine.accept(startColumns);
                    }
                }
            } else if (fileFormat.getRUN().equals(columns[fileFormat.getTypeField()])) {
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
        public String[] apply(FileFormat fileFormat, SimulationInfo info, String[] columns, Consumer<String[]> addLine) {
            if (fileFormat.getREQUEST().equals(columns[fileFormat.getTypeField()])) {
                if (Long.parseLong(columns[fileFormat.getCompleteTimestampField()]) >= info.lastRequestComplete - ms) {
                    return null;
                }
            } else if (fileFormat.getUSER().equals(columns[fileFormat.getTypeField()])) {
                if (Long.parseLong(columns[4]) >= info.lastRequestComplete - ms || Long.parseLong(columns[5]) >= info.lastRequestComplete - ms) {
                    return null;
                }
            }
            return columns;
        }
    }
}
