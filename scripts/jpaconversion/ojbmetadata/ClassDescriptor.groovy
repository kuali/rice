package ojbmetadata
import groovy.transform.ToString;

@ToString(includePackage=false)
public class ClassDescriptor {
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

