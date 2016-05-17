package org.jboss.performance.parser.printer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johara on 11/05/16.
 */
public class Util {

    public static List<StatsPrinter> getPrinters() {
        List<StatsPrinter> printers = new ArrayList<>();
        printers.add(new SummaryStatsPrinter());
        printers.add(new PercentileStatsPrinter(50));
        printers.add(new PercentileStatsPrinter(75));
        printers.add(new PercentileStatsPrinter(95));
        printers.add(new PercentileStatsPrinter(99));
        printers.add(new PercentileStatsPrinter(99.9));
        printers.add(new PercentileStatsPrinter(99.99));
        printers.add(new PercentileStatsPrinter(99.999));
        printers.add(new PercentileStatsPrinter(99.9999));

        return printers;
    }

}
