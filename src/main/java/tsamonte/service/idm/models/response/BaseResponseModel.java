package tsamonte.service.idm.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.logger.ServiceLogger;

import javax.ws.rs.core.Response;

/**
 * The class BaseResponseModel contains all common components of every API response.
 *
 * Response Model:
 *      - resultCode (int, required)
 *      - message (string, required)
 *
 * Responses that require only the result code and message will utilize this class.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseModel {
    @JsonIgnore
    private Result result;

    // Empty default constructor for inheritance
    public BaseResponseModel() {}

    public BaseResponseModel(Result result) { this.result = result; }

    @JsonProperty("resultCode")
    public int getResultCode() { return result.getResultCode(); }

    @JsonProperty("message")
    public String getMessage() { return result.getMessage(); }

    @JsonIgnore
    public Result getResult() { return result; }

    @JsonIgnore
    public void setResult(Result result) { this.result = result; }

    /**
     * Builds an API response using the Result object stored in this class.
     *
     * If result is null or reflects an Internal Server Error, an INTERNAL_SERVER_ERROR response is returned.
     * Otherwise, appropriate response is returned.
     *
     * @return javax.ws.rs.core.Response object containing appropriate API response
     */
    @JsonIgnore
    public Response buildResponse() {
        ServiceLogger.LOGGER.info("Response being build with Result: " + result);

        if (result == null || result.getStatus() == Response.Status.INTERNAL_SERVER_ERROR)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        return Response.status(result.getStatus()).entity(this).build();
    }
}
