/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;

/**
 * Ad Hoc Route Person Business Object
 */
@IdClass(AdHocRoutePersonId.class)
@Entity
@Table(name="KRNS_ADHOC_RTE_ACTN_RECIP_T")
public class AdHocRoutePerson extends AdHocRouteRecipient {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private transient Person person;

    public AdHocRoutePerson() {
        setType(PERSON_TYPE);
        try {
            person = (Person)Class.forName("org.kuali.rice.kim.bo.impl.PersonImpl").newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setType(Integer type) {
        if (!PERSON_TYPE.equals(type)) {
            throw new IllegalArgumentException("cannot change type to " + type);
        }
        super.setType(type);
    }

    @Override
    public String getName() {
        if ( person == null || person.getPrincipalName() == null || !person.getPrincipalName().equalsIgnoreCase( getId() ) ) {
            if (getId() != null) {
                person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName( getId() );
            }
            else {
                return "";
            }
        }
        if ( person == null ) {
            return "";
        }
        return person.getName();
    }

    public Person getPerson() {
        if ((person == null) || !StringUtils.equals(person.getPrincipalId(), getId())) {
            person = KimApiServiceLocator.getPersonService().getPerson(getId());
        }

        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

