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
def dir = '.'
xmlExtension = "schema-refactor.xml"
outputFile = "confluence-output.txt"

count = 0
for (arg in args) {
	if (arg == '-ext') xmlExtension = args[count + 1]
	if (arg == '-out') outputFile = args[count + 1]
	count++
}	

def processDir( dir ) {
        
	def files = new File(dir).list()
    def outputFile = new File (outputFile)
	if (outputFile.exists()) {
		outputFile.delete();
	}
    def ctr = 0
    def indCols= ""
    def newIndName = ""
    def oldIndName = ""
    def tableSummary = "h3. Table Summary\n|| Old Table Name || New Table Name ||\n"
    def sequenceSummary = "h3. Sequence Summary\n|| Old Sequence Name || New Sequence Name ||\n"
    def detailOutput = "h3. Table Details\nColumns whose names have changed will be in *bold*.\n\n"
    
    files.each {
        String fileName ->
        if ( fileName.endsWith( xmlExtension ) ) {
            File file = new File(dir,fileName)
            if ( file.isDirectory() ) {
                processDir( file.getAbsolutePath() )
            } else {
            	def root = new XmlParser().parseText(file.getText())	
            	def tables = root.table.findAll{ //tables
            		table ->
            		def oldTable = ""
            		def newTable = ""
            		table.attributes().findAll { 
            			if ( it.getKey() == "oldName" )	{
            				oldTable = it.getValue()
            			}
            			if ( it.getKey() == "newName" ) {
            				newTable = it.getValue()
            			}
            			if ( it.getKey() == "name") {
            				oldTable = it.getValue()
            				newTable = it.getValue()
            			}
            		}
            		
            		tableSummary += "|[#" + oldTable + "]|" + newTable + "|\n"
            		
            		detailOutput += "h3. " + oldTable + "\n"
            		detailOutput += "Old Table Name: *" + oldTable + "*\n"
            		detailOutput += "New Table Name: *" + newTable + "*\n"
            		detailOutput += "|| Old Column Name || New Column Name ||\n"
			
            		table.column.findAll{ //columns
            			col ->
            			def oldCol = ""
            			def newCol = ""
            			def columnChanged = false
            			col.attributes().findAll {
            				if ( it.getKey() == "oldName" )	{
            					oldCol = "*" + it.getValue() + "*"
            				}
            				if ( it.getKey() == "newName" )	{
            					newCol = "*" + it.getValue() + "*"
            				}
            				if ( it.getKey() == "name" ) {
            					oldCol = it.getValue()
            					newCol = it.getValue()
            				}
            			} //colatt
            			if (oldCol != "") {
            				detailOutput += "|" + oldCol + "|" + newCol + "|\n"
            			}
            		} //columns
					
            	}
            	
            	def sequences = root.sequence.findAll{ //tables
            		sequence ->
            		def oldSequence = ""
            		def newSequence = ""
            		sequence.attributes().findAll { 
            			if ( it.getKey() == "oldName" )	{
            				oldSequence = it.getValue()
            			}
            			if ( it.getKey() == "newName" ) {
            				newSequence = it.getValue()
            			}
            			if ( it.getKey() == "name") {
            				oldSequence = it.getValue()
            				newSequence = it.getValue()
            			}
            		}
        			sequenceSummary += "|" + oldSequence + "|" + newSequence + "|\n"
            	}

            	outputFile.append(tableSummary)
            	outputFile.append(sequenceSummary)
        		outputFile.append(detailOutput)

            } 
        } 
	}
}
processDir(dir)

