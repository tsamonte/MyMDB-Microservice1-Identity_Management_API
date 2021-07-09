package tsamonte.service.idm.database.access;

import tsamonte.service.idm.IDMService;
import tsamonte.service.idm.logger.ServiceLogger;
import tsamonte.service.idm.security.Session;
import tsamonte.service.idm.security.Token;

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
    private static boolean sessionExistsForUser(String email) {
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
            ps.setInt(3, session.getStatus());
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

    public static Session retrieve(String email, String sessionID) {
        try {
            Session result = null;

            String query = "SELECT *" +
                    " FROM session" +
                    " WHERE email = ? AND session_id = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, sessionID);

            ResultSet rs = ps.executeQuery();

            // Since only one value is expected, only need to call rs.next() once
            while(rs.next()) {
                result = Session.rebuildSession(rs.getString("email"),
                        Token.rebuildToken(rs.getString("session_id")),
                        rs.getTimestamp("time_created"),
                        rs.getTimestamp("last_used"),
                        rs.getTimestamp("expr_time"),
                        rs.getInt("status")
                );
            }
            return result;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int update(Session session) {
        try {
            // When updating a particular session, the email, session_id, and time_created will never change
            String update = "UPDATE session" +
                    " SET status = ?, last_used = ?, expr_time = ?" +
                    " WHERE email = ? AND session_id = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(update);

            ps.setInt(1, session.getStatus());
            ps.setTimestamp(2, session.getLastUsed());
            ps.setTimestamp(3, session.getExprTime());
            ps.setString(4, session.getEmail());
            ps.setString(5, session.getSessionID().toString());

            ServiceLogger.LOGGER.info("Trying update: " + ps.toString());
            int affectedRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update succeeded.");

            return affectedRows;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Update failed: Unable to update session table.");
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
