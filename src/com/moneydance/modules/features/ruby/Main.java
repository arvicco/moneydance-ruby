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

/**
 * Pluggable Moneydance module used to give users access to Ruby scripting interface.
 */

public class Main
        extends FeatureModule {

    private AccountListWindow accountListWindow = null;
    private RubyEngine ruby = null;

    public static void main(String[] args) {
        Main ext = new Main();
        ext.init();
    }

    public void init() {
        // Register this module to be invoked via the application toolbar
        FeatureModuleContext context = getContext();
        try {
            ruby = new RubyEngine(this);
            System.err.println("eval: " + ruby.eval("'Hello from outside Ruby!'"));
            ruby.eval("puts 'Hello from Ruby!'");
            ruby.eval("STDERR.puts 'Hello from Ruby STDERR!'");
            ruby.eval("$LOAD_PATH << 'lib'");
            ruby.eval("STDERR.puts $LOAD_PATH");
            ruby.eval("require 'console'");

            Object archive = ruby.eval("Console.new");

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
     * Process an invocation of this module with the given URI
     */
    public void invoke(String uri) {
        System.err.println("ERR JRuby invoke");
        System.out.println("OUT JRuby invoke");
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
