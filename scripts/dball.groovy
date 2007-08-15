if (args.length > 2) { 
	println 'usage: groovy dball.groovy [-pdir PROJECT_DIR]'
	println '       PROJECT_DIR defaults to /java/projects/rice'
	System.exit(1)	
}

PROJECT_DIR = '/java/projects/rice'
//PROJECT_DIR = '/Users/natjohns/eclipse-3.3-workspace/rice'

count = 0
for (arg in args) {
   	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	count++
}	

println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
	System.exit(2)
}

// Just add comma separated values for ignored DDL
ignores = ['FS_UNIVERSAL_USR_T']

// The file that contains drop statements
db = new File(PROJECT_DIR + '/kns/src/main/config/sql/rice_db_destroy.sql')
if (db.exists()) {
    db.delete()
}       

// Do the KNS DDL Drops
createdrops(db, PROJECT_DIR + '/kns/src/main/config/ddl/sequences')
createdrops(db, PROJECT_DIR + '/kns/src/main/config/ddl/tables')

// Do the KEW DDL Drops
createdrops(db, PROJECT_DIR + '/kew/src/main/config/ddl/sequences')
createdrops(db, PROJECT_DIR + '/kew/src/main/config/ddl/tables')

// Do the KSB DDL Drops (at this point it only quartz tables)
createdrops(db, PROJECT_DIR + '/ksb/src/main/config/ddl')

merge(db, PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app_drops.sql')

// The file that contains bootstrap statements
db = new File(PROJECT_DIR + '/kns/src/main/config/sql/rice_db_bootstrap.sql')
if (db.exists()) {
    db.delete()
}       

// Do the KNS DDL Creates
mergeandstrip(db, PROJECT_DIR + '/kns/src/main/config/ddl/sequences')
mergeandstrip(db, PROJECT_DIR + '/kns/src/main/config/ddl/tables')
mergeandstrip(db, PROJECT_DIR + '/kns/src/main/config/ddl/indexes')
mergeandstrip(db, PROJECT_DIR + '/kns/src/main/config/ddl/constraints')

// Do the KEW DDL Creates
mergeandstrip(db, PROJECT_DIR + '/kew/src/main/config/ddl/sequences')
mergeandstrip(db, PROJECT_DIR + '/kew/src/main/config/ddl/tables')
mergeandstrip(db, PROJECT_DIR + '/kew/src/main/config/ddl/indexes')
mergeandstrip(db, PROJECT_DIR + '/kew/src/main/config/ddl/constraints')

// Do the KSB DDL Drops (at this point it only quartz tables)
mergeandstrip(db, PROJECT_DIR + '/ksb/src/main/config/ddl')

merge(db, PROJECT_DIR + '/kns/src/main/config/sql/rice_data.sql')
merge(db, PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app.sql')

System.exit(0)


def merge(db, file) {
	f = new File(file)
    f.eachLine {
	    ln ->
        db << ln           
        db << '\n'
	}
}

def mergeandstrip(db, dir) {
	def p = ~/.*\.ddl/
	new File(dir).eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! ignores.contains(name.substring(0, name.indexOf(".")))) {
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
	def p = ~/.*\.ddl/
	new File(dir).eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! ignores.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln -> 
		        if (ln.trim().toUpperCase().startsWith("CREATE TABLE")) {				
		            drop = ln.substring("CREATE TABLE".length() + 1)
		            db << "DROP TABLE ${drop[0 .. (drop.indexOf(' '))]} CASCADE CONSTRAINTS"
		            db << '\n'
		            db << "/"
		            db << '\n'
		        }
		        if (ln.trim().toUpperCase().startsWith("CREATE SEQUENCE")) {				
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
It will create or replace the following files in ${PROJECT_DIR}/kns/src/main/config/sql/:
    1) ${PROJECT_DIR}/kns/src/main/config/sql/rice_db_destroy.sql
    2) ${PROJECT_DIR}/kns/src/main/config/sql/rice_db_bootstrap.sql

If this is not what you want, please supply more information:
    usage: groovy dball.groovy [-pdir PROJECT_DIR]
           PROJECT_DIR defaults to /java/projects/rice

Do you want to continue (yes/no)?"""
}
