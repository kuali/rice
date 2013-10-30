package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;

public final class ResolverUtil {

    private static final Log LOG = LogFactory.getLog(ResolverUtil.class);

    private ResolverUtil() {
        throw new UnsupportedOperationException("do not call");
    }

    public static boolean canFieldBeAnnotated(FieldDeclaration node) {
        final TypeDeclaration dclr = (TypeDeclaration) node.getParentNode();
        if (!ModifierSet.isStatic(node.getModifiers()) && dclr.getParentNode() instanceof CompilationUnit) {
            //handling nested classes
            return true;
        }
        return false;
    }

    public static String logMsgForField(String enclosingClass, String fieldName, String mappedClass) {
        return enclosingClass + "." + fieldName + " for the mapped class " + mappedClass;
    }

    public static String logMsgForClass(String enclosingClass, String mappedClass) {
        return enclosingClass + " for the mapped class " + mappedClass;
    }

    public static Class<?> getType(String clazz, String fieldName) {
        try {
            Class<?> c = Class.forName(clazz);
            Field field = null;
            while (field == null && c != Object.class) {
                Field[] fields = c.getDeclaredFields();
                field = getField(fields, fieldName);
                c = c.getSuperclass();
            }
            if (field != null) {
                return field.getType();
            }
        } catch (Exception e) {
            LOG.error("Cannot get type from " + clazz + "." + fieldName, e);
        }
        return null;
    }

    private static Field getField(Field[] fields, String fieldName) {
        if (fields != null) {
            for (Field field : fields) {
                if (fieldName.equals(field.getName())) {
                    return field;
                }
            }
        }
        return null;
    }
}
