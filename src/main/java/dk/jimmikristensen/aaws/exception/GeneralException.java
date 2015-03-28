package dk.jimmikristensen.aaws.exception;

import dk.jimmikristensen.aaws.error.ErrorCode;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GeneralException extends WebApplicationException {
    private ErrorCode errorCode;
    Response.Status status;

    public GeneralException(String message, ErrorCode errorCode, Response.Status status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Response.Status getStatus() {
        return status;
    }
}
