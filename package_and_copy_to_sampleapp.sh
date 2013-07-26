#
# Copyright 2005-2013 The Kuali Foundation
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

RICE_VERSION=2.3.0-M2-SNAPSHOT
DEST=rice-framework/krad-sampleapp/target/rice-krad-sampleapp-${RICE_VERSION}/WEB-INF/lib

MODULE=krad-web-framework
pushd rice-framework/$MODULE
mvn install -Dmaven.surefire.skip=true -DskipTests
popd
#cp -v rice-framework/$MODULE/target/rice-$MODULE-${RICE_VERSION}.jar $DEST

#MODULE=krad-app-framework
#pushd rice-framework/$MODULE
#mvn install -Dmaven.surefire.skip=true -DskipTests
#popd
#cp -v rice-framework/$MODULE/target/rice-$MODULE-${RICE_VERSION}.jar $DEST

#MODULE=krad-data
#pushd rice-framework/$MODULE
#mvn install -Dmaven.surefire.skip=true -DskipTests
#popd
#cp -v rice-framework/$MODULE/target/rice-$MODULE-${RICE_VERSION}.jar $DEST

#MODULE=impl
#pushd rice-middleware/$MODULE
#mvn install -Dmaven.surefire.skip=true -DskipTests
#popd
#cp -v rice-middleware/$MODULE/target/rice-$MODULE-${RICE_VERSION}.jar $DEST

MODULE=krad-sampleapp
pushd rice-framework/$MODULE
mvn clean package -Dmaven.surefire.skip=true -DskipTests
popd

