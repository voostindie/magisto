# Magisto

## Introduction

Magisto is a *very* simple tool that runs against a Git clone and that exports all files in it to an HTML site. All Markdown files are converted to HTML using a single template in the process. All other files are kept intact.

It's as simple as that. No more. No less.

If your ambitions reach beyond these plain goals, then Magisto is not the tool for you.

## Background

At the company I work for there are a lot of Git repositories containing documentation in Markdown format. Although our Git repository manager ([Stash](http://www.atlassian.com/stash)) has a web-based viewer that nicely renders Markdown as HTML, that interface is too complex for most audiences. Most users want to view the documentation without distractions (files, directories, commits, branches, tags, diffs... WTF?!).

By running Magisto on those Git repositories and sticking the output on a web server, we get distraction-free documentation.

## Installation

Magisto requires Java 7 or later. It's a fat JAR, so once downloaded you can execute it directly, with `java -jar magisto-<VERSION>.jar`.

## Usage

Run Magisto with the `-h` or `--help` command line argument to get something like this:

```raw
The options available are:
	[--force -f] : Forces overwriting
	[--help -h]
	[--source -s value] : Source directory
	--target -t value : Target directory
```

The source directory is an optional argument and defaults to the current one.

The `-f` flag enforces that files that would normally be skipped are also processed. This can be useful in some edge cases, like when you've changed the custom page template. You shouldn't need it for day to day usage.

When executed with valid arguments, Magisto:

* (*Not yet*) checks that the source directory is indeed a Git clone.
* Checks that the target directory either doesn't exist, is empty, or contains an earlier Magisto export.
* Goes through all files in the source directory recursively, copying them to the target directory, transforming all Markdown files to HTML files in the process. Where:
    * Files that haven't changed since the last run aren't touched.
    * Files that are no longer in the source directory are removed from the target directory.

That's about it. Excepting some add-ons. See below.

## Configuration

You can configure Magisto in two ways:

1. By supplying a custom page template.
2. By adding additional static content to the output.

### Custom template

To transform Markdown to HTML, Magisto uses a FreeMarker template. By default Magisto uses one that is built-in. If you want to create your own, you can. Just name it `.page.ftl` and put it in the root of the source directory.

A template typically uses a *model* to base its output on. Magisto exposes the following data to the model:

* `timestamp` (`java.util.Date`): the time the Markdown file was converted to HTML.
* `path` (`java.nio.file.Path`): the relative path to the source file.
* `title` (`java.lang.String`): the title of the page. This is the text of the first header (atx-style).
* `content` (`java.lang.String`): the contents of the page. This is the converted Markdown content, already in HTML.

If you need to generate a link to a local file in your template, for example to your favicon, use the custom `link` directive that Magisto provides. For example:

```html
<link rel="icon"  href="<@link path="/static/favicon.png"/>">
```

This ensures that the link will always resolve correctly, independent of how deep the Markdown file being processed is in the source directory structure. (In other words: if the Markdown file is for example two levels deep, then the link will be prefixed with "`../..`".) By processing links in this manner, it doesn't matter where you put the output on your web server; the links will always resolve correctly.

### Static content

All files in `.static` are copied over to the target directory as is. This is where you can put your favicon, or the static content that you refer to from your template, like CSS, images, fonts, JavaScript and so on.

Note: static content will **never** overwrite source content. Source content is considered more important and therefore always has precedence.
