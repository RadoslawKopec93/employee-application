package employee.application.model.enums;

public enum ProviderType {
    GITHUB("github");

    private final String value;

    ProviderType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
