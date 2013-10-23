package ojbmetadata
import groovy.transform.ToString;

@ToString(includePackage=false)
public class CollectionDescriptor {
	def name
	def collectionClass
	def elementClassRef
	def orderBy
	def sort
	def indirectionTable
	def proxy
	def autoRetrieve
	def autoUpdate
	def autoDelete
	def fkPointingToThisClassColumn
	def fkPointingToElementClassColumn
	def inverseForeignKeys = []
	def manyToMany
	def orderByElements = []
}
