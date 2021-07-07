package tsamonte.service.idm.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.database.access.SessionRecords;
import tsamonte.service.idm.database.access.UserRecords;
import tsamonte.service.idm.database.model.UserModel;
import tsamonte.service.idm.endpoints.helpers.EmailPasswordHelper;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.models.request.LoginRegisterRequestModel;
import tsamonte.service.idm.models.response.BaseResponseModel;
import tsamonte.service.idm.models.response.LoginSessionResponseModel;
import tsamonte.service.idm.security.Session;

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
 * POST /api/idm/login
 *
 * Request Model:
 *      - email (string, required)
 *      - password (char[], required)
 *
 * Response Model:
 *      - resultCode (int)
 *      - message (string)
 *      - session_id (string, optional)
 */
@Path("login")
public class LoginEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginResponse(@Context HttpHeaders headers, String jsonText) {
        LoginRegisterRequestModel requestModel;
        LoginSessionResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, LoginRegisterRequestModel.class);
        }
        catch (IOException e) {
            e.printStackTrace();

            // resultCode = -3; 400 Bad request; "JSON Parse Exception."
            if(e instanceof JsonParseException) {
                responseModel = new LoginSessionResponseModel(Result.JSON_PARSE_EXCEPTION, null);
            }

            // resultCode = -2; 400 Bad request; "JSON Mapping Exception."
            else if (e instanceof JsonMappingException) {
                responseModel = new LoginSessionResponseModel(Result.JSON_MAPPING_EXCEPTION, null);
            }

            // resultCode = -1; 500 Internal Server Error; "Internal server error."
            else {
                responseModel = new LoginSessionResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            }

            return responseModel.buildResponse();
        }

        ServiceLogger.LOGGER.info("Received login request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // When password is null or an empty array is passed
        // resultCode = -12; 400 Bad request; "Password has invalid length."
        if(requestModel.getPassword() == null || requestModel.getPassword().length == 0) {
            responseModel = new LoginSessionResponseModel(Result.PASSWORD_IS_EMPTY, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // When email is null or empty
        // resultCode = -10; 400 Bad request; "Email address has invalid length."
        if(requestModel.getEmail() == null || requestModel.getEmail().length() == 0) {
            responseModel = new LoginSessionResponseModel(Result.EMAIL_IS_EMPTY, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = -11; 400 Bad request; "Email address has invalid format."
        if(!EmailPasswordHelper.isValidEmailFormat(requestModel.getEmail())) {
            responseModel = new LoginSessionResponseModel(Result.EMAIL_INVALID_FORMAT, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // Search for user based on given email
        UserModel user = UserRecords.retrieve(requestModel.getEmail());

        // resultCode = 14; 200 OK; "User not found."
        if(user == null) {
            responseModel = new LoginSessionResponseModel(Result.USER_NOT_FOUND, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = 11; 200 OK; "Passwords do not match."
        if(!EmailPasswordHelper.passwordsMatch(user, requestModel.getPassword())) {
            responseModel = new LoginSessionResponseModel(Result.PASSWORDS_DO_NOT_MATCH, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = 120; 200 OK; "User logged in successfully."
        Session session = Session.createSession(requestModel.getEmail());

        SessionRecords.insert(session);

        responseModel = new LoginSessionResponseModel(Result.USER_LOGGED_IN_SUCCESSFULLY, null);
        ServiceLogger.LOGGER.info(responseModel.getMessage());
        return responseModel.buildResponse();
    }
}
