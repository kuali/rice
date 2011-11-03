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
// This script verifies the following about the refactoring mapping files:
//
// 1) That no identifiers are longer than 30 characters
// 2) That no column names are mapped differently (i.e. one old column
//    name being mapped to 2 different ones in different tables)
// 3) That all indexes, references and constraints are referencing valid
//    old and new table and column names
//
// The result of the verification is printed to standard out

def dir = '.'
xmlExtension = "schema-refactor.xml"
tableMap = [:]
newTableMap = [:]
sequenceMap = [:]
fileList = []

count = 0
for (arg in args) {
	if (arg == '-ext') xmlExtension = args[count + 1]
	count++
}	

def processDir( dir ) {
        
	def files = new File(dir).list()    
    
    files.each {
        String fileName ->
        if ( fileName.endsWith( xmlExtension ) ) {
            File file = new File(dir,fileName)
            fileList.add(file)
            if ( file.isDirectory() ) {
                processDir( file.getAbsolutePath() )
            } else {
            	def root = new XmlParser().parseText(file.getText())
            	
            	// tables
            	
            	def tables = root.table.findAll{
            		table ->
            		def mapped = constructNameMapped(table);            		
            		tableObj = new Table(mapped.oldName, mapped.newName)
            		tableMap[tableObj.oldName] = tableObj
            		newTableMap[tableObj.newName] = tableObj
			
            		// columns
            		
            		table.column.findAll{
            			col ->
            			mapped = constructNameMapped(col)
            			columnObj = new Column(mapped.oldName, mapped.newName)
            			tableObj.columns.add(columnObj)
            		}
            		
            		// indexes
            		
            		table.index.findAll{
            			index ->
            			mapped = constructNameMapped(index)
            			indexObj = new Index(mapped.oldName, mapped.newName)
            			tableObj.indexes.add(indexObj)
            			
            			// index columns
            			
            			index."index-column".findAll{
            				indexCol ->
            				mapped = constructNameMapped(indexCol)
                			indexColumnObj = new Column(mapped.oldName, mapped.newName)
                			indexObj.columns.add(indexColumnObj)
            			}
            			
            		}
            		
            		// uniques
            		
            		table.unique.findAll{
            			unique ->
            			mapped = constructNameMapped(unique)
            			uniqueObj = new Unique(mapped.oldName, mapped.newName)
            			tableObj.uniques.add(uniqueObj)
            			
            			// unique columns
            			
            			unique."unique-column".findAll{
            				uniqueCol ->
            				mapped = constructNameMapped(uniqueCol)
            				uniqueColObj = new Column(mapped.oldName, mapped.newName)
            				uniqueObj.columns.add(uniqueColObj)
            			}
            		}
            		
            		// foreign keys
            		
            		table."foreign-key".findAll{
            			foreignKey ->
            			mapped = constructNameMapped(foreignKey)
            			foreignTableMapped = constructForeignTableMapped(foreignKey)
            			foreignKeyObj = new ForeignKey(mapped.oldName, mapped.newName, foreignTableMapped.oldName, foreignTableMapped.newName)
            			tableObj.foreignKeys.add(foreignKeyObj)
            			
            			// references
            			
            			foreignKey.reference.findAll{
            				reference ->
            				foreignMapped = constructForeignMapped(reference)
            				localMapped = constructLocalMapped(reference)
            				referenceObj = new Reference(foreignMapped.oldName, foreignMapped.newName, localMapped.oldName, localMapped.newName)
            				foreignKeyObj.references.add(referenceObj)
            			}
            			
            		}
					
            	}
            	
            	def sequences = root.sequence.findAll{
            		sequence ->
            		def mapped = constructNameMapped(sequence);            		
            		sequenceObj = new Sequence(mapped.oldName, mapped.newName)
            		sequenceMap[sequenceObj.oldName] = sequenceObj
        			
            	}

            }
        } 
	}
	
}

def constructMapped(element, oldNameAttribute, newNameAttribute, defaultNameAttribute) {
	def oldName
	def newName
	element.attributes().findAll { 
		if ( it.getKey() == oldNameAttribute)	{
			oldName = it.getValue()
		}
		if ( it.getKey() == newNameAttribute) {
			newName = it.getValue()
		}
		if ( it.getKey() == defaultNameAttribute) {
			oldName = it.getValue()
			newName = it.getValue()
		}
	}
	return new Mapped(oldName, newName)
}
	
def constructNameMapped(element) {
	return constructMapped(element, "oldName", "newName", "name")
}

def constructForeignTableMapped(element) {
	return constructMapped(element, "oldForeignTable", "newForeignTable", "foreignTable")
}

def constructForeignMapped(element) {
	return constructMapped(element, "oldForeign", "newForeign", "foreign")
}

def constructLocalMapped(element) {
	return constructMapped(element, "oldLocal", "newLocal", "local")
}

processDir(dir)
printSummary()
verifyMappings()
println ""
println "...DONE."

def printSummary() {
	def tableCount = 0
	def tableColumnCount = 0
	def indexCount = 0
	def indexColumnCount = 0
	def uniqueCount = 0
	def uniqueColumnCount = 0
	def foreignKeyCount = 0
	def referenceCount = 0
	def sequenceCount = 0
	for (table in tableMap.values()) {
		tableCount++
		for (column in table.columns) {
			tableColumnCount++
		}
		for (index in table.indexes) {
			indexCount++
			for (indexColumn in index.columns) {
				indexColumnCount++
			}
		}
		for (unique in table.uniques) {
			uniqueCount++
			for (uniqueColumn in unique.columns) {
				uniqueColumnCount++
			}
		}
		for (foreignKey in table.foreignKeys) {
			foreignKeyCount++
			for (reference in foreignKey.references) {
				referenceCount++
			}
		}
	}
	for (sequence in sequenceMap) {
		sequenceCount++
	}
	println "##########################################################"
	println "# Executing Verification of files:"
	for (file in fileList) {
		println "# " + file.getName()
	}
	println "##########################################################"
	println "Number of Elements being processed: "
	println "                       tables: " + tableCount
	println "                table columns: " + tableColumnCount
	println "                      indexes: " + indexCount
	println "                index columns: " + indexColumnCount
	println "           unique constraints: " + uniqueCount
	println "    unique constraint columns: " + uniqueColumnCount
	println "                 foreign keys: " + foreignKeyCount
	println "                   references: " + referenceCount
	println "                    sequences: " + sequenceCount
	println ""
}

def verifyMappings() {
	def columnNameMappings = [:]
	for (table in tableMap.values()) {
		verifyIdentifierLength(table.newName)
		for (column in table.columns) {
			verifyIdentifierLength(column.newName)
			// check for a duplicate column mapping
			existingMapping = columnNameMappings.get(column.oldName)
			if (existingMapping == null) {
				existingMapping = new ArrayList()
			}
			existingMapping.add([table.newName, column.newName])
			columnNameMappings.put(column.oldName, existingMapping)
		}
		for (unique in table.uniques) {
			verifyIdentifierLength(unique.newName)
			for (column in unique.columns) {
				verifyColumnReferences("unique.columns", table, column.oldName, column.newName)
			}
		}
		for (index in table.indexes) {
			verifyIdentifierLength(index.newName)
			for (column in index.columns) {
				verifyColumnReferences("index.columns", table, column.oldName, column.newName)
			}
		}
		for (foreignKey in table.foreignKeys) {
			verifyIdentifierLength(foreignKey.newName)
			verifyTableReferences("foreign-key:" + table.oldName, foreignKey.oldForeignTable, foreignKey.newForeignTable)
			for (ref in foreignKey.references) {
				foreignTableObj = tableMap[foreignKey.oldForeignTable]
				verifyColumnReferences("reference.foreign:" + table.oldName, foreignTableObj, ref.oldForeign, ref.newForeign)
				verifyColumnReferences("reference.local:" + table.oldName,  table, ref.oldLocal, ref.newLocal)
			}
		}
	}
	// now print info on duplicate column mappings
	for (oldColumnName in columnNameMappings.keySet()) {
		existingMapping = columnNameMappings.get(oldColumnName)
		duplicateDetected = false
		currentNewName = null
		for (mapping in existingMapping) {
			if (currentNewName == null) {
				currentNewName = mapping[1]
			} else if (currentNewName != mapping[1]) {
				duplicateDetected = true;
				break;
			}
		}
		if (duplicateDetected) {
			println "WARN: multiple column name mapping detected:  " + oldColumnName
			for (mapping in existingMapping) {
				println "     " + mapping[0] + "." + mapping[1]
			}
		}
	}
}

def verifyIdentifierLength(identifier) {
	if (identifier.length() > 30) {
		println "ERROR: " + identifier + " is longer than 30 characters!"
	}
}

def verifyTableReferences(label, oldTableName, newTableName) {
	foundOld = false
	foundNew = false
	for (table in tableMap.values()) {
		if (table.oldName == oldTableName) {
			foundOld= true
		}
		if (table.newName == newTableName) {
			foundNew = true
		}
	}
	if (!foundOld) {
		println "ERROR [" + label + "]: Failed to find the old table with name '" + oldTableName
	}
	if (!foundNew) {
		println "ERROR [" + label + "]: Failed to find the new table with name '" + newTableName
	}
}

def verifyColumnReferences(label, table, oldColumnName, newColumnName) {
	foundOld = false
	foundNew = false
	for (column in table.columns) {
		if (column.oldName == oldColumnName) {
			foundOld= true
		}
		if (column.newName == newColumnName) {
			foundNew = true
		}
	}
	if (!foundOld) {
		println "ERROR [" + label + "]: Failed to find the old column with name '" + oldColumnName + "' in table " + table.oldName
	}
	if (!foundNew) {
		println "ERROR [" + label + "]: Failed to find the new column with name '" + newColumnName + "' in table " + table.newName
	}
}

class Mapped {
	def oldName
	def newName
	Mapped(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}
}

class Table extends Mapped {
	def columns = []
	def indexes = []
	def uniques = []
	def foreignKeys = []
	Table(String oldName, String newName) {
		super(oldName, newName)
	}
}

class Column extends Mapped {
	Column(String oldName, String newName) {
		super(oldName, newName);
	}
}

class Index extends Mapped {
	def columns = []
	Index(String oldName, String newName) {
		super(oldName, newName);
	}
}

class Unique extends Mapped {
	def columns = []
	Unique (String oldName, String newName) {
		super(oldName, newName);
	}
}

class ForeignKey extends Mapped {
	def oldForeignTable
	def newForeignTable
	def references = []
	ForeignKey(String oldName, String newName, String oldForeignTable, String newForeignTable) {
		super(oldName, newName)
		this.oldForeignTable = oldForeignTable
		this.newForeignTable = newForeignTable
	}
}

class Reference {
	def oldForeign
	def newForeign
	def oldLocal
	def newLocal
	Reference(String oldForeign, String newForeign, String oldLocal, String newLocal) {
		this.oldForeign = oldForeign
		this.newForeign = newForeign
		this.oldLocal = oldLocal
		this.newLocal = newLocal
	}
}

class Sequence extends Mapped {
	Sequence(String oldName, String newName) {
		super(oldName, newName)
	}
}