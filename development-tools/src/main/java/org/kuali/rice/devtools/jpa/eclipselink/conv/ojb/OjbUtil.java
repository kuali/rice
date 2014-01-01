/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.jpa.eclipselink.conv.ojb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.ConnectionDescriptorXmlHandler;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataException;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
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

    /**
     * Starting with a root class, get the entire tree of mapped objects including collections and references.
     * Cycles are correctly handled.
     *
     * @param rootClass the top level class to start with.
     * @return a collection of classes to process
     */
    public static Collection<String> getMappedTree(String rootClass, Collection<DescriptorRepository> descriptorRepositories) {
        final Set<String> processed = new HashSet<String>();
        getMappedTree(rootClass, descriptorRepositories, processed);
        return processed;
    }

    private static void getMappedTree(String rootClass, Collection<DescriptorRepository> descriptorRepositories, Set<String> processed) {
        if (processed.contains(rootClass)) {
            return;
        }

        processed.add(rootClass);
        final ClassDescriptor cd = findClassDescriptor(rootClass, descriptorRepositories);
        if (cd != null) {
            final Collection<ObjectReferenceDescriptor> ords = cd.getObjectReferenceDescriptors();
            if (ords != null) {
                for (ObjectReferenceDescriptor ord : ords) {
                    getMappedTree(ord.getItemClassName(), descriptorRepositories, processed);
                }
            }

            final Collection<CollectionDescriptor> clds = cd.getCollectionDescriptors();
            if (clds != null) {
                for (ObjectReferenceDescriptor cld : clds) {
                    getMappedTree(cld.getItemClassName(), descriptorRepositories, processed);
                }
            }

        } else {
            LOG.warn("ClassDescriptor not found for " + rootClass);
        }
    }

    public static boolean isMappedColumn(String clazz, String fieldName, Collection<DescriptorRepository> descriptorRepositories) {
        final ClassDescriptor cd = findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            return cd.getFieldDescriptorByName(fieldName) != null ||
                    cd.getObjectReferenceDescriptorByName(fieldName) != null ||
                    cd.getCollectionDescriptorByName(fieldName) != null;
        }
        return false;
    }

    public static Collection<DescriptorRepository> getDescriptorRepositories(Collection<String> ojbFiles) throws Exception {
        final Collection<DescriptorRepository> drs = new ArrayList<DescriptorRepository>();

        //first parse & get all of the mapped classes
        for (String file : ojbFiles) {
            DescriptorRepository repository = OjbUtil.readDescriptorRepository(file);
            if ( repository != null ) {
                drs.add(repository);
            }
        }

        return drs;
    }

    public static ClassDescriptor findClassDescriptor(String clazz, Collection<DescriptorRepository> descriptorRepositories) {
        for (DescriptorRepository dr : descriptorRepositories) {
            ClassDescriptor cd = (ClassDescriptor) dr.getDescriptorTable().get(clazz);

            if (cd != null) {
                //handle extends.  don't return class descriptor for extent classes
                if (cd.getExtentClassNames() == null || cd.getExtentClassNames().isEmpty()) {
                    return cd;
                }
            }
        }
        return null;
    }

    public static FieldDescriptor findFieldDescriptor(String clazz, String fieldName, Collection<DescriptorRepository> descriptorRepositories) {
        final ClassDescriptor cd = findClassDescriptor(clazz, descriptorRepositories);
        return cd != null ? cd.getFieldDescriptorByName(fieldName) : null;
    }

    public static ObjectReferenceDescriptor findObjectReferenceDescriptor(String clazz, String fieldName, Collection<DescriptorRepository> descriptorRepositories) {
        final ClassDescriptor cd = findClassDescriptor(clazz, descriptorRepositories);
        return cd != null ? cd.getObjectReferenceDescriptorByName(fieldName) : null;
    }

    public static CollectionDescriptor findCollectionDescriptor(String clazz, String fieldName, Collection<DescriptorRepository> descriptorRepositories) {
        final ClassDescriptor cd = findClassDescriptor(clazz, descriptorRepositories);
        return cd != null ? cd.getCollectionDescriptorByName(fieldName) : null;
    }

    public static Collection<String> getPrimaryKeyNames(String clazz, Collection<DescriptorRepository> descriptorRepositories) {
        final Collection<String> pks = new ArrayList<String>();
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        for(FieldDescriptor pk : cd.getPkFields()) {
            pks.add(pk.getAttributeName());
        }
        return pks;
    }

    /**
     * Parses a repository file and populates an ojb datastructure representing the file.
     * @param filename the file to parse
     * @return a DescriptorRepository or null
     */
    public static DescriptorRepository readDescriptorRepository(String filename) {
        LOG.info( "Processing Repository: " + filename);
        try {
            return (DescriptorRepository) buildRepository(filename, DescriptorRepository.class);
        } catch (Exception e) {
            LOG.error("Unable to process descriptor repository: " + filename);
            LOG.error( e.getMessage() );
            // Explicitly not logging the exception - it has already been dumped by earlier logging 
        }
        return null;
    }

    /**
     * Gets all the mapped classes
     */
    public static Set<String> mappedClasses(Collection<DescriptorRepository> descriptors) throws Exception {
        final Set<String> mappedClasses = new HashSet<String>();
        for (DescriptorRepository dr : descriptors) {
            for (Map.Entry<String, ClassDescriptor> entry : ((Map<String, ClassDescriptor>) dr.getDescriptorTable()).entrySet()) {
                final Collection<String> extents = entry.getValue().getExtentClassNames();
                if (extents != null && !extents.isEmpty()) {
                    mappedClasses.addAll(extents);
                } else {
                    mappedClasses.add(entry.getKey());
                }
            }
        }
        return mappedClasses;
    }


    /**
     * Gets all the super classes & stopping when the super class matches a package prefix
     */
    public static Set<String> getSuperClasses(String clazzName, String packagePrefixToStop) throws Exception {

        final Set<String> superClasses = new HashSet<String>();

        Class<?> clazz = Class.forName(clazzName);
        for (Class<?> sc = clazz.getSuperclass(); sc != null && sc != Object.class && !sc.getName().startsWith(packagePrefixToStop);) {
            superClasses.add(sc.getName());
            sc = sc.getSuperclass();
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
