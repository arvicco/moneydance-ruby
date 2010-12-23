require 'jruby'

#java_require 'ruby_main'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

#import java.awt.EventQueue

#import com.moneydance.modules.features.ruby.rb.RubyConsole
#require "lib/ruby_console"

# Ruby class that does all the heavy lifting for java Moneydance extension
# Main (com.moneydance.apps.md.controller.FeatureModule).
# Java Main should just properly set up JRuby runtime,
# and then delegate everything else to RubyMain instance
#
class RubyMain

  def initialize main, context, engine
    STDERR.puts 'Starting RubyMain...'
    @main, @context, @engine = main, context, engine
    # Register irb url to be invoked via the application toolbar
    @context.register_feature(@main, 'irb', icon('ruby'), @main.name);
  end

  java_signature 'java.awt.Image icon(String action)'

  def icon action = 'ruby'
    loader = @main.get_class.get_class_loader
    stream = loader.get_resource_as_stream("/com/moneydance/modules/features/ruby/#{action}.gif")
    bytes = stream.to_io.read.to_java_bytes
    java.awt.Toolkit.default_toolkit.create_image(bytes)
  end

  java_signature 'synchronized void cleanup()'
  # This is called when a data set is closed, so that the extension can
  # let go of any references that it may have to the data or the GUI.
  #
  def cleanup
    STDERR.puts "cleanup called"
    if @console
      @console.dispose
      System.gc
    end
  end

  java_signature 'public void invoke(String uri)'
  # Process an invocation of this module with the given URI
  #
  def invoke uri
    STDERR.puts "invoke called with: #{uri}"
    command, args = uri.split /[:?&]/
    send *[command, args].flatten.compact
  end

  java_signature 'synchronized void irb()'
  # Shows Moneydance IRB console, starts new one if necessary.
  # Moneydance URI: moneydance:fmodule:ruby:irb
  #
  def irb
    STDERR.puts "irb called"
    if @console
      STDERR.puts 'Showing Ruby console'
      @console.show
    else
      STDERR.puts 'Creating Ruby console'
      # We need to address RubyConsole via full java name... Why?
      @console = com.moneydance.modules.features.ruby.rb.RubyConsole.new(self, @context)
    end
  end

  java_signature 'synchronized void file(String path)'

  def file path
    STDERR.puts "file called with: #{path}"
    load path
  end
end
