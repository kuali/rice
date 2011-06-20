package org.kuali.rice.kim.impl.identity.visa

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kim.api.identity.visa.EntityVisaContract
import javax.persistence.Id
import javax.persistence.GeneratedValue
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import javax.persistence.Column
import org.kuali.rice.kim.api.identity.visa.EntityVisa
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "KRIM_ENTITY_VISA_T")
class EntityVisaBo extends PersistableBusinessObjectBase implements EntityVisaContract {
    @Id
    @GeneratedValue(generator="KRIM_ENTITY_VISA_ID_S")
    @GenericGenerator(name="KRIM_ENTITY_VISA_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters=[
            @Parameter(name="sequence_name",value="KRIM_ENTITY_VISA_ID_S"),
            @Parameter(name="value_column",value="id")
        ])
    @Column(name = "ID")
    String id;

    @Column(name = "ENTITY_ID")
    String entityId;
	
    @Column(name = "VISA_TYPE_KEY")
    String visaTypeKey;
	
    @Column(name = "VISA_ENTRY")
    String visaEntry;
	
    @Column(name = "VISA_ID")
    String visaId;
    
    /*
       * Converts a mutable EntityVisaBo to an immutable EntityVisa representation.
       * @param bo
       * @return an immutable EntityVisa
       */
      static EntityVisa to(EntityVisaBo bo) {
        if (bo == null) { return null }
        return EntityVisa.Builder.create(bo).build()
      }

      /**
       * Creates a EntityVisaBo business object from an immutable representation of a EntityVisa.
       * @param an immutable EntityVisa
       * @return a EntityVisaBo
       */
      static EntityVisaBo from(EntityVisa immutable) {
        if (immutable == null) {return null}

        EntityVisaBo bo = new EntityVisaBo()
        bo.id = immutable.id
        bo.entityId = immutable.entityId
        bo.visaTypeKey = immutable.visaTypeKey
        bo.visaEntry = immutable.visaEntry
        bo.visaId = immutable.visaId
        bo.versionNumber = immutable.versionNumber
        bo.objectId = immutable.objectId

        return bo;
      }
}
