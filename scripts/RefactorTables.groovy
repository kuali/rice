def dir = 'F:/Shubhangi_RiceWorkspace/rice-0.9.4/scripts'
xmlExtension = "schema-refactor.xml"

oldTable = "";
newTable = "";

def processDir( dir ) {
    println "Processing Directory: " + dir
    def files = new File(dir).list()
    def sqlFile = new File ("upgrades/0.9.3 to 0.9.4/Refactor.sql")
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
            } 
	    else {
			println "Processing XML "+fileName     	
			def root = new XmlParser().parseText(file.getText())	
			def tables = root.table.findAll{ //tables
				table ->
				table.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldTable = it.getValue()
				if ( it.getKey() == "newName" )	newTable = it.getValue()
				}
//		println "ALTER TABLE " + oldTable +" RENAME TO "+newTable
			sqlFile.append("ALTER TABLE ${oldTable} RENAME TO ${newTable}\n/\n")
			
			table.column.findAll{ //columns
			col ->
			ctr = 0
			oldCol = ""
			newCol = ""
			col.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldCol = it.getValue()
				if ( it.getKey() == "newName" )	newCol = it.getValue()
			} //colatt
			if ( oldCol != "" && newCol != "" && ctr ==0){
//				println "ALTER TABLE "+newTable+" RENAME COLUMN "+oldCol+" TO "+newCol
	   	        sqlFile.append("ALTER TABLE ${newTable} RENAME COLUMN ${oldCol} TO ${newCol}\n/\n")
				ctr = ctr + 1
			}
		} //columns
		
		table.index.findAll{ //indexes
			ind ->
			indCols = "("
			ctr = 0
			newIndName= " "
			oldIndName=" "
			
			ind.attributes().findAll{ 
				if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
				if ( it.getKey() == "newName" )	newIndName = it.getValue()
				} //indAttr
			
			ind.children().findAll{ 
				indCol ->
				indCol.attributes().findAll{
				if ( it.getKey()=="newName" || it.getKey()=="name") {	//	println it.getValue()
					ctr= ctr +1
					if (ctr >1)
						indCols +=","+it.getValue()
					else
						indCols+=it.getValue()
				} //indColAttr
		} //indCol
//		println "DROP INDEX ${oldIndName} \n/\n CREATE INDEX ${newIndName} ON ${newTable} ${indCols})\n/\n"
		sqlFile.append("DROP INDEX ${oldIndName} \n/\nCREATE INDEX ${newIndName} ON ${newTable} ${indCols})\n/\n")

		} //indexes 
		indCols = "("
		ctr = 0
		newIndName= " "
		oldIndName=" "

		table.unique.findAll { //unique 
			u ->
		indCols = "("
		ctr = 0
		newIndName= " "
		oldIndName=" "
		
			u.attributes().findAll{
			if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
			if ( it.getKey() == "newName" )	newIndName = it.getValue()
		      	} //uat
			u.children().findAll{ 
				uc ->
				uc.attributes().findAll{
				println it.getKey() +" U===U "+ it.getValue()
				if (it.getKey()=="name" || it.getKey()=="newName" )
				{
					ctr= ctr +1
					if (ctr >1)
						indCols +=","+it.getValue()
					else
						indCols+=it.getValue()
				} //if
			}//uattr
		} //uchil 
		println "DROP INDEX ${oldIndName} \n/\n CREATE UNIQUE INDEX ${newIndName} ON ${newTable} ${indCols})\n/\n"
		sqlFile.append("DROP INDEX ${oldIndName} \n/\nCREATE UNIQUE INDEX ${newIndName} ON ${newTable} ${indCols})\n/\n")
	  }//unique

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
		println it.getKey() +" === "+ it.getValue()
			if ( it.getKey()=="oldName" ) oldIndName= it.getValue()
			if ( it.getKey() == "newName" )	newIndName = it.getValue()
			if ( it.getKey()=="oldForeignTable" ) oldFtable= it.getValue()
			if ( it.getKey() == "newForeignTable"  ) newFtable = it.getValue()
      	} //fat
		f.children().findAll{ 
			uc ->
				uc.attributes().findAll{
				println it.getKey() +"FF===FF"+ it.getValue()
				if (it.getKey()=="foreign" || it.getKey()=="oldForeign" )
				{
					fctr = fctr +1
					if (fctr > 1 && !fCols.endsWith(it.getValue())) fCols +=","+it.getValue()
					else fCols+=it.getValue()

				} //if
				if (it.getKey()=="local" || it.getKey()=="newLocal" )
				{
					lctr = lctr +1
					if (lctr > 1) lCols +=","+it.getValue()
					else lCols+=it.getValue()					
				} //if
			}//fattr
		} //fchil 
		if ( lCols != "(" && fCols != "(" && ctr ==0){
		println "DROP CONSTRAINT ${oldIndName} \n/\nALTER TABLE ${newTable} ADD CONSTRAINT ${newIndName} FOREIGN KEY  ${lCols}) REFERENCES ${newFtable} ${fCols}) \n/\n"
		sqlFile.append("DROP CONSTRAINT ${oldIndName} \n/\nALTER TABLE ${newTable} ADD CONSTRAINT ${newIndName} FOREIGN KEY  ${lCols}) REFERENCES ${newFtable} ${fCols}) \n/\n")
		ctr = ctr + 1
		}//if
	  }////foreign-key
	}
}


	oldSeq =""
	newSeq =""
	def sequences = root.sequence.findAll{ //tables
			sequence ->
			sequence.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldSeq = it.getValue()
				if ( it.getKey() == "newName" )	newSeq = it.getValue()
				}
//		println "CREATE SEQUENCE ${newSeq} START WITH "+oldSeq+".NEXTVAL \n/\n DROP SEQUENCE ${oldSeq}\n/\n "
		sqlFile.append("CREATE SEQUENCE ${newSeq} START WITH "+oldSeq+".NEXTVAL \n/\n DROP SEQUENCE ${oldSeq}\n/\n ")
		}	
}//tables
} //else
} //files
} //processDir
processDir(dir)
