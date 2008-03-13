// a simple script which checks whether or not the junit report shows any failures or errors
//
// Will return with an error code of 1 if there is an error or failure
// Will return with an error code of 2 if the junit report directory cannot be found

import javax.xml.xpath.*
import javax.xml.parsers.*

def junitReportDir = new File('target/junitreport')

if (!junitReportDir.exists()) {
	println 'JUnitReport Directory does not exist!'
	properties["notestreports"] = "true"
	return
}

def xpath = XPathFactory.newInstance().newXPath();
def files = junitReportDir.listFiles().grep(~/.*xml$/)
for (file in files) {
	def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	def reportDoc = builder.parse(file)
	if (xpath.evaluate( '/testsuites/testsuite[@errors>0]', reportDoc, XPathConstants.BOOLEAN )) {
		properties["testsfailed"] = "true"
		return
	}
	if (xpath.evaluate( '/testsuites/testsuite[@failures>0]', reportDoc, XPathConstants.BOOLEAN )) {
		properties["testsfailed"] = "true"
		return
	}
}
