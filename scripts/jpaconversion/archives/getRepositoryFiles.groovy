/* Configurable Fields */
def ojbMappingPattern = ~/.*OJB.*repository.*xml/
def projHome = '/java/projects/play/rice-1.1.0'

/* End User Configurable Fields */
def sourceDirectories = []
def repositories = []

getRespositoryFiles(projHome, ojbMappingPattern, repositories, sourceDirectories)

println 'Found '+repositories.size().toString()+' OJB mapping files:'
repositories.each {println it}
println 'Found the files in the following '+sourceDirectories.size().toString()+' Source Directories:'
sourceDirectories.each {println it}

def getRespositoryFiles(String projHome, ojbMappingPattern, ArrayList repositories, ArrayList sourceDirectories){
    repositories.clear()
    sourceDirectories.clear()

    // local helpers
    def addRepository = { File f -> 
            repositories.add( f.getPath() );
            sourceDirectories.add( f.getParent() )
            }

    def dir = new File(projHome)

    println 'directoryName='+dir.getPath()
    println 'ojbMappingPattern='+ojbMappingPattern

    dir.eachFileMatch(ojbMappingPattern, addRepository)
    dir.eachDirRecurse { File myFile ->
        myFile.eachFileMatch(ojbMappingPattern, addRepository)
        }

}



