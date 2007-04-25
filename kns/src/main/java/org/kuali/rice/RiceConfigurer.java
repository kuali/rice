/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice;

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import edu.iu.uis.eden.util.ClassLoaderUtils;

public class RiceConfigurer implements InitializingBean {
    
    private static String configurationFile;
    public static final String DEFAULT_CONFIGURATION_FILE = "classpath:configuration.properties";
    
    private String datasourcePropertiesLocation;
    private String encryptionPropertiesLocation;
    private String keystorePropertiesLocation;
    private String mailProperties;
    
    private Properties properties;

    public String getDatasourcePropertiesLocation() {
        return datasourcePropertiesLocation;
    }

    public void setDatasourcePropertiesLocation(String datasourcePropertiesLocation) {
        this.datasourcePropertiesLocation = datasourcePropertiesLocation;
    }

    public String getEncryptionPropertiesLocation() {
        return encryptionPropertiesLocation;
    }

    public void setEncryptionPropertiesLocation(String encryptionPropertiesLocation) {
        this.encryptionPropertiesLocation = encryptionPropertiesLocation;
    }

    public String getKeystorePropertiesLocation() {
        return keystorePropertiesLocation;
    }

    public void setKeystorePropertiesLocation(String keystorePropertiesLocation) {
        this.keystorePropertiesLocation = keystorePropertiesLocation;
    }

    public String getMailProperties() {
        return mailProperties;
    }

    public void setMailProperties(String mailProperties) {
        this.mailProperties = mailProperties;
    }

    public void afterPropertiesSet() throws Exception {
        Properties props = getPropsFromFile(getConfigurationFile());
        props.putAll(getPropsFromFile(props.getProperty("datasource.properties.file")));
        props.putAll(getPropsFromFile(props.getProperty("encryption.properties.file")));
        props.putAll(getPropsFromFile(props.getProperty("keystore.properties.file")));
        props.putAll(getPropsFromFile(props.getProperty("mail.properties.file")));
        this.setProperties(props);
    }

    private Properties getPropsFromFile(String fileName) throws Exception {
        if (fileName == null) {
            return new Properties();
        }
        if (fileName.indexOf("classpath:") == -1) {
            fileName = "file:" + fileName;
        }
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        Properties props = new Properties();
        props.load(resourceLoader.getResource(fileName).getInputStream());
        return props;
    }

    public static String getConfigurationFile() {
        if (configurationFile == null) {
            return DEFAULT_CONFIGURATION_FILE;
        }
        return configurationFile;
    }

    public static void setConfigurationFile(String overrideConfigurationFile) {
        RiceConfigurer.configurationFile = overrideConfigurationFile;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
}
