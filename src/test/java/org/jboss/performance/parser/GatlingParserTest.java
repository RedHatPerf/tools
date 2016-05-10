package org.jboss.performance.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jboss.performance.parser.printer.PercentileStatsPrinter;
import org.jboss.performance.parser.printer.SummaryStatsPrinter;

/**
 * Unit test for simple App.
 */
public class GatlingParserTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GatlingParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(GatlingParserTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
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
