package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.field.MessageField;

import java.io.Serializable;

public class NodePrototype implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageField labelPrototype;
    private Group dataGroupPrototype;

    public NodePrototype() {

    }

    /**
     * @param labelPrototype the labelPrototype to set
     */
    public void setLabelPrototype(MessageField labelPrototype) {
        this.labelPrototype = labelPrototype;
    }

    /**
     * @return the labelPrototype
     */
    public MessageField getLabelPrototype() {
        return this.labelPrototype;
    }

    /**
     * @param dataGroupPrototype the dataGroupPrototype to set
     */
    public void setDataGroupPrototype(Group dataGroupPrototype) {
        this.dataGroupPrototype = dataGroupPrototype;
    }

    /**
     * @return the dataGroupPrototype
     */
    public Group getDataGroupPrototype() {
        return this.dataGroupPrototype;
    }
}