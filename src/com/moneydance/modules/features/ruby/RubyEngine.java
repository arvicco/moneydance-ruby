package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.controller.Util;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.jsr223.JRubyEngineFactory;

import javax.script.*;
import java.io.File;
import java.util.Properties;

/**
 *
 */
public class RubyEngine {
    private static String RubyType = "jsr";
    private Object ruby = null;
    private Main extension = null;

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
                // Initialize Ruby as JSR 233 (ScriptEngine)
                ScriptEngineManager manager = new ScriptEngineManager();

                System.err.println("manager: " + manager);
                System.err.println(manager.getEngineFactories());

                 ruby = manager.getEngineByName("jruby");
                if (ruby == null) {
                    ScriptEngineFactory factory = (ScriptEngineFactory) new JRubyEngineFactory();
                    ruby = factory.getScriptEngine();
                }
                Invocable invocable = (Invocable) ruby;
            } else {
                // Initialize Ruby as RedBridge (ScriptingContainer)
                ruby = new ScriptingContainer();
//                ruby.setLoadPaths(Arrays.asList(new String[]{"lib"}));
            }
            System.err.println("ruby: " + ruby);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby init");
        }
    }

    /**
    * Executes Ruby script given as a String. The default Scripting mechanism is used.
    *
    * @param script The script language source to be executed.
    *
    * @return The value returned from the execution of the script.
    *
    * @throws NullPointerException if the argument is null.
    * @throws javax.script.ScriptException if error occurs in script.
    */
   public Object eval(String script) throws ScriptException {
       Object result;

       System.err.println("ruby evals: " + script);

       if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
           //  Invoke Ruby via JSR 233 (ScriptEngine)
           result = ((ScriptEngine)ruby).eval(script);
       } else {
           // Invoke Ruby via RedBridge (ScriptingContainer)
           result = ((ScriptingContainer)ruby).runScriptlet(script);
       }
       return result;
   }
}
