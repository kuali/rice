package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.VoidVisitorHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.MappedSuperClassResolver;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This visitor is designed to add @MappedSuperClass to an AST along with the corresponding import statement.
 */
public class MappedSuperClassVisitor extends OjbDescriptorRepositoryAwareVisitor {

    private final VoidVisitorHelper<Object> annotationHelper;

    public MappedSuperClassVisitor(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);

        final Collection<AnnotationResolver> annotations = new ArrayList<AnnotationResolver>();
        annotations.add(new MappedSuperClassResolver());

        annotationHelper = new AnnotationHelper(descriptorRepositories, annotations);
    }

    @Override
    public void visit(final CompilationUnit n, final Object arg) {
        super.visit(n, arg);
        ParserUtil.sortImports(n.getImports());
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration n, final Object arg) {
        annotationHelper.visitPre(n, arg);

        ParserUtil.deconstructMultiDeclarations(ParserUtil.getFieldMembers(n.getMembers()));

        if (n.getMembers() != null) {
            for (final BodyDeclaration member : n.getMembers()) {
                member.accept(this, arg);
            }
        }

        annotationHelper.visitPost(n, arg);
    }

    @Override
    public void visit(final FieldDeclaration n, final Object arg) {
        annotationHelper.visitPre(n, arg);

        //insert logic here if needed

        annotationHelper.visitPost(n, arg);
    }
}
