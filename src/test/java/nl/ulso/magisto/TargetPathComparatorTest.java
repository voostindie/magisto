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

package nl.ulso.magisto;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Comparator;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertTrue;

public class TargetPathComparatorTest {

    private Comparator<Path> comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new TargetPathComparator("html");
    }

    @Test
    public void testUnrelatedPathsAreSortedInOrder() throws Exception {
        final Path path1 = createPath("src", "main", "java", "Main.java");
        final Path path2 = createPath("src", "test", "java", "MainTest.java");
        assertTrue(comparator.compare(path1, path2) < 0);
    }

    @Test
    public void testConvertedPathBeforeOtherPaths1() throws Exception {
        final Path path1 = createPath("file.html");
        final Path path2 = createPath("file.exe");
        assertTrue(comparator.compare(path1, path2) < 0);
    }

    @Test
    public void testConvertedPathBeforeOtherPaths2() throws Exception {
        final Path path1 = createPath("file.jpg");
        final Path path2 = createPath("file.html");
        assertTrue(comparator.compare(path1, path2) > 0);
    }
}