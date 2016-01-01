package nl.ulso.magisto;

import nl.ulso.magisto.action.ActionCategory;
import nl.ulso.magisto.action.ActionType;
import nl.ulso.magisto.action.DummyAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StatisticsTest {

    @Before
    public void setUp() throws Exception {
        DummyLogHandler.install();
    }

    @After
    public void tearDown() throws Exception {
        DummyLogHandler.uninstall();
    }

    @Test(expected = IllegalStateException.class)
    public void testBeginMayBeCalledOnlyOnce() throws Exception {
        new Statistics().begin().begin();
    }

    @Test(expected = IllegalStateException.class)
    public void testEndCannotBeCalledWithoutBegin() throws Exception {
        new Statistics().end();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrintCannotBeCalledWithoutEnd() throws Exception {
        new Statistics().begin().log();
    }

    @Test(expected = IllegalStateException.class)
    public void testActionPerformedCannotBeCalledBeforeStart() throws Exception {
        new Statistics().registerActionPerformed(createAction(SOURCE, COPY_SOURCE));
    }

    @Test(expected = IllegalStateException.class)
    public void testActionPerformedCannotBeCalledAfterEnd() throws Exception {
        new Statistics().begin().end().registerActionPerformed(createAction(SOURCE, COPY_SOURCE));
    }

    @Test
    public void testSuccessfulRun() throws Exception {
        new Statistics()
                .begin()
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .registerActionPerformed(createAction(SOURCE, DELETE_TARGET))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(SOURCE, SKIP_SOURCE))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(SOURCE, CONVERT_SOURCE))
                .registerActionPerformed(createAction(SOURCE, CONVERT_SOURCE))
                .registerActionPerformed(createAction(SOURCE, SKIP_SOURCE))
                .registerActionPerformed(createAction(STATIC, SKIP_STATIC))
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .end()
                .log();
        String log = DummyLogHandler.getLog();
        assertThat(log, containsString("Done!"));
        assertThat(log, containsString("Copied 3 source"));
        assertThat(log, containsString("Copied 4 static"));
        assertThat(log, containsString("Converted 2 source"));
        assertThat(log, containsString("Deleted 1 target"));
        assertThat(log, containsString("Skipped 2 source"));
        assertThat(log, containsString("Skipped 1 static"));
    }

    private DummyAction createAction(ActionCategory category, ActionType type) {
        return new DummyAction(null, null, category, type);
    }

}