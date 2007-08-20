
// Just add comma separated values for ignored DDL
ignores = ['FS_UNIVERSAL_USR_T']

PROJECT_DIR = '/java/projects/rice'
//PROJECT_DIR = '/Users/natjohns/eclipse-3.3-workspace/rice'

// Do the KNS DDL           
createdrops(PROJECT_DIR + '/kns/src/main/config/ddl/sequences')
createdrops(PROJECT_DIR + '/kns/src/main/config/ddl/tables')

// Do the KEW DDL
createdrops(PROJECT_DIR + '/kew/src/main/config/ddl/sequences')
createdrops(PROJECT_DIR + '/kew/src/main/config/ddl/tables')

// Do the KSB DDL Drops
createdrops(PROJECT_DIR + '/ksb/src/main/config/ddl/tables')

// Do the KIM DDL
createdrops(PROJECT_DIR + '/kim/src/main/config/ddl/sequences')
createdrops(PROJECT_DIR + '/kim/src/main/config/ddl/tables')

System.exit(0)



def createdrops(dir) {
	def p = ~/.*\.ddl/
	new File(dir).eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! ignores.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln -> 
		        if (ln.trim().toUpperCase().startsWith("CREATE TABLE")) {				
		            drop = ln.substring("CREATE TABLE".length() + 1)
		            println "DROP TABLE ${drop[0 .. (drop.indexOf(' '))]} CASCADE CONSTRAINTS"
		            println "/"
		        }
		        if (ln.trim().toUpperCase().startsWith("CREATE SEQUENCE")) {				
		            drop = ln.substring("CREATE SEQUENCE".length() + 1)
		            println "DROP SEQUENCE ${drop[0 .. (drop.indexOf(' '))]}"
		            println "/"
		        }
		    }
	    }
	}
}