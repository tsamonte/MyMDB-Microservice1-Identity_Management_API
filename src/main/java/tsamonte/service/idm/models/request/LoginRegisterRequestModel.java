package tsamonte.service.idm.models.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The class LoginRegisterRequestModel will be utilized by the following endpoints:
 *      - /api/idm/login
 *      - /api/idm/register
 *
 * Request Model:
 *      - email (string, required)
 *      - password (char[], required)
 */
public class LoginRegisterRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "password", required = true)
    private char[] password;

    @JsonCreator
    public LoginRegisterRequestModel(@JsonProperty(value = "email", required = true) String email,
                                     @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }

    @JsonProperty(value = "email", required = true)
    public String getEmail() { return email; }

    @JsonProperty(value = "password", required = true)
    public char[] getPassword() { return password; }
}
