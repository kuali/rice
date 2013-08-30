/**
 * Done for saner error messages.
 */
def shell = new GroovyShell(this.getClass().classLoader);
shell.run(new File("src/main/groovy/org/kuali/rice/scripts/KradConversion.groovy",))

