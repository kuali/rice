package org.kuali.rice.devtools.jpa.eclipselink.conv;


import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.common.CommonUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor.EntityVisitor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor.MappedSuperClassVisitor;

import java.io.File;
import java.util.Collection;

/**
 * a work in progress driver.  just an example of how to use this conversion program
 */
public class OjbToJpaConversionDriver {

    private static final Log LOG = LogFactory.getLog(OjbToJpaConversionDriver.class);

    private static void setupConfig() {
        ConversionConfig cfg = ConversionConfig.getInstance();
        cfg.setProjectHomeDir("/Users/Travis/Documents/idea/ws/kuali/kc/kc_project-krad_prototype");
        cfg.setProjectResourceDir("/src/main/resources");
        cfg.setProjectSourceDir("/src/main/java");

        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/award/repository-award.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/budget/repository-budget.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/coi/repository-coi.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/committee/repository-committee.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/common/committee/repository-commonCommittee.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/iacuc/repository-iacuc.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/iacuc/repository-iacucCommittee.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/institutionalproposal/repository-institutionalproposal.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/irb/repository-irb.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/negotiation/repository-negotiation.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/personmasschange/repository-personmasschange.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/proposaldevelopment/repository-proposaldevelopment.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/questionnaire/repository-questionnaire.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/subaward/repository-subAward.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/org/kuali/kra/timeandmoney/repository-timeandmoney.xml"));
        cfg.addOjbRepositoryFile(fullResourcePath(cfg, "/repository.xml"));

       // cfg.addConverter(org.kuali.kra.infrastructure.OjbBudgetDecimalFieldConversion.class.getName(), BudgetDecimalConverter.class.getName());
        cfg.addConverter("org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiEncryptDecryptFieldConversion", "org.kuali.rice.krad.data.jpa.converters.EncryptionConverter");
        //cfg.addConverter(org.kuali.kra.infrastructure.OjbOnOffCampusFlagFieldConversion.class.getName(), null);
        //cfg.addConverter(org.kuali.kra.award.contacts.UnitContactTypeConverter.class.getName(), null);
        cfg.addConverter("org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiDecimalFieldConversion", "org.kuali.rice.krad.data.jpa.converters.KualiDecimalConverter");
        //cfg.addConverter(org.kuali.kra.infrastructure.OjbBlobClobFieldConersion.class.getName(), null);
        cfg.addConverter("org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion", "org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter");
    }

    public static void main(String...s) throws Exception {

        setupConfig();

        final Collection<DescriptorRepository> drs = OjbUtil.getDescriptorRepositories(ConversionConfig.getInstance().getOjbRepositoryFiles());
        /*Set<String> convs = new HashSet<String>();
        for (DescriptorRepository d : drs) {
            for (Object desc : d.getDescriptorTable().values()) {
                if (((ClassDescriptor) desc).getCollectionDescriptors() != null) {
                    for (CollectionDescriptor cd : (Collection<CollectionDescriptor>) ((ClassDescriptor) desc).getCollectionDescriptors()) {
                        if (cd.getQueryCustomizer() != null) {
                            convs.add(cd.getQueryCustomizer().getClass().getName());
                        }
                    }
                }
            }
        }

        for (String c : convs) {
            System.out.println("cfg.addCustomizer(\"" + c + "\", \"\")");
        }*/


        //1: handle all the classes directly mapped in OJB
        final Collection<String> ojbMappedClasses = OjbUtil.mappedClasses(drs);

        for (String ojbMappedFile : toFilePaths(ConversionConfig.getInstance(), ojbMappedClasses)) {
            //if (ojbMappedFile.endsWith("DevelopmentProposal.java")) {
                final CompilationUnit unit = JavaParser.parse(new File(ojbMappedFile));
                new EntityVisitor(drs).visit(unit, null);
                LOG.info(unit.toString());
            //}
        }

        //2: handle all the classes that are super classes of ojb mapped files but not residing in rice
        final Collection<String> superClasses = OjbUtil.getSuperClasses(ojbMappedClasses, "org.kuali.rice");

        for (String superClassFile : toFilePaths(ConversionConfig.getInstance(), superClasses)) {
            if (superClassFile.endsWith("KraPersistableBusinessObjectBase.java")) {
                final CompilationUnit unit = JavaParser.parse(new File(superClassFile));
                new MappedSuperClassVisitor(drs).visit(unit, null);
                //LOG.info(unit.toString());
            }
        }
    }

    private static Collection<String> toFilePaths(ConversionConfig cfg, Collection<String> mappedClasses) {
        return CommonUtil.toFilePaths(mappedClasses, cfg.getProjectHomeDir(), cfg.getProjectSourceDir());
    }

    private static String fullResourcePath(ConversionConfig cfg, String remaining) {
        return cfg.getProjectHomeDir() + cfg.getProjectResourceDir() + remaining;
    }
}
