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