#unalias -a
#OFFLINE=--offline

if [[ "$1" == "-c" ]]; then
	pushd ../krad-data
	mvn $OFFLINE install -Dmaven.surefire.skip=true -DskipTests
	popd
fi

DEBUG_FLAGS=
if [[ "$1" == "-debug" ]]; then
	DEBUG_FLAGS="-Dmaven.failsafe.debug"
fi
mvn $OFFLINE -Dit.test=org.kuali.rice.krad.data.provider.RollbackExceptionErrorReportingTest verify -Pitests $DEBUG_FLAGS
