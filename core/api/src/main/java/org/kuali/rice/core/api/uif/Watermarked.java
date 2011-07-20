package org.kuali.rice.core.api.uif;

/**
 * A Control with a watermark.
 */
public interface Watermarked {

    /**
     * The watermark value to put on a control.  This field can be null.
     *
     * @return the watermark value or null.
     */
    String getWatermark();
}
