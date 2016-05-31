package org.jboss.performance.parser.format;

/**
 * Created by johara on 31/05/16.
 */
public class FileVersionA extends BaseFileVersion {

    private static final int TYPE_FIELD = 2;
    private static final int COMPLETE_TIMESTAMP_FIELD = 8;
    private static final int REQUEST_TIMESTAMP_FIELD = 5;
    private static final int CLASS_FIELD = 0;
    private static final int ID_FIELD = 1;
    private static final int HEADER_START_TIME_FIELD = 3;

    public int getTypeField() {
        return TYPE_FIELD;
    }

    public int getCompleteTimestampField() {
        return COMPLETE_TIMESTAMP_FIELD;
    }

    public int getRequestTimestampField() {
        return REQUEST_TIMESTAMP_FIELD;
    }

    public int getClassTypeField() {
        return CLASS_FIELD;
    }

    public int getIdField() {
        return ID_FIELD;
    }

    public int getHeaderStartTimeField() {
        return HEADER_START_TIME_FIELD;
    }


}
