pushd db/impex/master
# Refresh the development database
mvn -Pdb,mysql,rice clean install
# Refresh the integration test database
mvn -Pdb,mysql,integration-test clean install
popd