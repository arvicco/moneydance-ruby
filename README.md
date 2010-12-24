DESCRIPTION
-----------

This is a MoneyDance extension that adds Ruby scripting support to it.

Moneydance is a full-featured personal financial management application featuring
online banking, online bill payment, investment management, budget tracking,
scheduled transactions, check printing, detailed reports/graphs and more.

It is cross-platform (Java) and offers best programming API for extension among
similar applications. However, currently it only supports Java and Python.

PREREQUISITES
-----------

You should have MoneyDance 2010, Java 1.6 and JRuby 1.5+ installed and functional.

INSTALL EXTENSION
-----------

Complete instructions and download [here](http://arvicco.github.com/moneydance-ruby).

INSTALL FROM SOURCE
-----------

  $ git clone git://github.com/arvicco/moneydance-ruby.git
  $ cd moneydance-ruby
  $ rake ant:jar

This produces ruby.ext in dist subdirectory. You can then install this file into
MoneyDance menu: Extensions->Add->From file [Next>] Choose ruby.ext file.
Ignore the warning that you're loading unsigned extension.

USAGE
-----------

Installed extension adds "Ruby interface" item to Extensions menu. This item opens
IRB console that you can use to run arbitrary code against live MoneyDance and its
data files. In future, I plan to add capacity for loading/running Ruby script files,
as well as nice ORM wrapper around MoneyDance data file (probably ActiveModel).

RESOURCES
-----------

Homepage: (http://arvicco.github.com/moneydance-ruby)
MoneyDance: (http://moneydance.com/)
MoneyDance API: (http://moneydance.com/dev/ext_api_docs/index.html)
Moneydance URI Scheme: (http://moneydance.com/dev/urischeme)
Primer on Moneydance Methods: (http://arvicco.github.com/moneydance-ruby/Primer.html)

LICENSE
-----------

Copyright (c) 2010 Arvicco. Free and unrestricted. See LICENSE for details.