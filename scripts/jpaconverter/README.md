OJB to JPA Configuration Conversion Tool
========================================

As part of the upgrade to use KRAD, you will need to update all your OJB-mapped `BusinessObject` classes into JPA-mapped data objects.

For this, the Rice project has provided a tool which should extract most of your OJB metadata and use it to annotate your Java classes.

The script is written in Groovy and uses a Groovy configuration file for the more verbose elements.

## Setup

1. Download the `JpaConverter.groovy` and `config-sample.groovy` scripts to an appropriate location.
2. If you do not have your own Groovy Grapes setup, place the XML content below into `$HOME/.groovy/grapeConfig.xml`.
3. Update (and rename) the `config-sample.groovy` script with the appropriate values for your project.

```
<ivysettings>
  <property name="ivy.cache.ttl.default" value="24h"/>
  <settings defaultResolver="downloadGrapes"/>
  <resolvers>
    <chain name="downloadGrapes" returnFirst="true">
      <filesystem name="cachedGrapes">
        <ivy pattern="${user.home}/.groovy/grapes/[organisation]/[module]/ivy-[revision].xml"/>
        <artifact pattern="${user.home}/.groovy/grapes/[organisation]/[module]/[type]s/[artifact]-[revision](-[classifier]).[ext]"/>
      </filesystem>
      <ibiblio name="localm2" root="file:${user.home}/.m2/repository/" checkmodified="true" changingPattern=".*" changingMatcher="regexp" m2compatible="true"/>
      <ibiblio name="mavencentral" root="http://repo.maven.apache.org/maven2/" m2compatible="true"/>
      <ibiblio name="codehaus" root="http://repository.codehaus.org/" m2compatible="true"/>
    </chain>
  </resolvers>
</ivysettings>
```

## Usage

```
usage: groovy JpaConverter.groovy -b <project base directory> -c <location of config file> [options]
 -b,--base <base directory>   Absolute path to the base directory for the
                              conversion.
 -c,--config <config file>    Location of groovy configuration file
 -e,--errorsonly              If set, the script will only report any
                              errors or warnings it will encounter and
                              *not* update any files.
 -h,--help                    show usage information
 -n,--dryrun                  If set, the script will dump the resulting
                              java files to the console instead of
                              updating the existing files.
    --replace                 Replace all existing JPA annotations on
                              classes referenced by OJB files.
```

## Recommendations

1. Upon first run, use the `--errorsonly` flag.  This will give you the smallest output and allow you to review any expected problems with the translation by altering the classes or OJB mapping files.

## Configuration Options

All the elements below should be set in the Groovy configuration file.  See the provided `config-sample.groovy` for examples and use it as a starting point.

**`classpathDirectories`** : This is a list of directories, relative to the base path passed in on the command line, which will be used to resolve your compiled classes.  The converter inspects the code to obtain needed information, and so needs a compiled version of all business object classes.  Each directory is expected to be the base of a class file output directory.  (E.g., `target/classes`)

**`classpathJarDirectories`** : A list of directories, relative to the base path, which will be scanned for jar files.  Each jar file found will be added to the classpath.  This should contain paths to all the libraries used by your project in order to resolve dependencies of your business objects.  (E.g., a `WEB-INF/lib` directory)

**`sourceDirectories`** :  A list of directories, relative to the base path, which will be scanned for the matching source files for your business objects.  The first `.java` file which matches a given class will be used and modified by this tool.

**`repositoryFiles`** : A list of the files, relative to the base path, of the OJB files you want to scan and process.  Only classes referenced in `class-descriptor` elements in these files will be touched by the conversion process.

**`converterMappings`** : A mapping of class name to new JPA converter names.  If you edit this map, leave all the existing entries in place and add your application-specific converters to the end of the list.  If you have converters in your application which are not covered by the implementations already in the conversion list, you will need to create a new JPA converter class (`javax.persistence.AttributeConverter`) and then add the fully qualified class name in this map.

## Notes

## Troubleshooting

### Unable to Resolve Class Errors

* Clear out your `$HOME/.groovy/grapes` directory to force a reload of the dependencies.
* Add `-Divy.message.logger.level=4` to the groovy command line (before the groovy class) to help troubleshoot and dependency retrieval issues.
* If you are having problems finding Kuali artifacts, or you are running against a snapshot build during testing, you may need to add the line below to your `grapesConfig.xml` file:

```
<ibiblio name="kuali" root="http://nexus.kuali.org/content/groups/public/" m2compatible="true"/>
```
