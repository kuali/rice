#
# Copyright 2005-2017 The Kuali Foundation
#
# Licensed under the Educational Community License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.opensource.org/licenses/ecl2.php
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

MAVEN_REPO=$HOME/.m2/repository

groovy \
	-classpath ../../../target/classes:$MAVEN_REPO/com/google/code/javaparser/javaparser/1.0.9/javaparser-1.0.9.jar:$MAVEN_REPO/org/kuali/db/ojb/db-ojb/1.0.4-patch8/db-ojb-1.0.4-patch8.jar \
	GroovyWrapperScript.groovy \
    --destfile  OJB_to_JPA_Annotations.jar \
    --groovyc \
    --mainclass Main \
    --addjar    $MAVEN_REPO/commons-io/commons-io/2.1/commons-io-2.1.jar \
    --addjar    $MAVEN_REPO/com/google/code/javaparser/javaparser/1.0.9/javaparser-1.0.9.jar \
    --addjar    $MAVEN_REPO/org/kuali/db/ojb/db-ojb/1.0.4-patch8/db-ojb-1.0.4-patch8.jar \
    --addjar    $MAVEN_REPO/commons-lang/commons-lang/2.6/commons-lang-2.6.jar \
    --addjar    $MAVEN_REPO/log4j/log4j/1.2.17/log4j-1.2.17.jar

     