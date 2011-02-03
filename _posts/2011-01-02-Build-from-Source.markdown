---
layout: post
title: Build extension from source
---

## Building Moneydance Ruby extension from source

This is for those who enjoy doing things their own way.

You should have Moneydance 2010 as well as Java 1.6 and JRuby 1.5+ installed and
functional to build extension from source.

Download Moneydance Ruby source code in either
<a href="http://github.com/arvicco/moneydance-ruby/zipball/master">ZIP</a> or
<a href="http://github.com/arvicco/moneydance-ruby/tarball/master">TAR</a>
formats, unzip/untar it - say, to "moneydance-ruby" dir.

Alternatively, you can use [Git](http://git-scm.com) to clone the project:

    $ git clone git://github.com/arvicco/moneydance-ruby

Change to source code dir, run Rake task that builds the extension.

    $ cd moneydance-ruby
    $ rake ant:jar

This should compile and build "ruby.mxt" extension file inside "dist" directory.
You can then install it into Moneydance using technique described in
"Installing Moneydance Ruby" section.
