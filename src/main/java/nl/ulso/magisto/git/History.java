package nl.ulso.magisto.git;

import java.io.IOException;
import java.util.List;

public interface History {

    public List<Commit> getCommits() throws IOException;

    public Commit getLastCommit() throws IOException;

}
