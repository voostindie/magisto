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

import nl.ulso.magisto.io.RealFileSystemAccessor;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

/**
 * Launches the Magisto application.
 * <p>
 * This is the only place where:
 * <ul>
 * <li>Program arguments are parsed</li>
 * <li>Output is generated directly to System.out</li>
 * <li>Output is generated directly to System.err</li>
 * </ul>
 * </p>
 */
public class Launcher {

    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    public static void main(String[] arguments) {
        if (arguments.length != 1) {
            System.err.println("Sorry, I can't run with these specific program arguments.");
            System.err.println("I expect exactly one: the directory to export to.");
            System.exit(-1);
        }
        final Magisto magisto = new Magisto(new RealFileSystemAccessor());
        run(magisto, WORKING_DIRECTORY, arguments[0]);
    }

    private static void run(Magisto magisto, String sourceDirectory, String targetDirectory) {
        final long start = currentTimeMillis();
        try {
            magisto.run(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            System.err.println("Oops! An IO exception occurred. This one: " + e.getMessage());
            System.exit(-1);
        } finally {
            final long end = currentTimeMillis();
            System.out.println("Done! This run took me about " + (end - start) + " milliseconds");
        }
    }
}
