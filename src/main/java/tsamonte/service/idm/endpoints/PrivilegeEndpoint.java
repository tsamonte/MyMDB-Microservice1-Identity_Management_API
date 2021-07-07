package tsamonte.service.idm.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.database.access.UserRecords;
import tsamonte.service.idm.database.model.UserModel;
import tsamonte.service.idm.endpoints.helpers.EmailPasswordHelper;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.models.request.PrivilegeRequestModel;
import tsamonte.service.idm.models.response.BaseResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * POST /api/idm/privilege
 *
 * Request Model:
 *      - email (string, required)
 *      - plevel (int, required)
 *
 * Response Model:
 *      - resultCode (int)
 *      - message (string)
 */
@Path("privilege")
public class PrivilegeEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response privilegeResponse(@Context HttpHeaders headers, String jsonText) {
        PrivilegeRequestModel requestModel;
        BaseResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);
        }
        catch (IOException e) {
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new BaseResponseModel(Result.JSON_PARSE_EXCEPTION);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new BaseResponseModel(Result.JSON_MAPPING_EXCEPTION);
            }

            // resultCode = -1; 500 Internal Server Error; "Internal server error."
            else {
                responseModel = new BaseResponseModel(Result.INTERNAL_SERVER_ERROR);
            }

            return responseModel.buildResponse();
        }

        // resultCode = -14; 400 Bad request; "Privilege level out of valid range."
        if(requestModel.getPLevel() < 1 || requestModel.getPLevel() > 5) {
            responseModel = new BaseResponseModel(Result.PRIVILEGE_LEVEL_OUT_OF_RANGE);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = -10; 400 Bad request; "Email address is empty."
        if(requestModel.getEmail() == null || requestModel.getEmail().length() == 0) {
            responseModel = new BaseResponseModel(Result.EMAIL_IS_EMPTY);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = -11; 400 Bad request; "Email address has invalid format."
        if(!EmailPasswordHelper.isValidEmailFormat(requestModel.getEmail())) {
            responseModel = new BaseResponseModel(Result.EMAIL_INVALID_FORMAT);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        UserModel user = UserRecords.retrieve(requestModel.getEmail());

        // resultCode = 14; 200 OK; "User not found."
        if(user == null) {
            responseModel = new BaseResponseModel(Result.USER_NOT_FOUND);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // --------------------------------------------------------------

        // resultCode = 140; 200 OK; "User has sufficient privilege level."
        if (user.getPlevel() <= requestModel.getPLevel()) {
            responseModel = new BaseResponseModel(Result.SUFFICIENT_PRIVILEGE_LEVEL);
        }
        // resultCode = 141; 200 OK; "User has insufficient privilege level."
        else {
            responseModel = new BaseResponseModel(Result.INSUFFICIENT_PRIVILEGE_LEVEL);
        }
        ServiceLogger.LOGGER.info(responseModel.getMessage());
        return responseModel.buildResponse();

        // --------------------------------------------------------------
    }
}
