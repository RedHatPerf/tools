package org.jboss.performance.parser.printer;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Created by johara on 10/05/16.
 */
public class PercentileStatsPrinter implements StatsPrinter{

    private final double centile;

    public PercentileStatsPrinter(double centile) {
        this.centile = centile;
    }

    @Override
    public void printStats(List<Double> valueList) {
        Stream<Double> timesStream = valueList.stream();
        long centil = new Double(valueList.size() / 100.00 * centile).longValue();

        OptionalDouble centileValue = timesStream.mapToDouble(Double::doubleValue).sorted().limit(centil).reduce((first, second) -> second);

        System.out.println(centile + "th Centile: " + centileValue.getAsDouble());
    }
}
