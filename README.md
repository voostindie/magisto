# Magisto

## Introduction

Magisto is a *very* simple tool that runs against a local Git clone and that exports all files in the clone to an HTML site. All Markdown files are converted to HTML using a single template in the process. All other files are kept intact.

It's as simple as that. No more. No less.

If your ambitions reach beyond these plain goals, then Magisto is not the tool for you.

## Background

At the company I work for there are a lot of Git repositories containing documentation in Markdown format. Although our Git repository manager ([Stash](http://www.atlassian.com/stash)) has a web-based viewer that nicely renders Markdown as HTML, that interface is too complex for most audiences. Most users want to view the documentation without distractions (commits, branches, tags, diffs... WTF?!).

## Installation

Magisto requires Java 7 or later. It's a fat JAR, so once downloaded you can execute it directly, with `java -jar magisto-<VERSION>.jar`.

## Usage

You must run Magisto from the local Git clone directory. Otherwise it won't work. It requires one argument: the directory to write the export to. When executed it:

* Checks that the current directory is indeed a Git clone.
* Checks that the target directory either doesn't exist, is empty, or contains an earlier Magisto export.
* Goes through all files in the current directory recursively, copying them to the export directory, transforming all Markdown files to HTML files in the process. Where:
    * Files that haven't changed since the last export aren't touched.
    * Files that are no longer in the working directory are removed from the target directory.

That's about it.

## Configuration

You can configure Magisto in two ways:

1. By supplying a custom page template.
2. By adding additional static content to the output.

## Custom template

To transform Markdown to HTML, Magisto uses a FreeMarker template. By default Magisto uses one that is built-in. If you want to create your own, you can. Just name it `.page.ftl` and put it in the root of the source directory.

A template typically uses a *model* to base its output on. Magisto exposes the following data to the model:

* `title`: the title of the page. This is the text on the first header in the Markdown input.
* `content`: the contents of the page. This is the converted Markdown content, already in HTML.
* `...`: ...to be specified...

## Static content

All files in `.static` are copied over to the target directory as is. This is where you can put your favicon, or the static content that you refer to from your template, like CSS, images, fonts, JavaScript and so on.
