package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CustomizerResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(CustomizerResolver.class);
    public static final String PACKAGE = "org.eclipse.persistence.annotations";
    public static final String SIMPLE_NAME = "Customizer";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public CustomizerResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    public Level getLevel() {
        return Level.CLASS;
    }

    @Override
    public NodeData resolve(Collection<DescriptorRepository> drs, Node node, Object arg) {
        if (!(node instanceof ClassOrInterfaceDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on ClassOrInterfaceDeclaration");
        }

        final TypeDeclaration dclr = (TypeDeclaration) node;
        if (!(dclr.getParentNode() instanceof CompilationUnit)) {
            //handling nested classes
            return null;
        }
        final String name = dclr.getName();
        final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
        final String fullyQualifiedClass = pckg + "." + name;
        final Collection<String> customizedFields = getCustomizedFields(fullyQualifiedClass);
        if (customizedFields == null || customizedFields.isEmpty()) {
            LOG.info("Table has no customized fields for mapped class " + fullyQualifiedClass);
            return null;
        }
        return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr("CreateCustomizerFor" + customizedFields.toString())),
                new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
    }

    private Collection<String> getCustomizedFields(String clazz) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            Collection<String> customizedFields = new ArrayList<String>();
            for (CollectionDescriptor cld : (Collection<CollectionDescriptor>) cd.getCollectionDescriptors()) {
                if (cld.getQueryCustomizer() != null) {
                    customizedFields.add(cld.getAttributeName());
                }
            }
            return customizedFields;
        }
        return Collections.emptySet();
    }
}
