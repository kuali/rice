/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
def dir = '/java/projects/rice-1.0.0'
def Schema  = new File (dir,'scripts/upgrades/0.9.3 to 0.9.4/support/schema-refactor.xml')
tablelist = [:]
columnlist = [:]
seqlist = [:]
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
				if(oldCol != "" && newCol !="")	//{
					columnlist.put(oldCol,newCol) //	println "${oldCol} ${newCol} "+columnlist.size()} //adding to map								
			} //cols
			
		tablelist.put(oldTable,newTable)
		}//tables
	
def sequences = root.sequence.findAll{ //seq
			sequence ->
			sequence.attributes().findAll { 
				if ( it.getKey() == "oldName" )	oldSeq = it.getValue()
				if ( it.getKey() == "newName" )	newSeq = it.getValue()
				}
				if(oldSeq != "" && newSeq !="")	{
					seqlist.put(oldSeq,newSeq) 
					println "${oldSeq} ${newSeq} "+seqlist.size()
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
    
println "******* Processing specified files ******"
    filesToReplace.each {
        fileName ->
            File  file = new File (dir,fileName)
            def ojbroot = new XmlParser().parseText(file.getText()) 
			def t = ojbroot."class-descriptor".findAll{ cd ->
			
			cd.attributes().findAll { 
				if ( it.getKey() == "table" )		oldTable = it.getValue()
				} //find all table atts
				cd.@table = tablelist.get(oldTable)
			
			cd.children().findAll{ fd ->
				fd.attributes().findAll { 
				if ( it.getKey() == "column" )		oldCol = it.getValue()	
				if ( it.getKey() == "sequence-name" ) oldSeq = it.getValue()		 
				}//columns
				if (columnlist.get(oldCol)!=null ) fd.@column = columnlist.get(oldCol)
				if (oldSeq !="" && seqlist.get(oldSeq)!=null ){
				 fd.@"sequence-name" = seqlist.get(oldSeq)
				 println "Inside sequence-name"	
				 }	
				oldSeq =""		
			}//fied Desc				
		}//class desc
		def outfile = new File (dir,fileName+".bk")
		outfile.delete()
		def fwriter = new FileWriter( outfile )
		new XmlNodePrinter(new PrintWriter(fwriter)).print(ojbroot)	
            
    }//filename
		
