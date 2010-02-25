The refactor work break the big scripts into 8 modules, they work fine as scripts, but they have not 
been converted into groovy project yet,  so if you import them into a groovy project, it may have errors

main script: ojb2jpa.groovy
	configuration: 
		1, change "run option properties" to do what you want to do
		2, change "File Path Properties" to decide source files you want to convert
		3, change "persistence detail properties" to decide output
		4, for detail description of configuration, please see ./acchives/ojb2jpa.readme
		
    usage: >groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/ojb2jpa.groovy
    
help script: jpa_makeup.groovy(for clean existing annotations and add @TRANSIENT)
  
    usage: groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/jpa_makeup.groovy [CLEAN | TRANSIENT]

