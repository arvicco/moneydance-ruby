require 'jruby'
require 'irb'
require 'irb/completion'

#java_require 'moneydance_irb'  # Require this file, instead of pasting code into .java
java_package 'com.moneydance.modules.features.ruby.rb'

# Moneydance IRB Console
class RubyConsole

  if ARGV.empty?
    # default options, esp. useful for jrubyw
    ARGV << '--readline' << '--prompt' << 'inf-ruby'
  end

  import java.awt.Color
  import java.awt.Font
  import javax.swing.JFrame
  import java.awt.EventQueue

  # Dummy static method
  def self.noop

  end

# Try to find preferred font family, use otherwise -- err --  otherwise
  def self.find_font(otherwise, style, size, *families)
    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names
    fontname = families.find(proc { otherwise }) { |name| avail_families.include? name }
    Font.new(fontname, style, size)
  end

  text = javax.swing.JTextPane.new
  text.font = find_font('Monospaced', Font::PLAIN, 14, 'Monaco', 'Andale Mono')
  text.margin = java.awt.Insets.new(8, 8, 8, 8)
  text.caret_color = Color.new(0xa4, 0x00, 0x00)
  text.background = Color.new(0xf2, 0xf2, 0xf2)
  text.foreground = Color.new(0xa4, 0x00, 0x00)

  pane = javax.swing.JScrollPane.new
  pane.viewport_view = text
  frame = JFrame.new('Moneydance Interactive Ruby Console (tab will autocomplete)')
#  frame.default_close_operation = JFrame::EXIT_ON_CLOSE
#  frame.default_close_operation = JFrame::DISPOSE_ON_CLOSE
  frame.default_close_operation = JFrame::HIDE_ON_CLOSE

#  frame.addWindowListener(WindowAdapter.new {
#public void windowClosing(WindowEvent e) {
#System.exit(0);
#}
#});
  frame.set_size(700, 600)
  frame.content_pane.add(pane)
  tar = org.jruby.demo.TextAreaReadline.new(text,
                                            " Welcome to the Moneydance IRB Console [#{JRUBY_VERSION}] \n\n")
  JRuby.objectspace = true # useful for code completion
  tar.hook_into_runtime_with_streams(JRuby.runtime)

  EventQueue.invoke_later proc { frame.visible = true; STDOUT.puts "SVisible..."; sleep 1 }

  IRB.start(__FILE__)

  STDOUT.puts "IRB finished..."

  def dispose
    STDOUT.puts "Disposing..."
    frame.dispose
    STDOUT.puts "Disposed..."
  end
end
