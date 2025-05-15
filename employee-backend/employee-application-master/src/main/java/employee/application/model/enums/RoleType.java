package employee.application.model.enums;

public enum RoleType {
    ADMIN("admin"),
    EMPLOYEE("employee"),
    HR("hr"),;

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
;
}
