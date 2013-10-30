package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.VoidVisitorHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ColumnResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ConvertResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.CustomizerResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.EntityResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.GeneratedValueResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.IdClassResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.IdResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.JoinTableResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.LobResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ManyToManyResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ManyToOneResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.MappedSuperClassResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OneToManyResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OneToOneResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OrderByResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.PortableSequenceGeneratorResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TableResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TemporalResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TransientResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.VersionResolver;

/**
 * For visiting ojb mapped entities and their super classes.
 */
public class EntityVisitor extends OjbDescriptorRepositoryAwareVisitor {
    private static final Log LOG = LogFactory.getLog(OjbDescriptorRepositoryAwareVisitor.class);

    private final VoidVisitorHelper<String> annotationHelper;

    public EntityVisitor(Collection<DescriptorRepository> descriptorRepositories, Map<String,String> converterMappings ) {
        super(descriptorRepositories);

        final Collection<AnnotationResolver> annotations = new ArrayList<AnnotationResolver>();
        annotations.add(new EntityResolver(getDescriptorRepositories()));
        annotations.add(new MappedSuperClassResolver(getDescriptorRepositories()));
        annotations.add(new TableResolver(getDescriptorRepositories()));
        annotations.add(new CustomizerResolver(getDescriptorRepositories()));
        annotations.add(new TransientResolver(getDescriptorRepositories()));
        annotations.add(new PortableSequenceGeneratorResolver(getDescriptorRepositories()));
        annotations.add(new GeneratedValueResolver(getDescriptorRepositories()));
        annotations.add(new IdResolver(getDescriptorRepositories()));
        annotations.add(new OneToOneResolver(getDescriptorRepositories()));
        annotations.add(new OneToManyResolver(getDescriptorRepositories()));
        annotations.add(new ManyToOneResolver(getDescriptorRepositories()));
        annotations.add(new ManyToManyResolver(getDescriptorRepositories()));
        annotations.add(new JoinTableResolver(getDescriptorRepositories()));
        annotations.add(new OrderByResolver(getDescriptorRepositories()));
        annotations.add(new ColumnResolver(getDescriptorRepositories()));
        annotations.add(new ConvertResolver(getDescriptorRepositories(),converterMappings));
        annotations.add(new VersionResolver(getDescriptorRepositories()));
        annotations.add(new TemporalResolver(getDescriptorRepositories()));
        annotations.add(new LobResolver(getDescriptorRepositories()));
        annotations.add(new IdClassResolver(getDescriptorRepositories()));

        annotationHelper = new AnnotationHelper(annotations);
    }

    @Override
    public void visit(final CompilationUnit n, final String mappedClass) {
        super.visit(n, mappedClass);
        ParserUtil.sortImports(n.getImports());
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration n, final String mappedClass) {
        annotationHelper.visitPre(n, mappedClass);

        ParserUtil.deconstructMultiDeclarations(ParserUtil.getFieldMembers(n.getMembers()));

        if (n.getMembers() != null) {
            for (final BodyDeclaration member : n.getMembers()) {
                member.accept(this, mappedClass);
            }
        }

        annotationHelper.visitPost(n, mappedClass);
    }

    @Override
    public void visit(final FieldDeclaration n, final String mappedClass) {
        annotationHelper.visitPre(n, mappedClass);

        //insert logic here if needed

        annotationHelper.visitPost(n, mappedClass);
    }
}
