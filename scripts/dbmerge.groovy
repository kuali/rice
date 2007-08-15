// Just add comma separated values for ignored DDL
ignores = ['FS_UNIVERSAL_USR_T']

PROJECT_DIR = '/java/projects/rice'
//PROJECT_DIR = '/Users/natjohns/eclipse-3.3-workspace/rice'

// Do the KNS DDL
mergeandstrip(PROJECT_DIR + '/kns/src/main/config/ddl/sequences')
mergeandstrip(PROJECT_DIR + '/kns/src/main/config/ddl/tables')
mergeandstrip(PROJECT_DIR + '/kns/src/main/config/ddl/indexes')
mergeandstrip(PROJECT_DIR + '/kns/src/main/config/ddl/constraints')

// Do the KEW DDL
mergeandstrip(PROJECT_DIR + '/kew/src/main/config/ddl/sequences')
mergeandstrip(PROJECT_DIR + '/kew/src/main/config/ddl/tables')
mergeandstrip(PROJECT_DIR + '/kew/src/main/config/ddl/indexes')
mergeandstrip(PROJECT_DIR + '/kew/src/main/config/ddl/constraints')

System.exit(0)



def mergeandstrip(dir) {
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
		            println ln            
		        }
		    }
	    }
	}
}