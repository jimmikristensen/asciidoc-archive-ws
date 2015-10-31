package dk.jimmikristensen.aaws.domain.asciidoc;

public enum DocType {
    HTML("HTML"), ASCIIDOC("ADOC"), UNKNOWN("UNKNOWN");

    private final String status;

    private DocType(String status) {
        this.status = status;
    }

    public String getType() {
        return status;
    }

    public static DocType fromString(String value) {
        for (DocType os : DocType.values()) {
            if (os.getType().equalsIgnoreCase(value)) {
                return os;
            }
        }
        return UNKNOWN;
    }
    
    public static String getValidTypes() {
        String typeStr = "";
        for (DocType os : DocType.values()) {
            if (os != UNKNOWN) {
                typeStr += os.getType()+" ";
            }
        }
        return typeStr;
    }
}
