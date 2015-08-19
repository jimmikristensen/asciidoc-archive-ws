package dk.jimmikristensen.aaws.domain.github;


public enum CommitStatus {
    ADDED("added"), MODIFIED("modified"), RENAMED("renamed"), UNKNOWN("unknown");

    private final String status;

    private CommitStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return status;
    }

    public static CommitStatus fromString(String value) {
        for (CommitStatus os : CommitStatus.values()) {
            if (os.getType().equalsIgnoreCase(value)) {
                return os;
            }
        }
        return UNKNOWN;
    }
}
