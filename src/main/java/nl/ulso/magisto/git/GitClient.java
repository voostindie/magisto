package nl.ulso.magisto.git;

import java.nio.file.Path;

public interface GitClient {

    History getHistory(Path path);
}
