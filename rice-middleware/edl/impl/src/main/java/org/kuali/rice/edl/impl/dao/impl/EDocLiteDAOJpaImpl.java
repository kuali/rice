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
package org.kuali.rice.edl.impl.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;
import org.kuali.rice.edl.impl.dao.EDocLiteDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * JPA implementation of the EDOcLiteDAO
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EDocLiteDAOJpaImpl implements EDocLiteDAO {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EDocLiteDAOJpaImpl.class);

    /** static value for active indicator */
    private static final String ACTIVE_IND_CRITERIA = "activeInd";

    /** static value for name */
    private static final String NAME_CRITERIA = "name";

    //** static value for edl name */
    private static final String EDL_NAME = "edlName";

    /** static value for UPPER */
    private static final String UPPER = "UPPER";

    /** static value for definition */
    private static final String DEFINITION = "definition";

    /** static value for style */
    private static final String STYLE = "style";

    /** Service that persists data to and from the underlying datasource. */
    private DataObjectService dataObjectService;

    /**
     * Returns the data object service.
     * @return the {@link DataObjectService}
     */
    public DataObjectService getDataObjectService() {
        return this.dataObjectService;
    }

    /**
     *
     * @see #getDataObjectService()
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDocLiteDefinition saveEDocLiteDefinition(final EDocLiteDefinition edocLiteData) {
        return this.dataObjectService.save(edocLiteData, PersistenceOption.FLUSH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDocLiteAssociation saveEDocLiteAssociation(final EDocLiteAssociation assoc) {
        return this.dataObjectService.save(assoc, PersistenceOption.FLUSH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDocLiteDefinition getEDocLiteDefinition(final String defName) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(NAME_CRITERIA, defName), equal(ACTIVE_IND_CRITERIA, Boolean.TRUE));
        List<EDocLiteDefinition> edls = this.dataObjectService.findMatching(EDocLiteDefinition.class,
                criteria.build()).getResults();
        if (null != edls && !edls.isEmpty()) {
            return edls.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDocLiteAssociation getEDocLiteAssociation(final String docTypeName) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(EDL_NAME, docTypeName), equal(ACTIVE_IND_CRITERIA, Boolean.TRUE));
        List<EDocLiteAssociation> edls = this.dataObjectService.findMatching(EDocLiteAssociation.class,
                criteria.build()).getResults();

        if (null != edls && !edls.isEmpty()) {
            return edls.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getEDocLiteDefinitions() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(ACTIVE_IND_CRITERIA, Boolean.TRUE));
        List<EDocLiteDefinition> defs = this.dataObjectService.findMatching(EDocLiteDefinition.class,
                criteria.build()).getResults();
        ArrayList<String> names = new ArrayList<String>(defs.size());
        if (!defs.isEmpty()) {
            for (EDocLiteDefinition def : defs) {
                names.add(def.getName());
            }
        }

        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EDocLiteAssociation> getEDocLiteAssociations() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(ACTIVE_IND_CRITERIA, Boolean.TRUE));
        return this.dataObjectService.findMatching(EDocLiteAssociation.class, criteria.build()).getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EDocLiteAssociation> search(final EDocLiteAssociation edocLite) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (edocLite.getActiveInd() != null) {
            predicates.add(equal(ACTIVE_IND_CRITERIA, edocLite.getActiveInd()));
        }
        if (edocLite.getDefinition() != null) {
            predicates.add(like(UPPER + "(" + DEFINITION + ")", "%" + edocLite.getDefinition().toUpperCase() + "%"));
        }
        if (edocLite.getEdlName() != null) {
            predicates.add(like(UPPER + "(" + EDL_NAME + ")", "%" + edocLite.getEdlName().toUpperCase() + "%"));
        }
        if (edocLite.getStyle() != null) {
            predicates.add(like(UPPER + "(" + STYLE + ")", "%" + edocLite.getStyle().toUpperCase() + "%"));
        }
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(predicates.toArray(new Predicate[predicates.size()]));
        return this.dataObjectService.findMatching(EDocLiteAssociation.class, builder.build()).getResults();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDocLiteAssociation getEDocLiteAssociation(final Long associationId) {
        return dataObjectService.find(EDocLiteAssociation.class, associationId);
    }
}
