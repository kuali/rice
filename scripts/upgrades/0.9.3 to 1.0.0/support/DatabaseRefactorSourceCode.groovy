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
def conversionRootDir = "/java/projects/rice-1.0.0"
def mappingDir = "."
xmlExtension = "schema-refactor.xml"

excludeDirs = [ "target" ]

jpaTablePattern = [ "(@Table.*name\\s*=\\s*\")#\"",  "\$1#\"" ]
jpaColumnPattern = [ "(@Column.*name\\s*=\\s*\")#\"",  "\$1#\"" ]
jpaJoinColumnPattern = [ "(@JoinColumn.*name\\s*=\\s*\")#\"",  "\$1#\"" ]
jpaSequencePattern = [ "(@Sequence.*name\\s*=\\s*\")#\"",  "\$1#\"" ]

ojbTablePattern1 = [ "(table=\")#\"", "\$1#\"" ]
ojbTablePattern2 = [ "(indirection-table=\")#\"", "\$1#\"" ]
ojbColumnPattern = [ "(column=\")#\"", "\$1#\"" ]
ojbSequencePattern = [ "(sequence-name=\")#\"", "\$1#\"" ]

jpaFilePattern = ".java"
ojbFilePattern = "OJB-repository-.*\\.xml"

count = 0
for (arg in args) {
	if (arg == '-ext') xmlExtension = args[count + 1]
	count++
}	

tableMap = [:]
columnMap = [:]
sequenceMap = [:]

jpaReplacements = []
ojbReplacements = []

def loadMappings( dir ) {
	def files = new File(dir).list()
	files.each {
		String fileName ->
		if (fileName.endsWith(xmlExtension)) {
			File file = new File(dir, fileName)
            if ( file.isDirectory() ) {
                loadMappings( file.getAbsolutePath() )
            } else {
            	def root = new XmlParser().parseText(file.getText())	
            	def tables = root.table.findAll{
            		table ->
            		table.attributes().findAll { 
            			if ( it.getKey() == "oldName" )	oldTable = it.getValue()
            			if ( it.getKey() == "newName" ) newTable = it.getValue()
            			if ( it.getKey() == "name") {
            				oldTable = it.getValue()
            				newTable = it.getValue()
            			}
            		}
            		if (oldTable != newTable) {
            			tableMap[oldTable] = newTable
            		}
				
            		table.column.findAll{
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
            			}
            			if (oldCol != newCol) {
            				columnMap[oldCol] = newCol
            			}
            		}
            	}
            	def sequences = root.sequence.findAll{
            		sequence ->
            		sequence.attributes().findAll { 
            			if ( it.getKey() == "oldName" )	oldSeq = it.getValue()
            			if ( it.getKey() == "newName" ) newSeq = it.getValue()
            			if ( it.getKey() == "name") {
            				oldSeq = it.getValue()
            				newSeq = it.getValue()
            			}
            		}
            		if (oldSeq != newSeq) {
            			sequenceMap[oldSeq] = newSeq
            		}
            	}
            }
		}
	}
}

def loadReplacements() {
	for (oldTable in tableMap.keySet()) {
		regexp = jpaTablePattern[0].replaceAll("#", oldTable)
		replacement = jpaTablePattern[1].replaceAll("#", tableMap[oldTable])
		jpaReplacements.add([regexp, replacement])
		regexp = ojbTablePattern1[0].replaceAll("#", oldTable)
		replacement = ojbTablePattern1[1].replaceAll("#", tableMap[oldTable])
		ojbReplacements.add([regexp, replacement])
		regexp = ojbTablePattern2[0].replaceAll("#", oldTable)
		replacement = ojbTablePattern2[1].replaceAll("#", tableMap[oldTable])
		ojbReplacements.add([regexp, replacement])
	}
	for (oldColumn in columnMap.keySet()) {
		regexp = jpaColumnPattern[0].replaceAll("#", oldColumn)
		replacement = jpaColumnPattern[1].replaceAll("#", columnMap[oldColumn])
		jpaReplacements.add([regexp, replacement])
		regexp = jpaJoinColumnPattern[0].replaceAll("#", oldColumn)
		replacement = jpaJoinColumnPattern[1].replaceAll("#", columnMap[oldColumn])
		jpaReplacements.add([regexp, replacement])
		regexp = ojbColumnPattern[0].replaceAll("#", oldColumn)
		replacement = ojbColumnPattern[1].replaceAll("#", columnMap[oldColumn])
		ojbReplacements.add([regexp, replacement])
	}
	for (oldSeq in sequenceMap.keySet()) {
		regexp = jpaSequencePattern[0].replaceAll("#", oldSeq)
		replacement = jpaSequencePattern[1].replaceAll("#", sequenceMap[oldSeq])
		jpaReplacements.add([regexp, replacement])
		regexp = ojbSequencePattern[0].replaceAll("#", oldSeq)
		replacement = ojbSequencePattern[1].replaceAll("#", sequenceMap[oldSeq])
		ojbReplacements.add([regexp, replacement])
	}
	println "####################"
	println "# JPA Replacements #"
	println "####################"
	for (replacement in jpaReplacements) {
		println replacement[0] + " -> " + replacement[1]
	}
	println "####################"
	println "# OJB Replacements #"
	println "####################"
	for (replacement in ojbReplacements) {
		println replacement[0] + " -> " + replacement[1]
	} 
}

def convertDir( dir ) {
    def files = new File(dir).list()    
    files.each {
        String fileName ->
        File file = new File(dir, fileName)
        if ( file.isDirectory()  && !excludeDirs.contains(fileName)) {
        	convertDir( file.getAbsolutePath() )
        } else {
            if ( fileName.endsWith( jpaFilePattern ) ) {
            	convertFile(file, jpaReplacements)
            }
            if (fileName.matches( ojbFilePattern )) {
            	convertFile(file, ojbReplacements)
            }
        }
    }
}

def convertFile( file, replacements ) {
	println "Converting File: " + file.getAbsolutePath()
	String originalFileText = file.text
    String convertedFileText = originalFileText
    replacements.each {
        fromStr, toStr -> 
        //println "Converting: " + fromStr + " to " + toStr
        convertedFileText = convertedFileText.replaceAll( fromStr, toStr )
    }
	if ( !convertedFileText.equals( originalFileText ) ) {
		file.delete();
        file << convertedFileText;
	}
}

loadMappings(mappingDir)
loadReplacements()
convertDir(conversionRootDir)
