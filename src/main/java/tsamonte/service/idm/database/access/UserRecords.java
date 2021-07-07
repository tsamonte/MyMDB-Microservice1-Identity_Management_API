package tsamonte.service.idm.database.access;

import tsamonte.service.idm.IDMService;
import tsamonte.service.idm.database.model.UserModel;
import tsamonte.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserRecords {
    /**
     * Checks if a user with the specified email already exists
     *
     * @param email The email to be tested
     * @return true if email is found in User table; false if not
     */
    public static boolean userExists(String email) {
        try {
            String query = "SELECT *" +
                    " FROM user" +
                    " WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) return true;
            return false;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: unable to retrieve user records.");
            e.printStackTrace();
            return false;
        }
    }

    public static int insert(UserModel user) {
        try {
            String insertStatement = "INSERT INTO user" +
                    " (email, status, plevel, salt, pword)" +
                    " VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = IDMService.getCon().prepareStatement(insertStatement);
            ps.setString(1, user.getEmail());
            ps.setInt(2,user.getStatus());
            ps.setInt(3, user.getPlevel());
            ps.setString(4, user.getSalt());
            ps.setString(5, user.getPword());

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");

            return affectedRows; // >= 1; insert successful
        }
        catch (SQLIntegrityConstraintViolationException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Duplicate insertion attempted");
            e.printStackTrace();
            return -2; // duplicate insertion
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert user records");
            e.printStackTrace();
            return -1; // insert not successful
        }
    }

    /**
     * Retrieve an entire row from user table based on email.
     * Since the email is the only unique value known by the user, only retrieval by email is allowed.
     *
     * @param email Value to search on in database
     * @return UserModel object that mimics structure of User table in db; returns null if user is not found
     */
    public static UserModel retrieve(String email) {
        try {
            UserModel result = null;

            String query = "SELECT *" +
                    " FROM user" +
                    " WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                result = new UserModel(rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getInt("status"),
                        rs.getInt("plevel"),
                        rs.getString("salt"),
                        rs.getString("pword"));
            }

            return result;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: unable to retrieve user records.");
            e.printStackTrace();
            return null;
        }
    }
}
