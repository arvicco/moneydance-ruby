require 'jruby'

#java_require 'ruby_main'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

#import java.awt.Color
#import java.awt.Font
#import javax.swing.JFrame
#import java.awt.EventQueue
#import com.moneydance.modules.features.ruby.rb.RubyConsole

#require "lib/ruby_console"
# Ruby class that does all heavy lifting for normal Moneydance extension
# Main (com.moneydance.apps.md.controller.FeatureModule)
# Java Main should just properly set up JRuby runtime, and then
# delegate everything else to RubyMain instance
#
class RubyMain
  def initialize main, context, engine
    @main, @context, @engine = main, context, engine
    STDERR.puts 'Starting RubyMain...'
    @console = com.moneydance.modules.features.ruby.rb.RubyConsole.new( @main, @context)
  end
end