package org.jboss.performance.parser.consumer;

import java.util.DoubleSummaryStatistics;

/**
 * Created by johara on 10/05/16.
 */
public class ExtendedDoubleSummaryStatistics extends DoubleSummaryStatistics {

    public ExtendedDoubleSummaryStatistics() {
        super();
    }

    /**
     * Records another value into the summary information.
     *
     * @param value the input value
     */
    @Override
    public void accept(double value) {
        super.accept(value);
    }

    /**
     * Combines the state of another {@code DoubleSummaryStatistics} into this
     * one.
     *
     * @param other another {@code DoubleSummaryStatistics}
     * @throws NullPointerException if {@code other} is null
     */
    public void combine(DoubleSummaryStatistics other) {
        super.combine(other);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a non-empty string representation of this object suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     */
    @Override
    public String toString() {
        return super.toString();
    }


}
