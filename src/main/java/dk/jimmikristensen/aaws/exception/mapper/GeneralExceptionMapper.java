package dk.jimmikristensen.aaws.exception.mapper;

import dk.jimmikristensen.aaws.error.ErrorCode;
import dk.jimmikristensen.aaws.error.GeneralError;
import dk.jimmikristensen.aaws.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    private final Logger log = LoggerFactory.getLogger(GeneralExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {

        Response.Status status;
        GeneralError generalError;

        if (exception instanceof GeneralException) {
            generalError = new GeneralError(((GeneralException) exception).getErrorCode().getCode(), exception.getMessage());
            status = ((GeneralException) exception).getStatus();
        } else {
            if (exception instanceof WebApplicationException) {
                status = Response.Status.fromStatusCode(((WebApplicationException) exception).getResponse().getStatus());
            } else {
                status = Response.Status.INTERNAL_SERVER_ERROR;
            }
            generalError = new GeneralError(ErrorCode.UNKNOWN_ERROR.getCode(), "Requested resource not found");
        }

        log.error("exceptionMapper", exception);
        return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE).entity(generalError).build();

    }

}