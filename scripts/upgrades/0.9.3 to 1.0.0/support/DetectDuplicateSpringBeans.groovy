/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * This script needs listOfFilesToCheckForDuplicates.txt as an input file which contains name of all the
 * spring bean Files to check for duplicate copies and duplicate beans. 
 * 
 * This script performs the following operations: 
 * 
 * - checks for duplicate copies of SpringBean files. If duplicate files are found, 
 * the report is written to DuplicateReport.txt 
 * 
 * - checks for duplicate entries of a SpringBean. If duplicate beans are found, 
 * the report is written to DuplicateBeanReport.txt
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
import org.apache.xpath.XPathAPI;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

inputFile = new File("listOfFilesToCheckForDuplicates.txt");
listOfInputFiles = []
listOfSearchableFiles = []
listOfXMLFileObjects = []
springBeansSourceDirStr = "impl/src/main/resources"
duplicateReport = new File("DuplicateReport.txt");
duplicateBeanReport = new File("DuplicateBeanReport.txt")
beanMap= [:];
duplicateBeans = [:];
numberOfDuplicateBeans = 0;
/**
 * returns the root directory of the project
 */
def projectRoot(){	
	URL url = getClass().getProtectionDomain().getCodeSource().getLocation()
	File thisFile = new File(url.toURI())
	return thisFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile()	
}

/**
 * responsible for cleaning up existing files
 */
def init(){
	if(duplicateReport.exists()){
		duplicateReport.delete();
	}
	if(duplicateBeanReport.exists()){
		duplicateBeanReport.delete();
	}
}

/**
 * used to read the input file provided and return a list
 */
def readInputFile(file){	
	if (file.exists()) {
		
		if(file.size() != 0){			
			return file.readLines();
		}
		else{
			println "The File is Empty"
			System.exit(0);
		}		
	}
	else{
		println "No file found"
		System.exit(0);
	}	
}

/**
 * gets all the files with .xml extension and prepares a list of searchable files 
 */
def searchFile(){	
	File rootFile = projectRoot()	
	File resourcesDir = new File(rootFile.toString() + rootFile.separatorChar + springBeansSourceDirStr)
	FileNameFinder fileNameFinder = new FileNameFinder()
	if(resourcesDir.isDirectory()){
		resourcesDir.eachFileRecurse({
			File currentFile = new File(it.toString())
			String ext = currentFile.toString().substring(currentFile.toString().lastIndexOf('.')+1, currentFile.toString().length());
			if(!currentFile.isHidden() && currentFile.isFile() && ext.equals("xml")){				
				listOfSearchableFiles.add(currentFile);	
			}
		})
	}	
}

/**
 * detects if duplicate files are present. 
 * If Duplicates are found they will be written to DuplicateReport.txt
 * If no duplicates are found "No Duplicates Found!" is displayed on the consule. 
 */
def detectDuplicateFiles(){
	Boolean isRecordingDuplicates = false;
	listOfDuplicates = []
	listOfInputFiles.each({
		File inputFile = new File(it.toString())			
		int numberOfCopies = 0
		isRecordingDuplicates = false;
		for(int i = 0; i < listOfSearchableFiles.size();i++){
			File file = listOfSearchableFiles[i];
			if(inputFile.name == file.name){						
				listOfXMLFileObjects.add(file);
				numberOfCopies++;						
				if(numberOfCopies > 1){							
					if(isRecordingDuplicates){
						listOfDuplicates.add(listOfSearchableFiles.get(listOfSearchableFiles.indexOf(file)))
						listOfSearchableFiles.remove(listOfSearchableFiles.indexOf(file))
					}
					else{
						i=-1
						isRecordingDuplicates = true
					}							
				}						
			}
		}					
	})	
	if(listOfDuplicates.size()>1){			
		println "Duplicates Found!"			
		duplicateReport.createNewFile();
		listOfDuplicates.each({
			File file = new File(it.toString());				
			duplicateReport.append(file.name + " found at " + file.canonicalPath + System.getProperty("line.separator"))
			
		})
		println "Report has been written to " + duplicateReport.canonicalPath
	}
	else{
		println "No duplicates found!"
	}
}


/**
 * processes every single bean
 * if a duplicate bean is found, then 
 * - get the value from this beanMap
 * - then add it to the duplicateBean HashMap.
 * - change the name of the beanID and then add the duplicateBean HashMap 
 */
def processBean(beanID, file){	
	if(beanMap.containsKey(beanID) && !(beanMap.getAt(beanID).canonicalPath.toString().equals(file.canonicalPath))){	 
		duplicateBeans.putAt(beanID, beanMap.getAt(beanID))	
		beanID = beanID + "_" + (++numberOfDuplicateBeans)
		duplicateBeans.putAt(beanID, file)
	}
	else{
		beanMap.putAt(beanID, file);
	}		
}

/**
 * checks for presence of duplicate beans
 */
def detectDuplicateSpringBeans(){
	
	listOfXMLFileObjects.each({
		
		File file = new File(it.toString())		
//		println file.name;
		def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		def inputStream = new FileInputStream(file)
		def elements     = builder.parse(inputStream).documentElement		
		XPathAPI.selectNodeList(elements, '/beans/bean').each{
			numberOfDuplicateBeans = 0;
			Node currentNode = (Node)it;
			if(currentNode.getNodeType() == Node.ELEMENT_NODE){
				Element element = currentNode;
				//works only for beans which has an id attaribute
				if(element.hasAttribute("id")){
					beanID = element.getAttribute("id")
				}									
			}
			processBean(beanID, file)
		}			
		
	})
	if(!duplicateBeans.isEmpty()){		
		println "Duplicate bean found!"
		duplicateBeanReport.createNewFile();
		
		duplicateBeans.keySet().each({					
			File file = duplicateBeans.getAt(it)
			duplicateBeanReport.append("Bean ID:" + it.toString() + "  File:" + file.path + System.getProperty("line.separator"));
		})
		println "Report written to :"+duplicateBeanReport.canonicalPath
	}
	else{
		println "No Duplicate Spring Beans found!"
	}
}


init()
listOfInputFiles = readInputFile(inputFile)
searchFile()
detectDuplicateFiles()
detectDuplicateSpringBeans()
