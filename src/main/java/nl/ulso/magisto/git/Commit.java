package nl.ulso.magisto.git;

import java.util.Date;

/**
 * Exposes information on a single commit; it's passed in the page model for each file.
 */
public class Commit {
    static final Commit DEFAULT_COMMIT = new Commit("UNKNOWN", new Date(0), "UNKNOWN", "UNKNOWN", "-", "-");

    private final String id;
    private final Date timestamp;
    private final String committer;
    private final String emailAddress;
    private final String shortMessage;
    private final String fullMessage;


    public Commit(String id, Date timestamp, String committer, String emailAddress, String shortMessage,
                  String fullMessage) {
        this.id = id;
        this.timestamp = timestamp;
        this.committer = committer;
        this.emailAddress = emailAddress;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    public String getId() {
        return id;
    }

    public String getShortId() {
        return id.substring(0, 7);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCommitter() {
        return committer;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }
}
