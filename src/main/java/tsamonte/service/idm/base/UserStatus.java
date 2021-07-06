package tsamonte.service.idm.base;

public enum UserStatus {
    ACTIVE (1),
    CLOSED (2),
    LOCKED (3),
    REVOKED (4);

    private final int statusID;

    UserStatus (int statusID) { this.statusID = statusID; }

    public int getStatusID() { return statusID; }
}
