package org.jboss.performance.parser.printer;

import java.util.List;

/**
 * Created by johara on 10/05/16.
 */
public interface StatsPrinter {
    void printStats(List<Double> valueList);
}
