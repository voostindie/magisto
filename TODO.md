# TO DO

## Basic set

* Use a custom template if available
* Copy over the static content, if any.

## Improvements

In arbitrary order, all not yet clear:

* Better built-in template
* More data in the model, e.g.
    * Source
        * Reference to the Git remote, with branch and hash
    * Table of contents
        * List of all headers in the Markdown file
    * Site structure
        * Links to siblings, parents and children
* Built-in macro's and/or directives for the FreeMarker template
* Configurable prefix for Magisto files (e.g. "_" instead of ".")
* Implement Magisto as a Maven plugin that can run without pom.xml
