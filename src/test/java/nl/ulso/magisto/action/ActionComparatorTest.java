/*
 * Copyright 2014 Vincent Oostindie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.magisto.action;

import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ActionComparatorTest {

    private final ActionComparator comparator = new ActionComparator();

    @Test
    public void testSkipBeforeDelete() throws Exception {
        final SkipAction skip = new SkipAction(relativePath("a"));
        final DeleteAction delete = new DeleteAction(relativePath("a"));
        assertEquals(-1, comparator.compare(skip, delete));
        assertEquals(1, comparator.compare(delete, skip));
    }

    @Test
    public void testSkipBeforeCopy() throws Exception {
        final SkipAction skip = new SkipAction(relativePath("a"));
        final CopyAction copy = new CopyAction(relativePath("a"));
        assertEquals(-1, comparator.compare(skip, copy));
        assertEquals(1, comparator.compare(copy, skip));
    }

    @Test
    public void testSkipBeforeConvert() throws Exception {
        final SkipAction skip = new SkipAction(relativePath("a"));
        final ConvertAction convert = new ConvertAction(relativePath("a"));
        assertEquals(-1, comparator.compare(skip, convert));
        assertEquals(1, comparator.compare(convert, skip));
    }

    @Test
    public void testDeleteBeforeCopy() throws Exception {
        final DeleteAction delete = new DeleteAction(relativePath("a"));
        final CopyAction copy = new CopyAction(relativePath("a"));
        assertEquals(-1, comparator.compare(delete, copy));
        assertEquals(1, comparator.compare(copy, delete));
    }

    @Test
    public void testDeleteBeforeConvert() throws Exception {
        final DeleteAction delete = new DeleteAction(relativePath("a"));
        final ConvertAction convert = new ConvertAction(relativePath("a"));
        assertEquals(-1, comparator.compare(delete, convert));
        assertEquals(1, comparator.compare(convert, delete));
    }

    @Test
    public void testConvertBeforeCopy() throws Exception {
        final ConvertAction convert = new ConvertAction(relativePath("a"));
        final CopyAction copy = new CopyAction(relativePath("a"));
        assertEquals(-1, comparator.compare(convert, copy));
        assertEquals(1, comparator.compare(copy, convert));
    }

    @Test
    public void testSkipOrderedLexicographically() throws Exception {
        final SkipAction skip1 = new SkipAction(relativePath("a"));
        final SkipAction skip2 = new SkipAction(relativePath("b"));
        assertEquals(-1, comparator.compare(skip1, skip2));
        assertEquals(1, comparator.compare(skip2, skip1));
    }

    @Test
    public void testCopyOrderedLexicographically() throws Exception {
        final CopyAction copy1 = new CopyAction(relativePath("a"));
        final CopyAction copy2 = new CopyAction(relativePath("b"));
        assertEquals(-1, comparator.compare(copy1, copy2));
        assertEquals(1, comparator.compare(copy2, copy1));
    }

    @Test
    public void testDeleteOrderedLexicographicallyReversed() throws Exception {
        final DeleteAction delete1 = new DeleteAction(relativePath("a"));
        final DeleteAction delete2 = new DeleteAction(relativePath("b"));
        assertEquals(1, comparator.compare(delete1, delete2));
        assertEquals(-1, comparator.compare(delete2, delete1));
    }

    @Test
    public void testConvertOrderedLexicographically() throws Exception {
        final ConvertAction convert1 = new ConvertAction(relativePath("a"));
        final ConvertAction convert2 = new ConvertAction(relativePath("b"));
        assertEquals(-1, comparator.compare(convert1, convert2));
        assertEquals(1, comparator.compare(convert2, convert1));
    }

    private Path relativePath(String fileName) {
        return FileSystems.getDefault().getPath(fileName);
    }
}