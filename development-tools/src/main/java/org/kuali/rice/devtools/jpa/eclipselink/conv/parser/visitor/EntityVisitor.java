/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.VoidVisitorHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ColumnResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ConvertResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.CustomizerResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.EntityResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.EnumeratedResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.GeneratedValueResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.IdClassResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.IdResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.JoinColumnResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.JoinColumnsResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.JoinTableResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.LobResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ManyToManyResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ManyToOneResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.MappedSuperClassResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OneToManyResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OneToOneResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.OrderByResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.PortableSequenceGeneratorResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.PrimaryKeyJoinColumnResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.PrimaryKeyJoinColumnsResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.ResolverUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TableResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TemporalResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.TransientResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver.VersionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * For visiting ojb mapped entities and their super classes.
 */
public class EntityVisitor extends OjbDescriptorRepositoryAwareVisitor {
    private static final Log LOG = LogFactory.getLog(EntityVisitor.class);

    //warning this can grow rather large and is never cleared out
    private static final Map<String, Map<String, CompilationUnit>> PROCESSED_CACHE = new HashMap<String, Map<String, CompilationUnit>>();

    private final VoidVisitorHelper<String> annotationHelper;

    public void setErrorsOnly() {
        Logger.getLogger("org.kuali.rice.devtools.jpa.eclipselink.conv").setLevel(Level.WARN);
    }
    
    public EntityVisitor(Collection<DescriptorRepository> descriptorRepositories, Map<String,String> converterMappings, boolean removeExisting, boolean upperCaseDbArtifactNames) {
        super(descriptorRepositories);
        System.out.println( "Created new EntityVisitor for JPA Conversion" );
//        try {
//            Properties p = new Properties();
//            p.load( getClass().getClassLoader().getResourceAsStream("log4j.properties"));
//            PropertyConfigurator.configure( p );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        BasicConfigurator.configure();
        Logger.getLogger("org.kuali.rice.devtools.jpa.eclipselink.conv").setLevel(Level.INFO);
        if (converterMappings == null || converterMappings.isEmpty()) {
            throw new IllegalArgumentException("converterMappings cannot be null or empty");
        }

        final Collection<AnnotationResolver> annotations = new ArrayList<AnnotationResolver>();
        annotations.add(new EntityResolver(getDescriptorRepositories()));
        annotations.add(new MappedSuperClassResolver(getDescriptorRepositories()));
        annotations.add(new TableResolver(getDescriptorRepositories(), upperCaseDbArtifactNames));
        annotations.add(new CustomizerResolver(getDescriptorRepositories()));
        annotations.add(new TransientResolver(getDescriptorRepositories()));
        annotations.add(new PortableSequenceGeneratorResolver(getDescriptorRepositories(), upperCaseDbArtifactNames));
        annotations.add(new GeneratedValueResolver(getDescriptorRepositories(), upperCaseDbArtifactNames));
        annotations.add(new IdResolver(getDescriptorRepositories()));
        annotations.add(new OneToOneResolver(getDescriptorRepositories()));
        annotations.add(new OneToManyResolver(getDescriptorRepositories()));
        annotations.add(new PrimaryKeyJoinColumnResolver(getDescriptorRepositories()));
        annotations.add(new PrimaryKeyJoinColumnsResolver(getDescriptorRepositories()));
        annotations.add(new ManyToOneResolver(getDescriptorRepositories()));
        annotations.add(new ManyToManyResolver(getDescriptorRepositories()));
        annotations.add(new JoinTableResolver(getDescriptorRepositories()));
        annotations.add(new JoinColumnResolver(getDescriptorRepositories()));
        annotations.add(new JoinColumnsResolver(getDescriptorRepositories()));
        annotations.add(new OrderByResolver(getDescriptorRepositories()));
        annotations.add(new ColumnResolver(getDescriptorRepositories(), upperCaseDbArtifactNames));
        annotations.add(new ConvertResolver(getDescriptorRepositories(),converterMappings));
        annotations.add(new VersionResolver(getDescriptorRepositories()));
        annotations.add(new TemporalResolver(getDescriptorRepositories()));
        annotations.add(new LobResolver(getDescriptorRepositories()));
        annotations.add(new EnumeratedResolver(getDescriptorRepositories()));
        annotations.add(new IdClassResolver(getDescriptorRepositories()));

        annotationHelper = new AnnotationHelper(annotations, removeExisting);
    }

    @Override
    public void visit(final CompilationUnit n, final String mappedClass) {
        if (StringUtils.isBlank(mappedClass)) {
            throw new IllegalArgumentException("mappedClass cannot be blank");
        }

        super.visit(n, mappedClass);
        ParserUtil.sortImports(n.getImports());

        processedCache(n, mappedClass, PROCESSED_CACHE);
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

    /**
     * When there is a common super class with multiple subclasses there is a potential for different mapping configurations
     * on the super class's fields (attributes, references, collections).  This is because the subclass's mapping metadata is
     * used to determine how to map the super class.
     *
     * This method is designed to log a message when these types on conflicts are detected during conversion.  The differences
     * are not logged and must be manually evaluated on a case by case basis.
     *
     * This is an error situation that must be resolved in order to properly map and entity.  You cannot have
     * one attribute in a superclass as @Transient for one subclass while mapped to a @Column for another subclass.
     *
     * When this cases arise you will likely need do one of the following:
     *
     * 1) use the @AssociationOverride and/or @AttributeOverride in a subclass
     * 2) modify the database table structure to make a uniform mapping possible, modify the ojb mapping to
     * 3) move certain fields out of the superclass into subclasses
     * 4) create new superclasses to make it possible to have correct mapping
     *
     * @param n the compilation unit, already modified by the visitor
     * @param mappedClass the mapped class who's metadata was used to annotate the compilation unit
     * @param cache the cache that stores compilation unit information
     */
    private void processedCache(CompilationUnit n, String mappedClass, Map<String, Map<String, CompilationUnit>> cache) {
        final String enclosingName = n.getPackage().getName() + "." + n.getTypes().get(0).getName();

        if (!enclosingName.equals(mappedClass)) {
            Map<String, CompilationUnit> entries = cache.get(enclosingName);
            if (entries == null) {
                entries = new HashMap<String, CompilationUnit>();
                entries.put(mappedClass, n);
                cache.put(enclosingName, entries);
            } else {
                if (!equalsAny(n, mappedClass, entries, enclosingName)) {
                    //put this unique version of the AST in the cache... don't bother storing equal versions
                    entries.put(mappedClass, n);
                    cache.put(enclosingName, entries);
                }
            }
        }
    }

    private boolean equalsAny(CompilationUnit n, String mappedClass, Map<String, CompilationUnit> entries, String enclosingName) {
        boolean equalsAny = false;
        for (Map.Entry<String, CompilationUnit> entry : entries.entrySet()) {
            if (!entry.getValue().equals(n)) {
                LOG.error(ResolverUtil.logMsgForClass(enclosingName, mappedClass) + " does not equal the modified AST for " + ResolverUtil.logMsgForClass(enclosingName, entry.getKey()) +
                        ". This likely means that a super class' fields have different mapping configurations across mapped subclasses.");
            } else {
                equalsAny = true;
            }
        }

        return equalsAny;
    }
}
