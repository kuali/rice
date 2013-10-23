package ojbmetadata
import groovy.transform.ToString;

@ToString(includePackage=false)
public class Field {
	def id
	def name
	def column
	def jdbcType
	def primarykey
	def nullable
	def indexed
	def autoincrement
	def sequenceName
	def locking
	def conversion
	def access
}
