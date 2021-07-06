package tsamonte.service.idm.base;

import javax.ws.rs.core.Response;

/**
 * Enum Result encapsulates an API response's result code, message, and HTTP Status Code
 */
public enum Result {
    // Common error results
    JSON_PARSE_EXCEPTION (-3, "JSON Parse Exception.", Response.Status.BAD_REQUEST),
    JSON_MAPPING_EXCEPTION (-2, "JSON Mapping Exception.", Response.Status.BAD_REQUEST),
    INTERNAL_SERVER_ERROR (-1, "Internal Server Error.", Response.Status.INTERNAL_SERVER_ERROR),

    // Service-specific error results
    PRIVILEGE_LEVEL_OUT_OF_RANGE (-14, "Privilege level out of valid range.", Response.Status.BAD_REQUEST),
    TOKEN_INVALID_LENGTH (-13, "Token has invalid length.", Response.Status.BAD_REQUEST),
    PASSWORD_IS_EMPTY(-12, "Password is empty.", Response.Status.BAD_REQUEST),
    EMAIL_INVALID_FORMAT (-11, "Email address has invalid format.", Response.Status.BAD_REQUEST),
    EMAIL_IS_EMPTY(-10, "Email address is empty.", Response.Status.BAD_REQUEST),

    // Service-specific success results
    PASSWORDS_DO_NOT_MATCH (11, "Passwords do not match", Response.Status.OK),
    PASSWORD_DOESNT_MEET_LENGTH_REQUIREMENT (12, "Password does not meet length requirements.", Response.Status.OK),
    PASSWORD_DOESNT_MEET_CHAR_REQUIREMENT (13, "Password does not meet character requirements.", Response.Status.OK),
    USER_NOT_FOUND (14, "User not found.", Response.Status.OK),
    EMAIL_ALREADY_EXISTS (16, "Email already in use", Response.Status.OK),
    USER_REGISTERED_SUCCESSFULLY (110, "User registered successfully.", Response.Status.OK),
    USER_LOGGED_IN_SUCCESSFULLY (120, "User logged in successfully.", Response.Status.OK),
    SESSION_ACTIVE (130, "Session is active.", Response.Status.OK),
    SESSION_EXPIRED (131, "Session is expired.", Response.Status.OK),
    SESSION_CLOSED (132, "Session is closed.", Response.Status.OK),
    SESSION_REVOKED (133, "Session is revoked.", Response.Status.OK),
    SESSION_NOT_FOUND (134, "Session not found.", Response.Status.OK),
    SUFFICIENT_PRIVILEGE_LEVEL (140, "User has sufficient privilege level.", Response.Status.OK),
    INSUFFICIENT_PRIVILEGE_LEVEL (141, "User has insufficient privilege level.", Response.Status.OK);

    private final int resultCode;
    private final String message;
    private final Response.Status status;

    Result(int resultCode, String message, Response.Status status) {
        this.resultCode = resultCode;
        this.message = message;
        this.status = status;
    }

    public int getResultCode() { return resultCode; }

    public String getMessage() { return message; }

    public Response.Status getStatus() { return status; }
}
