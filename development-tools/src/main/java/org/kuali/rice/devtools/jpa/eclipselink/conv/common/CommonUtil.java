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

    private static String getJavaSourceFilePath(String clazz, String projectDir, String sourceDir) {
        return projectDir + sourceDir + "/" + clazz.replace('.', '/') + ".java";
    }

    public static Collection<String> toFilePaths(Collection<String> classNames, String projectDir, String sourceDir) {
        Set<String> filePaths = new HashSet<String>();

        for (String clazz : classNames) {
            filePaths.add(getJavaSourceFilePath(clazz, projectDir, sourceDir));
        }

        return filePaths;
    }
}
