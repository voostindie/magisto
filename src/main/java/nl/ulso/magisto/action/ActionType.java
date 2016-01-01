package nl.ulso.magisto.action;

/**
 * Specifies the action type, used mostly for keeping and logging statistics.
 */
public enum ActionType {

    SKIP_SOURCE("Skipped", "source"),
    SKIP_STATIC("Skipped", "static"),
    COPY_SOURCE("Copied", "source"),
    COPY_STATIC("Copied", "static"),
    DELETE_TARGET("Deleted", "target"),
    CONVERT_SOURCE("Converted", "source");

    private final String pastTenseVerb;
    private final String fileType;

    private ActionType(String pastTenseVerb, String fileType) {
        this.pastTenseVerb = pastTenseVerb;
        this.fileType = fileType;
    }

    public String getPastTenseVerb() {
        return pastTenseVerb;
    }

    public String getFileType() {
        return fileType;
    }
}
