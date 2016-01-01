package nl.ulso.magisto.action;

import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ActionSetTest {

    @Test
    public void testAddActions() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopySourceAction(createPath("source1"));
        actions.addCopySourceAction(createPath("source2"));
        final List<Action> performed = performActions(actions);
        assertEquals(2, performed.size());
    }

    @Test
    public void testStaticCopyReplacesSourceDelete() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addDeleteTargetAction(createPath("file"));
        actions.addCopyStaticAction(createPath("file"), ".");
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.COPY_STATIC, performed.get(0).getActionType());
    }

    @Test
    public void testStaticSkipReplacesSourceDelete() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addSkipStaticAction(createPath("file"));
        actions.addDeleteTargetAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.SKIP_STATIC, performed.get(0).getActionType());
    }

    @Test
    public void testSourceCopyReplacesStaticCopy() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopyStaticAction(createPath("file"), ".");
        actions.addCopySourceAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.COPY_SOURCE, performed.get(0).getActionType());
    }

    @Test
    public void testSourceSkipReplacesStaticCopy() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addSkipSourceAction(createPath("file"));
        actions.addCopyStaticAction(createPath("file"), ".");
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.SKIP_SOURCE, performed.get(0).getActionType());
    }

    @Test(expected = IllegalStateException.class)
    public void testPerformedActionsCannotBePerformedAgain() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopySourceAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        performed.get(0).perform(null, null, null);
    }

    private List<Action> performActions(ActionSet actions) throws IOException {
        final List<Action> performed = new ArrayList<>();
        actions.performAll(new DummyFileSystem(), createPath("."), createPath("."), new ActionCallback() {
            @Override
            public void actionPerformed(Action action) {
                performed.add(action);
            }
        });
        return performed;
    }
}