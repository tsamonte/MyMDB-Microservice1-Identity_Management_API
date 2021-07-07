package tsamonte.service.idm.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import tsamonte.service.idm.base.PrivilegeLevel;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.base.UserStatus;
import tsamonte.service.idm.database.access.UserRecords;
import tsamonte.service.idm.database.model.UserModel;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.models.request.LoginRegisterRequestModel;
import tsamonte.service.idm.models.response.BaseResponseModel;
import tsamonte.service.idm.endpoints.helpers.EmailPasswordHelper;
import tsamonte.service.idm.security.Crypto;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * POST /api/idm/register
 *
 * Request Model:
 *      - email (string, required)
 *      - password (char[], required)
 *
 * Response Model:
 *      - resultCode (int)
 *      - message (string)
 */
@Path("register")
public class RegisterEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerResponse(@Context HttpHeaders headers, String jsonText) {
        LoginRegisterRequestModel requestModel;
        BaseResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, LoginRegisterRequestModel.class);
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

        ServiceLogger.LOGGER.info("Received register request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // When password is null or an empty array is passed
        // resultCode = -12; 400 Bad request; "Password has invalid length."
        if(requestModel.getPassword() == null || requestModel.getPassword().length == 0) {
            responseModel = new BaseResponseModel(Result.PASSWORD_IS_EMPTY);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // When email is null or empty
        // resultCode = -10; 400 Bad request; "Email address has invalid length."
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

        // resultCode = 12; 200 OK; "Password does not meet length requirements."
        if(!EmailPasswordHelper.isValidPasswordLength(requestModel.getPassword())) {
            responseModel = new BaseResponseModel(Result.PASSWORD_DOESNT_MEET_LENGTH_REQUIREMENT);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = 13; 200 OK; "Password does not meet character requirements."
        if(!EmailPasswordHelper.isValidPasswordContents(requestModel.getPassword())) {
            responseModel = new BaseResponseModel(Result.PASSWORD_DOESNT_MEET_CHAR_REQUIREMENT);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = 16; 200 OK; "Email already in use."
        if(UserRecords.userAlreadyExists(requestModel.getEmail())) {
            responseModel = new BaseResponseModel(Result.EMAIL_ALREADY_EXISTS);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = 110; 200 OK; "User registered successfully."

        // --------------------Salt & hash password----------------------
        byte[] salt = Crypto.genSalt();

        // Use salt to hash password
        char[] password = requestModel.getPassword();
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        // Encode salt & password; these values will be stored in db
        String encodedSalt = Hex.encodeHexString(salt);
        String encodedPassword = Hex.encodeHexString(hashedPassword);

        // --------------------------------------------------------------

        // Insert new user into user table of database
        UserModel newUser = new UserModel(null, requestModel.getEmail(), UserStatus.ACTIVE.getStatusID(), PrivilegeLevel.USER.getPlevel(), encodedSalt, encodedPassword);
        UserRecords.insert(newUser);
        responseModel = new BaseResponseModel(Result.USER_REGISTERED_SUCCESSFULLY);
        ServiceLogger.LOGGER.info(responseModel.getMessage());
        return responseModel.buildResponse();
    }
}
