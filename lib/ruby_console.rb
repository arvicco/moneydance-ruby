require 'jruby'
require 'irb'
require 'irb/completion'

#java_require 'ruby_console'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

import java.awt.Color
import java.awt.Font
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.JFileChooser
import com.moneydance.awt.AwtUtil
java_import 'java.awt.event.ActionListener' # Compiler needs this directive to implement ActionListener
java_import 'java.awt.event.ActionEvent' # Compiler needs this directive to implement ActionListener

# Moneydance IRB Console
# Implements ActionListener: The listener interface for receiving action events.
# Instance of class that implements this interface can be registered with any component
# using the component's #addActionListener method. When the action event occurs,
# registered listener's #actionPerformed method is invoked.
class RubyConsole
  java_implements ActionListener

  # Try to find preferred font family, use otherwise -- err -- otherwise
  def self.find_font otherwise, style, size, *preferred
    available = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names
    font_name = preferred.find { |name| available.include? name } || otherwise
    Font.new(font_name, style, size)
  end

  # Starts new RubyConsole
  def initialize ruby_main
    STDERR.puts "RubyConsole init called"
    @ruby_main = ruby_main

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
    STDERR.puts "RubyConsole run_irb called"
    text = javax.swing.JTextPane.new
    text.font = RubyConsole.find_font 'Monospaced', Font::PLAIN, 14, 'Menlo', 'Monaco', 'Andale Mono'
    text.margin = java.awt.Insets.new(8, 8, 8, 8)
    text.caret_color = Color.new(0xa4, 0x00, 0x00)
    text.background = Color.new(0xf2, 0xf2, 0xf2)
    text.foreground = Color.new(0xa4, 0x00, 0x00)

    @irb_pane = javax.swing.JScrollPane.new
    @irb_pane.viewport_view = text

    @file_button = javax.swing.JButton.new "Load file"

    pane = javax.swing.JPanel.new java.awt.GridBagLayout.new
    pane.add(@irb_pane, AwtUtil.getConstraints(0, 0, 1, 1, 4, 1, true, true))
    pane.add(@file_button, AwtUtil.getConstraints(0, 3, 1, 0, 1, 1, false, true))

#    enableEvents(WindowEvent.WINDOW_CLOSING);
    @file_button.addActionListener(self)

    @frame = JFrame.new "Moneydance Interactive JRuby #{JRUBY_VERSION} Console " +
                            "(tab will autocomplete)"
    @frame.default_close_operation = close_operation
    @frame.set_size 800, 800
    @frame.content_pane.add pane # @frame.add pane

    header = " MD - Moneydance context: ComMoneydanceAppsMdController::Main \n" +
        " ROOT - Moneydance root account: ComMoneydanceAppsMdModel::RootAccount \n" +
        " TRANS - Moneydance TransactionSet: ComMoneydanceAppsMdModel::TransactionSet \n\n"

    readline = org.jruby.demo.TextAreaReadline.new text, header
    JRuby.objectspace = true # useful for code completion
    readline.hook_into_runtime_with_streams(JRuby.runtime)

    EventQueue.invoke_later proc { show }

    ARGV << '--readline' << '--prompt' << 'inf-ruby'
    IRB.start(__FILE__)
    @ruby_main.cleanup
  end

  java_signature 'public void actionPerformed(ActionEvent event)'
  # from ActionListener interface: Invoked when an action event occurs.
  def action_performed event
    case event.source
      when @file_button
        load_file
        show
      else
        STDERR.puts "Got unexpected event: #{event}"
    end
  end

  # Interactively loads Ruby script into IRB session
  #
  def load_file
    fc = JFileChooser.new
    fc.setDialogTitle("Choose Ruby Script File")
    if fc.showOpenDialog(@frame) == JFileChooser::APPROVE_OPTION
      @ruby_main.file fc.getSelectedFile.absolute_path
    else
      STDERR.puts "Unrecognized Ruby Script File"
    end
  end

  java_signature 'void show()'
  # Shows console window
  #
  def show
    @frame.set_visible true
    @frame.to_front
    @irb_pane.request_focus
  end

  java_signature 'void dispose()'
  # Properly disposes of Swing elements
  #
  def dispose
    @frame.set_visible false
    @frame.dispose
  end
end
