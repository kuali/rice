package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * This class represents an AgendaAttribute business object.
 * Agenda attributes provide a way to attach custom data to an agenda based on the agenda's type.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRMS_AGENDA_ATTR_T")
public class AgendaAttributeBo extends BaseAttributeBo implements Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_AGENDA_ATTR_S")
    @GeneratedValue(generator = "KRMS_AGENDA_ATTR_S")
    @Id
    @Column(name = "AGENDA_ATTR_ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENDA_ID")
    private AgendaBo agenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTR_DEFN_ID", referencedColumnName = "ATTR_DEFN_ID")
    private KrmsAttributeDefinitionBo attributeDefinition;

    @Override
    public KrmsAttributeDefinitionContract getAttributeDefinition() {
        return attributeDefinition;
    }

    public void setAttributeDefinition(KrmsAttributeDefinitionBo attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

    public String getAgendaId() {
        if (agenda != null) {
            return agenda.getId();
        }

        return null;
    }

    public AgendaBo getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaBo agenda) {
        this.agenda = agenda;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
