def dir = '.'
xmlExtension = "schema-refactor.xml"
outputFile = "refactor.sql"
includeIndexes = "true"
includeConstraints = "true"

oldTable = "";
newTable = "";

count = 0
for (arg in args) {
	if (arg == '-ext') xmlExtension = args[count + 1]
	if (arg == '-out') outputFile = args[count + 1]
	if (arg == '-ind') includeIndexes = args[count + 1]
	if (arg == '-con') includeConstraints = args[count + 1]
	count++
}	


def processDir( dir ) {
    println "Processing Directory: " + dir
    def files = new File(dir).list()
    def sqlFile = new File (outputFile)
    if (sqlFile.exists()) {
		sqlFile.delete();
	}
	
    def LongerNames = new File("LongerIncorrectNames.txt")
    if (LongerNames.exists()) {
		LongerNames.delete();
	}
	
    def ctr = 0
	def indCols= ""
	def newIndName = ""
	def oldIndName = ""
    
    files.each {
        String fileName ->
        if ( fileName.endsWith( xmlExtension ) ) {
            File file = new File(dir,fileName)
            println "Processing File: " + file.getAbsolutePath()
            if ( file.isDirectory() ) {
                processDir( file.getAbsolutePath() )
            } else {
			println "Processing XML "+fileName     	
			def root = new XmlParser().parseText(file.getText())	
			def tables = root.table.findAll{ //tables
				table ->
				table.attributes().findAll { 
					if ( it.getKey() == "oldName" )	oldTable = it.getValue()
					if ( it.getKey() == "newName" ) newTable = it.getValue()
					if ( it.getKey() == "name") {
						oldTable = it.getValue()
						newTable = it.getValue()
					}
				}

				sqlFile.append("ALTER TABLE ${oldTable} RENAME TO ${newTable}\n/\n")
			
				table.column.findAll{ //columns
					col ->
					ctr = 0
					oldCol = ""
					newCol = ""
					col.attributes().findAll { 
						if ( it.getKey() == "oldName" )	oldCol = it.getValue()
						if ( it.getKey() == "newName" )	newCol = it.getValue()
						if ( it.getKey() == "name") {
							oldCol = it.getValue()
							newCol = it.getValue()
						}
					} //colatt
					if ( oldCol != "" && newCol != "" && newCol.length() < 30 && ctr ==0 && oldCol != newCol){
			   	        sqlFile.append("ALTER TABLE ${newTable} RENAME COLUMN ${oldCol} TO ${newCol}\n/\n")
						ctr = ctr + 1
					}
				} //columns
		
				if (includeIndexes == "true") {
				table.index.findAll{
					ind ->
					ctr = 0
					newIndName= " "
					oldIndName=" "
			
					ind.attributes().findAll{ 
						if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
						if ( it.getKey() == "newName" )	newIndName = it.getValue()
						if ( it.getKey() == "name" ) {
							 oldIndName= it.getValue()
							 newIndName = it.getValue()
						}
					} //indAttr
					
					if (oldIndName != newIndName) {
						sqlFile.append("ALTER INDEX ${oldIndName} RENAME TO ${newIndName}\n/\n")
					}
				}				
				}
				
				if (includeConstraints == "true") {
					
				indCols = "("
				ctr = 0
				newIndName= " "
				oldIndName=" "

				table.unique.findAll { //unique 
					u ->
					//indCols = "("
					ctr = 0
					newIndName= " "
					oldIndName=" "
					
					u.attributes().findAll{
						if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
						if ( it.getKey() == "newName" )	newIndName = it.getValue()
						if ( it.getKey() == "name" ) {
							oldIndName= it.getValue()
							newIndName = it.getValue()
						}
			      	} //uat
			
			      	if (oldIndName != newIndName) {
			      		sqlFile.append("ALTER TABLE ${newTable} RENAME CONSTRAINT ${oldIndName} TO ${newIndName}\n/\n")
			      	}
				}

	  	table."foreign-key".findAll { //foreign-key
			f ->
			indCols = "("
			fctr = 0
			lctr = 0
			newIndName= " "
			oldIndName=" "
			newFtable =""
			fCols = "("
			lCols = "("
			ctr = 0

			f.attributes().findAll{
				if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
				if ( it.getKey() == "newName" )	newIndName = it.getValue()
				if ( it.getKey() == "name" ) {
					oldIndName= it.getValue()
					newIndName = it.getValue()
				}
	      	}
			if (oldIndName != newIndName) {
				sqlFile.append("ALTER TABLE ${newTable} RENAME CONSTRAINT ${oldIndName} TO ${newIndName}\n/\n")
			}
	  }//foreign-key
	  
				}
	  
	}

	oldSeq =""
	newSeq =""
	def sequences = root.sequence.findAll{ //tables
		sequence ->
		sequence.attributes().findAll { 
			if ( it.getKey() == "oldName" )	oldSeq = it.getValue()
			if ( it.getKey() == "newName" )	newSeq = it.getValue()
			if ( it.getKey() == "name" ) {
				oldSeq = it.getValue()
				newSeq = it.getValue()
			}
		}
		if (oldSeq != newSeq) {
			sqlFile.append("RENAME ${oldSeq} TO ${newSeq}\n/\n")
		}
	}
}//tables
} //else
println "Completed Processing XML "+fileName
} //files   	
} //processDir
processDir(dir)
