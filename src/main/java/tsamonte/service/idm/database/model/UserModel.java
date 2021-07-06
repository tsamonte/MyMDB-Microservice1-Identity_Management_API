package tsamonte.service.idm.database.model;

/**
 * Class UserModel mimics the structure of the user table in the database.
 * This class will be used when retrieving entire rows from the database.
 */
public class UserModel {
    private Integer user_id;
    private String email;
    private int status;
    private int plevel;
    private String salt;
    private String pword;

    public UserModel (Integer user_id, String email, int status, int plevel, String salt, String pword) {
        this.user_id = user_id;
        this.email = email;
        this.status = status;
        this.plevel = plevel;
        this.salt = salt;
        this.pword = pword;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public int getPlevel() {
        return plevel;
    }

    public String getSalt() {
        return salt;
    }

    public String getPword() {
        return pword;
    }
}
