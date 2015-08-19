package dk.jimmikristensen.aaws.domain.asciidoc;

public enum ContentType {
    HTML("HTML"), ASCIIDOC("ADOC");

    private final String status;

    private ContentType(String status) {
        this.status = status;
    }

    public String getType() {
        return status;
    }

    public static ContentType fromString(String value) {
        for (ContentType os : ContentType.values()) {
            if (os.getType().equalsIgnoreCase(value)) {
                return os;
            }
        }
        return null;
    }
}
