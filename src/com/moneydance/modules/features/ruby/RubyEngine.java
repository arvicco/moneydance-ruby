package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.sun.tools.internal.ws.Invoker;
import org.jruby.embed.InvokeFailedException;
import org.jruby.embed.ScriptingContainer;

import org.jruby.embed.jsr223.JRubyEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class wraps JRuby scripting Engine or Container
 */
public class RubyEngine {
    private static String RubyType = "jsr";
    private Main extension;

    public RubyEngine(Main extension) {
        this.extension = extension;

        try {
            // Set up jruby-complete.jar and System path to it
            File rubyMxt = new File(Common.getFeatureModulesDirectory(), "ruby.mxt");
            File rubyJar = new File(Common.getFeatureModulesDirectory(), "jruby-complete.jar");
            Util.copyFile(rubyMxt, rubyJar);

            Properties props = System.getProperties();
            String classPath = props.getProperty("java.class.path");
            classPath = classPath + File.pathSeparator + rubyJar.getCanonicalPath();
            props.put("java.class.path", classPath);
            System.setProperties(props);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
                //  Invoke Ruby via JSR 233 (ScriptEngine)
                ScriptEngineManager manager = new ScriptEngineManager();

                System.err.println("manager: " + manager);
                System.err.println(manager.getEngineFactories());

                ScriptEngine ruby = manager.getEngineByName("jruby");
                if (ruby == null) {
                    ScriptEngineFactory factory = (ScriptEngineFactory) new JRubyEngineFactory();
                    ruby = factory.getScriptEngine();
                }
                Invocable invocable = (Invocable) ruby;

                System.err.println("ruby: " + ruby);
                System.err.println("eval: " + ruby.eval("'Hello from outside Ruby!'"));
                ruby.eval("puts 'Hello from Ruby!'");
                ruby.eval("STDERR.puts 'Hello from Ruby STDERR!'");
                ruby.eval("$LOAD_PATH << 'lib'");
                ruby.eval("STDERR.puts $LOAD_PATH");
                ruby.eval("require 'console'");

                Object archive = ruby.eval("Console.new");
            } else {
                // Invoke Ruby via RedBridge (ScriptingContainer)
                ScriptingContainer ruby = new ScriptingContainer();
//                ruby.setLoadPaths(Arrays.asList(new String[]{"lib"}));
                System.err.println("ruby: " + ruby);

                ruby.runScriptlet("puts 'Hello from Ruby!'");
                ruby.runScriptlet("STDERR.puts 'Hello from Ruby STDERR!!'");
                ruby.runScriptlet("require 'console'");
                Object console = ruby.runScriptlet("Console.new");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby init");
        }
    }
}
