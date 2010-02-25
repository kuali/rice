import java.util.ArrayList;


//this is the util class

def generateFile(path, text){
	
	println("*******writting to\t" + path);
	
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

//this is for clean up
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

def cleanBackupFiles(classes, sourceDirectories, projHome, backupExtension, logger, verbose){
	/*
	 Remove the backup.java files.
	 */
	classes.values().each {
		c ->     
		def backupFile
		def file
		sourceDirectories.each {
			dir -> 
			file = projHome + dir + c.className.replaceAll("\\.", "/") + ".java" + backupExtension
			if (new File(file).exists()) {
				backupFile = new File(file)
			}
		}
		
		if (!backupFile) {
			logger.log "${backupFile} does not exist.  Can not remove it."
			return
		}
		
		if (backupFile.exists()) {
			backupFile.delete()
			if (verbose) println "Deleting ${file}"
		}
	}
}

def stripeModuleName(String s){
	
	String name = s.substring(s.lastIndexOf("-") + 1, s.lastIndexOf("."));
	
	System.out.println(name);
}

//this is for main script, should be combinted with the above one later
def getRespositoryFiles(String projHome, resourceDir, ojbMappingPattern, ArrayList repositories){
	
	repositories.clear()
	
	// local helpers
	def addRepository = { File f -> 
		repositories.add( f.getPath() );
	}
	
	def dir = new File(projHome+resourceDir)
	
	println 'directoryName='+dir.getPath()
	println 'ojbMappingPattern='+ojbMappingPattern
	
	dir.eachFileMatch(ojbMappingPattern, addRepository)
	dir.eachDirRecurse { File myFile ->
		myFile.eachFileMatch(ojbMappingPattern, addRepository)
	}
	
}