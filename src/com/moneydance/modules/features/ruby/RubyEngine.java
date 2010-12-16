package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.controller.Util;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.jsr223.JRubyEngineFactory;

import javax.script.*;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class RubyEngine {
    private static String RubyType = "jsra";
    private Object ruby = null;
    private Main extension = null;

    public static void main(String[] args) throws ScriptException, IOException {
        RubyEngine ext = new RubyEngine(new Main());
    }

    public RubyEngine(Main extension) {
        this.extension = extension;
        try {
            // Set up jruby-complete.jar and System path to it
            File featureDir = Common.getFeatureModulesDirectory();
            File rubyJar = new File(featureDir, "jruby-complete.jar");
            if (!rubyJar.exists()) {
                File rubyMxt = new File(featureDir, "ruby.mxt");
                Util.copyFile(rubyMxt, rubyJar);
            }

            String path = System.getProperty("java.class.path");
            path += File.pathSeparator + rubyJar.getCanonicalPath();
            System.setProperty("java.class.path", path);
            System.err.println("classpath: " + System.getProperty("java.class.path"));

            String jrubyhome = "file:" + rubyJar.getCanonicalPath() + "!/META-INF/jruby.home";
            String jrubybin = "file:" + rubyJar.getCanonicalPath() + "!/META-INF/jruby.home/bin";
            String jirb_swing = jrubyhome + "/bin/jirb_swing";
            System.setProperty("jruby.home", jrubyhome);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {

//                System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
//                classPath = System.getProperty("org.jruby.embed.class.path");
//                System.out.println("rubyJar: " + rubyJar.getCanonicalPath());
//                System.out.println("org.jruby.embed.class.path: " + classPath);
//                System.setProperty("org.jruby.embed.class.path", jrubyhome + "/bin");
//                System.setProperty("jruby.home", jrubyhome);

                // Initialize Ruby as JSR 233 (ScriptEngine)
                ScriptEngineFactory factory = (ScriptEngineFactory) new JRubyEngineFactory();
                ruby = factory.getScriptEngine();
                Invocable invocable = (Invocable) ruby;

                System.err.println("context: " + ((ScriptEngine) ruby).getContext());
            } else {
                // Initialize Ruby as RedBridge (ScriptingContainer)
                ScriptingContainer ruby = new ScriptingContainer();// LocalContextScope.SINGLETHREAD);
                ruby.setClassLoader(ruby.getClass().getClassLoader());

                this.ruby = ruby;
                System.err.println("jrubyhome: " + ruby.getHomeDirectory());
//                jrubyhome = ruby.getHomeDirectory();
//                String[] paths = {jrubyhome + "/bin"};
//                ruby.setLoadPaths(Arrays.asList(paths));       // add "bin" directory to $LOAD_PATH
//                ruby.setLoadPaths(Arrays.asList(new String[]{"lib"}));
            }

            System.err.println("ruby: " + ruby);
            eval("$LOAD_PATH << '" + jrubybin + "'");
            eval("puts $LOAD_PATH");
            eval("load 'jirb_swing'", jirb_swing);

        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby init");
        }
    }

    /**
     * Executes Ruby script given as a String (via default Scripting mechanism), with
     * scriptName is substituted for __FILE__, roughly equivalent to "-S scriptName".
     *
     * @param script     The script language source to be executed.
     * @param scriptName Name given to the executed script.
     * @return The value returned from the execution of the script.
     * @throws NullPointerException if the argument is null.
     */
    public Object eval(String script, String scriptName) {
        Object result;
        try {
            System.err.println("ruby evals (" + "scriptName" + "): " + script);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
                //  Invoke Ruby via JSR 233 (ScriptEngine)
                ((ScriptEngine) ruby).getContext().setAttribute(
                        ScriptEngine.FILENAME, scriptName, ScriptContext.ENGINE_SCOPE);
                result = ((ScriptEngine) ruby).eval(script);
            } else {
                // Invoke Ruby via RedBridge (ScriptingContainer)
                ((ScriptingContainer) ruby).setScriptFilename(scriptName);
                result = ((ScriptingContainer) ruby).runScriptlet(script);
            }
            return result;
        } catch (ScriptException e) {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby eval of " + scriptName);
            return null;
        }
    }

    /**
     * Executes Ruby script given as a String. The default Scripting mechanism is used.
     *
     * @param script The script language source to be executed.
     * @return The value returned from the execution of the script.
     * @throws NullPointerException         if the argument is null.
     * @throws javax.script.ScriptException if error occurs in script.
     */
    public Object eval(String script) {
        Object result;
        try {
            System.err.println("ruby evals: " + script);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
                //  Invoke Ruby via JSR 233 (ScriptEngine)
                result = ((ScriptEngine) ruby).eval(script);
            } else {
                // Invoke Ruby via RedBridge (ScriptingContainer)
                result = ((ScriptingContainer) ruby).runScriptlet(script);
            }
            return result;
        } catch (ScriptException e) {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby eval");
            return null;
        }
    }
}
