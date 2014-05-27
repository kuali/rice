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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatching;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findSingleMatching;

/**
 * Implementation of {@link TermBoService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermBoServiceImpl implements TermBoService {

    private DataObjectService dataObjectService;

    /**
     * @param dataObjectService the dataObjectService to set
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermSpecificationById(java.lang.String)
     */
    @Override
    public TermSpecificationDefinition getTermSpecificationById(String id) {
        TermSpecificationDefinition result = null;

        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id must not be blank or null");
        }

        TermSpecificationBo termSpecificationBo = dataObjectService.find(TermSpecificationBo.class, id);

        if (termSpecificationBo != null) {
            List<ContextValidTermBo> contextValidTermBos =
                    findMatching(dataObjectService, ContextValidTermBo.class,
                            Collections.singletonMap("termSpecification.id", termSpecificationBo.getId()));

            if (contextValidTermBos != null) for (ContextValidTermBo contextValidTerm : contextValidTermBos) {
                termSpecificationBo.getContextIds().add(contextValidTerm.getContextId());
            }

            result = TermSpecificationDefinition.Builder.create(termSpecificationBo).build();
        }

        return result;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermSpecification(org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition)
     */
    @Override
    public TermSpecificationDefinition createTermSpecification(TermSpecificationDefinition termSpec) {
        if (!StringUtils.isBlank(termSpec.getId())) {
            throw new RiceIllegalArgumentException("for creation, TermSpecification.id must be null");
        }

        TermSpecificationBo termSpecBo = TermSpecificationBo.from(termSpec);

        // save relations to the contexts on the BO
        if (!CollectionUtils.isEmpty(termSpec.getContextIds())) {
            for (String contextId : termSpec.getContextIds()) {
                ContextValidTermBo contextValidTerm = new ContextValidTermBo();
                contextValidTerm.setContextId(contextId);
                contextValidTerm.setTermSpecification(termSpecBo);

                termSpecBo.getContextValidTerms().add(contextValidTerm);
            }
        }

        termSpecBo = dataObjectService.save(termSpecBo, PersistenceOption.FLUSH);

        return TermSpecificationBo.to(termSpecBo);
    }

    @Override
    public void updateTermSpecification(TermSpecificationDefinition termSpec) throws RiceIllegalArgumentException {
        if (termSpec == null) {
            throw new IllegalArgumentException("term specification is null");
        }

        // must already exist to be able to update
        final String termSpecificationId = termSpec.getId();
        final TermSpecificationBo existing = dataObjectService.find(TermSpecificationBo.class, termSpecificationId);

        if (existing == null) {
            throw new IllegalStateException("the term specification does not exist: " + termSpec);
        }

        final TermSpecificationDefinition toUpdate;

        if (!existing.getId().equals(termSpec.getId())) {
            // if passed in id does not match existing id, correct it
            final TermSpecificationDefinition.Builder builder = TermSpecificationDefinition.Builder.create(termSpec);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = termSpec;
        }

        // copy all updateable fields to bo
        TermSpecificationBo boToUpdate = TermSpecificationBo.from(toUpdate);
        reconcileContextValidTerms(existing, boToUpdate);

        // update the rule and create new attributes
        dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);

    }

    /**
     * Transfer any ContextValidTermBos that still apply from existing to boToUpdate, and create new ContextValidTermBos
     * for any new context IDs that are found in boToUpdate.
     *
     * <p>This method is side effecting, it makes modifications to boToUpdate.contextValidTerms. </p>
     *
     * @param existing the TermSpecificationBo which has been fetched from the database
     * @param boToUpdate the new TermSpecificationBo which will (later) be persisted
     */
    private void reconcileContextValidTerms(TermSpecificationBo existing,
            TermSpecificationBo boToUpdate) {

        // add all contextValidTerms that still apply
        for (ContextValidTermBo contextValidTerm : existing.getContextValidTerms()) {
            if (boToUpdate.getContextIds().contains(contextValidTerm.getContextId())) {
                boToUpdate.getContextValidTerms().add(contextValidTerm);
            }
        }

        // add new contextValidTerms for new context IDs
        for (String contextId : boToUpdate.getContextIds()) {
            boolean alreadyInContextValidTerms = false;

            for (ContextValidTermBo contextValidTerm : boToUpdate.getContextValidTerms()) {
                if (contextId.equals(contextValidTerm.getContextId())) {
                    alreadyInContextValidTerms = true;
                    break;
                }
            }

            if (!alreadyInContextValidTerms) {
                ContextValidTermBo contextValidTerm = new ContextValidTermBo();
                contextValidTerm.setContextId(contextId);
                contextValidTerm.setTermSpecification(boToUpdate);

                boToUpdate.getContextValidTerms().add(contextValidTerm);
            }
        }
    }

    @Override
    public void deleteTermSpecification(String id) throws RiceIllegalArgumentException {
        if (id == null) {
            throw new RiceIllegalArgumentException("agendaId is null");
        }

        final TermSpecificationBo existing = dataObjectService.find(TermSpecificationBo.class, id);

        if (existing == null) {
            throw new IllegalStateException("the TermSpecification to delete does not exists: " + id);
        }

        dataObjectService.delete(existing);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#createTerm(org.kuali.rice.krms.api.repository.term.TermDefinition)
     */
    @Override
    public TermDefinition createTerm(TermDefinition termDef) {
        if (!StringUtils.isBlank(termDef.getId())) {
            throw new RiceIllegalArgumentException("for creation, TermDefinition.id must be null");
        }

        TermBo termBo = TermBo.from(termDef);
        termBo = dataObjectService.save(termBo, PersistenceOption.FLUSH);

        return TermBo.to(termBo);
    }

    @Override
    public void updateTerm(TermDefinition term) throws RiceIllegalArgumentException {
        if (term == null) {
            throw new IllegalArgumentException("term is null");
        }

        // must already exist to be able to update
        final String termId = term.getId();
        final TermBo existing = dataObjectService.find(TermBo.class, termId);

        if (existing == null) {
            throw new IllegalStateException("the term resolver does not exist: " + term);
        }

        final TermDefinition toUpdate;

        if (!existing.getId().equals(term.getId())) {
            // if passed in id does not match existing id, correct it
            final TermDefinition.Builder builder = TermDefinition.Builder.create(term);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = term;
        }

        // copy all updateable fields to bo
        TermBo boToUpdate = TermBo.from(toUpdate);

        // update the rule and create new attributes
        dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);
    }

    @Override
    public void deleteTerm(String id) throws RiceIllegalArgumentException {
        if (id == null) {
            throw new RiceIllegalArgumentException("termId is null");
        }

        TermBo existing = dataObjectService.find(TermBo.class, id);

        if (existing == null) {
            throw new IllegalStateException("the term to delete does not exists: " + id);
        }

        dataObjectService.delete(existing);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermResolver(org.kuali.rice.krms.api.repository.term.TermResolverDefinition)
     */
    @Override
    public TermResolverDefinition createTermResolver(TermResolverDefinition termResolver) {
        if (!StringUtils.isBlank(termResolver.getId())) {
            throw new RiceIllegalArgumentException("for creation, TermResolverDefinition.id must be null");
        }

        TermResolverBo termResolverBo = TermResolverBo.from(termResolver);

        termResolverBo = (TermResolverBo) dataObjectService.save(termResolverBo, PersistenceOption.FLUSH);

        return TermResolverBo.to(termResolverBo);
    }

    @Override
    public void updateTermResolver(TermResolverDefinition termResolver) throws RiceIllegalArgumentException {
        if (termResolver == null) {
            throw new IllegalArgumentException("term resolver is null");
        }

        // must already exist to be able to update
        final String termResolverId = termResolver.getId();
        final TermResolverBo existing = dataObjectService.find(TermResolverBo.class, termResolverId);

        if (existing == null) {
            throw new IllegalStateException("the term resolver does not exist: " + termResolver);
        }

        final TermResolverDefinition toUpdate;

        if (!existing.getId().equals(termResolver.getId())) {
            // if passed in id does not match existing id, correct it
            final TermResolverDefinition.Builder builder = TermResolverDefinition.Builder.create(termResolver);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = termResolver;
        }

        // copy all updateable fields to bo
        TermResolverBo boToUpdate = TermResolverBo.from(toUpdate);

        // delete any old, existing attributes
        QueryByCriteria crit =
                QueryByCriteria.Builder.forAttribute(KrmsImplConstants.PropertyNames.TermResolver.TERM_RESOLVER_ID, toUpdate.getId()).build();

        dataObjectService.deleteMatching(TermResolverAttributeBo.class, crit);

        // update the rule and create new attributes
        dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);
    }

    @Override
    public void deleteTermResolver(String id) throws RiceIllegalArgumentException {
        if (id == null) {
            throw new RiceIllegalArgumentException("agendaId is null");
        }

        TermSpecificationBo existing = dataObjectService.find(TermSpecificationBo.class, id);

        if (existing == null) {
            throw new IllegalStateException("the TermResolver to delete does not exists: " + id);
        }

        dataObjectService.delete(existing);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#getTerm(java.lang.String)
     */
    @Override
    public TermDefinition getTerm(String id) {
        TermDefinition result = null;

        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id must not be blank or null");
        }

        TermBo termBo = dataObjectService.find(TermBo.class, id);

        if (termBo != null) {
            result = TermBo.to(termBo);
        }

        return result;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermResolverById(java.lang.String)
     */
    @Override
    public TermResolverDefinition getTermResolverById(String id) {
        TermResolverDefinition result = null;

        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id must not be blank or null");
        }

        TermResolverBo termResolverBo = dataObjectService.find(TermResolverBo.class, id);

        if (termResolverBo != null) {
            result = TermResolverBo.to(termResolverBo);
        }

        return result;
    }

    @Override
    public List<TermResolverDefinition> findTermResolversByOutputId(String id, String namespace) {
        List<TermResolverDefinition> results = null;

        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id must not be blank or null");
        }

        if (StringUtils.isBlank(namespace)) {
            throw new RiceIllegalArgumentException("namespace must not be blank or null");
        }

        Map<String, String> critMap = new HashMap<String, String>(2);

        critMap.put("outputId", id);
        critMap.put("namespace", namespace);

        QueryByCriteria crit = QueryByCriteria.Builder.andAttributes(critMap).build();

        QueryResults<TermResolverBo> termResolverBos = dataObjectService.findMatching(TermResolverBo.class, crit);

        if (!CollectionUtils.isEmpty(termResolverBos.getResults())) {
            results = new ArrayList<TermResolverDefinition>(termResolverBos.getResults().size());

            for (TermResolverBo termResolverBo : termResolverBos.getResults()) {
                results.add(TermResolverBo.to(termResolverBo));
            }
        } else {
            results = Collections.emptyList();
        }

        return results;
    }

    @Override
    public List<TermResolverDefinition> findTermResolversByNamespace(String namespace) {
        List<TermResolverDefinition> results = null;

        if (StringUtils.isBlank(namespace)) {
            throw new RiceIllegalArgumentException("namespace must not be blank or null");
        }

        QueryByCriteria crit = QueryByCriteria.Builder.forAttribute("namespace", namespace).build();

        QueryResults<TermResolverBo> termResolverBos = dataObjectService.findMatching(TermResolverBo.class, crit);

        if (!CollectionUtils.isEmpty(termResolverBos.getResults())) {
            results = new ArrayList<TermResolverDefinition>(termResolverBos.getResults().size());

            for (TermResolverBo termResolverBo : termResolverBos.getResults()) {
                if (termResolverBo != null) {
                    results.add(TermResolverBo.to(termResolverBo));
                }
            }
        } else {
            results = Collections.emptyList();
        }

        return results;
    }

    @Override
    public TermResolverDefinition getTermResolverByNameAndNamespace(String name,
            String namespace) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is null or blank");
        }

        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);
        TermResolverBo bo = dataObjectService.find(TermResolverBo.class, map);

        return TermResolverBo.to(bo);
    }

    @Override
    public TermSpecificationDefinition getTermSpecificationByNameAndNamespace(String name,
            String namespace) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is null or blank");
        }

        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);
        TermSpecificationBo bo = findSingleMatching(dataObjectService, TermSpecificationBo.class, map);

        return TermSpecificationBo.to(bo);
    }

    @Override
    public List<TermSpecificationDefinition> findAllTermSpecificationsByContextId(String contextId) {
        List<TermSpecificationDefinition> results = null;

        if (StringUtils.isBlank(contextId)) {
            throw new RiceIllegalArgumentException("contextId must not be blank or null");
        }

        QueryByCriteria crit = QueryByCriteria.Builder.forAttribute("contextId", contextId).build();

        QueryResults<ContextValidTermBo> contextValidTerms =
                dataObjectService.findMatching(ContextValidTermBo.class, crit);

        if (!CollectionUtils.isEmpty(contextValidTerms.getResults())) {
            results = new ArrayList<TermSpecificationDefinition>(contextValidTerms.getResults().size());

            for (ContextValidTermBo validTerm : contextValidTerms.getResults()) {
                results.add(TermSpecificationBo.to(validTerm.getTermSpecification()));
            }
        } else {
            results = Collections.emptyList();
        }

        return results;
    }
}
