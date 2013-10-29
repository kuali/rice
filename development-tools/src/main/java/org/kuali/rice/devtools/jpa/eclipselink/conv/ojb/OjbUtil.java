package org.kuali.rice.devtools.jpa.eclipselink.conv.ojb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ConnectionDescriptorXmlHandler;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataException;
import org.apache.ojb.broker.metadata.RepositoryXmlHandler;
import org.apache.ojb.broker.util.ClassHelper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class OjbUtil {

    private static final Log LOG = LogFactory.getLog(OjbUtil.class);

    private OjbUtil() {
        throw new UnsupportedOperationException("do not call");
    }

    public static Collection<DescriptorRepository> getDescriptorRepositories(Collection<String> ojbFiles) throws Exception {
        final Collection<DescriptorRepository> drs = new ArrayList<DescriptorRepository>();

        //first parse & get all of the mapped classes
        for (String file : ojbFiles) {
            drs.add(OjbUtil.readDescriptorRepository(file));
        }

        return drs;
    }

    public static ClassDescriptor findClassDescriptor(String clazz, Collection<DescriptorRepository> descriptorRepositories) {
        for (DescriptorRepository dr : descriptorRepositories) {
            ClassDescriptor cd = (ClassDescriptor) dr.getDescriptorTable().get(clazz);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }

    /**
     * Parses a repository file and populates an ojb datastructure representing the file.
     * @param filename the file to parse
     * @return a DescriptorRepository or null
     */
    public static DescriptorRepository readDescriptorRepository(String filename) {
        try {
            return (DescriptorRepository) buildRepository(filename, DescriptorRepository.class);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets all the mapped classes & their super classes stopping when the super class matches a package prefix
     */
    public static Set<String> mappedClasses(Collection<DescriptorRepository> descriptors) throws Exception {
        Set<String> mappedClasses = new HashSet<String>();
        for (DescriptorRepository dr : descriptors) {
            mappedClasses.addAll(((Map<String, ClassDescriptor>) dr.getDescriptorTable()).keySet());
        }
        return mappedClasses;
    }


    /**
     * Gets all the super classes & stopping when the super class matches a package prefix
     */
    public static Set<String> getSuperClasses(Collection<String> classes, String packagePrefixToStop) throws Exception {

        Set<String> superClasses = new HashSet<String>();
        for (String clazzName : classes) {
            Class<?> clazz = Class.forName(clazzName);
            for (Class<?> sc = clazz.getSuperclass(); sc != null && sc != Object.class && !sc.getName().startsWith(packagePrefixToStop);) {
                superClasses.add(sc.getName());
                sc = sc.getSuperclass();
            }

        }

        return superClasses;
    }

    private static Object buildRepository(String repositoryFileName, Class targetRepository) throws IOException, ParserConfigurationException, SAXException {
        URL url = buildURL(repositoryFileName);

        String pathName = url.toExternalForm();

        LOG.debug("Building repository from :" + pathName);
        InputSource source = new InputSource(pathName);
        URLConnection conn = url.openConnection();
        conn.setUseCaches(false);
        conn.connect();
        InputStream i = conn.getInputStream();
        source.setByteStream(i);
        try {
            return readMetadataFromXML(source, targetRepository);
        } finally {
            try {
                i.close();
            } catch (IOException x) {
                LOG.warn("unable to close repository input stream [" + x.getMessage() + "]", x);
            }
        }
    }


    private static Object readMetadataFromXML(InputSource source, Class target) throws ParserConfigurationException, SAXException, IOException {
        // get a xml reader instance:
        SAXParserFactory factory = SAXParserFactory.newInstance();
        LOG.debug("RepositoryPersistor using SAXParserFactory : " + factory.getClass().getName());

        SAXParser p = factory.newSAXParser();
        XMLReader reader = p.getXMLReader();

        Object result;
        if (DescriptorRepository.class.equals(target)) {
            // create an empty repository:
            DescriptorRepository repository = new DescriptorRepository();
            // create handler for building the repository structure
            org.xml.sax.ContentHandler handler = new RepositoryXmlHandler(repository);
            // tell parser to use our handler:
            reader.setContentHandler(handler);
            reader.parse(source);
            result = repository;
        } else if (ConnectionRepository.class.equals(target)) {
            // create an empty repository:
            ConnectionRepository repository = new ConnectionRepository();
            // create handler for building the repository structure
            org.xml.sax.ContentHandler handler = new ConnectionDescriptorXmlHandler(repository);
            // tell parser to use our handler:
            reader.setContentHandler(handler);
            reader.parse(source);
            //LoggerFactory.getBootLogger().info("loading XML took " + (stop - start) + " msecs");
            result = repository;
        } else
            throw new MetadataException("Could not build a repository instance for '" + target +
                    "', using source " + source);
        return result;
    }

    private static URL buildURL(String repositoryFileName) throws MalformedURLException {
        //j2ee compliant lookup of resources
        URL url = ClassHelper.getResource(repositoryFileName);

        // don't be too strict: if resource is not on the classpath, try ordinary file lookup
        if (url == null) {
            try {
                url = new File(repositoryFileName).toURL();
            }
            catch (MalformedURLException ignore) {
            }
        }

        if (url != null) {
            LOG.info("OJB Descriptor Repository: " + url);
        } else {
            throw new MalformedURLException("did not find resource " + repositoryFileName);
        }
        return url;
    }
}
