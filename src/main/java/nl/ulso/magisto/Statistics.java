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

import java.io.PrintStream;

/**
 * Keeps tracks of what Magisto is doing.
 */
public class Statistics {

    private long start = -1;
    private long end = -1;

    public Statistics begin() {
        if (start != -1) {
            throw new IllegalStateException("begin() may be called only once!");
        }
        start = System.currentTimeMillis();
        return this;
    }

    public Statistics end() {
        if (end != -1) {
            throw new IllegalStateException("end() may be called only once!");
        }
        if (start == -1) {
            throw new IllegalStateException("begin() must be called before end() is called!");
        }
        end = System.currentTimeMillis();
        return this;
    }

    public void print(PrintStream out) {
        if (end == -1) {
            throw new IllegalStateException("end() must be called before print() is called!");
        }
        final long duration = end - start;
        out.println("Done! This run took me about " + duration + " milliseconds. Here's what I did:");
        // TODO: keep track of and print the amount of files matched, processed, copied, and so on.
    }
}
