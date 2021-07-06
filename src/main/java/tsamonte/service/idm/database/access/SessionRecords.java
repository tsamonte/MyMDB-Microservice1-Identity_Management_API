package tsamonte.service.idm.database.access;

import tsamonte.service.idm.IDMService;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.security.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionRecords {
    /**
     * Checks if a session already exists for the user. If it does, the session must be deleted before
     * inserting a new one
     *
     * @param email user used to query session
     * @return true if session already exists; false otherwise
     */
    public static boolean sessionExistsForUser(String email) {
        try {
            String query = "SELECT *" +
                    " FROM session" +
                    " WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            // Since only one value is expected, only need to call rs.next() once
            if(rs.next()) {
                ServiceLogger.LOGGER.info("Session with this user already exists. Remaking session.");
                return true;
            }
            return false;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int insert(Session session) {
        // if a session already exists for the user, we must delete it
        if(sessionExistsForUser(session.getEmail())) {
            delete(session);
        }

        // at this point, guaranteed that no session exists for the user yet
        try {
            String insert = "INSERT INTO session" +
                    " (session_id, email, status, time_created, last_used, expr_time)" +
                    " VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = IDMService.getCon().prepareStatement(insert);
            ps.setString(1, session.getSessionID().toString());
            ps.setString(2, session.getEmail());
            ps.setInt(3, Session.ACTIVE);
            ps.setTimestamp(4, session.getTimeCreated());
            ps.setTimestamp(5, session.getLastUsed());
            ps.setTimestamp(6, session.getExprTime());

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert succeeded.");

            return affectedRows;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert new user record.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int delete(Session session) {
        try {
            String delete = "DELETE FROM session" +
                    " WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(delete);
            ps.setString(1, session.getEmail());

            ServiceLogger.LOGGER.info("Trying delete: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete succeeded.");

            return affectedRows;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Delete failed: Unable to delete existing record");
            e.printStackTrace();
            return -1;
        }
    }
}
