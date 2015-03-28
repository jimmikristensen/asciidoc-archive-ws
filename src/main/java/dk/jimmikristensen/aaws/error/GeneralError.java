package dk.jimmikristensen.aaws.error;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeneralError {
    private int errorCode;
    private String exceptionMessage = "";

    public GeneralError() {
    }

    public GeneralError(int errorCode, String exceptionMessage) {
        this.errorCode = errorCode;
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the int representation - not the enum itself
     */
    public int getErrorCode() {
        return errorCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}