/************************************************************************************************
 * IF WANT TO RUN AGAINT MULTI ojb config file:
 * set resourceHome = '$resource_root' for your project
 * for example for rice: resourceHome = '/java/projects/play/rice-1.1.0/impl/src/main/resources
 * 
 **********************************************************************************************/

import java.util.ArrayList;

def ojbMappingPattern = ~/.*OJB.*repository.*xml/
def resourceHome = '/java/projects/play/rice-1.1.0/impl/src/main/resources/org/kuali/rice/ken'
def srcHome = '/java/projects/play/rice-1.1.0/impl/src/main/java'
def sourceDirectories = []
def repositories = []
def classes = []
def files = []

def getRespositoryFiles(String resourcejHome, ojbMappingPattern, ArrayList repositories, ArrayList sourceDirectories){
	repositories.clear()
	sourceDirectories.clear()
	
	// local helpers
	def addRepository = { File f -> 
		repositories.add( f.getPath() );
		sourceDirectories.add( f.getParent() )
	}
	
	def dir = new File(resourcejHome)
	
	println 'directoryName='+dir.getPath()
	println 'ojbMappingPattern='+ojbMappingPattern
	
	dir.eachFileMatch(ojbMappingPattern, addRepository)
	dir.eachDirRecurse { File myFile ->
		myFile.eachFileMatch(ojbMappingPattern, addRepository)
	}
	
}

class ClassDescriptor {
	def compoundPrimaryKey = false
	def pkClassIdText = ""
	def cpkFilename = ""
	def tableName
	def className
	def primaryKeys = []
	def fields = [:]
	def referenceDescriptors = [:]
	def collectionDescriptors = [:]
}

def loadClasses(repositories, classes){
	
	repositories.each {
		repository -> 
		println 'Parsing repository file: '+repository.toString()
		def xml = new XmlParser().parse(new File(repository))
		def classDescriptors = xml['class-descriptor']
		
		classDescriptors.each { 
			cd -> 
			//def classDescriptor = new ClassDescriptor()
			println("********get class:\t" + cd.'@class')
			classes.add(cd.'@class')
		}
	}
		
}

def findExistingBOs(srcHome, classes, files){
	classes.each{
		cls->   def file = srcHome + '/'+ cls.replaceAll("\\.", "/") + ".java"
		
		println("************find file:\t" + file)
		
		files.add(file)
		}
	}

def removeAnnotatonLine(files){
	
	def javaFile
	def backupFile
	
	files.each{
		file-> println("************working on file:\t" + file)
		if (new File(file).exists()) {
			javaFile = new File(file)
			backupFile = new File(file + '.backup')		
			def text = ""
			//scan file by line or convert file to list of lines....
			javaFile.eachLine{line->
				
			if(!line.toString().trim().startsWith("@")){
			println("******get this line*****\t" + line)
			text = text + "\n" + line.toString();}
			}
			
			//println("***************************get file\n"+text);
			
			//javaFile << text
			generateFile(file, text);
		}
		}
	}

def generateFile(path, text){
	
	def persistFile = new File(path);
	def backupFile = new File(path + '.backup');
	if (persistFile.exists()){
		if (backupFile.exists()){
			backupFile.delete();
		}	
		persistFile.renameTo(backupFile);
		persistFile.delete();
	}
	
	persistFile << text
	persistFile << "\n"
	
}

def addTransient(files){
	
//	def javaFile
//	def backupFile
//	
//	files.each{
//		file->
//		if (new File(file).exists()) {
//			javaFile = new File(file)
//			backupFile = new File(file + '.backup')
//			
//			def text
//			//scan file by line
//			def line = xxxx.readLine()
//			//if no @ in previous line && line started with private/protected && line end with;
//			text += '@transient\n' + line;
//			
//			file< text
//		}
//		
//	}
	
	}

getRespositoryFiles(resourceHome, ojbMappingPattern, repositories, sourceDirectories)

println 'Found '+repositories.size().toString()+' OJB mapping files:'
repositories.each {println it}

loadClasses(repositories, classes)

findExistingBOs(srcHome, classes, files )

removeAnnotatonLine(files)









