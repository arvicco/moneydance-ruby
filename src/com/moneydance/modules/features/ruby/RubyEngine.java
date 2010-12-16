package com.moneydance.modules.features.ruby;

import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.controller.Util;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.jsr223.JRubyEngineFactory;
import org.jruby.javasupport.JavaEmbedUtils;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.*;

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

    public RubyEngine() throws javax.script.ScriptException, IOException {
        // Set up jruby-complete.jar and System path to it
        File featureDir = Common.getFeatureModulesDirectory();
        File rubyJar = new File(featureDir, "jruby-complete.jar");

        String classPath = System.getProperty("java.class.path");
        classPath = classPath + File.pathSeparator + rubyJar.getCanonicalPath();
        System.setProperty("java.class.path", classPath);

        //
        ScriptingContainer ruby = new ScriptingContainer();
        ruby.setClassLoader(ruby.getClass().getClassLoader());
        ruby.runScriptlet("puts $LOAD_PATH");
        ruby.runScriptlet("require 'irb'");
        ruby.runScriptlet("require 'java'");

        // Initialize Ruby as JSR 233 (ScriptEngine)
//        ScriptEngineFactory factory = (ScriptEngineFactory) new JRubyEngineFactory();
//        ScriptEngine ruby = factory.getScriptEngine();
//        ruby.eval("puts $LOAD_PATH");
//        ruby.eval("require 'irb'");
        this.ruby = ruby;
    }

    public RubyEngine(Main extension) {
        this.extension = extension;
        try {
            // Set up jruby-complete.jar and System path to it
            File featureDir = Common.getFeatureModulesDirectory();
            File rubyMxt = new File(featureDir, "ruby.mxt");
            File rubyJar = new File(featureDir, "jruby-complete.jar");
            Util.copyFile(rubyMxt, rubyJar);

            Properties props = System.getProperties();
            String classPath = props.getProperty("java.class.path");
            classPath = classPath + File.pathSeparator + rubyJar.getCanonicalPath();
            props.put("java.class.path", classPath);
            System.setProperties(props);
            System.err.println("classpath: " + System.getProperty("java.class.path"));

            String jrubyhome =
                    "file:" + rubyJar.getCanonicalPath() + "!/META-INF/jruby.home";
            String jrubybin =
                    "file:" + rubyJar.getCanonicalPath() + "!/META-INF/jruby.home/bin";

            if (RubyType.equals("jsr") || RubyType.equals("jsr233")) {

//                System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
////                classPath = System.getProperty("org.jruby.embed.class.path");
////                System.out.println("rubyJar: " + rubyJar.getCanonicalPath());
////                System.out.println("org.jruby.embed.class.path: " + classPath);
////                System.setProperty("org.jruby.embed.class.path", jrubyhome + "/bin");
//                System.setProperty("jruby.home", jrubyhome);

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
                System.err.println("context: " + ((ScriptEngine) ruby).getContext());

                ((ScriptEngine) ruby).getContext().setAttribute(
                        ScriptEngine.FILENAME,
                        jrubyhome + "/bin/jirb_swing",
                        ScriptContext.ENGINE_SCOPE);
                eval("$LOAD_PATH << '" + jrubybin + "'");
                eval("puts $LOAD_PATH");
                eval("load 'jirb_swing'");

//                tar = org.jruby.demo.TextAreaReadline.new(text,
//                "Welcome to the JRuby IRB Console [#{JRUBY_VERSION}] \n\n"


                eval("require 'java' #'jruby'                                          \n " +
                        "  require 'irb'                                                 \n " +
                        "  require 'irb/completion'                                      \n " +

                        "  if ARGV.empty?                                                 \n " +
                        "    # default options, esp. useful for jrubyw                    \n " +
                        "    ARGV << '--readline' << '--prompt' << 'inf-ruby'             \n " +
                        "  end                                                            \n " +

                        "  import java.awt.Color                                           \n " +
                        "  import java.awt.Font                                            \n " +
                        "  import javax.swing.JFrame                                       \n " +
                        "  import java.awt.EventQueue                                      \n " +

                        "  # Try to find preferred font family, use otherwise -- err --  otherwise                      \n " +
                        "  def find_font(otherwise, style, size, *families)                                             \n " +
                        "    avail_families = java.awt.GraphicsEnvironment.local_graphics_environment.available_font_family_names\n " +
                        "    fontname = families.find(proc {otherwise}) { |name| avail_families.include? name }  \n " +
                        "    Font.new(fontname, style, size)                                                  \n " +
                        "  end                                                                             \n " +

                        "  text = javax.swing.JTextPane.new                                             \n " +
                        "  text.font = find_font('Monospaced', Font::PLAIN, 14, 'Monaco', 'Andale Mono');" +
                        "  text.margin = java.awt.Insets.new(8,8,8,8)                              \n " +
                        "  text.caret_color = Color.new(0xa4, 0x00, 0x00)                         \n " +
                        "  text.background = Color.new(0xf2, 0xf2, 0xf2)                         \n " +
                        "  text.foreground = Color.new(0xa4, 0x00, 0x00)                        \n " +

                        "  pane = javax.swing.JScrollPane.new                                   \n " +
                        "  pane.viewport_view = text                                           \n " +
                        "  frame = JFrame.new('JRuby IRB Console (tab will autocomplete)')    \n " +
                        "  frame.default_close_operation = JFrame::EXIT_ON_CLOSE              \n " +
                        "  frame.set_size(700, 600)                                           \n " +
                        "  frame.content_pane.add(pane)                                       \n " +
//                                "  tar = org.jruby.demo.TextAreaReadline.new(text,                    \n " +
//                                "        ' Welcome to the JRuby IRB Console [#{JRUBY_VERSION}] \n\n') \n " +
                        "  JRuby.objectspace = true # useful for code completion              \n " +
                        "  tar.hook_into_runtime_with_streams(JRuby.runtime)                  \n " +
                        "                                                                     \n " +
                        "  # We need to show the frame on EDT,                               \n " +
                        "  # to avoid deadlocks.                                             \n " +

                        "   # Once JRUBY-2449 is fixed,                                       \n " +
                        "   # the code will me simplifed.                                     \n " +
                        "   class FrameBringer                                                \n " +
                        "     include java.lang.Runnable                                      \n " +
                        "     def initialize(frame)                                           \n " +
                        "       @frame = frame                                                \n " +
                        "     end                                                             \n " +
                        "     def run                                                         \n " +
                        "       @frame.visible = true                                         \n " +
                        "     end                                                             \n " +
                        "   end                                                               \n " +

                        "   EventQueue.invoke_later(FrameBringer.new(frame))                  \n " +

                        "   # From vanilla IRB                                                 \n " +
                        "   if __FILE__ == $0                                                  \n " +
                        "     IRB.start(__FILE__)                                             \n " +
                        "   else                                                              \n " +
                        "     # check -e option                                               \n " +
                        "     if /^-e$/ =~ $0                                                 \n " +
                        "       IRB.start(__FILE__)                                           \n " +
                        "     else                                                            \n " +
                        "       IRB.setup(__FILE__)                                           \n " +
                        "     end                                                             \n " +
                        "   end                                                               \n " +
                        "   frame.dispose");


            } else {
                // Initialize Ruby as RedBridge (ScriptingContainer)
                ScriptingContainer ruby = new ScriptingContainer();// LocalContextScope.SINGLETHREAD);
//                ruby.setLoadPaths(Arrays.asList(new String[]{"lib"}));
                ruby.setClassLoader(ruby.getClass().getClassLoader());

                this.ruby = ruby;
//                     jrubyhome = ruby.getHomeDirectory();
//                     String[] paths = {jrubyhome + "/bin"};
//                     ruby.setLoadPaths(Arrays.asList(paths));       // add "bin" directory to $LOAD_PATH
                String[] paths = {jrubybin};
//                     ruby.setLoadPaths(Arrays.asList(paths));       // add "bin" directory to $LOAD_PATH
                String jirb_swing = jrubyhome + "/bin/jirb_swing";
//                     eval("$LOAD_PATH << '" + jrubybin + "'");
                ruby.setScriptFilename(jirb_swing);    //  equivalent to "-S /path/to/jirb_swing" option
                eval("load '" + jirb_swing + "'");
//                eval("load 'jirb_swing'");

            }
            System.err.println("ruby: " + ruby);
        } catch (
                Exception e
                )

        {
            e.printStackTrace(System.err);
            System.err.println("Caught exception during JRuby init");
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
