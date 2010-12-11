require 'rake/clean'
require 'ant'

CLEAN.include 'dist', 'build'
CLOBBER.include 'doc'

namespace :ant do
  version = "2.2"
  src = "src"
  dist = "dist"
  tmp = "tmp"
  misc = "misc"
  lib = "lib"
  doc = "doc"
  build = "build"
  build_compiler = "classic"
  build_compiler_fulldepend = "true"
  privkeyfile = "#{misc}/priv_key"
  pubkeyfile = "#{misc}/pub_key"
  privkeyid = "99"
  debug = "on"
  optimize = "off"

  def fileset_with src, build
    fileset :dir => src, :includes =>
        "com/moneydance/modules/features/ruby/meta_info.dict,
             com/moneydance/modules/features/ruby/*.gif,
             com/moneydance/modules/features/ruby/*.jpg,
             com/moneydance/modules/features/ruby/*.jpeg"
    fileset :dir => build, :includes => "com/moneydance/modules/features/ruby/**"
  end

  def classpath_with lib
    classpath do
      fileset :dir => lib, :includes => "*.jar"
    end
  end

  directory doc
  directory build
  directory dist

  desc 'Build the documentation'
  task :javadoc => doc do
    ant.javadoc :sourcefiles => FileList["#{src}/**/*.java"], :destdir => doc do
      classpath_with lib
    end
  end

  desc 'Compile sources'
  task :compile => [:clean, build] do
    puts "Compiling in #{src}"
    ant.javac :srcdir => src, :destdir => build, :target =>"1.6",
              :debug => debug, :optimize => optimize,
              :includes => "com/moneydance/modules/features/ruby/**" do
      classpath_with lib
    end
  end

  desc 'Build jar package (jruby-complete bundled)'
  task :jar_bundle => [:compile, dist] do
    ant.jar :destfile => "#{dist}/ruby.mxt" do
      fileset_with src, build
      zipfileset :src => "lib/jruby-complete.jar"
    end
  end

  desc 'Update jar package (jruby-complete)'
  task :jar_update => [:compile, dist] do
    ant.copy :file => "lib/jruby-complete.jar", :tofile => "#{dist}/jruby-complete.jar"
    ant.jar :destfile => "#{dist}/jruby-complete.jar", :update => true do
      fileset_with src, build
    end
    ant.move :file => "#{dist}/jruby-complete.jar", :tofile => "#{dist}/ruby.mxt"
  end

  # Fails on try_ruby with NoClassDefFoundError
  desc 'Build jar package (jruby-complete as a separate jar)'
  task :jar_separate => [:compile, dist] do
    ant.jar :destfile => "#{dist}/ruby.mxt" do
#      ant.jar :destfile => "#{dist}/ruby.mxt", :manifest => "small.manifest" do
      fileset_with src, build
    end
    ant.copy :file => "lib/jruby-complete.jar", :tofile => "#{dist}/jruby-complete.jar"
  end

  # Fails on load with NullPointerException
  desc 'Build jar package (via shell commands, not ant)'
  task :jar_shell => [:compile, dist] do
    # Extract jruby-complete so we can combine it with the app
    Dir.chdir(build) do
      sh 'jar -xf ../lib/jruby-complete.jar'
    end
    sh "jar -cfm #{dist}/ruby.mxt small.manifest -C #{build} ."
  end

  desc 'Sign jar package'
  task :sign => :jar_update do
    ant.java :newenvironment => "true",
             :classname => "com.moneydance.admin.KeyAdmin" do
      classpath_with lib
      arg :value => "signextjar"
      arg :value => privkeyfile
      arg :value => privkeyid
      arg :value => "ruby"
      arg :line => "#{dist}/ruby.mxt"
    end
    ant.move :file => "s-ruby.mxt", :tofile => "#{dist}/ruby.mxt"
  end

  desc 'Generate keys'
  task :genkeys do
    ant.java :classname => "com.moneydance.admin.KeyAdmin" do
      classpath_with lib
      arg :value => "genkey"
      arg :value => privkeyfile
      arg :value => pubkeyfile
    end
  end
end