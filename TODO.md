# TO DO

## Basic set

* Check working directory
* Check target directory
    * May not exist, be empty, or contain an empty `.magisto-export` file.
* Create a list of files to update
    * Get all files in the working directory
    * Get all files from the target directory
    * Compare the two lists
    * Create a command for each mismatch -> copy, convert, or delete
* Execute all commands
    * If a custom template is present, apply that one
    * Otherwise: apply built-in template
* Write a new `.magisto-export` file.
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
* ...
