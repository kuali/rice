// generates master drop and create sql

PROJECT_DIR = '/java/projects/rice'

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
MODULES = ['kns', 'kew', 'ksb', 'kim', 'ken', 'kom']
MASTER_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_destroy.sql' 
MASTER_CREATE_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_bootstrap.sql'

SAMPLEAPP_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app_drops.sql'
SAMPLEAPP_DATA_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app.sql'

// prompt and read user input
println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
    System.exit(2)
}

// HANDLE DESTROY SQL
println "Creating master drop SQL: " + MASTER_DESTROY_SQL

// The file that contains drop statements
destroySql = new File(MASTER_DESTROY_SQL)
if (destroySql.exists()) {
    destroySql.delete()
}       

// concatenate all sequence and table drops
MODULES.each() {
    moduleName ->
    println "Concatenating drop SQL for module " + moduleName 
    createdrops(destroySql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences');
    createdrops(destroySql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables');
}

println "Concatenating sample app drop SQL"
merge(destroySql, SAMPLEAPP_DESTROY_SQL)

println "Done."


// HANDLE BOOTSTRAP SQL
println "Creating master bootstrap SQL: " + MASTER_CREATE_SQL

// The file that contains bootstrap statements
bootstrapSql = new File(MASTER_CREATE_SQL)
if (bootstrapSql.exists()) {
    bootstrapSql.delete()
}       

println "Concatenating bootstrap tables, sequences, indexes, and constraints."
MODULES.each() {
    moduleName ->
    println "Concatenating create SQL for module " + moduleName
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/indexes')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/constraints')
}

println "Concatenating bootstrap data."

MODULES.each() {
    moduleName ->
    println "Concatenating bootstrap data for module " + moduleName
    merge(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/sql/' + moduleName.toUpperCase() + 'Bootstrap.sql')
}

println "Concatenating Sample Application bootstrap data."
merge(bootstrapSql, SAMPLEAPP_DATA_SQL)

println "Done."

System.exit(0)

// FUNCTIONS

def merge(db, file) {
	f = new File(file)
	if (!f.isFile()) {
	   println file + " does not exist, skipping"
	   return
	}
	db << "-- Concatenating " + f.getName() + "\r\n"
	db << f.getText()
	db << '\r\n'
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
