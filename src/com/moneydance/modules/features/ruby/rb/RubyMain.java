package com.moneydance.modules.features.ruby.rb;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class RubyMain extends RubyObject  {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("require 'jruby'\n" +
            "\n" +
            "#java_require 'ruby_main'  # Require this file, instead of pasting code into .java\n" +
            "java_package 'com.moneydance.modules.features.ruby.rb'\n" +
            "\n" +
            "#import java.awt.EventQueue\n" +
            "\n" +
            "#import com.moneydance.modules.features.ruby.rb.RubyConsole\n" +
            "#require \"lib/ruby_console\"\n" +
            "\n" +
            "# Ruby class that does all the heavy lifting for java Moneydance extension\n" +
            "# Main (com.moneydance.apps.md.controller.FeatureModule).\n" +
            "# Java Main should just properly set up JRuby runtime,\n" +
            "# and then delegate everything else to RubyMain instance\n" +
            "#\n" +
            "class RubyMain\n" +
            "\n" +
            "  def initialize main, context, engine\n" +
            "    STDERR.puts 'Starting RubyMain...'\n" +
            "    @main, @context, @engine = main, context, engine\n" +
            "\n" +
            "    # Register irb url to be invoked via the application toolbar\n" +
            "    @context.register_feature(@main, 'irb', icon('ruby'), @main.name);\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'java.awt.Image icon(String action)'\n" +
            "  # Extracting extension icon (currently not used anywhere)\n" +
            "  def icon action = 'ruby'\n" +
            "    loader = @main.get_class.get_class_loader\n" +
            "    stream = loader.get_resource_as_stream(\"/com/moneydance/modules/features/ruby/#{action}.gif\")\n" +
            "    bytes = stream.to_io.read.to_java_bytes\n" +
            "    java.awt.Toolkit.default_toolkit.create_image(bytes)\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'synchronized void cleanup()'\n" +
            "  # This is called when a data set is closed, so that the extension can\n" +
            "  # let go of any references that it may have to the data or the GUI.\n" +
            "  #\n" +
            "  def cleanup\n" +
            "    STDERR.puts \"cleanup called\"\n" +
            "    if @console\n" +
            "      @console.dispose\n" +
            "      System.gc\n" +
            "    end\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'public void invoke(String uri)'\n" +
            "  # Process an invocation of this module with the given URI\n" +
            "  #\n" +
            "  def invoke uri\n" +
            "    # Setting universally accessible constants in JRuby runtime for Moneydance access\n" +
            "    # Not possible to do it in initialize since datafile is not yet loaded there\n" +
            "    Object.const_set :MD, @context\n" +
            "    Object.const_set :ROOT, MD.root_account\n" +
            "    Object.const_set :TRANS, ROOT.transaction_set\n" +
            "\n" +
            "    STDERR.puts \"invoke called with: #{uri}\"\n" +
            "    command, args = uri.split /[:?&]/\n" +
            "    send *[command, args].flatten.compact\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'synchronized void irb()'\n" +
            "  # Shows Moneydance IRB console, starts new one if necessary.\n" +
            "  # Moneydance URI: moneydance:fmodule:ruby:irb\n" +
            "  #\n" +
            "  def irb\n" +
            "    if @console\n" +
            "      @console.show\n" +
            "    else\n" +
            "      # We need to address RubyConsole via full java name... Why?\n" +
            "      @console ||= com.moneydance.modules.features.ruby.rb.RubyConsole.new self\n" +
            "    end\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'synchronized void file(String path)'\n" +
            "\n" +
            "  def file path\n" +
            "    STDERR.puts \"file called with: #{path}\"\n" +
            "    puts \"Loading file: #{path}\"\n" +
            "    begin\n" +
            "      load path\n" +
            "    rescue => e\n" +
            "      puts e.inspect\n" +
            "      puts e.backtrace.join(\"\\n\\tfrom \")\n" +
            "    end\n" +
            "  end\n" +
            "end\n" +
            "").toString();
        __ruby__.executeScript(source, "lib/ruby_main.rb");
        RubyClass metaclass = __ruby__.getClass("RubyMain");
        metaclass.setRubyStaticAllocator(RubyMain.class);
        if (metaclass == null) throw new NoClassDefFoundError("Could not load Ruby class: RubyMain");
        __metaclass__ = metaclass;
    }

    /**
     * Standard Ruby object constructor, for construction-from-Ruby purposes.
     * Generally not for user consumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    private RubyMain(Ruby ruby, RubyClass metaclass) {
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
        return new RubyMain(ruby, metaClass);
    }

    
    public  RubyMain(Object main, Object context, Object engine) {
        this(__ruby__, __metaclass__);
        IRubyObject ruby_main = JavaUtil.convertJavaToRuby(__ruby__, main);
        IRubyObject ruby_context = JavaUtil.convertJavaToRuby(__ruby__, context);
        IRubyObject ruby_engine = JavaUtil.convertJavaToRuby(__ruby__, engine);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize", ruby_main, ruby_context, ruby_engine);

    }

    
    public java.awt.Image icon(String action) {
        IRubyObject ruby_action = JavaUtil.convertJavaToRuby(__ruby__, action);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "icon", ruby_action);
        return (java.awt.Image)ruby_result.toJava(java.awt.Image.class);

    }

    
    public synchronized void cleanup() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "cleanup");
        return;

    }

    
    public void invoke(String uri) {
        IRubyObject ruby_uri = JavaUtil.convertJavaToRuby(__ruby__, uri);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "invoke", ruby_uri);
        return;

    }

    
    public synchronized void irb() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "irb");
        return;

    }

    
    public synchronized void file(String path) {
        IRubyObject ruby_path = JavaUtil.convertJavaToRuby(__ruby__, path);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "file", ruby_path);
        return;

    }

}
