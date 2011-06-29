package com.moneydance.modules.features.ruby.rb;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class RubyConsole extends RubyObject implements ActionListener {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("require 'jruby'\n" +
            "require 'irb'\n" +
            "require 'irb/completion'\n" +
            "\n" +
            "#java_require 'ruby_console'  # Require this file, instead of pasting code into .java\n" +
            "java_package 'com.moneydance.modules.features.ruby.rb'\n" +
            "\n" +
            "import java.awt.Color\n" +
            "import java.awt.Font\n" +
            "import java.awt.EventQueue\n" +
            "import javax.swing.JFrame\n" +
            "import javax.swing.JFileChooser\n" +
            "import com.moneydance.awt.AwtUtil\n" +
            "java_import 'java.awt.event.ActionListener' # Compiler needs this directive to implement ActionListener\n" +
            "java_import 'java.awt.event.ActionEvent' # Compiler needs this directive to implement ActionListener\n" +
            "\n" +
            "# Moneydance IRB Console\n" +
            "# Implements ActionListener: The listener interface for receiving action events.\n" +
            "# Instance of class that implements this interface can be registered with any component\n" +
            "# using the component's #addActionListener method. When the action event occurs,\n" +
            "# registered listener's #actionPerformed method is invoked.\n" +
            "class RubyConsole\n" +
            "  java_implements ActionListener\n" +
            "\n" +
            "  # Try to find preferred font family, use otherwise -- err -- otherwise\n" +
            "  def self.find_font otherwise, style, size, *preferred\n" +
            "    available = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names\n" +
            "    font_name = preferred.find { |name| available.include? name } || otherwise\n" +
            "    Font.new(font_name, style, size)\n" +
            "  end\n" +
            "\n" +
            "  # Starts new RubyConsole\n" +
            "  def initialize ruby_main\n" +
            "    STDERR.puts \"RubyConsole init called\"\n" +
            "    @ruby_main = ruby_main\n" +
            "\n" +
            "    if EventQueue.dispatch_thread?\n" +
            "      # Called from Moneydance GUI, use thread other than EventQueue.dispatch_thread\n" +
            "      Thread.new do\n" +
            "        run_irb\n" +
            "      end\n" +
            "    else\n" +
            "      # Called from command line (rake ant:load), we're not inside Moneydance GUI\n" +
            "      run_irb JFrame::EXIT_ON_CLOSE\n" +
            "    end\n" +
            "  end\n" +
            "\n" +
            "  # Runs Swing wrapper for IRB - call from thread other than EventQueue.dispatch_thread\n" +
            "  #\n" +
            "  def run_irb close_operation = JFrame::HIDE_ON_CLOSE # DISPOSE_ON_CLOSE ?\n" +
            "    STDERR.puts \"RubyConsole run_irb called\"\n" +
            "    text = javax.swing.JTextPane.new\n" +
            "    text.font = RubyConsole.find_font 'Monospaced', Font::PLAIN, 14, 'Menlo', 'Monaco', 'Andale Mono'\n" +
            "    text.margin = java.awt.Insets.new(8, 8, 8, 8)\n" +
            "    text.caret_color = Color.new(0xa4, 0x00, 0x00)\n" +
            "    text.background = Color.new(0xf2, 0xf2, 0xf2)\n" +
            "    text.foreground = Color.new(0xa4, 0x00, 0x00)\n" +
            "\n" +
            "    @irb_pane = javax.swing.JScrollPane.new\n" +
            "    @irb_pane.viewport_view = text\n" +
            "\n" +
            "    @file_button = javax.swing.JButton.new \"Load file\"\n" +
            "\n" +
            "    pane = javax.swing.JPanel.new java.awt.GridBagLayout.new\n" +
            "    pane.add(@irb_pane, AwtUtil.getConstraints(0, 0, 1, 1, 4, 1, true, true))\n" +
            "    pane.add(@file_button, AwtUtil.getConstraints(0, 3, 1, 0, 1, 1, false, true))\n" +
            "\n" +
            "#    enableEvents(WindowEvent.WINDOW_CLOSING);\n" +
            "    @file_button.addActionListener(self)\n" +
            "\n" +
            "    @frame = JFrame.new \"Moneydance Interactive JRuby #{JRUBY_VERSION} Console \" +\n" +
            "                            \"(tab will autocomplete)\"\n" +
            "    @frame.default_close_operation = close_operation\n" +
            "    @frame.set_size 800, 800\n" +
            "    @frame.content_pane.add pane # @frame.add pane\n" +
            "\n" +
            "    header = \" MD - Moneydance context: ComMoneydanceAppsMdController::Main \\n\" +\n" +
            "        \" ROOT - Moneydance root account: ComMoneydanceAppsMdModel::RootAccount \\n\" +\n" +
            "        \" TRANS - Moneydance TransactionSet: ComMoneydanceAppsMdModel::TransactionSet \\n\\n\"\n" +
            "\n" +
            "    readline = org.jruby.demo.TextAreaReadline.new text, header\n" +
            "    JRuby.objectspace = true # useful for code completion\n" +
            "    readline.hook_into_runtime_with_streams(JRuby.runtime)\n" +
            "\n" +
            "    EventQueue.invoke_later proc { show }\n" +
            "\n" +
            "    ARGV << '--readline' << '--prompt' << 'inf-ruby'\n" +
            "    IRB.start(__FILE__)\n" +
            "    @ruby_main.cleanup\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'public void actionPerformed(ActionEvent event)'\n" +
            "  # from ActionListener interface: Invoked when an action event occurs.\n" +
            "  def action_performed event\n" +
            "    case event.source\n" +
            "      when @file_button\n" +
            "        load_file\n" +
            "        show\n" +
            "      else\n" +
            "        STDERR.puts \"Got unexpected event: #{event}\"\n" +
            "    end\n" +
            "  end\n" +
            "\n" +
            "  # Interactively loads Ruby script into IRB session\n" +
            "  #\n" +
            "  def load_file\n" +
            "    fc = JFileChooser.new\n" +
            "    fc.setDialogTitle(\"Choose Ruby Script File\")\n" +
            "    if fc.showOpenDialog(@frame) == JFileChooser::APPROVE_OPTION\n" +
            "      @ruby_main.file fc.getSelectedFile.absolute_path\n" +
            "    else\n" +
            "      STDERR.puts \"Unrecognized Ruby Script File\"\n" +
            "    end\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'void show()'\n" +
            "  # Shows console window\n" +
            "  #\n" +
            "  def show\n" +
            "    @frame.set_visible true\n" +
            "    @frame.to_front\n" +
            "    @irb_pane.request_focus\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'void dispose()'\n" +
            "  # Properly disposes of Swing elements\n" +
            "  #\n" +
            "  def dispose\n" +
            "    @frame.set_visible false\n" +
            "    @frame.dispose\n" +
            "  end\n" +
            "end\n" +
            "").toString();
        __ruby__.executeScript(source, "lib/ruby_console.rb");
        RubyClass metaclass = __ruby__.getClass("RubyConsole");
        metaclass.setRubyStaticAllocator(RubyConsole.class);
        if (metaclass == null) throw new NoClassDefFoundError("Could not load Ruby class: RubyConsole");
        __metaclass__ = metaclass;
    }

    /**
     * Standard Ruby object constructor, for construction-from-Ruby purposes.
     * Generally not for user consumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    private RubyConsole(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    /**
     * A static method used by JRuby for allocating instances of this object
     * from Ruby. Generally not for user comsumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public static IRubyObject __allocate__(Ruby ruby, RubyClass metaClass) {
        return new RubyConsole(ruby, metaClass);
    }

    
    public static Object find_font(Object otherwise, Object style, Object size, Object preferred) {
        IRubyObject ruby_args[] = new IRubyObject[4];
        ruby_args[0] = JavaUtil.convertJavaToRuby(__ruby__, otherwise);
        ruby_args[1] = JavaUtil.convertJavaToRuby(__ruby__, style);
        ruby_args[2] = JavaUtil.convertJavaToRuby(__ruby__, size);
        ruby_args[3] = JavaUtil.convertJavaToRuby(__ruby__, preferred);

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), __metaclass__, "find_font", ruby_args);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public  RubyConsole(Object ruby_main) {
        this(__ruby__, __metaclass__);
        IRubyObject ruby_ruby_main = JavaUtil.convertJavaToRuby(__ruby__, ruby_main);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize", ruby_ruby_main);

    }

    
    public Object run_irb(Object close_operation) {
        IRubyObject ruby_close_operation = JavaUtil.convertJavaToRuby(__ruby__, close_operation);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "run_irb", ruby_close_operation);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public void actionPerformed(ActionEvent event) {
        IRubyObject ruby_event = JavaUtil.convertJavaToRuby(__ruby__, event);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "action_performed", ruby_event);
        return;

    }

    
    public Object load_file() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "load_file");
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public void show() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "show");
        return;

    }

    
    public void dispose() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "dispose");
        return;

    }

}
