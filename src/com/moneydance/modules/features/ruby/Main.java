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
        System.err.println("init() called for extension: " +
                getIDStr() + " by " + getVendor() + ", url: " + getVendorURL());
        try {
            rubyEngine = new RubyEngine(this);
            rubyMain = new RubyMain(this, getContext(), rubyEngine);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Get a descriptive name for this extension. This is called before Main#init(),
     * so it is not possible to delegate to RubyMain
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
        rubyMain.invoke(uri);
    }

    /**
     * This is called when a data set is closed, so that the extension can
     * let go of any references that it may have to the data or the GUI.
     */
    public void cleanup() {
        rubyMain.cleanup();
    }
}
