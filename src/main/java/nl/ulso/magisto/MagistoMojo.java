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

import nl.ulso.magisto.action.RealActionFactory;
import nl.ulso.magisto.converter.RealFileConverterFactory;
import nl.ulso.magisto.io.RealFileSystemAccessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Runs Magisto as a Maven plugin.
 */
@Mojo(name = "export", requiresProject = false)
public class MagistoMojo extends AbstractMojo {

    @Parameter(property = "source", defaultValue = ".")
    private String sourceDirectory;

    @Parameter(property = "target", required = true)
    private String targetDirectory;

    @Parameter(property = "force", defaultValue = "false")
    private boolean forceOverwrite;

    @Override

    public void execute() throws MojoExecutionException, MojoFailureException {
        final Magisto magisto = new Magisto(forceOverwrite, new RealFileSystemAccessor(), new RealActionFactory(),
                new RealFileConverterFactory());
        try {
            magisto.run(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            throw new MojoFailureException("IOException occurred", e);
        }
    }
}
