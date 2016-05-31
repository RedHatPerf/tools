package org.jboss.performance.parser.format;

/**
 * Created by johara on 31/05/16.
 */
public interface FileFormat {
    String getSEPARATOR();

    String getREQUEST();

    String getRUN();

    String getUSER();

    String getSTART();

    String getEND();

    int getTypeField();

    int getCompleteTimestampField();

    int getRequestTimestampField();

    int getClassTypeField();

    int getIdField();

    boolean validateFormat(String header);

    int getHeaderStartTimeField();
}
