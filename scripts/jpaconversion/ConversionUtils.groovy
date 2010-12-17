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
	
	name
}

def getOJBConfigFiles(String projHome, resourceDir, ojbMappingPattern, ArrayList repositories){
	
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