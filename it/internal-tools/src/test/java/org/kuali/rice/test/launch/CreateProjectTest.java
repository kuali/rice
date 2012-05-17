package org.kuali.rice.test.launch;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;



import java.io.File;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: sonam
 * Date: 5/9/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateProjectTest  {

    private static final Logger log = Logger.getLogger(CreateProjectTest.class);

    private static final String INTERNAL_TOOL_MODULE_NAME = "internal-tools";
    private String rdir;

    @Test
    public void testCreateProjectForSampleApp() throws IOException {
        setBaseDirSystemProperty(INTERNAL_TOOL_MODULE_NAME);

        File file = new File(getBaseDir());
        String parentDir1 = file.getParent();

        file = new File(parentDir1);
        String parentDir2 = file.getParent();

        rdir =  parentDir2;

        ClassLoader parent = new CreateProjectTest().getClass().getClassLoader();
        GroovyClassLoader loader;
        loader = new GroovyClassLoader(parent);
        Class groovyClass = null;
        try {
            groovyClass = loader.parseClass(new File(rdir + "/scripts/createproject.groovy"));

        } catch (IOException e) {
            e.printStackTrace();
        }

       GroovyObject groovyObject = null;
        try {
            groovyObject = (GroovyObject) groovyClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Object[] args = {"-name", "MyRiceSampleApp","-pdir",getBaseDir()+"/target","-rdir",rdir,"-sampleapp","-testmode"};
        groovyObject.invokeMethod("main", args);

    }

    @Test
    public void testCreateProjectForSampleAppNoProjectDirectory() throws IOException {
        setBaseDirSystemProperty(INTERNAL_TOOL_MODULE_NAME);

        File file = new File(getBaseDir());
        String parentDir1 = file.getParent();

        file = new File(parentDir1);
        String parentDir2 = file.getParent();

        rdir =  parentDir2;

        ClassLoader parent = new CreateProjectTest().getClass().getClassLoader();
        GroovyClassLoader loader;
        loader = new GroovyClassLoader(parent);
        Class groovyClass = null;
        try {
            groovyClass = loader.parseClass(new File(rdir + "/scripts/createproject.groovy"));

        } catch (IOException e) {
            e.printStackTrace();
        }

       GroovyObject groovyObject = null;
        try {
            groovyObject = (GroovyObject) groovyClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Object[] args = {"-name", "MyRiceSampleApp","-rdir",rdir,"-sampleapp","-testmode"};
        groovyObject.invokeMethod("main", args);

    }

    @Test
    public void testCreateProjectForStandaloneAppProjectDirectory() throws IOException {
        setBaseDirSystemProperty(INTERNAL_TOOL_MODULE_NAME);

        File file = new File(getBaseDir());
        String parentDir1 = file.getParent();

        file = new File(parentDir1);
        String parentDir2 = file.getParent();

        rdir =  parentDir2;

        ClassLoader parent = new CreateProjectTest().getClass().getClassLoader();
        GroovyClassLoader loader;
        loader = new GroovyClassLoader(parent);
        Class groovyClass = null;
        try {
            groovyClass = loader.parseClass(new File(rdir + "/scripts/createproject.groovy"));

        } catch (IOException e) {
            e.printStackTrace();
        }

       GroovyObject groovyObject = null;
        try {
            groovyObject = (GroovyObject) groovyClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Object[] args = {"-name", "MyRiceStandaloneApp","-rdir",rdir,"-standalone","-testmode"};
        groovyObject.invokeMethod("main", args);

    }

    @Test
    public void testCreateProjectForStandalone() throws IOException {
            setBaseDirSystemProperty(INTERNAL_TOOL_MODULE_NAME);

            File file = new File(getBaseDir());
            String parentDir1 = file.getParent();

            file = new File(parentDir1);
            String parentDir2 = file.getParent();

            rdir =  parentDir2;

            ClassLoader parent = new CreateProjectTest().getClass().getClassLoader();
            GroovyClassLoader loader;
            loader = new GroovyClassLoader(parent);
            Class groovyClass = null;
            try {
                groovyClass = loader.parseClass(new File(rdir + "/scripts/createproject.groovy"));

            } catch (IOException e) {
                e.printStackTrace();
            }

           GroovyObject groovyObject = null;
            try {
                groovyObject = (GroovyObject) groovyClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Object[] args = {"-name", "MyRiceStandaloneApp","-pdir",getBaseDir()+"/target","-rdir",rdir,"-standalone","-testmode"};
            groovyObject.invokeMethod("main", args);

        }

   /**
     * maven will set this property and find resources from the config based on it. This makes eclipse testing work because
     * we have to put the basedir in our config files in order to find things when testing from maven
     */
    protected void setBaseDirSystemProperty(String moduleBaseDir) {
        if (System.getProperty("basedir") == null) {
        	final String userDir = System.getProperty("user.dir");

            System.setProperty("basedir", userDir + ((userDir.endsWith(File.separator + "it" + File.separator + moduleBaseDir)) ? "" : File.separator + "it" + File.separator + moduleBaseDir));
        }
    }

    protected String getBaseDir() {
        return System.getProperty("basedir");
    }
}
