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

    public static <T> T loadExtension(ExtensionDefinitionContract extensionDefinition) {
        return ExtensionUtils.<T>loadExtension(extensionDefinition, null);
    }

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
