package tsamonte.service.idm.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.database.access.SessionRecords;
import tsamonte.service.idm.database.access.UserRecords;
import tsamonte.service.idm.endpoints.helpers.EmailPasswordHelper;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.models.request.SessionRequestModel;
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
 * POST /api/idm/session
 *
 * Request Model:
 *      - email (string, required)
 *      - session_id (string, required)
 *
 * Response Model:
 *      - resultCode (int)
 *      - message (string)
 *      - session_id (string, optional)
 */
@Path("session")
public class SessionEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sessionResponse(@Context HttpHeaders headers, String jsonText) {
        SessionRequestModel requestModel;
        LoginSessionResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, SessionRequestModel.class);
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

        // resultCode = -13; 400 Bad request; "Token has invalid length."
        if(requestModel.getSessionID() == null || requestModel.getSessionID().length() != 128) {
            responseModel = new LoginSessionResponseModel(Result.TOKEN_INVALID_LENGTH, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        // resultCode = -10; 400 Bad request; "Email address is empty."
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

        // resultCode = 14; 200 OK; "User not found."
        if(!UserRecords.userExists(requestModel.getEmail())) {
            responseModel = new LoginSessionResponseModel(Result.USER_NOT_FOUND, null);
            ServiceLogger.LOGGER.warning(responseModel.getMessage());
            return responseModel.buildResponse();
        }

        Session retrievedSession = SessionRecords.retrieve(requestModel.getEmail());

        // Note: if the status for retrievedSession is "active", we should check if it's expired before proceeding
        if(retrievedSession.getStatus() == Session.ACTIVE) {
            // resultCode = 130; 200 OK; "Session is active."
            if (retrievedSession.isDataValid()) {
                responseModel = new LoginSessionResponseModel(Result.SESSION_ACTIVE, retrievedSession.getSessionID().toString());

                // when the session is still active, we update the last_used timestamp in the db
                retrievedSession.update();
            }
            // resultCode = 131; 200 OK; "Session is expired."
            else {
                responseModel = new LoginSessionResponseModel(Result.SESSION_EXPIRED, retrievedSession.getSessionID().toString());

                // when the session is expired, we update that status in the db
                retrievedSession.setStatus(Session.EXPIRED);
            }

            SessionRecords.update(retrievedSession);
        }

        // Note, session could still be retrieved as expired, so have a separate check for that too
        // resultCode = 131; 200 OK; "Session is expired."
        else if (retrievedSession.getStatus() == Session.EXPIRED) {
            responseModel = new LoginSessionResponseModel(Result.SESSION_EXPIRED, retrievedSession.getSessionID().toString());
        }

        // resultCode = 132; 200 OK; "Session is closed."
        else if (retrievedSession.getStatus() == Session.CLOSED) {
            responseModel = new LoginSessionResponseModel(Result.SESSION_CLOSED, retrievedSession.getSessionID().toString());
        }

        // resultCode = 133; 200 OK; "Session is revoked."
        else if (retrievedSession.getStatus() == Session.REVOKED) {
            responseModel = new LoginSessionResponseModel(Result.SESSION_REVOKED, retrievedSession.getSessionID().toString());
        }
        // resultCode = 134; 200 OK; "Session not found."
        else {
            responseModel = new LoginSessionResponseModel(Result.SESSION_NOT_FOUND, retrievedSession.getSessionID().toString());
        }

        ServiceLogger.LOGGER.warning(responseModel.getMessage());
        return responseModel.buildResponse();
    }
}
