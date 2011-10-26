package org.kuali.rice.kew.api.extension;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

import javax.xml.namespace.QName;

/**
 * Contains utilities related to the loading of extension resources.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ExtensionUtils {

    /**
     * Loads the extension object for the given extension definition.
     *
     * @param extensionDefinition the definition of the extension to load
     * @param <T> the type of the extension object which is to be loaded
     *
     * @return the loaded extension, or null if no extension was loaded for the given definition
     */
    public static <T> T loadExtension(ExtensionDefinitionContract extensionDefinition) {
        return ExtensionUtils.<T>loadExtension(extensionDefinition, null);
    }

    /**
     * Loads the extension object for the given extension definition, using the default applicationId if the given
     * extension definition has a null applicationId.
     *
     * @param extensionDefinition the definition of the extension to load
     * @param defaultApplicationId the application id to use when attempting to loading the extension if the
     * application id on the given definition is null
     * @param <T> the type of the extension object which is to be loaded
     *
     * @return the loaded extension, or null if no extension was loaded for the given definition
     */
    public static <T> T loadExtension(ExtensionDefinitionContract extensionDefinition, String defaultApplicationId) {
        T extensionService = null;
        // first check if the class name is a valid and available java class
        String resourceDescriptor = extensionDefinition.getResourceDescriptor();
        ObjectDefinition extensionObjectDefinition = getExtensionObjectDefinition(extensionDefinition,
                defaultApplicationId);
        extensionService = GlobalResourceLoader.<T>getObject(extensionObjectDefinition);
        if (extensionService == null) {
            // if we can't find a class, try a service
            extensionService = GlobalResourceLoader.<T>getService(QName.valueOf(resourceDescriptor));
        }
        return extensionService;
    }

    private static ObjectDefinition getExtensionObjectDefinition(ExtensionDefinitionContract extensionDefinition, String defaultApplicationId) {
        if (StringUtils.isBlank(extensionDefinition.getApplicationId()) && StringUtils.isNotBlank(defaultApplicationId)) {
            return new ObjectDefinition(extensionDefinition.getResourceDescriptor(), defaultApplicationId);
        } else {
            return new ObjectDefinition(extensionDefinition.getResourceDescriptor(), extensionDefinition.getApplicationId());
        }
    }

    private ExtensionUtils() {
        throw new UnsupportedOperationException();
    }

}
