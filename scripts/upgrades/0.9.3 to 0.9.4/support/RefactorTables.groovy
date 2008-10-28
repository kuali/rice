def dir = 'F:/Shubhangi_RiceWorkspace/rice-0.9.4/scripts'
xmlExtension = "schema-refactor.xml"

oldTable = "";
newTable = "";

def processDir( dir ) {
    println "Processing Directory: " + dir
    def files = new File(dir).list()
    def sqlFile = new File ("upgrades/0.9.3 to 0.9.4/Refactor.sql")
    if (sqlFile.exists()) {
		sqlFile.delete();
	}
	
    def LongerNames = new File("upgrades/0.9.3 to 0.9.4/LongerIncorrectNames.txt")
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
            } 
	    else {
			println "Processing XML "+fileName     	
			def root = new XmlParser().parseText(file.getText())	
			def tables = root.table.findAll{ //tables
				table ->
				table.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldTable = it.getValue()
				if ( it.getKey() == "newName" ) newTable = it.getValue()
				
				}

				if (newTable!="" && newTable.length() > 30 )	
					LongerNames.append(newTable+"\n")
				else
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
					if ( oldCol != "" && newCol != "" && newCol.length() < 30 && ctr ==0){
			   	        sqlFile.append("ALTER TABLE ${newTable} RENAME COLUMN ${oldCol} TO ${newCol}\n/\n")
						ctr = ctr + 1
					}
					else if (newCol.length() > 30) 
						LongerNames.append(newCol+"\n")			
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
					
					if (newIndName.length() > 30 )	LongerNames.append(newIndName+"\n")
					
						ind.children().findAll{ 
							indCol ->
							indCol.attributes().findAll{
							if ( it.getKey()=="newName" || it.getKey()=="name") {	
								ctr= ctr +1
								if (ctr >1)
									indCols +=","+it.getValue()
								else
									indCols+=it.getValue()				
						} //indColAttr				
					} //indCol
					//Removed File Writes From Here....
				} //indexes 
				if (newIndName.length() < 30 ) 
					sqlFile.append("DROP INDEX ${oldIndName} \n/\nCREATE INDEX ${newIndName} ON ${newTable} ${indCols})\n/\n")
				else if (newIndName.length() > 30 )
					LongerNames.append(newIndName+"\n/\n")
				
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
					
						if (it.getKey()=="name" || it.getKey()=="newName" )
						{
							ctr= ctr +1
							if (ctr >1)
								indCols +=","+it.getValue()
							else
								indCols+=it.getValue()
							if (it.getValue().length() > 30 )	LongerNames.append(it.getValue()+"\n/\n")
						} //if
					}//uattr
				} //uchil //Removed File Writes From Here....
			if (newIndName.length() < 30 ) 
				sqlFile.append("ALTER TABLE ${oldTable} disable CONSTRAINT ${oldIndName} \n/\n ALTER TABLE ${newTable} ADD  CONSTRAINT ${newIndName} UNIQUE ${indCols})\n/\n")
			else if (newIndName.length() > 30 ) 
				LongerNames.append(newIndName+"\n/\n")

			}//unique ALTER TABLE table_name drop CONSTRAINT constraint_name; ALTER TABLE supplier add CONSTRAINT supplier_unique UNIQUE (supplier_id);

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
				if ( it.getKey()=="oldForeignTable" ) oldFtable= it.getValue()
				if ( it.getKey() == "newForeignTable"  ) newFtable = it.getValue()
	      	} //fat
			f.children().findAll{ 
				uc ->
					uc.attributes().findAll{
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
			} //fchil //Removed File Writes From Here....
		if ( lCols != "(" && fCols != "(" && ctr ==0 && newIndName.length() < 30 ){
		sqlFile.append("ALTER TABLE ${newTable} DROP CONSTRAINT ${oldIndName} \n/\nALTER TABLE ${newTable} ADD CONSTRAINT ${newIndName} FOREIGN KEY  ${lCols}) REFERENCES ${newFtable} ${fCols}) \n/\n")
		ctr = ctr + 1
		}//if
		else if (newIndName.length() > 30)
			LongerNames.append(newIndName+"\n/\n")	
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
		}
		if ( newSeq.length() <30)
			sqlFile.append("CREATE SEQUENCE ${newSeq} START WITH "+oldSeq+".NEXTVAL \n/\n DROP SEQUENCE ${oldSeq}\n/\n ")
		else if(newSeq.length() > 30)
			LongerNames.append(newSeq+"\n/\n")
		}	
}//tables
} //else
println "Completed Processing XML "+fileName
} //files   	
} //processDir
processDir(dir)
