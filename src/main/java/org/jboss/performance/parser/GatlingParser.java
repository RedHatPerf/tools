package org.jboss.performance.parser;

import org.jboss.performance.parser.printer.Util;

public class GatlingParser {
    public static void main(String[] args) {
        String filename = args[0];

        if (filename != null) {
            SimulationLogParser simulationLogParser = new SimulationLogParser(filename);

            simulationLogParser.parseLogFile();

            //print stats
            simulationLogParser.printStats(Util.getPrinters());
        }
    }
}
