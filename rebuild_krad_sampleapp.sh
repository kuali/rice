GOALS=${@:-install}
mvn $GOALS -pl :rice-krad-sampleapp -am -T 4

