package nl.ulso.magisto;

import nl.ulso.magisto.action.DummyActionFactory;
import nl.ulso.magisto.converter.DummyFileConverterFactory;
import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LauncherTest {

    @Rule
    public final ExpectedSystemExit systemExit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule errorLog = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule outputLog = new SystemOutRule().enableLog();

    @Before
    public void setUp() throws Exception {
        DummyLogHandler.install();
    }

    @After
    public void tearDown() throws Exception {
        DummyLogHandler.uninstall();
    }

    @Test
    public void testNoProgramArguments() throws Exception {
        systemExit.expectSystemExitWithStatus(-1);
        Launcher.setDummyMagistoForTesting(new DummyMagisto());
        Launcher.main(new String[]{});
        assertThat(errorLog.getLog(), containsString("invalid arguments"));
        assertThat(outputLog.getLog(), is(""));
    }

    @Test
    public void testValidProgramArguments() throws Exception {
        Launcher.setDummyMagistoForTesting(new DummyMagisto());
        Launcher.main(new String[]{"-t", "foo"});
        assertThat(errorLog.getLog(), is(""));
        assertThat(outputLog.getLog(), is(""));
        assertThat(DummyLogHandler.getLog(), containsString("Done!"));
    }

    @Test
    public void testIOExceptionDuringRun() throws Exception {
        systemExit.expectSystemExitWithStatus(-1);
        Launcher.setDummyMagistoForTesting(new DummyMagistoWithIOException());
        Launcher.main(new String[]{"-t", "foo"});
        assertThat(errorLog.getLog(), containsString("Oops!"));
        assertThat(errorLog.getLog(), containsString("--expected--"));
        assertThat(outputLog.getLog(), is(""));
    }

    @Test
    public void testSourceDirectoryNotDefined() throws Exception {
        final Options options = Launcher.parseProgramOptions(new String[]{"-t", "foo"});
        final String sourceDirectory = Launcher.resolveSourceDirectory(options);
        assertThat(sourceDirectory, is(System.getProperty("user.dir")));
    }

    @Test
    public void testSourceDirectoryDefined() throws Exception {
        final Options options = Launcher.parseProgramOptions(new String[]{"-s", "bar", "-t", "foo"});
        final String sourceDirectory = Launcher.resolveSourceDirectory(options);
        assertThat(sourceDirectory, is("bar"));
    }

    @Test
    public void testDefaultMagistoCreation() throws Exception {
        Launcher.setDummyMagistoForTesting(null);
        final Magisto magisto = Launcher.createMagisto(false, null);
        assertNotNull(magisto);
    }

    @Test
    public void testNormalLogging() throws Exception {
        Launcher.configureLoggingSystem(false);
        Logger.getGlobal().log(Level.FINE, "FINE");
        Logger.getGlobal().log(Level.INFO, "INFO");
        final String log = DummyLogHandler.getLog();
        assertThat(log, not(containsString("FINE")));
        assertThat(log, containsString("INFO"));
    }

    @Test
    public void testVerboseLogging() throws Exception {
        Launcher.configureLoggingSystem(true);
        Logger.getGlobal().log(Level.FINE, "FINE");
        Logger.getGlobal().log(Level.INFO, "INFO");
        final String log = DummyLogHandler.getLog();
        assertThat(log, containsString("FINE"));
        assertThat(log, containsString("INFO"));
    }

    private static final class DummyMagisto extends Magisto {
        private DummyMagisto() {
            super(false, new DummyFileSystem(), new DummyActionFactory(), new DummyFileConverterFactory());
        }

        @Override
        public Statistics run(String sourceDirectory, String targetDirectory) throws IOException {
            return new Statistics().begin().end();
        }
    }

    private static final class DummyMagistoWithIOException extends Magisto {
        private DummyMagistoWithIOException() {
            super(false, new DummyFileSystem(), new DummyActionFactory(), new DummyFileConverterFactory());
        }

        @Override
        public Statistics run(String sourceDirectory, String targetDirectory) throws IOException {
            throw new IOException("--expected--");
        }
    }
}