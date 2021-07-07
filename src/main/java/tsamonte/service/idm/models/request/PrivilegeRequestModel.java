package tsamonte.service.idm.models.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The class PrivilegeRequestModel will be utilized by the following endpoints:
 *      - /api/idm/privilege
 *
 * Request Model:
 *      - email (string, required)
 *      - plevel (int, required)
 */
public class PrivilegeRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "plevel", required = true)
    private int plevel;

    @JsonCreator
    public PrivilegeRequestModel(@JsonProperty(value = "email", required = true) String email,
                               @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @JsonProperty(value = "email", required = true)
    public String getEmail() { return email; }

    @JsonProperty(value = "plevel", required = true)
    public int getPLevel() { return plevel; }
}
