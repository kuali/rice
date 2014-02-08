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
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_TERM_RSLVR_PARM_SPEC_T")
public class TermResolverParameterSpecificationBo implements Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_TERM_RSLVR_PARM_SPEC_S")
    @GeneratedValue(generator = "KRMS_TERM_RSLVR_PARM_SPEC_S")
    @Id
    @Column(name = "TERM_RSLVR_PARM_SPEC_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @ManyToOne()
    @JoinColumn(name = "TERM_RSLVR_ID", referencedColumnName = "TERM_RSLVR_ID")
    private TermResolverBo termResolver;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static String to(TermResolverParameterSpecificationBo bo) {
        if (bo == null) {
            return null;
        }

        return bo.name;
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param resolver immutable object
     * @param name
     * @return the mutable bo
     */
    public static TermResolverParameterSpecificationBo from(TermResolverDefinition resolver, String name) {
        if (resolver == null) {
            return null;
        }

        if (StringUtils.isBlank(name)) {
            return null;
        }

        TermResolverParameterSpecificationBo bo = new TermResolverParameterSpecificationBo();

        // we won't set termResolver here, it gets set in TermResolver.from

        bo.name = name;

        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTermResolverId() {
        if (termResolver != null) {
            return termResolver.getId();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public TermResolverBo getTermResolver() {
        return termResolver;
    }

    public void setTermResolver(TermResolverBo termResolver) {
        this.termResolver = termResolver;
    }
}
