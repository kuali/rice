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

