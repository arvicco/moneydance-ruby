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
 * Wrapper around Ruby Scripting Engine or Container exposing unified interface.
 */
public class RubyEngine {
    private static String RubyType = "container";
    private Main extension = null;
    public ScriptEngine engine = null;
    public Invocable invocable = null;
    public ScriptingContainer container = null;
    public String home = null;

    public static void main(String[] args) throws ScriptException, IOException {
        RubyEngine ext = new RubyEngine(new Main());
    }

    public RubyEngine(Main extension) {
        this.extension = extension;
        try {
            // Set up jruby-complete.jar in Moneydance features dir
            File featureDir = Common.getFeatureModulesDirectory();
            File rubyJar = new File(featureDir, "jruby-complete.jar");
            File rubyMxt = new File(featureDir, "ruby.mxt");
            Util.copyFile(rubyMxt, rubyJar);

            // Add System path to jruby-complete.jar
            String path = System.getProperty("java.class.path");
            path += File.pathSeparator + rubyJar.getCanonicalPath();
            System.setProperty("java.class.path", path);
            System.err.println("classpath: " + System.getProperty("java.class.path"));

            //Set up jruby.home
            home = "file:" + rubyJar.getCanonicalPath() + "!/META-INF/jruby.home";
            System.setProperty("jruby.home", home);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
//                System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
//                classPath = System.getProperty("org.jruby.embed.class.path");
//                System.out.println("rubyJar: " + rubyJar.getCanonicalPath());
//                System.out.println("org.jruby.embed.class.path: " + classPath);
//                System.setProperty("org.jruby.embed.class.path", jrubyhome + "/bin");
//                System.setProperty("jruby.home", jrubyhome);

                // Initialize Ruby as JSR 233 (ScriptEngine)
                ScriptEngineFactory factory = (ScriptEngineFactory) new JRubyEngineFactory();
                engine = factory.getScriptEngine();
                invocable = (Invocable) engine;

                System.err.println("context: " + engine.getContext());
            } else {
                // Initialize Ruby as RedBridge (ScriptingContainer)
                container = new ScriptingContainer(LocalContextScope.THREADSAFE);// LocalContextScope.SINGLETHREAD);

                // Assign classloader since MD loader causes path problems
                container.setClassLoader(container.getClass().getClassLoader());
                eval("2");
                System.err.println("jrubyhome: " + container.getHomeDirectory());
//                jrubyhome = container.getHomeDirectory();
//                String[] paths = {jrubyhome + "/bin"};
//                container.setLoadPaths(Arrays.asList(paths));       // add "bin" directory to $LOAD_PATH
//                container.setLoadPaths(Arrays.asList(new String[]{"lib"}));
            }
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
            System.err.println("ruby evals (" + scriptName + "): " + script);

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {
                //  Invoke Ruby via JSR 233 (ScriptEngine)
                engine.getContext().setAttribute(
                        ScriptEngine.FILENAME, scriptName, ScriptContext.ENGINE_SCOPE);
                result = engine.eval(script);
            } else {
                // Invoke Ruby via RedBridge (ScriptingContainer)
                container.setScriptFilename(scriptName);
                result = container.runScriptlet(script);
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
     * @throws NullPointerException if the argument is null.
     */
    public Object eval(String script) {
        return eval(script, "script");
    }
}
