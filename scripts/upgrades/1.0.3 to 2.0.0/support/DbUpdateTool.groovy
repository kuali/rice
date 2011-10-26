import javax.swing.filechooser.FileNameExtensionFilter
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database update tool that supports multiple pluggable actions.
 */

abstract class DbCommand {
    /**
     * If we had metadata that identified db version, this would be the place to check
     * whether the command applied
     */
    def boolean isValid(Connection c) { true }
    def abstract help()
    def abstract perform(Connection c, List<String> args)
}

COMMANDS = [:]

def File getScriptPath() {
    def url = this.class.protectionDomain.codeSource.location
    try {
        new File(url.toURI())
    } catch(URISyntaxException e) {
        new File(url.getPath())
    }
}

def void enumerate_scripts(File dir, String pattern, Closure c = { -> }) {
    dir.listFiles([accept: { file, filename -> filename.endsWith(pattern) }] as FilenameFilter).each {
        def class_name = (it.name =~ /(.*)${pattern}$/)[0][1]
        c.call(class_name)
    }
}

def Map loadCommands(File dir) {
    def commands = [:]
    enumerate_scripts(dir, "Command.groovy", {
      name ->
        println "Loading command " + name
        commands[name] = Class.forName(name + "Command", true, this.class.classLoader)
    })
    commands
}

def DbCommand createCommand(String name) {
    COMMANDS[name].newInstance()
}

def loadDrivers() {
    ["com.mysql.jdbc.Driver", "oracle.jdbc.OracleDriver"].each {
        try {
            Class.forName(it)
        } catch (Exception e) {
            println e
        }
    }
}

def script_dir = getScriptPath().parent
def lib_dir = new File(script_dir, 'lib')

// add 'lib' dir to classloader
//this.class.classLoader.rootLoader.addURL(lib_dir.toURL())
this.class.classLoader.addURL(lib_dir.toURL())

COMMANDS = loadCommands(lib_dir)

if (args.length < 4) {
    println 'usage: groovy DbUpdateTool.groovy <jdbc url> <username> <pass> <command> <args>'
    COMMANDS.each { key, value ->
        printf("%-40.40s %s\n", key, createCommand(key).help())
    }
    return
}

def url = args[0]
def user = args[1]
def pass = args[2]
def command_name = args[3]
def command_args = args.length > 4 ? args[4..-1] : []

loadDrivers()
def con = DriverManager.getConnection(url, user, pass)

def command = createCommand(command_name)
if (!command.isValid(con)) {

} else {
    command.perform(con, args as List<String>)
}