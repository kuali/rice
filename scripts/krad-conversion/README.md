KNS KRAD Conversion Script
---
Note: This script is still IN DEVELOPMENT.

Purpose:
---
To produce a non-destructive testable krad-compatible screens from existing KNS forms

Assumptions:
---
- Converting project to rice 2.2+

How it works:
---
KRAD Conversion script builds a maven war overlay project using your existing project and appends all converted files to the
new project.  This allows testing of the krad additions in isolation.  Conversion is broken down in scaffolding, dictionary
conversion, and struts conversion.


Usage:
---

Locate the project directory and a target directory for running your conversion script.

If there are class files located in the original project you may need them for compiling the project.  If the original
project is maven, update the maven-war-plugin to include attachClasses property set to true.  Add the classes dependency
into the project's conversion.properties file (see knsapp.conversion.properties for example)

Create a new file based on src/main/resources/project.conversion.properties, filling out all fields based on the project.
Be sure to include a full directory path and that the output.dir contains no files of importance as it will be cleaned
before building the new project.

Run 'mvn -Dalt.config.location="<project.conversion.properties.file>" install'

The script will generate a war overlay structure in order to maintain the dependencies and original files of the app
without requiring the need to copy in place.

Once the script is complete, run 'mvn clean package' on the target project


