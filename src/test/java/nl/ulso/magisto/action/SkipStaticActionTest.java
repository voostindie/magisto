package nl.ulso.magisto.action;

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class SkipStaticActionTest {

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.SKIP_STATIC, new SkipStaticAction(createPath("skip")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.STATIC, new SkipStaticAction(createPath("skip")).getActionCategory());
    }
}