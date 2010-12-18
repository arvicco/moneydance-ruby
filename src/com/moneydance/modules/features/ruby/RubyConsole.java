package com.moneydance.modules.features.ruby;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;


public class RubyConsole extends RubyObject  {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("require 'jruby'\n" +
            "require 'irb'\n" +
            "require 'irb/completion'\n" +
            "\n" +
            "#java_require 'moneydance_irb'  # Require this file, instead of pasting code into .java\n" +
            "java_package 'com.moneydance.modules.features.ruby'\n" +
            "\n" +
            "# Moneydance IRB Console\n" +
            "class RubyConsole\n" +
            "\n" +
            "  if ARGV.empty?\n" +
            "    # default options, esp. useful for jrubyw\n" +
            "    ARGV << '--readline' << '--prompt' << 'inf-ruby'\n" +
            "  end\n" +
            "\n" +
            "  import java.awt.Color\n" +
            "  import java.awt.Font\n" +
            "  import javax.swing.JFrame\n" +
            "  import java.awt.EventQueue\n" +
            "\n" +
            "  # Dummy static method\n" +
            "  def self.noop\n" +
            "\n" +
            "  end\n" +
            "\n" +
            "# Try to find preferred font family, use otherwise -- err --  otherwise\n" +
            "  def self.find_font(otherwise, style, size, *families)\n" +
            "    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names\n" +
            "    fontname = families.find(proc { otherwise }) { |name| avail_families.include? name }\n" +
            "    Font.new(fontname, style, size)\n" +
            "  end\n" +
            "\n" +
            "  text = javax.swing.JTextPane.new\n" +
            "  text.font = find_font('Monospaced', Font::PLAIN, 14, 'Monaco', 'Andale Mono')\n" +
            "  text.margin = java.awt.Insets.new(8, 8, 8, 8)\n" +
            "  text.caret_color = Color.new(0xa4, 0x00, 0x00)\n" +
            "  text.background = Color.new(0xf2, 0xf2, 0xf2)\n" +
            "  text.foreground = Color.new(0xa4, 0x00, 0x00)\n" +
            "\n" +
            "  pane = javax.swing.JScrollPane.new\n" +
            "  pane.viewport_view = text\n" +
            "  frame = JFrame.new('Moneydance Interactive Ruby Console (tab will autocomplete)')\n" +
            "#  frame.default_close_operation = JFrame::EXIT_ON_CLOSE\n" +
            "#  frame.default_close_operation = JFrame::DISPOSE_ON_CLOSE\n" +
            "  frame.default_close_operation = JFrame::HIDE_ON_CLOSE\n" +
            "\n" +
            "#  frame.addWindowListener(WindowAdapter.new {\n" +
            "#public void windowClosing(WindowEvent e) {\n" +
            "#System.exit(0);\n" +
            "#}\n" +
            "#});\n" +
            "  frame.set_size(700, 600)\n" +
            "  frame.content_pane.add(pane)\n" +
            "  tar = org.jruby.demo.TextAreaReadline.new(text,\n" +
            "                                            \" Welcome to the Moneydance IRB Console [#{JRUBY_VERSION}] \\n\\n\")\n" +
            "  JRuby.objectspace = true # useful for code completion\n" +
            "  tar.hook_into_runtime_with_streams(JRuby.runtime)\n" +
            "\n" +
            "  EventQueue.invoke_later proc { frame.visible = true; STDOUT.puts \"SVisible...\"; sleep 1 }\n" +
            "\n" +
            "  IRB.start(__FILE__)\n" +
            "\n" +
            "  STDOUT.puts \"IRB finished...\"\n" +
            "\n" +
            "  def dispose\n" +
            "    STDOUT.puts \"Disposing...\"\n" +
            "    frame.dispose\n" +
            "    STDOUT.puts \"Disposed...\"\n" +
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
        
    /**
     * Default constructor. Invokes this(Ruby, RubyClass) with the classloader-static
     * Ruby and RubyClass instances assocated with this class, and then invokes the
     * no-argument 'initialize' method in Ruby.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public RubyConsole() {
        this(__ruby__, __metaclass__);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize");
    }

    
    public static Object noop() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), __metaclass__, "noop");
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public static Object find_font(Object otherwise, Object style, Object size, Object families) {
        IRubyObject ruby_args[] = new IRubyObject[4];
        ruby_args[0] = JavaUtil.convertJavaToRuby(__ruby__, otherwise);
        ruby_args[1] = JavaUtil.convertJavaToRuby(__ruby__, style);
        ruby_args[2] = JavaUtil.convertJavaToRuby(__ruby__, size);
        ruby_args[3] = JavaUtil.convertJavaToRuby(__ruby__, families);

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), __metaclass__, "find_font", ruby_args);
        return (Object)ruby_result.toJava(Object.class);

    }

    
    public Object dispose() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "dispose");
        return (Object)ruby_result.toJava(Object.class);

    }

}
