pushd rice-framework/krad-sampleapp/target/classes
find * -mtime -15m -print0 | cpio -pd0 --quiet  ../rice-krad-sampleapp-2.3.0-SNAPSHOT/WEB-INF/classes
popd
