package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.DummyFileConverter;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;

public class RealActionFactoryTest {

    private final ActionFactory factory = new RealActionFactory();

    @Test
    public void testSkipSourceAction() throws Exception {
        assertNotNull(factory.skipSource(createPath("skip")));
    }

    @Test
    public void testSkipStaticAction() throws Exception {
        assertNotNull(factory.skipStatic(createPath("skip")));
    }

    @Test
    public void testCopySourceAction() throws Exception {
        assertNotNull(factory.copySource(createPath("copy")));
    }

    @Test
    public void testCopyStaticAction() throws Exception {
        assertNotNull(factory.copyStatic(createPath("copy"), "."));
    }

    @Test
    public void testConvertAction() throws Exception {
        assertNotNull(factory.convertSource(createPath("convert"), new DummyFileConverter()));
    }

    @Test
    public void testDeleteTargetAction() throws Exception {
        assertNotNull(factory.deleteTarget(createPath("delete")));
    }

}