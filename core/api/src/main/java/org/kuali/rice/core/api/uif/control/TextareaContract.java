package org.kuali.rice.core.api.uif.control;

public interface TextareaContract extends AbstractControlContract, Watermarked, SingleValued {
    Integer getRows();
    Integer getCols();
}
