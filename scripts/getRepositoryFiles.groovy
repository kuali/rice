/* Configurable Fields */
def ojbMappingPattern = ~/.*OJB.*repository.*xml/
def projHome = 'c:/Rice/projects/play'

/* End User Configurable Fields */
def sourceDirectories = []
def repositories = []

getRespositoryFiles(projHome, ojbMappingPattern, repositories, sourceDirectories)


def getRespositoryFiles(String projHome, ojbMappingPattern, ArrayList repositories, ArrayList sourceDirectories){
    repositories = []
    sourceDirectories = []

    // local helpers
    def cl = { File f -> println f.getName() }
    def addRepository = { File f -> 
            repositories.add( f );
            sourceDirectories.add( f.getParent() )
            }

    def dir = new File(projHome)

    println 'directoryName='+dir.getPath()
    println 'ojbMappingPattern='+ojbMappingPattern

    dir.eachFileMatch(ojbMappingPattern, addRepository)
    dir.eachDirRecurse { File myFile ->
        myFile.eachFileMatch(ojbMappingPattern, addRepository)
        }

    println 'Found '+repositories.size().toString()+' OJB mapping files:'
    repositories.each cl

    println 'Found the files in the following '+sourceDirectories.size().toString()+' Source Directories:'
    sourceDirectories.each {println it}
}

