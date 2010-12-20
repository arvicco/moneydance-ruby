require 'jruby'
require 'irb'
require 'irb/completion'

#java_require 'ruby_console'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

import java.awt.Color
import java.awt.Font
import javax.swing.JFrame
import java.awt.EventQueue

# Moneydance IRB Console
class RubyConsole

  # Try to find preferred font family, use otherwise -- err -- otherwise
  def self.find_font otherwise, style, size, *families
    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names
    fontname = families.find(proc { otherwise }) { |name| avail_families.include? name }
    Font.new(fontname, style, size)
  end

  # Starts new RubyConsole
  def initialize extension, context
    @extension = extension
    @context = context
    @root = context.get_root_account
    Object.const_set :EXT, @extension
    Object.const_set :MD, @context
    Object.const_set :ROOT, @root

    if EventQueue.dispatch_thread?
      # Called from Moneydance GUI, use thread other than EventQueue.dispatch_thread
      Thread.new do
        run_swing JFrame::HIDE_ON_CLOSE
      end
    else
      # Called from command line (rake ant:load), we're not inside Moneydance GUI
      run_swing JFrame::EXIT_ON_CLOSE
    end
  end

  # Runs Swing wrapper for IRB - call from thread other than EventQueue.dispatch_thread
  #
  def run_swing close_operation
    text = javax.swing.JTextPane.new
    text.font = RubyConsole.find_font 'Monospaced', Font::PLAIN, 14, 'Monaco', 'Andale Mono'
    text.margin = java.awt.Insets.new(8, 8, 8, 8)
    text.caret_color = Color.new(0xa4, 0x00, 0x00)
    text.background = Color.new(0xf2, 0xf2, 0xf2)
    text.foreground = Color.new(0xa4, 0x00, 0x00)

    pane = javax.swing.JScrollPane.new
    pane.viewport_view = text

    @frame = JFrame.new "Moneydance Interactive JRuby #{JRUBY_VERSION} Console " +
        "(tab will autocomplete)"
    @frame.default_close_operation = close_operation # DISPOSE_ON_CLOSE
    @frame.set_size 700, 600
    @frame.content_pane.add pane

    header = "MD - Moneydance context: ComMoneydanceAppsMdController::Main\n" +
        "EXT - This Moneydance extension: ComMoneydanceModulesFeaturesRuby::Main\n" +
        "ROOT - Moneydance root account: ComMoneydanceAppsMdModel::RootAccount\n\n"
    readline = org.jruby.demo.TextAreaReadline.new text, header
    JRuby.objectspace = true # useful for code completion
    readline.hook_into_runtime_with_streams(JRuby.runtime)

    EventQueue.invoke_later proc { show }

    ARGV << '--readline' << '--prompt' << 'inf-ruby'
    IRB.start(__FILE__)
    @extension.cleanup
  end

  java_signature 'void show()'
  # Shows console window
  #
  def show
    @frame.set_visible true
    @frame.to_front
    @frame.request_focus
  end

  java_signature 'void dispose()'
  # Properly disposes of Swing elements
  #
  def dispose
    @frame.set_visible false
    @frame.dispose
  end
end
