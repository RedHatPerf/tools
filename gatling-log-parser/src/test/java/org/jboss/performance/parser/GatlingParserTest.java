package org.jboss.performance.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jboss.performance.parser.printer.Util;

/**
 * Unit test for simple App.
 */
public class GatlingParserTest
        extends TestCase {

    public static final String SIMULATION_LOG_FILE = "simulation.log";
    public static final String SIMULATION_LOG_FILE_FORMAT_B = "simulation.formatB.log";

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
    public void testFormatA() {

        String filePath = this.getClass().getClassLoader().getResource(SIMULATION_LOG_FILE).getPath();

        SimulationLogParser simulationLogParser = new SimulationLogParser(filePath);

        simulationLogParser.parseLogFile();

        //print stats
        simulationLogParser.printStats(Util.getPrinters());

    }

    public void testFormatB() {

        String filePath = this.getClass().getClassLoader().getResource(SIMULATION_LOG_FILE_FORMAT_B).getPath();

        SimulationLogParser simulationLogParser = new SimulationLogParser(filePath);

        simulationLogParser.parseLogFile();

        //print stats
        simulationLogParser.printStats(Util.getPrinters());

    }
}
