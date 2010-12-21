package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.*;
import com.moneydance.apps.md.model.*;

import java.awt.*;

// Imports compiled Ruby code
import com.moneydance.modules.features.ruby.rb.*;

/**
 * Pluggable Moneydance module used to give users access to Ruby scripting interface.
 */

public class Main
        extends FeatureModule {

    private RubyEngine rubyEngine = null;
    private RubyMain rubyMain = null;

    public void init() {
        System.err.println("init() called...");
        // Register this module to be invoked via the application toolbar
        FeatureModuleContext context = getContext();
        try {
            System.err.println("user.dir: " + System.getProperty("user.dir"));
            rubyEngine = new RubyEngine(this);
            // We need to kick Ruby runtime to wake it up. Without running Ruby runtime,
            // compiled Ruby fails on org.jruby.Ruby.getGlobalRuntime() call
            rubyEngine.eval("STDERR.puts 'Starting Moneydance Ruby runtime...'");
            rubyMain = new RubyMain(this, context, rubyEngine);
//            context.registerFeature(this, "irb", getIcon(), getName());

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public String getName() {
        // Main#getName() is accessed before Main#init, impossible to delegate
        return "Ruby Interface";
    }

    public Image getIcon() {
        return rubyMain.icon("ruby");
    }

    /**
     * Process an invocation of this module with the given URI
     */
    public void invoke(String uri) {
        System.err.println("invoke() called...");
        rubyMain.invoke(uri);
    }

    /**
     * This is called when a data set is closed, so that the extension can
     * let go of any references that it may have to the data or the GUI.
     */
    public void cleanup() {
        System.err.println("cleanup() called...");
        rubyMain.cleanup();
    }
}
