main script: ojb2jpa.groovy

	configuration: 
		1, change "run option properties" to do what you want to do
		2, change "File Path Properties" to decide source files you want to convert
		3, change "persistence detail properties" to decide output
		
    usage: >groovy -cp classpath ~/ojb2jpa.groovy
    
help script: jpa_makeup.groovy(for clean existing annotations and add @TRANSIENT)
  
    usage: groovy ~/jpaconversion/jpa_makeup.groovy [CLEAN | TRANSIENT]

more details are coming soon...