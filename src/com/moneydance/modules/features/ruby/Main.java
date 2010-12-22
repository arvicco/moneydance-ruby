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

    /**
     * Initialize the extension. This is called just after the module is loaded.
     * At this point the moneydance UI has already been loaded (but is not necessarily
     * visible) and the data file may or may not have been loaded.
     */
    public void init() {
        System.err.println("init() called...");
        System.err.println(getIDStr()+ getVendor() + getVendorURL());
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

    /**
     * Get a descriptive name for this extension. This is called before Main#init(),
     * so it is not possible to delegate to RubyMain
     *
     */
     public String getName() {
        return "Ruby Interface";
    }

    /**
     * Get an icon image that represents this extension and can be used in a button bar.
     */
    public Image getIconImage() {
        return rubyMain.icon("ruby");
    }

    /**
     * This is called when an extension is invoked using a moneydance URI.
     * Only the part of the URI after the extension identifier is included in the URI.
     * For example, if the following URI is invoked:
     * moneydance:module:taxman:show_summary:year=1999
     * then the invoke method of the "taxman" extension is called with the uri \set to:
     * "show_summary:year=1999"
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
