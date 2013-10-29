package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversionDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

public class ConvertResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(ConvertResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Convert";

    private final Collection<DescriptorRepository> descriptorRepositories;
    private Map<String,String> converterMappings;

    public ConvertResolver(Collection<DescriptorRepository> descriptorRepositories, Map<String,String> converterMappings) {
        this.descriptorRepositories = descriptorRepositories;
        this.converterMappings = converterMappings;
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
                return getAnnotationNodes(fullyQualifiedClass, ParserUtil.getFieldName(field), dclr);
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

    private String getJpaConverterForOjbClass( String ojbConverter ) {
        for ( String key : converterMappings.keySet() ) {
            // Substring match
            if ( ojbConverter.contains(key) ) {
                return converterMappings.get(key);
            }
        }
        return null;
    }
    
    /** gets the annotation but also adds an import in the process if a Convert annotation is required. */
    private NodeData getAnnotationNodes(String clazz, String fieldName, TypeDeclaration dclr) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            FieldDescriptor fd = cd.getFieldDescriptorByName(fieldName);
            final FieldConversion fc = fd.getFieldConversion();
            //in ojb all columns have at least the default field conversion
            if (fc != null && FieldConversionDefaultImpl.class != fc.getClass()) {
                LOG.info(clazz + "." + fieldName + " field has a converter " + fc.getClass().getName());

                final String jpaConverter = getJpaConverterForOjbClass(fc.getClass().getName());
                if (jpaConverter == null) {
                    LOG.error(clazz + "." + fieldName + " field has a converter " + fc.getClass().getName()
                        + " but a replacement converter was not configured, unable to set Convert class");
                    return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr(null)),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
                } else if ( StringUtils.isBlank(jpaConverter) ) {
                    LOG.info( clazz + "." + fieldName + " field has a converter " + fc.getClass().getName() 
                            + " But no converter definition is needed due to default converter configuration." );
                } else {
                    final String shortClassName = ClassUtils.getShortClassName(jpaConverter);
                    final String packageName = ClassUtils.getPackageName(jpaConverter);
                    return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr(shortClassName + ".class")),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                            Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(packageName), shortClassName), false, false)));
                }
            }
        }
        return null;
    }
}
