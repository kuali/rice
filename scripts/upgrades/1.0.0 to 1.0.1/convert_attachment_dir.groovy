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
