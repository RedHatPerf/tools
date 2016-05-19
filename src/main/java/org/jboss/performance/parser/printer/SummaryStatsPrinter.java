package org.jboss.performance.parser.printer;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by johara on 10/05/16.
 */
public class SummaryStatsPrinter implements StatsPrinter{

    @Override
    public void printStats(List<Double> valueList) {
        Stream<Double> timesStream = valueList.parallelStream();

        DoubleSummaryStatistics maxResponseTime = timesStream.mapToDouble(Double::doubleValue).summaryStatistics();

        System.out.println("count: " + maxResponseTime.getCount());
        System.out.println("min: " + maxResponseTime.getMin());
        System.out.println("max: " + maxResponseTime.getMax());
        System.out.println("mean: " + maxResponseTime.getAverage());

    }
}
