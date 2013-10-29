def config = new ConfigSlurper().parse(new File('config.groovy').toURL())

println config