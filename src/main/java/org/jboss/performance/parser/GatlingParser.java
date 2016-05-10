package org.jboss.performance.parser;

import org.jboss.performance.parser.printer.PercentileStatsPrinter;
import org.jboss.performance.parser.printer.SummaryStatsPrinter;

/**
 * Hello world!
 *
 */
public class GatlingParser
{
    public static void main( String[] args )
    {
        String filename = args[0];

        if(filename != null){
            SimulationLogParser simulationLogParser = new SimulationLogParser("/home/johara/Work/projects/benchmarks/webapp-rs/target/gatling/results/put-1462831084072/simulation.log");

            simulationLogParser.parseLogFile();

            //Print stats
            simulationLogParser.printStats(new SummaryStatsPrinter());
            simulationLogParser.printStats(new PercentileStatsPrinter(50));
            simulationLogParser.printStats(new PercentileStatsPrinter(95));
            simulationLogParser.printStats(new PercentileStatsPrinter(99));
            simulationLogParser.printStats(new PercentileStatsPrinter(99.9));
            simulationLogParser.printStats(new PercentileStatsPrinter(99.99));
            simulationLogParser.printStats(new PercentileStatsPrinter(99.999));
            simulationLogParser.printStats(new PercentileStatsPrinter(99.9999));

        }
    }
}
