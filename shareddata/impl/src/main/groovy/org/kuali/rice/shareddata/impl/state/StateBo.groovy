package org.kuali.rice.shareddata.impl.state

import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.state.State
import org.kuali.rice.shareddata.api.state.StateContract
import org.kuali.rice.shareddata.impl.country.CountryBo
import javax.persistence.*

@IdClass(StateId.class)
@Entity
@Table(name = "KR_STATE_T")
class StateBo extends PersistableBusinessObjectBase implements StateContract, Inactivateable {

    @Id
    @Column(name = "POSTAL_STATE_CD")
    def String code;

    @Id
    @Column(name = "POSTAL_CNTRY_CD")
    def String countryCode;

    @Column(name = "POSTAL_STATE_NM")
    def String name;

    @Type(type = "yes_no")
    @Column(name = "ACTV_IND")
    def boolean active;

    @ManyToOne(targetEntity = CountryBo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "POSTAL_CNTRY_CD", insertable = false, updatable = false)
    def CountryBo country;

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static State to(StateBo bo) {
        if (bo == null) {
            return null
        }

        return State.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static StateBo from(State im) {
        if (im == null) {
            return null
        }

        StateBo bo = new StateBo()
        bo.code = im.code
        bo.countryCode = im.countryCode
        bo.name = im.name
        bo.active = im.active

        return bo
    }
}
