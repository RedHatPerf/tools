package org.jboss.performance.parser.format;

/**
 * Created by johara on 31/05/16.
 */
public abstract class BaseFileVersion implements FileFormat {

    protected final String SEPARATOR = "\t";
    protected final String REQUEST = "REQUEST";
    protected final String RUN = "RUN";
    protected final String USER = "USER";
    protected final String START = "START";
    protected final String END = "END";

    @Override
    public String getSEPARATOR() {
        return SEPARATOR;
    }

    public String getREQUEST() {
        return REQUEST;
    }

    public String getRUN() {
        return RUN;
    }

    public String getUSER() {
        return USER;
    }

    public String getSTART() {
        return START;
    }

    public String getEND() {
        return END;
    }

    public boolean validateFormat(String header) {
        try{
            String[] splitHeader = header.split(getSEPARATOR());
            return splitHeader[this.getTypeField()].equals(this.getRUN());
        }
        catch (Exception e){
            return false;
        }

    }

}
