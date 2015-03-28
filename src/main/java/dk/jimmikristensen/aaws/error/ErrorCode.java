package dk.jimmikristensen.aaws.error;

public enum ErrorCode {

    UNKNOWN_ERROR(1000),
    DUPLICATE_KEY(1001),
    MISSING_PARAMS(1002),
    KEY_NOT_FOUND(1003),
    INVALID_PARAM(1004),
    PERSISTENCE_EXCEPTION(1005);

    private int code;

    /**
     * non arg constructor needed for JSON
     */
    private ErrorCode() {
    }

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ErrorCode getErrorCodeFromInt(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
}