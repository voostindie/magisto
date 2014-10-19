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

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.ValidationFailure;
import nl.ulso.magisto.action.RealActionFactory;
import nl.ulso.magisto.io.RealFileSystemAccessor;

import java.io.IOException;

/**
 * Launches the Magisto application.
 * <p>
 * This is the only place where:
 * <ul>
 * <li>Program arguments are parsed</li>
 * <li>Output is generated directly to System.out</li>
 * <li>Output is generated directly to System.err</li>
 * <li>The {@link Magisto} class is instantiated/configured and run</li>
 * </ul>
 * </p>
 */
public class Launcher {

    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    private static Magisto DUMMY_MAGISTO = null; // For testing

    public static void main(String[] arguments) {
        try {
            final Options options = parseProgramOptions(arguments);
            final Magisto magisto = createMagisto();
            run(magisto, resolveSourceDirectory(options), options.getTargetDirectory());
        } catch (RuntimeException e) {
            System.exit(-1);
        }
    }

    static Options parseProgramOptions(String[] arguments) {
        try {
            return CliFactory.parseArguments(Options.class, arguments);
        } catch (ArgumentValidationException e) {
            System.err.println("You gave me one or more invalid arguments: ");
            for (ValidationFailure failure : e.getValidationFailures()) {
                System.err.println(failure);
            }
            throw new RuntimeException();
        }
    }

    static Magisto createMagisto() {
        if (DUMMY_MAGISTO != null) {
            return DUMMY_MAGISTO;
        }
        return new Magisto(new RealFileSystemAccessor(), new RealActionFactory());
    }

    private static void run(Magisto magisto, String sourceDirectory, String targetDirectory) {
        final Statistics statistics;
        try {
            statistics = magisto.run(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            System.err.println("Oops! An IO exception occurred...");
            System.err.println();
            System.err.println("Deliver the following to your friendly neighbourhood geek to help you out:");
            System.err.println();
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
        statistics.print(System.out);
    }

    static String resolveSourceDirectory(Options options) {
        final String sourceOption = options.getSourceDirectory();
        if (sourceOption != null) {
            return sourceOption;
        }
        return WORKING_DIRECTORY;
    }

    static void setDummyMagistoForTesting(Magisto dummyMagisto) {
        DUMMY_MAGISTO = dummyMagisto;
    }
}
