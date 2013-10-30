package org.kuali.rice.devtools.jpa.eclipselink.conv.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    public static Collection<String> toFilePaths(Collection<String> classNames, String projectDir, String sourceDir) {
        Set<String> filePaths = new HashSet<String>();

        for (String clazz : classNames) {
            filePaths.add(toFilePath(clazz, projectDir, sourceDir));
        }

        return filePaths;
    }
}
