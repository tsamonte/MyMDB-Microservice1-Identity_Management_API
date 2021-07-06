package tsamonte.service.idm.base;

public enum SessionStatus {
    ACTIVE (1),
    CLOSED (2),
    EXPIRED (3),
    REVOKED (4);

    private final int statusID;

    SessionStatus (int statusID) { this.statusID = statusID; }

    public int getStatusID () { return this.statusID; }
}
