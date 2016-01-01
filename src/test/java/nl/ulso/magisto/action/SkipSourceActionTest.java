package nl.ulso.magisto.action;

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class SkipSourceActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.SKIP_SOURCE, new SkipSourceAction(createPath("skip")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE, new SkipSourceAction(createPath("skip")).getActionCategory());
    }
}