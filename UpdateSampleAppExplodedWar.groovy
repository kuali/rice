/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#!/usr/bin/env groovy

@Grapes(
    @Grab(group='commons-io', module='commons-io', version='2.1')
)

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver

// def args = []
// args[0] = "-set"
// args[1] = "../../../../rice-framework/krad-sampleapp/target/classes"
// args[2] = "../../../../rice-framework/krad-sampleapp/target/rice-krad-sampleapp-2.3.0-M2-SNAPSHOT/WEB-INF/classes"
// args[3] = "-set"
// args[4] = "../../../../rice-framework/krad-sampleapp/src/main/webapp"
// args[5] = "../../../../rice-framework/krad-sampleapp/target/rice-krad-sampleapp-2.3.0-M2-SNAPSHOT"

//File sourceDir = new File( args[0] )
//File targetDir = new File( args[1] )

if ( args[0] != "-set" ) {
	println "Usage: UpdateSampleAppExplodedWar.groovy -set <sourceDir1> [<sourceDir2> ...] <targetDir1> [-set <sourceDir3> [<sourceDir4> ...] <targetDir2>]"
	System.exit(1)
}
println "Arguments: $args"
setLocationList = args.findIndexValues( { it == "-set" } );

println "Found -set at positions: $setLocationList"

beginEndMaps = [:]
for ( i = 0; i < setLocationList.size(); i++ ) {
    startLoc = setLocationList[i] + 1
    if ( i + 1 >= setLocationList.size() ) {
        endLoc = args.size() - 1
    } else {
        endLoc = setLocationList[i + 1] - 1
    }
    beginEndMaps.put(startLoc, endLoc)
}
println "Derived BeginEndMaps: $beginEndMaps"

IOFileFilter svnDirs = FileFilterUtils.and(
    FileFilterUtils.directoryFileFilter(),
    FileFilterUtils.nameFileFilter(".svn"));

IOFileFilter directories = FileFilterUtils.notFileFilter(svnDirs);

//IOFileFilter files       = FileFilterUtils.and(
//      FileFilterUtils.fileFileFilter(),
//      FileFilterUtils.suffixFileFilter(".java"));
IOFileFilter filter = directories;//FileFilterUtils.or(directories, files);

beginEndMaps.each { bem ->
    println "Processing Directory Set"
    def sourceDirs = []
    def targetDir = ""
    (bem.key..bem.value).each( { i ->
        println "Processing Index: $i : ${args[(int)i]}"
        if ( i < bem.value ) {
            sourceDirs << args[(int)i]
        } else {
            targetDir = args[(int)i]
        }
    })
    println "Source Paths: $sourceDirs"
    println "Target Path : $targetDir"

    sourceDirs.each { sourceDir ->
        FileAlterationObserver fao = new FileAlterationObserver(sourceDir,filter);

        fao.addListener( new FileMover(new File(sourceDir),new File(targetDir)) );

        FileAlterationMonitor fam = new FileAlterationMonitor(5000)
        fam.addObserver(fao);
        fam.start()
    }
}

class FileMover extends FileAlterationListenerAdaptor {
	File sourceDir;
	File targetDir;

	public FileMover( File sourceDir, File targetDir ) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}
	@Override
	public void onFileChange(File file) {
		println "File Changed: $file.canonicalPath"
		String relativePath = file.canonicalPath.replace(sourceDir.canonicalPath, "")
		println "Path Relative to base: $relativePath"
		File targetFile = new File( targetDir, relativePath);
		println "Copying to $targetFile.canonicalPath"
		FileUtils.copyFile(file, targetFile )
	}
}
BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
println "Press Enter to Quit:"
println ""
br.readLine()
System.exit(0)

