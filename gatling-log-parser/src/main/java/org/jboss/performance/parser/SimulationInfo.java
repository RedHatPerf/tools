package org.jboss.performance.parser;

/**
 * @author Radim Vansa &lt;rvansa@redhat.com&gt;
 */
public class SimulationInfo {
   private final String file;
   public String clazz;
   public String name;
   public long startTime;
   public long firstRequestStart = Long.MAX_VALUE;
   public long lastRequestComplete = Long.MIN_VALUE;

   public SimulationInfo(String file) {
      this.file = file;
   }
}
