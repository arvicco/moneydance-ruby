require 'rake/clean'
require 'ant'

CLEAN.include 'dist', 'build', 'src/**/rb' # Clean all compiled Ruby sources
CLOBBER.include 'doc'

namespace :ant do
  md_dir = "#{ENV["HOME"]}/.moneydance" # Moneydance user directory
  features = "#{md_dir}/fmodules/" # Features directory

  src = "src" # Java sources directory
  lib = "lib" # Ruby sources directory
  jars = "jars" # Jar dependensies directory
  doc = "doc" # Javadocs directory
  misc = "misc" # Misc files directory
  dist = "dist" # Final product directory
  build = "build" # Class files directory

  build_compiler = "classic"
  build_compiler_fulldepend = "true"
  privkeyfile = "#{misc}/priv_key"
  pubkeyfile = "#{misc}/pub_key"
  privkeyid = "99"
  debug = "on"
  optimize = "off"

  # TODO: OSX-specific, need to generalize
  md_command = "/Applications/Moneydance.app/Contents/MacOS/JavaApplicationStub " +
      "-invoke_and_quit moneydance:fmodule:ruby:irb" #runfile?=networth.py"
  tail_command = "iterm tail -f #{md_dir}/errlog.txt"

  def fileset_with src, build
    fileset :dir => src, :includes =>
        "com/moneydance/modules/features/ruby/meta_info.dict,
             com/moneydance/modules/features/ruby/*.gif,
             com/moneydance/modules/features/ruby/*.jpg,
             com/moneydance/modules/features/ruby/*.jpeg"
    fileset :dir => build, :includes => "com/moneydance/modules/features/ruby/**"
  end

  def classpath_with jars
    classpath do
      fileset :dir => jars, :includes => "*.jar"
    end
  end

  directory doc
  directory build
  directory dist

  desc 'Build the documentation'
  task :javadoc => doc do
    ant.javadoc :sourcefiles => FileList["#{src}/**/*.java"], :destdir => doc do
      classpath_with jars
    end
  end

  desc 'Compile Ruby sources'
  task :compile_ruby => [:clean, build] do
    sh "jrubyc -t #{src} --java #{lib}/**/*.rb"
  end

  desc 'Compile Java sources'
  task :compile_java => [:clean, build] do
    puts "Compiling in #{src}"
    ant.javac :srcdir => src, :destdir => build, :target =>"1.6",
              :debug => debug, :optimize => optimize,
              :includes => "com/moneydance/modules/features/ruby/**" do
      classpath_with jars
    end
  end

  desc 'Compile all sources'
  task :compile => [:compile_ruby, :compile_java]

  desc 'Default jar method'
  task :jar => 'jar:update'

  namespace :jar do
    desc 'Build jar bundle (jruby-complete is bundled into jar with your code)'
    task :bundle => [:compile, dist] do
      ant.jar :destfile => "#{dist}/ruby.mxt" do
        fileset_with src, build
        zipfileset :src => "jars/jruby-complete.jar"
      end
    end

    desc 'Update jruby-complete jar (your code is mixed into jruby-complete)'
    task :update => [:compile, dist] do
      ant.copy :file => "jars/jruby-complete.jar", :tofile => "#{dist}/jruby-complete.jar"
      ant.jar :destfile => "#{dist}/jruby-complete.jar", :update => true do
        fileset_with src, build
      end
      ant.move :file => "#{dist}/jruby-complete.jar", :tofile => "#{dist}/ruby.mxt"
    end

    # Fails with NoClassDefFoundError
    desc 'Separate jar (jruby-complete and your code as two separate jars)'
    task :separate => [:compile, dist] do
      ant.jar :destfile => "#{dist}/ruby.mxt" do
#      ant.jar :destfile => "#{dist}/ruby.mxt", :manifest => "small.manifest" do
        fileset_with src, build
      end
      ant.copy :file => "jars/jruby-complete.jar", :tofile => "#{dist}/jruby-complete.jar"
    end

    # Fails on load with NullPointerException
    desc 'Extract and repack jruby-complete with your code (via shell commands, not ant)'
    task :repack => [:compile, dist] do
      # Extract jruby-complete so we can combine it with the app
      Dir.chdir(build) do
        sh 'jar -xf ../jars/jruby-complete.jar'
      end
      sh "jar -cfm #{dist}/ruby.mxt #{misc}/small.manifest -C #{build} ."
    end
  end

  desc 'Sign jar package'
  task :sign => :jar do
    ant.java :newenvironment => "true",
             :classname => "com.moneydance.admin.KeyAdmin" do
      classpath_with jars
      arg :value => "signextjar"
      arg :value => privkeyfile
      arg :value => privkeyid
      arg :value => "ruby"
      arg :line => "#{dist}/ruby.mxt"
    end
    ant.move :file => "s-ruby.mxt", :tofile => "#{dist}/ruby.mxt"
  end

  desc 'Load extension package into Moneydance'
  task :load => :jar do
    ant.copy :file => "#{dist}/ruby.mxt", :tofile => "#{features}/ruby.mxt"
    sh "#{md_command}"
  end

  desc 'Start tracking Moneydance errors'
  task :tail do
    sh "#{tail_command}"
  end

  desc 'Generate keys'
  task :genkeys do
    ant.java :classname => "com.moneydance.admin.KeyAdmin" do
      classpath_with jars
      arg :value => "genkey"
      arg :value => privkeyfile
      arg :value => pubkeyfile
    end
  end
end