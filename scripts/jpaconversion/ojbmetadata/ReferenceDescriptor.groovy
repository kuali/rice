package ojbmetadata
import groovy.transform.ToString;

@ToString(includePackage=false)
public class ReferenceDescriptor {
	def name
	def classRef
	def proxy
	def autoRetrieve
	def autoUpdate
	def autoDelete
	def foreignKeys = []
}
