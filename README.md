# Magisto

## Introduction

Magisto is a *very* simple tool that runs against a Git clone and that exports all files in it to an HTML site. All Markdown files are converted to HTML using a single template in the process. All other files are kept intact.

It's as simple as that. No more. No less.

If your ambitions reach beyond these plain goals, then Magisto is not the tool for you.

## Background

At the company I work for there are a lot of Git repositories containing documentation in Markdown format. Although our Git repository manager ([Stash](http://www.atlassian.com/stash)) has a web-based viewer that nicely renders Markdown as HTML, that interface is too complex for most audiences. Most users want to view the documentation without distractions (files, directories, commits, branches, tags, diffs... WTF?!).

## Installation

Magisto requires Java 7 or later. It's a fat JAR, so once downloaded you can execute it directly, with `java -jar magisto-<VERSION>.jar`.

## Usage

Run Magisto with the `-h` or `--help` command line argument to get something like this:

```raw
The options available are:
	[--help -h]
	[--source -s value] : Source directory
	--target -t value : Target directory
```

The source directory is an optional argument and defaults to the current one.

When executed with valid arguments, Magisto:

* (*Not yet*) checks that the source directory is indeed a Git clone.
* Checks that the target directory either doesn't exist, is empty, or contains an earlier Magisto export.
* Goes through all files in the source directory recursively, copying them to the target directory, transforming all Markdown files to HTML files in the process. Where:
    * Files that haven't changed since the last run aren't touched.
    * Files that are no longer in the source directory are removed from the target directory.

That's about it.

## Configuration

You can configure Magisto in two ways:

1. By supplying a custom page template.
2. By adding additional static content to the output.

## Custom template

To transform Markdown to HTML, Magisto uses a FreeMarker template. By default Magisto uses one that is built-in. If you want to create your own, you can. Just name it `.page.ftl` and put it in the root of the source directory.

A template typically uses a *model* to base its output on. Magisto exposes the following data to the model:

* `timestamp` (`java.util.Date`): the time the Markdown file was converted to HTML.
* `path` (`java.nio.file.Path`): the relative path to the source file.
* `title` (`java.lang.String`): the title of the page. This is the text of the first header (atx-style).
* `content` (`java.lang.String`): the contents of the page. This is the converted Markdown content, already in HTML.

## Static content

All files in `.static` are copied over to the target directory as is. This is where you can put your favicon, or the static content that you refer to from your template, like CSS, images, fonts, JavaScript and so on.
