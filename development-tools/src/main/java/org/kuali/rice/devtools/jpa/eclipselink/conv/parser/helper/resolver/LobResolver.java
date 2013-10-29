package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LobResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(LobResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Lob";
    private static final Collection<Class> VALID_TYPES;
    private static final Collection<String> VALID_TYPES_STR;
    static {
        Collection<Class> tempClass = new ArrayList<Class>();
        tempClass.add(String.class);
        tempClass.add(byte[].class);
        tempClass.add(Byte[].class);
        tempClass.add(char[].class);
        tempClass.add(Character[].class);

        Collection<String> tempClassStr = new ArrayList<String>();
        for (Class<?> c : tempClass) {
            tempClassStr.add(c.getName());
        }

        VALID_TYPES = Collections.unmodifiableCollection(tempClass);
        VALID_TYPES_STR = Collections.unmodifiableCollection(tempClassStr);
    }

    private final Collection<DescriptorRepository> descriptorRepositories;

    public LobResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    public Level getLevel() {
        return Level.FIELD;
    }

    @Override
    public NodeData resolve(Node node, Object arg) {
        if (!(node instanceof FieldDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on FieldDeclaration");
        }

        final FieldDeclaration field = (FieldDeclaration) node;

        if (canBeAnnotated(field)) {
            final TypeDeclaration dclr = (TypeDeclaration) node.getParentNode();
            if (!(dclr.getParentNode() instanceof CompilationUnit)) {
                //handling nested classes
                return null;
            }
            final String name = dclr.getName();
            final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
            final String fullyQualifiedClass = pckg + "." + name;
            final boolean mappedColumn = isMappedColumn(fullyQualifiedClass, ParserUtil.getFieldName(field));
            if (mappedColumn) {
                return getAnnotationNodes(fullyQualifiedClass, ParserUtil.getFieldName(field));
            }
        }
        return null;
    }

    private boolean canBeAnnotated(FieldDeclaration node) {
        return !ModifierSet.isStatic(node.getModifiers());
    }

    private boolean isMappedColumn(String clazz, String fieldName) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            return cd.getFieldDescriptorByName(fieldName) != null;
        }
        return false;
    }

    private NodeData getAnnotationNodes(String clazz, String fieldName) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            final FieldDescriptor fd = cd.getFieldDescriptorByName(fieldName);
            final Class<?> fc = getType(clazz, fieldName);
            final String columnType = fd.getColumnType();
            if (isLob(columnType)) {
                if (isValidFieldType(fc)) {
                    return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
                } else {
                    LOG.error(clazz + "." + fieldName + " is not a valid field type for the @Lob annotation, must be one of " + VALID_TYPES_STR);
                }
            }

            return null;
        }
        return null;
    }

    private boolean isLob(String columnType) {
        return "BLOB".equals(columnType) || "CLOB".equals(columnType);
    }

    private Class<?> getType(String clazz, String fieldName) {
        try {
            final Class<?> c = Class.forName(clazz);
            final Field f = c.getDeclaredField(fieldName);
            return f.getType();
        } catch (Exception e) {
            LOG.error("Cannot get type from " + clazz + "." + fieldName, e);
        }
        return null;
    }

    private boolean isValidFieldType(Class type) {
        for (Class<?> c : VALID_TYPES) {
            if (c.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
}
