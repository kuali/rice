// generates master drop and create sql

PROJECT_DIR = '/java/projects/rice'
//PROJECT_DIR = '/Users/natjohns/eclipse-3.3-workspace/rice'

if (args.length > 2) { 
	println 'usage: groovy dball.groovy [-pdir PROJECT_DIR]'
	println '       PROJECT_DIR defaults to ' + PROJECT_DIR
	System.exit(1)	
}

count = 0
for (arg in args) {
   	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	count++
}	

// set up variables based on PROJECT_DIR
// Just add comma separated values for ignored DDL
IGNORES = ['FS_UNIVERSAL_USR_T']
MODULES = ['kns', 'kew', 'ksb', 'kim', 'ken']
MASTER_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_destroy.sql' 
MASTER_CREATE_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_bootstrap.sql'

SAMPLEAPP_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app_drops.sql'

RICE_DATA_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_data.sql' 
SAMPLEAPP_DATA_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app.sql'

// prompt and read user input
println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
    System.exit(2)
}

println "Creating master drop SQL: " + MASTER_DESTROY_SQL

// The file that contains drop statements
db = new File(MASTER_DESTROY_SQL)
if (db.exists()) {
    db.delete()
}       

// concatenate all sequence and table drops
MODULES.each() {
    moduleName ->
    println "Concatenating drop SQL for module " + moduleName 
    createdrops(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences');
    createdrops(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables');
}

println "Concatenating sample app drop SQL"
merge(db, SAMPLEAPP_DESTROY_SQL)

println "Done."


println "Creating master drop SQL: " + MASTER_CREATE_SQL

// The file that contains bootstrap statements
db = new File(MASTER_CREATE_SQL)
if (db.exists()) {
    db.delete()
}       

MODULES.each() {
    moduleName ->
    println "Concatenating create SQL for module " + moduleName
    mergeandstrip(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences')
    mergeandstrip(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables')
    mergeandstrip(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/indexes')
    mergeandstrip(db, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/constraints')
}

println "Concatenating Rice data SQL"
merge(db, RICE_DATA_SQL)
println "Concatenating Sample app data SQL"
merge(db, SAMPLEAPP_DATA_SQL)

println "Done."

System.exit(0)


// functions

def merge(db, file) {
	f = new File(file)
	db << f.getText()
}

def mergeandstrip(db, dir) {
	d = new File(dir)
    if (!d.isDirectory()) {
       println dir + " does not exist...skipping"
       return
    }
    def p = ~/.*\.ddl/
	d.eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! IGNORES.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln -> 
		        if (! (ln.trim().startsWith("TABLESPACE") || ln.trim().startsWith("/*") || ln.trim().startsWith("*"))) {
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX01', '')
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX02', '')
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX03', '')
		            ln = ln.replaceAll('USING INDEX', '')
		            db << ln           
		            db << '\n'
		        }
		    }
	    }
	}
}

def createdrops(db, dir) {
	d = new File(dir)
	if (!d.isDirectory()) {
	   println dir + " does not exist...skipping"
	   return
	}
    def p = ~/.*\.ddl/
	d.eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! IGNORES.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln ->
				line = ln.trim().toUpperCase()
				def matcher = line =~ /CREATE TABLE ([\p{Alnum}[_]]+)[ \(]*/
		        if (matcher.matches()) {
		            db << "DROP TABLE ${matcher[0][1]} CASCADE CONSTRAINTS"
		            db << '\n'
		            db << "/"
		            db << '\n'
		        }
		        if (line.startsWith("CREATE SEQUENCE")) {				
		            drop = ln.substring("CREATE SEQUENCE".length() + 1)
		            db << "DROP SEQUENCE ${drop[0 .. (drop.indexOf(' '))]}"
		            db << '\n'
		            db << "/"
		            db << '\n'
		        }
		    }
	    }
	}
}

def warningtext() {
"""
==================================================================
                            WARNING 
==================================================================
It will create or replace the following files:
    1) ${MASTER_DESTROY_SQL}
    2) ${MASTER_CREATE_SQL}

If this is not what you want, please supply more information:
    usage: groovy dball.groovy [-pdir PROJECT_DIR]
           PROJECT_DIR defaults to /java/projects/rice

Do you want to continue (yes/no)?"""
}
