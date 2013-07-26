#unalias -a
mvn --offline install -Dmaven.surefire.skip=true -DskipTests
pushd ../../rice-middleware/it/internal-tools
mvn --offline install -Dmaven.surefire.skip=true -DskipTests
popd
pushd ../krad-web-framework
mvn --offline install -Dmaven.surefire.skip=true -DskipTests
popd
pushd ../krad-it
mvn --offline -Dit.test=org.kuali.rice.krad.data.provider.jpa.JpaMetadataProviderTest verify -Pitests
popd