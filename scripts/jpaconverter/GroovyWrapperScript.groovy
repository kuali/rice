/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Wrap a script and groovy jars to an executable jar
 */
def cli = new CliBuilder()
cli.h( longOpt: 'help', required: false, 'show usage information' )
cli.d( longOpt: 'destfile', argName: 'destfile', required: false, args: 1, 'jar destintation filename, defaults to {mainclass}.jar' )
cli.m( longOpt: 'mainclass', argName: 'mainclass', required: true, args: 1, 'fully qualified main class, eg. HelloWorld' )
cli.c( longOpt: 'groovyc', required: false, 'Run groovyc' )
cli.l( longOpt: 'addjar', argName: 'Add JAR', required: false, args: 1, 'Add additional library jar file to wrapper jar.  (May be repeated)' )

//--------------------------------------------------------------------------
def opt = cli.parse(args)
if (!opt) {
	return
}
if (opt.h) {
	cli.usage();
	return
}

def mainClass = opt.m
def scriptBase = mainClass.replace( '.', '/' )
def scriptFile = new File( scriptBase + '.groovy' )
if (!scriptFile.canRead()) {
   println "Cannot read script file: '${scriptFile}'"
   return
}
def destFile = scriptBase + '.jar'
if (opt.d) {
  destFile = opt.d
}

//--------------------------------------------------------------------------
def ant = new AntBuilder()

def compileClasspath = []

if ( opt.l ) {
	opt.ls.each() { lib ->
		File libFile = new File( lib )
		println "Lib: $libFile.canonicalPath"
		if ( !libFile.canRead() ) {
			ant.echo( "Unable to read additional library file: $lib" )
			System.exit(-1)
		}
		compileClasspath += libFile.canonicalPath
	}
}


if (opt.c) {
  ant.echo( "Compiling ${scriptFile}" )
  org.codehaus.groovy.tools.FileSystemCompiler.main( [ scriptFile, "--classpath", compileClasspath.join(":") ] as String[] )
}
println "User Directory (user.dir)            : ${System.getProperty('user.dir')}"
println "Current Directory (.)                : ${new File(".").canonicalPath}"
println "User Home (user.home)                : ${System.getProperty('user.home')}"
println "Java Home (java.home)                : ${System.getProperty('java.home')}"
println "Groovy Home (groovy.home)            : ${System.getProperty('groovy.home')}"
println "Temporary Directory (java.io.tmpdir) : ${System.getProperty('java.io.tmpdir')}"

def GROOVY_HOME = new File( System.getProperty('groovy.home') )
if (!GROOVY_HOME.canRead()) {
  ant.echo( "Unable to read groovy home directory: '${System.getProperty('groovy.home')}'" )
  return
}


ant.jar( destfile: destFile, compress: true, index: true ) {
  fileset( dir: '.', includes: scriptBase + '*.class' )

  zipgroupfileset( dir: GROOVY_HOME, includes: 'embeddable/groovy-all-*.jar', excludes: 'embeddable/groovy-all-*-indy.jar' )
  zipgroupfileset( dir: GROOVY_HOME, includes: 'lib/commons*.jar' )
  if ( opt.l ) {
	  opt.ls.each() { lib ->
		  zipgroupfileset( dir: new File( lib ).parentFile.canonicalPath, includes: new File( lib ).name )
	  }
  }
  // add more jars here

  manifest {
	attribute( name: 'Main-Class', value: mainClass )
  }
}

ant.echo( "Run script using: \'java -jar ${destFile} ...\'" )