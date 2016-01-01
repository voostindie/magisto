package nl.ulso.magisto.action;

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ActionComparatorTest {

    private final ActionComparator comparator = new ActionComparator();

    @Test
    public void testSkipBeforeDelete() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final DeleteTargetAction delete = new DeleteTargetAction(createPath("a"));
        assertEquals(-1, comparator.compare(skip, delete));
        assertEquals(1, comparator.compare(delete, skip));
    }

    @Test
    public void testSkipBeforeCopy() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final CopySourceAction copy = new CopySourceAction(createPath("a"));
        assertEquals(-1, comparator.compare(skip, copy));
        assertEquals(1, comparator.compare(copy, skip));
    }

    @Test
    public void testSkipBeforeConvert() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final ConvertSourceAction convert = new ConvertSourceAction(createPath("a"), null);
        assertEquals(-1, comparator.compare(skip, convert));
        assertEquals(1, comparator.compare(convert, skip));
    }

    @Test
    public void testDeleteBeforeCopy() throws Exception {
        final DeleteTargetAction delete = new DeleteTargetAction(createPath("a"));
        final CopySourceAction copy = new CopySourceAction(createPath("a"));
        assertEquals(-1, comparator.compare(delete, copy));
        assertEquals(1, comparator.compare(copy, delete));
    }

    @Test
    public void testDeleteBeforeConvert() throws Exception {
        final DeleteTargetAction delete = new DeleteTargetAction(createPath("a"));
        final ConvertSourceAction convert = new ConvertSourceAction(createPath("a"), null);
        assertEquals(-1, comparator.compare(delete, convert));
        assertEquals(1, comparator.compare(convert, delete));
    }

    @Test
    public void testCopyBeforeConvert() throws Exception {
        final ConvertSourceAction convert = new ConvertSourceAction(createPath("a"), null);
        final CopySourceAction copy = new CopySourceAction(createPath("a"));
        assertEquals(-1, comparator.compare(copy, convert));
        assertEquals(1, comparator.compare(convert, copy));
    }

    @Test
    public void testSkipOrderedLexicographically() throws Exception {
        final SkipSourceAction skip1 = new SkipSourceAction(createPath("a"));
        final SkipSourceAction skip2 = new SkipSourceAction(createPath("b"));
        assertEquals(-1, comparator.compare(skip1, skip2));
        assertEquals(1, comparator.compare(skip2, skip1));
    }

    @Test
    public void testCopyOrderedLexicographically() throws Exception {
        final CopySourceAction copy1 = new CopySourceAction(createPath("a"));
        final CopySourceAction copy2 = new CopySourceAction(createPath("b"));
        assertEquals(-1, comparator.compare(copy1, copy2));
        assertEquals(1, comparator.compare(copy2, copy1));
    }

    @Test
    public void testDeleteOrderedLexicographicallyReversed() throws Exception {
        final DeleteTargetAction delete1 = new DeleteTargetAction(createPath("a"));
        final DeleteTargetAction delete2 = new DeleteTargetAction(createPath("b"));
        assertEquals(1, comparator.compare(delete1, delete2));
        assertEquals(-1, comparator.compare(delete2, delete1));
    }

    @Test
    public void testConvertOrderedLexicographically() throws Exception {
        final ConvertSourceAction convert1 = new ConvertSourceAction(createPath("a"), null);
        final ConvertSourceAction convert2 = new ConvertSourceAction(createPath("b"), null);
        assertEquals(-1, comparator.compare(convert1, convert2));
        assertEquals(1, comparator.compare(convert2, convert1));
    }
}