package org.kuali.rice.krad.theme.preprocessor;

import java.io.File;
import java.util.Properties;

/**
 * Theme pre processors are registered with {@link org.kuali.rice.krad.theme.ThemeBuilder} and invoked on
 * each theme processed
 *
 * <p>
 * Pre processors are invoked after overlays for the theme have been applied, but before any merging and
 * minification. Therefore they can create or modify assets for the theme
 * </p>
 *
 * <p>
 * Pre processors may also have configuration that is supplied through the theme properties
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ThemePreProcessor {

    /**
     * Invoked to perform processing on the given theme
     *
     * @param themeName name of the theme to process
     * @param themeDirectory directory containing the theme assets
     * @param themeProperties properties for the theme containing its configuration
     */
    public void processTheme(String themeName, File themeDirectory, Properties themeProperties);
}
