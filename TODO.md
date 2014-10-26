# TO DO

## Basic set

* Check that the source directory is a Git clone.
* Expose Git information to the page template.
* Copy over the static content, if any.

## Improvements

In arbitrary order, all not yet clear:

* More data in the model, e.g.
    * Table of contents
        * List of all headers in the Markdown file
    * Site structure
        * Links to siblings, parents and children
* Built-in macro's and/or directives for the FreeMarker template
* Configurable prefix for Magisto files (e.g. "_" instead of ".")
* Implement Magisto as a Maven plugin that can run without pom.xml
