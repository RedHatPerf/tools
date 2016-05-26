package org.jboss.performance.parser;

import org.jboss.performance.parser.printer.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GatlingParser {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
        }
        switch (args[0].toLowerCase().trim()) {
            case "stats":
                printStats(args);
                break;
            case "filter":
                filterLog(args);
                break;
            case "help":
            default:
                printHelp();
                break;
        }

    }

    private static void filterLog(String[] args) {
        String dest = null;
        List<SimulationLogParser.Filter> filters = new ArrayList<>();
        List<String> sources = new ArrayList<>();
        for (int i = 1; i < args.length; ++i) {
            switch (args[i].toLowerCase().trim()) {
                case "-d":
                case "--dest":
                    ++i;
                    dest = arg(args, i);
                    break;
                case "-s":
                case "--skip":
                    ++i;
                    filters.add(new SimulationLogParser.SkipFilter(Long.valueOf(arg(args, i))));
                    break;
                case "-t":
                case "--truncate":
                    ++i;
                    filters.add(new SimulationLogParser.TruncateFilter(Long.valueOf(arg(args, i))));
                    break;
                default:
                    sources.add(args[i]);
            }
        }
        if (dest == null) {
            System.err.println("No destination set!");
            System.exit(1);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
            for (String src : sources) {
                SimulationLogParser simulationLogParser = new SimulationLogParser(src);
                SimulationInfo info = simulationLogParser.parseInfo();
                simulationLogParser.rewrite(info, writer, filters);
            }
        } catch (IOException e) {
            e.printStackTrace();  // TODO: Customise this generated block
        }
    }

    private static String arg(String[] args, int i) {
        if (i >= args.length) {
            System.err.println("Argument " + args[i - 1] + " requires value");
            System.exit(1);
            return null;
        } else if (args[i].startsWith("-")) {
            System.err.println("Argument " + args[i - 1] + " has value starting with '-' - is that an option?");
            System.exit(1);
            return null;
        } else {
            return args[i];
        }
    }

    private static void printStats(String[] args) {
        String filename = args[1];

        if (filename != null) {
            SimulationLogParser simulationLogParser = new SimulationLogParser(filename);

            simulationLogParser.parseLogFile();

            //print stats
            simulationLogParser.printStats(Util.getPrinters());
        }
    }

    private static void printHelp() {
        System.err.println("Mode: stats, filter");
    }

}
