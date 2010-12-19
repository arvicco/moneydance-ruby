package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.*;
import com.moneydance.apps.md.model.*;

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

    public void init() {
        // Register this module to be invoked via the application toolbar
        FeatureModuleContext context = getContext();
        try {
            ruby = new RubyEngine(this);
            ruby.eval("puts 'Hello from Ruby!'");

            context.registerFeature(this, "showconsole",
                    getIcon("accountlist"),
                    getName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This is called when a data set is closed, so that the extension can
     * let go of any references that it may have to the data or the GUI.
     */
    public void cleanup() {
    //  Was:  synchronized void closeConsole()
        if (rubyConsole != null) {
            rubyConsole.dispose();
            rubyConsole = null;
            System.gc();
        }
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

    /**
     * Shows Moneydance IRB console, starts new one if necessary
     */
    private synchronized void showConsole() {
        if (rubyConsole == null) {
            rubyConsole = RubyConsole.start(this, getContext());
        } else {
            rubyConsole.show();
        }
    }

    public String getName() {
        return "Ruby Interface";
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
}
