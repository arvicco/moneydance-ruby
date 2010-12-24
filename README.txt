DESCRIPTION

This is a MoneyDance extension that adds Ruby scripting support to it.

Moneydance is a full-featured personal financial management application featuring
online banking, online bill payment, investment management, budget tracking,
scheduled transactions, check printing, detailed reports/graphs and more.

It is cross-platform (Java) and offers best programming API for extension among
similar applications. However, currently it only supports Java and Python.

PREREQUISITES

You should have MoneyDance 2010, Java 1.6 and JRuby 1.5 installed and functional.

INSTALL FROM SOURCE

$ git clone git://github.com/arvicco/moneydance-ruby.git
$ cd moneydance-ruby
$ rake ant:jar

This produces ruby.ext in dist subdirectory. You can then install this file into
MoneyDance menu: Extensions->Add->From file [Next>] Choose ruby.ext file.
Ignore the warning that you're loading unsigned extension.

USAGE

Installed extension adds "Ruby interface" item to Extensions menu. This item opens
IRB console that you can use to run arbitrary code against live MoneyDance and its
data files. In future, I plan to add capacity for loading/running Ruby script files,
as well as nice ORM wrapper around MoneyDance data file (probably ActiveModel).

RESOURCES

MoneyDance: http://moneydance.com/
MoneyDance API: http://moneydance.com/dev/ext_api_docs/index.html
Moneydance URI Scheme: http://moneydance.com/dev/urischeme
Primer on Moneydance Methods: http://moneydance.com/dev/RM-NetWorth/wiki_jython.html

LICENSE

Copyright (c) 2010 Arvicco

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.