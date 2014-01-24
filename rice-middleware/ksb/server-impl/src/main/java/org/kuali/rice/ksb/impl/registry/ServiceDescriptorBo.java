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
package org.kuali.rice.ksb.impl.registry;

import org.kuali.rice.core.api.mo.ModelObjectBasic;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceDescriptorContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="KRSB_SVC_DSCRPTR_T")
public class ServiceDescriptorBo implements ServiceDescriptorContract, ModelObjectBasic {

    @Id
    @GeneratedValue(generator = "KRSB_SVC_DSCRPTR_S")
    @PortableSequenceGenerator(name = "KRSB_SVC_DSCRPTR_S")
    @Column(name="SVC_DSCRPTR_ID")
	private String id;
	
	@Lob
	@Column(name="DSCRPTR", length = 4000)
	private String descriptor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Long getVersionNumber() {
        return null;
    }

    public static ServiceDescriptor to(ServiceDescriptorBo bo) {
		if (bo == null) {
			return null;
		}
		return ServiceDescriptor.Builder.create(bo).build();
	}
	
	public static ServiceDescriptorBo from(ServiceDescriptor im) {
		if (im == null) {
			return null;
		}

		ServiceDescriptorBo bo = new ServiceDescriptorBo();
		bo.id = im.getId();
		bo.descriptor = im.getDescriptor();

		return bo;
	}

}
