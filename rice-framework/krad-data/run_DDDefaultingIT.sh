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

#unalias -a
#OFFLINE=--offline
mvn $OFFLINE install -Dmaven.surefire.skip=true -DskipTests
pushd ../krad-web-framework
mvn $OFFLINE install -Dmaven.surefire.skip=true -DskipTests
popd
pushd ../krad-it
DEBUG_FLAGS=
if [[ "$1" == "-debug" ]]; then
	DEBUG_FLAGS="-Dmaven.failsafe.debug"
fi
mvn $OFFLINE -Dit.test=org.kuali.rice.krad.data.DataDictionaryMetadataDefaultingTest verify -Pitests $DEBUG_FLAGS
popd