def dir = '/java/projects/rice-1.0.0'
def Schema  = new File (dir,'scripts/upgrades/0.9.3 to 0.9.4/support/schema-refactor.xml')
tablemap = [:]
columnmap = [:]
seqmap = [:]
tablelist = []
columnlist = []
		def root = new XmlParser().parseText(Schema.getText())	
		def tables = root.table.findAll{ table ->
			table.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldTable = it.getValue()
				if ( it.getKey() == "newName" )	newTable = it.getValue()
				} //find all table atts
			
			def columns = root.table.column.findAll{ col ->
			
			col.attributes().findAll {
				oldCol = ""
            	newCol = ""
				if ( it.getKey() == "oldName" )	oldCol = it.getValue()				
				if ( it.getKey() == "newName" )	newCol = it.getValue()					
				} //find all col atts
				if(oldCol != "" && newCol !="")	{
					columnmap.put(oldCol,newCol) //	println "${oldCol} ${newCol} "+columnmap.size()
					columnlist << [oldCol,newCol]	
					} //adding to map								
			} //cols
		
		tablelist << [oldTable,newTable]	
		tablemap.put(oldTable,newTable)
		}//tables
	
def sequences = root.sequence.findAll{ //seq
			sequence ->
			sequence.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldSeq = it.getValue()
				if ( it.getKey() == "newName" )	newSeq = it.getValue()
				}
				if(oldSeq != "" && newSeq !="")	{
					seqmap.put(oldSeq,newSeq) 
			//		println "${oldSeq} ${newSeq} "+seqmap.size()
					}
		}	//seq
		
def filesToReplace = [
    '/impl/src/main/resources/org/kuali/rice/kcb/config/OJB-repository-kcb.xml',
    '/impl/src/main/resources/org/kuali/rice/ken/config/OJB-repository-ken.xml',
    '/impl/src/main/resources/org/kuali/rice/kew/config/OJB-repository-kew.xml',
    '/impl/src/main/resources/org/kuali/rice/kim/config/OJB-repository-kim.xml',
    '/impl/src/main/resources/org/kuali/rice/kns/config/OJB-repository-kns.xml',
    '/impl/src/main/resources/org/kuali/rice/ksb/config/OJB-repository-ksb.xml'
    ]
    
//def JavaFilesToReplace = []
String originalFileText =""
String convertedFileText = ""
String Jfilename = "" 
    
    filesToReplace.each {
        fileName ->
            File  file = new File (dir,fileName)
            def ojbroot = new XmlParser().parseText(file.getText()) 
			def t = ojbroot."class-descriptor".findAll{ cd ->
			
			cd.attributes().findAll { 
				if ( it.getKey() == "table" )		oldTable = it.getValue()
				if ( it.getKey() == "class" ){		//JavaFilesToReplace.add(it.getValue().replace('.','/')+".java")
					Jfilename = dir+"/impl/src/main/java/"+it.getValue().replace('.','/')+".java"
					File JavaFile= new File(Jfilename)					
					originalFileText = JavaFile.text
					}   
				} //find all table atts
				
				cd.@table = tablemap.get(oldTable)
				println fileName+" Processing  **** "+Jfilename
		 		convertedFileText = originalFileText
				if(oldTable !=null && tablemap.get(oldTable) !=null )
				convertedFileText = convertedFileText.replaceAll( oldTable, tablemap.get(oldTable))
			
			cd.children().findAll{ fd ->
				fd.attributes().findAll { 
				if ( it.getKey() == "column" )		oldCol = it.getValue()	
				if ( it.getKey() == "sequence-name" ) oldSeq = it.getValue()		 
				}//columns
				if (oldCol !=null && columnmap.get(oldCol)!=null ){
				 fd.@column = columnmap.get(oldCol)
				 convertedFileText = convertedFileText.replaceAll( oldCol, columnmap.get(oldCol))				 	
				 }
				if (oldSeq !="" && seqmap.get(oldSeq)!=null ){
				 fd.@"sequence-name" = seqmap.get(oldSeq)
				 }	
				oldSeq =""		
			}//fied Desc
			if ( !convertedFileText.equals( originalFileText ) ){
			 println 	convertedFileText
			 File bkpfile = new File(Jfilename+".bk")
			 bkpfile.delete();
			 bkpfile << convertedFileText;
             bkpfile << "\n";             
			 }			
		}//class desc	        
    }//filename
