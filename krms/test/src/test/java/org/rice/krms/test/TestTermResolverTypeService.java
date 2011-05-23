package org.rice.krms.test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krms.api.engine.TermResolutionException;
import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.framework.type.TermResolverTypeService;

public class TestTermResolverTypeService implements TermResolverTypeService {

    @Override
    public TermResolver<?> loadTermResolver(
            final TermResolverDefinition termResolverDefinition) {
        
        if ("testResolver1".equals(termResolverDefinition.getName())) {
            return new TermResolver<String>() {

                @Override
                public Set<TermSpecification> getPrerequisites() {
                    HashSet<TermSpecification> results = new HashSet<TermSpecification>();
                    
                    Set<TermSpecificationDefinition> prereqDefs = termResolverDefinition.getPrerequisites();
                    if (prereqDefs != null) for (TermSpecificationDefinition def : prereqDefs) {
                        results.add(new TermSpecification(def.getName(), def.getType()));
                    }
                    
                    return results;
                }

                @Override
                public TermSpecification getOutput() {
                    TermSpecificationDefinition def = termResolverDefinition.getOutput();
                    return new TermSpecification(def.getName(), def.getType());
                }

                @Override
                public Set<String> getParameterNames() {
                    return Collections.unmodifiableSet(termResolverDefinition.getParameterNames());
                }

                @Override
                public int getCost() {
                    return 1;
                }

                @Override
                public String resolve(
                        Map<TermSpecification, Object> resolvedPrereqs,
                        Map<String, String> parameters)
                        throws TermResolutionException {
                    return "RESULT1";
                }
                
            };
        }
        return null;
    }

}
