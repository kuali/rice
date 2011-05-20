/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.modifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.uif.UifPropertyPaths;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.HeaderField;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;

/**
 * Generates <code>Field</code> instances to produce a comparison view among
 * objects of the same type
 * 
 * <p>
 * Modifier is initialized with a List of <code>ComparableInfo</code> instances.
 * For each comparable info, a copy of the configured group field is made and
 * adjusted to the binding object path for the comparable. The comparison fields
 * are ordered based on the configured order property of the comparable. In
 * addition, a <code>HeaderField<code> can be generated to label each group 
 * of comparison fields.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CompareFieldCreateModifier extends ComponentModifierBase {
    private static final Logger LOG = Logger.getLogger(CompareFieldCreateModifier.class);

    private static final long serialVersionUID = -6285531580512330188L;

    private int defaultOrderSequence;
    private boolean generateCompareHeaders;

    private HeaderField headerFieldPrototype;
    private List<ComparableInfo> comparables;

    public CompareFieldCreateModifier() {
        defaultOrderSequence = 1;
        generateCompareHeaders = true;

        comparables = new ArrayList<ComparableInfo>();
    }

    /**
     * Generates the comparison fields
     * 
     * <p>
     * First the configured List of <code>ComparableInfo</code> instances are
     * sorted based on their order property. Then if generateCompareHeaders is
     * set to true, a <code>HeaderField</code> is created for each comparable
     * using the headerFieldPrototype and the headerText given by the
     * comparable. Finally for each field configured on the <code>Group</code>,
     * a corresponding comparison field is generated for each comparable and
     * adjusted to the binding object path given by the comparable in addition
     * to suffixing the id and setting the readOnly property
     * </p>
     * 
     * @see org.kuali.rice.kns.uif.modifier.ComponentModifier#performModification(org.kuali.rice.kns.uif.container.View,
     *      java.lang.Object, org.kuali.rice.kns.uif.core.Component)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performModification(View view, Object model, Component component) {
        if ((component != null) && !(component instanceof Group)) {
            throw new IllegalArgumentException("Compare field initializer only support Group components, found type: "
                    + component.getClass());
        }

        if (component == null) {
            return;
        }

        // list to hold the generated compare items
        List<Component> comparisonItems = new ArrayList<Component>();

        // sort comparables by their order property
        List<ComparableInfo> groupComparables = (List<ComparableInfo>) ComponentUtils.sort(comparables,
                defaultOrderSequence);

        // generate compare header
        if (isGenerateCompareHeaders()) {
            for (ComparableInfo comparable : groupComparables) {
                HeaderField compareHeaderField = ComponentUtils.copy(headerFieldPrototype, comparable.getIdSuffix());
                compareHeaderField.setHeaderText(comparable.getHeaderText());

                comparisonItems.add(compareHeaderField);
            }
        }

        // find the comparable to use for comparing value changes (if
        // configured)
        boolean performValueChangeComparison = false;
        String compareValueObjectBindingPath = null;
        for (ComparableInfo comparable : groupComparables) {
            if (comparable.isCompareToForValueChange()) {
                performValueChangeComparison = true;
                compareValueObjectBindingPath = comparable.getBindingObjectPath();
            }
        }

        // generate the compare items from the configured group
        Group group = (Group) component;
        for (Component item : group.getItems()) {
            int defaultSuffix = 0;
            for (ComparableInfo comparable : groupComparables) {
                Component compareItem = ComponentUtils.copy(item, comparable.getIdSuffix());

                ComponentUtils.setComponentPropertyDeep(compareItem, UifPropertyPaths.BIND_OBJECT_PATH,
                        comparable.getBindingObjectPath());
                if (comparable.isReadOnly()) {
                    compareItem.setReadOnly(true);
                    compareItem.setConditionalReadOnly("");
                }

                // do value comparison
//                if (performValueChangeComparison && comparable.isHighlightValueChange()
//                        && !comparable.isCompareToForValueChange()) {
//                    // get any attribute fields for the item so we can compare the values
//                    List<AttributeField> itemFields = ComponentUtils.getComponentsOfTypeDeep(compareItem, AttributeField.class);
//                    for (AttributeField field : itemFields) {
//                        String fieldBidingPath = field.getBindingInfo().getBindingPath();
//                        Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, fieldBidingPath);
//                    }
//                }

                comparisonItems.add(compareItem);
                defaultSuffix++;
            }
        }

        // update the group's list of components
        group.setItems(comparisonItems);
    }

    /**
     * Generates an id suffix for the comparable item
     * 
     * <p>
     * If the idSuffix to use if configured on the <code>ComparableInfo</code>
     * it will be used, else the given integer index will be used with an
     * underscore
     * </p>
     * 
     * @param comparable
     *            - comparable info to check for id suffix
     * @param index
     *            - sequence integer
     * @return String id suffix
     * @see org.kuali.rice.kns.uif.modifier.ComparableInfo.getIdSuffix()
     */
    protected String getIdSuffix(ComparableInfo comparable, int index) {
        String idSuffix = comparable.getIdSuffix();
        if (StringUtils.isBlank(idSuffix)) {
            idSuffix = "_" + index;
        }

        return idSuffix;
    }

    /**
     * @see org.kuali.rice.kns.uif.modifier.ComponentModifier#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> components = new HashSet<Class<? extends Component>>();
        components.add(Group.class);

        return components;
    }

    /**
     * Indicates the starting integer sequence value to use for
     * <code>ComparableInfo</code> instances that do not have the order property
     * set
     * 
     * @return int default sequence starting value
     */
    public int getDefaultOrderSequence() {
        return this.defaultOrderSequence;
    }

    /**
     * Setter for the default sequence starting value
     * 
     * @param defaultOrderSequence
     */
    public void setDefaultOrderSequence(int defaultOrderSequence) {
        this.defaultOrderSequence = defaultOrderSequence;
    }

    /**
     * Indicates whether a <code>HeaderField</code> should be created for each
     * group of comparison fields
     * 
     * <p>
     * If set to true, for each group of comparison fields a header field will
     * be created using the headerFieldPrototype configured on the modifier with
     * the headerText property of the comparable
     * </p>
     * 
     * @return boolean true if the headers should be created, false if no
     *         headers should be created
     */
    public boolean isGenerateCompareHeaders() {
        return this.generateCompareHeaders;
    }

    /**
     * Setter for the generate comparison headers indicator
     * 
     * @param generateCompareHeaders
     */
    public void setGenerateCompareHeaders(boolean generateCompareHeaders) {
        this.generateCompareHeaders = generateCompareHeaders;
    }

    /**
     * Prototype instance to use for creating the <code>HeaderField</code> for
     * each group of comparison fields (if generateCompareHeaders is true)
     * 
     * @return HeaderField header field prototype
     */
    public HeaderField getHeaderFieldPrototype() {
        return this.headerFieldPrototype;
    }

    /**
     * Setter for the header field prototype
     * 
     * @param headerFieldPrototype
     */
    public void setHeaderFieldPrototype(HeaderField headerFieldPrototype) {
        this.headerFieldPrototype = headerFieldPrototype;
    }

    /**
     * List of <code>ComparableInfo</code> instances the compare fields should
     * be generated for
     * 
     * <p>
     * For each comparable, a copy of the fields configured for the
     * <code>Group</code> will be created for the comparison view
     * </p>
     * 
     * @return List<ComparableInfo> comparables to generate fields for
     */
    public List<ComparableInfo> getComparables() {
        return this.comparables;
    }

    /**
     * Setter for the list of comparable info instances
     * 
     * @param comparables
     */
    public void setComparables(List<ComparableInfo> comparables) {
        this.comparables = comparables;
    }

}
