package tsamonte.service.idm.base;

public enum PrivilegeLevel {
    ROOT (1),
    ADMIN (2),
    EMPLOYEE (3),
    SERVICE (4),
    USER (5);

    private final int plevel;

    PrivilegeLevel (int plevel) { this.plevel = plevel; }

    public int getPlevel() { return this.plevel; }
}
