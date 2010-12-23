require 'jruby'
require 'java'
require 'irb'
require 'irb/completion'

#java_require 'ruby_console'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

import java.awt.Color
import java.awt.Font
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.JFileChooser
java_import 'java.awt.event.*' # Compiler needs this directive

# Moneydance IRB Console
class RubyConsole
  java_implements ActionListener

  # Try to find preferred font family, use otherwise -- err -- otherwise
  def self.find_font otherwise, style, size, *families
    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names
    fontname = families.find(proc { otherwise }) { |name| avail_families.include? name }
    Font.new(fontname, style, size)
  end

  # Starts new RubyConsole
  def initialize ruby_main, context
    @ruby_main = ruby_main
    @context = context
    @root = context.get_root_account
    Object.const_set :MD, @context
    Object.const_set :ROOT, @root

    if EventQueue.dispatch_thread?
      # Called from Moneydance GUI, use thread other than EventQueue.dispatch_thread
      Thread.new do
        run_irb
      end
    else
      # Called from command line (rake ant:load), we're not inside Moneydance GUI
      run_irb JFrame::EXIT_ON_CLOSE
    end
  end

  # Runs Swing wrapper for IRB - call from thread other than EventQueue.dispatch_thread
  #
  def run_irb close_operation = JFrame::HIDE_ON_CLOSE # DISPOSE_ON_CLOSE ?
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
    @frame.default_close_operation = close_operation
    @frame.set_size 700, 600
    @frame.content_pane.add pane

    header = " MD - Moneydance context: ComMoneydanceAppsMdController::Main \n" +
        " ROOT - Moneydance root account: ComMoneydanceAppsMdModel::RootAccount \n\n"
    readline = org.jruby.demo.TextAreaReadline.new text, header
    JRuby.objectspace = true # useful for code completion
    readline.hook_into_runtime_with_streams(JRuby.runtime)

    EventQueue.invoke_later proc { show }

    ARGV << '--readline' << '--prompt' << 'inf-ruby'
    IRB.start(__FILE__)
    @ruby_main.cleanup
  end

  java_signature 'public void actionPerformed(ActionEvent e)'

  def action_performed event

  end

  def load_file
    fc = JFileChooser.new
    fc.setDialogTitle("Choose Ruby File")
    if fc.showOpenDialog(self) == JFileChooser::APPROVE_OPTION
      STDERR.puts fc.getSelectedFile
      @ruby_main.file fc.getSelectedFile.absolute_path
    else
      STDERR.puts "Aaaargh"
    end
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
