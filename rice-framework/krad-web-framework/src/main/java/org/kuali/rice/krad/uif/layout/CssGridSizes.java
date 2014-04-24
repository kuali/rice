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
package org.kuali.rice.krad.uif.layout;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.CssConstants;

/**
 * CssGridSizes are used by CssGridLayoutManagers to determine how much "column/cell" width an item will take up in a
 * css grid layout.  It is important to note that sizes set will affect that screen size AND all screen sizes
 * larger than that size unless those screen sizes also have a size explicitly set.  Each "row" is 12 across, so no
 * sizes in this object are allowed to exceed this value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "cssGridSizes-bean", parent = "Uif-CssGridSizes")
public class CssGridSizes extends UifDictionaryBeanBase {
    private static final long serialVersionUID = 4390107040745451681L;

    private String cssClassString;

    private int xsSize;
    private int smSize;
    private int mdSize;
    private int lgSize;

    private int smOffset = -1;
    private int mdOffset = -1;
    private int lgOffset = -1;

    public CssGridSizes() {}

    public CssGridSizes(int xsSize, int smSize, int mdSize, int lgSize) {
        this.xsSize = xsSize;
        this.smSize = smSize;
        this.mdSize = mdSize;
        this.lgSize = lgSize;
    }

    /**
     * Gets the css class string to use on the cell div of this item, by concatnating the appropriate css
     * classes based on the size values set on this object
     *
     * @return the css class string that represents the size of the "cell" div for this item at different screen sizes
     */
    public String getCssClassString() {
        if (cssClassString != null) {
            return cssClassString;
        }

        cssClassString = "";

        concatenateSizeStyle(xsSize, CssConstants.CssGrid.XS_COL_PREFIX, 1);

        concatenateSizeStyle(smSize, CssConstants.CssGrid.SM_COL_PREFIX, 1);
        concatenateSizeStyle(smOffset, CssConstants.CssGrid.SM_OFFSET_PREFIX, 0);

        concatenateSizeStyle(mdSize, CssConstants.CssGrid.MD_COL_PREFIX, 1);
        concatenateSizeStyle(mdOffset, CssConstants.CssGrid.MD_OFFSET_PREFIX, 0);

        concatenateSizeStyle(lgSize, CssConstants.CssGrid.LG_COL_PREFIX, 1);
        concatenateSizeStyle(lgOffset, CssConstants.CssGrid.LG_OFFSET_PREFIX, 0);

        cssClassString = cssClassString.trim();

        return cssClassString;
    }

    /**
     * Concatenates the styleClassPrefix with the size passed in, if the size is of minSize or greater
     *
     * @param size the size to use
     * @param cssClassPrefix the css class prefix to use before size
     * @param minSize the minimum size
     */
    private void concatenateSizeStyle(int size, String cssClassPrefix, int minSize) {
        if (size >= minSize) {
            cssClassString += " " + cssClassPrefix + size;
        }

        if (size > CssGridLayoutManager.NUMBER_OF_COLUMNS) {
            throw new RuntimeException("Sizes in CssGridSizes cannot exceed " +
                    CssGridLayoutManager.NUMBER_OF_COLUMNS);
        }
    }

    /**
     * The size this Component's "cell" div will take up at an extra small screen size (phone).
     *
     * <p>
     *  If 0 this setting will be ignored (default).  This setting CANNOT exceed 12.
     * </p>
     *
     * @return the extra small size
     */
    @BeanTagAttribute(name = "xsSize")
    public int getXsSize() {
        return xsSize;
    }

    /**
     * @see CssGridSizes#getXsSize()
     */
    public void setXsSize(int xsSize) {
        this.xsSize = xsSize;
    }

    /**
     * The size this Component's "cell" div will take up at a small screen size (tablet).
     *
     * <p>
     *  If 0 this setting will be ignored (default).  This setting CANNOT exceed 12.
     * </p>
     *
     * @return the small size
     */
    @BeanTagAttribute(name = "smSize")
    public int getSmSize() {
        return smSize;
    }

    /**
     * @see CssGridSizes#getSmSize()
     */
    public void setSmSize(int smSize) {
        this.smSize = smSize;
    }

    /**
     * The size this Component's "cell" div will take up at a medium screen size (desktop).
     *
     * <p>
     *  If 0 this setting will be ignored (default).  This setting CANNOT exceed 12.
     * </p>
     *
     * @return the medium size
     */
    @BeanTagAttribute(name = "mdSize")
    public int getMdSize() {
        return mdSize;
    }

    /**
     * @see CssGridSizes#getMdSize()
     */
    public void setMdSize(int mdSize) {
        this.mdSize = mdSize;
    }

    /**
     * The size this Component's "cell" div will take up at a large screen size (large desktop).
     *
     * <p>
     *  If 0 this setting will be ignored (default).  This setting CANNOT exceed 12.
     * </p>
     *
     * @return the large size
     */
    @BeanTagAttribute(name = "lgSize")
    public int getLgSize() {
        return lgSize;
    }

    /**
     * @see CssGridSizes#getLgSize()
     */
    public void setLgSize(int lgSize) {
        this.lgSize = lgSize;
    }

    /**
     * The offset (amount "pushed over") the div will have before the Component content at a small screen
     * size (tablet).
     *
     * <p>
     *  If -1 this setting will be ignored (default).  This setting CANNOT exceed 12 AND should not be
     *  set to 12 as it will cause unintended behaviors (maximum should be 11 in most cases).  When using offset
     *  it is recommended that the size of content in the "row" PLUS the offset and size of this "cell" at each screen
     *  size DOES NOT exceed 12 or unintended layouts WILL result.  There is no extra small (xs) offset.
     * </p>
     *
     * @return the small size offset
     */
    @BeanTagAttribute(name = "smOffset")
    public int getSmOffset() {
        return smOffset;
    }

    /**
     * @see CssGridSizes#getSmOffset()
     */
    public void setSmOffset(int smOffset) {
        this.smOffset = smOffset;
    }

    /**
     * The offset (amount "pushed over") the div will have before the Component content at a medium screen
     * size (desktop), this can be set to 0 to override previous screen size offsets.
     *
     * <p>
     *  If -1 this setting will be ignored (default).  This setting CANNOT exceed 12 AND should not be
     *  set to 12 as it will cause unintended behaviors (maximum should be 11 in most cases).  When using offset
     *  it is recommended that the size of content in the "row" PLUS the offset and size of this "cell" at each screen
     *  size DOES NOT exceed 12 or unintended layouts WILL result.  There is no extra small (xs) offset.
     * </p>
     *
     * @return the medium size offset
     */
    @BeanTagAttribute(name = "mdOffset")
    public int getMdOffset() {
        return mdOffset;
    }

    /**
     * @see CssGridSizes#getMdOffset()
     */
    public void setMdOffset(int mdOffset) {
        this.mdOffset = mdOffset;
    }

    /**
     * The offset (amount "pushed over") the div will have before the Component content at a large screen
     * size (large desktop); this can be set to 0 to override previous screen size offsets.
     *
     * <p>
     *  If -1 this setting will be ignored (default).  This setting CANNOT exceed 12 AND should not be
     *  set to 12 as it will cause unintended behaviors (maximum should be 11 in most cases).  When using offset
     *  it is recommended that the size of content in the "row" PLUS the offset and size of this "cell" at each screen
     *  size DOES NOT exceed 12 or unintended layouts WILL result.  There is no extra small (xs) offset.
     * </p>
     *
     * @return the large size offset
     */
    @BeanTagAttribute(name = "lgOffset")
    public int getLgOffset() {
        return lgOffset;
    }

    /**
     * @see CssGridSizes#getLgOffset()
     */
    public void setLgOffset(int lgOffset) {
        this.lgOffset = lgOffset;
    }

    /**
     * Convenience setter for sizes which takes in 4 integers in this order: xsSize, smSize, mdSize, lgSize
     *
     * <p>
     *     The length of this array MUST be 4.  Any values 0 or less will have the same affect as not setting that size.
     * </p>
     *
     * @param sizes the sizes in order of xs, sm, md, lg
     */
    @BeanTagAttribute(name = "sizes", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public void setSizes(int[] sizes) {
        if (sizes == null || sizes.length != 4) {
            throw new RuntimeException("Sizes on CssGridSizes requires 4 and only 4 values.  Values that are not used "
                    + "can be set to 0 or less.");
        }

        xsSize = sizes[0];
        smSize = sizes[1];
        mdSize = sizes[2];
        lgSize = sizes[3];
    }

    /**
     * Convenience setter for offsets which takes in 3 integers in this order: smOffset, mdOffset, lgOffset
     *
     * <p>
     *     The length of this array MUST be 3.  Any values -1 or less will have the same affect as not setting that size
     *     because (unlike sizes) 0 is a valid value for offsets.
     * </p>
     *
     * @param offsets the sizes in order of sm, md, lg
     */
    @BeanTagAttribute(name = "offsets", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public void setOffsets(int[] offsets) {
        if (offsets == null || offsets.length != 3) {
            throw new RuntimeException("Offset on CssGridSizes requires 3 and only 3 values.  Values that are not used "
                    + "can be set to -1 or less.");
        }

        smOffset = offsets[0];
        mdOffset = offsets[1];
        lgOffset = offsets[2];
    }

    /**
     * Helper method to get the total space this div will take up at a small screen size, taking size, offset, and
     * other size settings into account
     *
     * @return the total small size
     */
    public int getTotalSmSize() {
        int totalSmSize = this.smSize;
        if (totalSmSize == 0 && this.xsSize != 0) {
            totalSmSize = this.xsSize;
        }

        if (smOffset > -1) {
            return totalSmSize + smOffset;
        } else {
            return totalSmSize;
        }
    }

    /**
     * Helper method to get the total space this div will take up at a medium screen size, taking size, offset, and
     * other size settings into account
     *
     * @return the total medium size
     */
    public int getTotalMdSize() {
        int totalMdSize = this.mdSize;
        if (totalMdSize == 0) {
            if (this.smSize != 0) {
                totalMdSize = this.smSize;
            } else if (this.xsSize != 0) {
                totalMdSize = this.xsSize;
            }
        }

        if (mdOffset > -1) {
            return totalMdSize + mdOffset;
        } else if (smOffset > -1) {
            return totalMdSize + smOffset;
        } else {
            return totalMdSize;
        }
    }

    /**
     * Helper method to get the total space this div will take up at a large screen size, taking size, offset, and
     * other size settings into account
     *
     * @return the total large size
     */
    public int getTotalLgSize() {
        int totalLgSize = this.lgSize;
        if (totalLgSize == 0) {
            if (this.mdSize != 0) {
                totalLgSize = this.mdSize;
            } else if (this.smSize != 0) {
                totalLgSize = this.smSize;
            } else if (this.xsSize != 0) {
                totalLgSize = this.xsSize;
            }
        }

        if (lgOffset > -1) {
            return totalLgSize + lgOffset;
        } else if (mdOffset > -1) {
            return totalLgSize + mdOffset;
        } else if (smOffset > -1) {
            return totalLgSize + smOffset;
        } else {
            return totalLgSize;
        }
    }
}
