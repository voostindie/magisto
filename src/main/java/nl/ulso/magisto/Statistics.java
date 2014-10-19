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

import nl.ulso.magisto.action.Action;
import nl.ulso.magisto.action.ActionType;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps tracks of what Magisto is doing.
 */
public class Statistics {

    private final Map<ActionType, Integer> actionsPerformed = new HashMap<>();
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

    public Statistics registerActionPerformed(Action action) {
        if (start == -1 || end != -1) {
            throw new IllegalStateException("registerActionPerformed() must be called after begin() and before end()!");
        }
        final ActionType actionType = action.getActionType();
        if (!actionsPerformed.containsKey(actionType)) {
            actionsPerformed.put(actionType, 1);
        } else {
            final Integer count = actionsPerformed.get(actionType);
            actionsPerformed.put(actionType, count + 1);
        }
        return this;
    }

    public void print(PrintStream out) {
        if (end == -1) {
            throw new IllegalStateException("end() must be called before print() is called!");
        }
        final long duration = end - start;
        out.println("Done! This run took me about " + duration + " milliseconds. Here's what I did:");
        for (Map.Entry<ActionType, Integer> entry : actionsPerformed.entrySet()) {
            out.format("- %s %d files", entry.getKey().getPastTenseVerb(), entry.getValue());
            out.println();
        }
    }

    public int countFor(ActionType actionType) {
        if (actionsPerformed.containsKey(actionType)) {
            return actionsPerformed.get(actionType);
        }
        return 0;
    }
}
