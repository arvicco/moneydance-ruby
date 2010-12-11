package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.*;
import com.moneydance.apps.md.model.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;

import java.util.Arrays;
import java.util.List;

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
 * Pluggable Moneydance module used to give users access to Ruby scripting interface.
 */

public class Main
        extends FeatureModule {
    private static String RubyType = "jsr";

    private AccountListWindow accountListWindow = null;

    public static void main(String[] args) {
        init_ruby(RubyType);
    }

    public static void init_ruby(String type) {
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

            if (type.equals("jsr") || type.equals("jsr233")) {
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

    public void init() {
        // Register this module to be invoked via the application toolbar
        FeatureModuleContext context = getContext();
        try {
            init_ruby(RubyType);
            context.registerFeature(this, "showconsole",
                    getIcon("accountlist"),
                    getName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void cleanup() {
        closeConsole();
    }

    private Image getIcon(String action) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            java.io.InputStream in =
                    cl.getResourceAsStream("/com/moneydance/modules/features/ruby/icon.gif");
            if (in != null) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
                byte buf[] = new byte[256];
                int n = 0;
                while ((n = in.read(buf, 0, buf.length)) >= 0)
                    bout.write(buf, 0, n);
                return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
            }
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * Process an invokation of this module with the given URI
     */
    public void invoke(String uri) {
        String command = uri;
        String parameters = "";
        int theIdx = uri.indexOf('?');
        if (theIdx >= 0) {
            command = uri.substring(0, theIdx);
            parameters = uri.substring(theIdx + 1);
        } else {
            theIdx = uri.indexOf(':');
            if (theIdx >= 0) {
                command = uri.substring(0, theIdx);
            }
        }

        if (command.equals("showconsole")) {
            showConsole();
        }
    }

    public String getName() {
        return "Ruby Interface";
    }

    private synchronized void showConsole() {
        if (accountListWindow == null) {
            accountListWindow = new AccountListWindow(this);
            accountListWindow.setVisible(true);
        } else {
            accountListWindow.setVisible(true);
            accountListWindow.toFront();
            accountListWindow.requestFocus();
        }
    }

    FeatureModuleContext getUnprotectedContext() {
        return getContext();
    }

    synchronized void closeConsole() {
        if (accountListWindow != null) {
            accountListWindow.goAway();
            accountListWindow = null;
            System.gc();
        }
    }
}
