package org.kuali.rice.devtools.jpa.eclipselink.conv.common;

/**
 * Class to hold generic helper methods.
 */
public final class CommonUtil {
    private CommonUtil() {
        throw new UnsupportedOperationException("do not call");
    }

    public static String toFilePath(String className, String projectDir, String sourceDir) {
        return projectDir + sourceDir + "/" + className.replace('.', '/') + ".java";
    }
}
