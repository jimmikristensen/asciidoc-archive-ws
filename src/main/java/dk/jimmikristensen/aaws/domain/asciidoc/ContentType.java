package dk.jimmikristensen.aaws.domain.asciidoc;

public enum ContentType {
    HTML("HTML"), ASCIIDOC("ADOC"), UNKNOWN("UNKNOWN");

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
        return UNKNOWN;
    }
    
    public static String getValidTypes() {
        String typeStr = "";
        for (ContentType os : ContentType.values()) {
            if (os != UNKNOWN) {
                typeStr += os.getType()+" ";
            }
        }
        return typeStr;
    }
}
