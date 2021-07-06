package tsamonte.service.idm.models.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tsamonte.service.idm.base.Result;
import tsamonte.service.idm.models.response.BaseResponseModel;

/**
 * The class LoginSessionResponseModel will be utilized by the following endpoints:
 *      - /api/idm/login
 *      - /api/idm/session
 *
 * Response Model:
 *      - resultCode (int, required)
 *      - message (string, required)
 *      - session_id (string, optional) // optional because if session is no longer valid, session_id won't be returned
 */
public class LoginSessionResponseModel extends BaseResponseModel {
    @JsonProperty(value = "session_id")
    private String session_id;

    @JsonCreator
    public LoginSessionResponseModel(Result result,
                                     @JsonProperty(value = "session_id") String session_id) {
        super(result);
        this.session_id = session_id;
    }

    @JsonProperty(value = "session_id")
    public String getSessionID() { return session_id; }
}
