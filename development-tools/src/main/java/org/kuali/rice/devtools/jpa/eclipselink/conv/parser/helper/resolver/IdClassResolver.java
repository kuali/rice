package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IdClassResolver implements AnnotationResolver {
    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "IdClass";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public IdClassResolver(Collection<DescriptorRepository> descriptorRepositories) {
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

        if (hasCompoundPrimaryKey(fullyQualifiedClass)) {
            final ClassOrInterfaceDeclaration primaryKeyClass = createPrimaryKeyClass(name);
            final String pkClassName = primaryKeyClass.getName();
            return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr(name + "." + pkClassName)),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false), primaryKeyClass);

        }
        return null;
    }

    private boolean hasCompoundPrimaryKey(String clazz) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {

            //This causes a stackoverflow and appears to not work correctly
            //return cd.getPkFields().length > 1;
            int i = 0;
            FieldDescriptor[] fds = cd.getFieldDescriptions();
            if (fds != null) {
                for (FieldDescriptor fd : fds) {
                    if (fd.isPrimaryKey()) {
                        i++;
                    }
                }
            return i > 1;
            }
        }
        return false;
    }

    private ClassOrInterfaceDeclaration createPrimaryKeyClass(String parentName) {
        ClassOrInterfaceDeclaration dclr = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL, false, parentName + "Id");
        dclr.setInterface(false);

        MethodDeclaration toString = new MethodDeclaration(ModifierSet.PUBLIC, new ClassOrInterfaceType("String"), "toString");
        toString.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));

        MethodDeclaration equals = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(PrimitiveType.Primitive.Boolean), "equals",
                Collections.singletonList(new Parameter(new ClassOrInterfaceType("Object"), new VariableDeclaratorId("other"))));
        equals.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));

        MethodDeclaration hashCode = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(PrimitiveType.Primitive.Int), "hashCode");
        hashCode.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));


        List<BodyDeclaration> members = new ArrayList<BodyDeclaration>();
        members.add(toString);
        members.add(equals);
        members.add(hashCode);

        dclr.setMembers(members);


        return dclr;
    }
}
