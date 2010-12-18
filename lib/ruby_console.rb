require 'jruby'
require 'irb'
require 'irb/completion'

#java_require 'moneydance_irb'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

import java.awt.Color
import java.awt.Font
import javax.swing.JFrame
import java.awt.EventQueue

# Moneydance IRB Console
class RubyConsole
#  Needed to start RubyConsole in a separate (!EventQueue.isDispatchThread()) thread
  java_implements 'Runnable'

# Try to find preferred font family, use otherwise -- err -- otherwise
  def self.find_font(otherwise, style, size, *families)
    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names
    fontname = families.find(proc { otherwise }) { |name| avail_families.include? name }
    Font.new(fontname, style, size)
  end

  def initialize extension, context
    @extension = extension
    @context = context
    @root = context.get_root_account
    Object.const_set :EXT, @extension
    Object.const_set :MD, @context
    Object.const_set :ROOT, @root
  end

  java_signature 'void run()'
  def run
    text = javax.swing.JTextPane.new
    text.font = RubyConsole.find_font 'Monospaced', Font::PLAIN, 14, 'Monaco', 'Andale Mono'
    text.margin = java.awt.Insets.new(8, 8, 8, 8)
    text.caret_color = Color.new(0xa4, 0x00, 0x00)
    text.background = Color.new(0xf2, 0xf2, 0xf2)
    text.foreground = Color.new(0xa4, 0x00, 0x00)

    pane = javax.swing.JScrollPane.new
    pane.viewport_view = text
    @frame = JFrame.new 'Moneydance Interactive Ruby Console (tab will autocomplete)'
    @frame.default_close_operation = JFrame::HIDE_ON_CLOSE # EXIT_ON_CLOSE, DISPOSE_ON_CLOSE
    @frame.set_size 700, 600
    @frame.content_pane.add pane

    header = " Welcome to the Moneydance IRB Console [#{JRUBY_VERSION}] \n\n"
    readline = org.jruby.demo.TextAreaReadline.new text, header

    JRuby.objectspace = true # useful for code completion
    readline.hook_into_runtime_with_streams(JRuby.runtime)

    EventQueue.invoke_later proc { show; STDOUT.puts "SVisible..."}

    ARGV << '--readline' << '--prompt' << 'inf-ruby'
    IRB.start(__FILE__)
    STDOUT.puts "IRB finished..."
  end

  def show
    @frame.setVisible(true)
#    @frame.toFront
#    @frame.requestFocus
  end

  def dispose
    @frame.setVisible(false)
    STDOUT.puts "Disposing..."
    @frame.dispose
    STDOUT.puts "Disposed..."
  end
end
