int LEVELS = 6

if ( args?.size() == 0 ) {
	println "A directory path to convert is required"
	System.exit(1)
}
String dirName = args[0]
	
println "Reading files from directory '$dirName'"

File dir = new File(dirName)
File[] files = dir.listFiles()

for ( file in files ) { 
	String filename = file.getName()	
	
	if ( file.isDirectory() && filename.length() > 1 && !filename.equals("pending") ) {
		String dirTemplate = filename.toUpperCase().replace(" ", "")
		int levels = dirTemplate.length() < LEVELS ? dirTemplate.length() : LEVELS;
		StringBuffer sb = new StringBuffer()
		for ( int i = 0; i < levels; i++ ) {
			sb.append(dirTemplate.charAt(i))
			sb.append("/")
		}
		String newPath = sb.toString();
		File newDir = new File(dir, newPath)
		newDir.mkdirs()
		if ( newDir.exists() ) {
			print "Moving directory '$filename'... "
			File newTarget = new File(newDir, filename)
			println file.renameTo(newTarget) ? "successful" : "failed!";
		}
		else {
			println("Could not create directory '$newPath'")
		}
	}
}
