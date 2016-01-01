package nl.ulso.magisto;

import com.lexicalscope.jewel.cli.Option;

/**
 * Represents the command-line options one can pass
 */
interface Options {

    @Option(shortName = "s", longName = "source", description = "Source directory, defaults to the current directory",
            defaultToNull = true)
    String getSourceDirectory();

    @Option(shortName = "t", longName = "target", description = "Target directory")
    String getTargetDirectory();

    @Option(shortName = "f", longName = "force", description = "Forces overwriting of files that would be skipped otherwise")
    boolean isForceOverwrite();

    @Option(shortName = "v", longName = "verbose", description = "Use verbose logging")
    boolean isVerbose();

    @Option(shortName = "h", longName = "help", description = "Show this help text", helpRequest = true)
    boolean isHelp();
}
