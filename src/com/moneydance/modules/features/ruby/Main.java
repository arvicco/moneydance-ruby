package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.*;
import com.moneydance.apps.md.model.*;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.jsr223.JRubyEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;

// Imports compiled Ruby code
import com.moneydance.modules.features.ruby.rb.*;

/**
 * Pluggable Moneydance module used to give users access to Ruby scripting interface.
 */

public class Main
        extends FeatureModule {

    private RubyConsole rubyConsole = null;
    private RubyEngine ruby = null;

    public static void main(String[] args) {
        Main ext = new Main();
        ext.init();
        ext.showConsole();
    }

    public void init() {
        // Register this module to be invoked via the application toolbar
        FeatureModuleContext context = getContext();
        try {
            ruby = new RubyEngine(this);
            ruby.eval("puts 'Hello from Ruby!'");
            ruby.eval("$LOAD_PATH << 'lib'");

            context.registerFeature(this, "showconsole",
                    getIcon("accountlist"),
                    getName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void cleanup() {
        if (rubyConsole != null) {
            rubyConsole.dispose();
            rubyConsole = null;
            System.gc();
        }
    }
//    synchronized void closeConsole() {

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
     * Process an invocation of this module with the given URI
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

    /**
     * Shows Moneydance IRB console
     */
    private synchronized void showConsole() {

        if (rubyConsole == null) {
            rubyConsole = new RubyConsole(this, getContext());
            Thread t = new Thread((Runnable) rubyConsole);
            t.start();
            try {
                if (!EventQueue.isDispatchThread()) {
                    // Only join in `rake ant:load` session, not inside Moneydance GUI
                    t.join();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            rubyConsole.show();
        }
    }

    FeatureModuleContext getUnprotectedContext() {
        return getContext();
    }
}
