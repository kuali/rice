package org.kuali.rice.kim.impl.type;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAbstractControl;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup;
import org.kuali.rice.core.api.uif.RemotableDatepicker;
import org.kuali.rice.core.api.uif.RemotableHiddenInput;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.uif.RemotableRadioButtonGroup;
import org.kuali.rice.core.api.uif.RemotableSelect;
import org.kuali.rice.core.api.uif.RemotableTextExpand;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.uif.RemotableTextarea;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.control.CheckboxControlDefinition;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.kns.datadictionary.control.MultiselectControlDefinition;
import org.kuali.rice.kns.datadictionary.control.RadioControlDefinition;
import org.kuali.rice.kns.datadictionary.control.SelectControlDefinition;
import org.kuali.rice.kns.datadictionary.control.TextControlDefinition;
import org.kuali.rice.kns.datadictionary.control.TextareaControlDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** a temp class to help with kim migration.  this should be deleted once kim migration is done. */
public class TempKimHelper {

    public static String getKimBasePath(){
	    String kimBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KimConstants.KimUIConstants.KIM_URL_KEY);
	    if (!kimBaseUrl.endsWith(KimConstants.KimUIConstants.URL_SEPARATOR)) {
		    kimBaseUrl = kimBaseUrl + KimConstants.KimUIConstants.URL_SEPARATOR;
	    }
	    return kimBaseUrl;
	}

    public static RemotableAbstractControl.Builder toRemotableAbstractControlBuilder(ControlDefinition control) {
            if (control.isCheckbox()) {
                return RemotableCheckboxGroup.Builder.create(getValues(control));
            } else if (control.isHidden()) {
                return RemotableHiddenInput.Builder.create();
            } else if (control.isMultiselect()) {
                RemotableSelect.Builder b = RemotableSelect.Builder.create(getValues(control));
                b.setMultiple(true);
                b.setSize(control.getSize());
            } else if (control.isRadio()) {
                return RemotableRadioButtonGroup.Builder.create(getValues(control));
            } else if (control.isSelect()) {
                RemotableSelect.Builder b = RemotableSelect.Builder.create(getValues(control));
                b.setMultiple(false);
                b.setSize(control.getSize());
            } else if (control.isText()) {
                final RemotableTextInput.Builder b = RemotableTextInput.Builder.create();
                b.setSize(control.getSize());
                return b;
            } else if (control.isTextarea()) {
                final RemotableTextarea.Builder b = RemotableTextarea.Builder.create();
                b.setCols(control.getCols());
                b.setRows(control.getRows());
                return b;
            }
        return null;
    }
    private static Map<String, String> getValues(ControlDefinition defn) {
        try {
        Class<KeyValuesFinder> clazz = (Class<KeyValuesFinder>) Class.forName(defn.getValuesFinderClass());
        KeyValuesFinder finder = clazz.newInstance();
        return finder.getKeyLabelMap();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<KimAttributeDefinition> toKimAttributeDefinitions(List<KimAttributeField> fields) {
        if (fields == null) {
            throw new IllegalArgumentException("fields was null");
        }

        final List<KimAttributeDefinition> defns = new ArrayList<KimAttributeDefinition>();
        for (KimAttributeField field : fields) {
            defns.add(toKimAttributeDefinition(field));
        }

        return Collections.unmodifiableList(defns);
    }

    public static KimAttributeDefinition toKimAttributeDefinition(KimAttributeField field) {
        if (field == null) {
            throw new IllegalArgumentException("field is null");
        }

        KimDataDictionaryAttributeDefinition ad = new KimDataDictionaryAttributeDefinition();
        ad.setKimAttrDefnId(field.getId());
        ad.setUnique(field.isUnique());

        final RemotableAttributeField attr = field.getAttributeField();
        ad.setName(attr.getName());
        ad.setDataType(attr.getDataType());
        ad.setShortLabel(attr.getShortLabel());
        ad.setLabel(attr.getLongLabel());
        ad.setSummary(attr.getHelpSummary());
        ad.setConstraint(attr.getHelpConstraint());
        ad.setDescription(attr.getHelpDescription());
        ad.setForceUppercase(attr.isForceUpperCase());
        ad.setMinLength(attr.getMinLength());
        ad.setMaxLength(attr.getMaxLength());
        ad.setExclusiveMin(attr.getMinValue() != null ? attr.getMinValue().toString() : null);
        ad.setInclusiveMax(attr.getMaxValue() != null ? attr.getMaxValue().toString() : null);
        if (StringUtils.isNotBlank(attr.getRegexConstraint())) {
            ValidationPattern pattern = new ValidationPattern() {

                @Override
                public Pattern getRegexPattern() {
                    return Pattern.compile(getRegexString());
                }

                @Override
                protected String getRegexString() {
                    return attr.getRegexConstraint();
                }

                @Override
                public ExportMap buildExportMap(String exportKey) {
                    ExportMap exportMap = new ExportMap(exportKey);
                    exportMap.set("type", "regex");
                    exportMap.set("pattern", getRegexString());

                    return exportMap;
                }

                @Override
                public String getValidationErrorMessageKey() {
                    return attr.getRegexContraintMsg();
                }
            };
            ad.setValidationPattern(pattern);
        }
        ad.setRequired(attr.isRequired());

        final RemotableAbstractControl control = field.getAttributeField().getControl();

        if (control != null) {
            ControlDefinition d = toControlDefinition(control);
            for (RemotableAbstractWidget widget : field.getAttributeField().getWidgets()) {
                if(widget instanceof RemotableQuickFinder) {
                    ad.setLookupBoClass(((RemotableQuickFinder) widget).getDataObjectClass());
                    ad.setLookupInputPropertyConversions(((RemotableQuickFinder) widget).getLookupParameters());
                    ad.setLookupReturnPropertyConversions(((RemotableQuickFinder) widget).getFieldConversions());
                } else if (widget instanceof RemotableDatepicker && d != null) {
                    d.setDatePicker(true);
                } else if (widget instanceof RemotableTextExpand && d != null) {
                    d.setExpandedTextArea(true);
                }
            }
            ad.setControl(d);
        }

        return ad;
    }

    private static ControlDefinition toControlDefinition(RemotableAbstractControl control) {
        if (control instanceof RemotableCheckboxGroup) {
            CheckboxControlDefinition checkbox = new CheckboxControlDefinition();
            return checkbox;
            //FIXME: adding keyValues
        } else if (control instanceof RemotableHiddenInput) {
            HiddenControlDefinition hidden = new HiddenControlDefinition();
            return hidden;
        } else if (control instanceof RemotableRadioButtonGroup) {
            RadioControlDefinition radio = new RadioControlDefinition();
            return radio;
            //FIXME: adding keyValues
        } else if (control instanceof RemotableSelect) {
            if (((RemotableSelect) control).isMultiple()) {
                MultiselectControlDefinition multiSelect = new MultiselectControlDefinition();
                multiSelect.setSize(((RemotableSelect) control).getSize());
                return multiSelect;
            } else {
                SelectControlDefinition select = new SelectControlDefinition();
                select.setSize(((RemotableSelect) control).getSize());
                return select;
            }
            //FIXME: adding keyValues
        } else if (control instanceof RemotableTextarea) {
            TextareaControlDefinition textarea = new TextareaControlDefinition();
            textarea.setRows(((RemotableTextarea) control).getRows());
            textarea.setCols(((RemotableTextarea) control).getCols());
            return textarea;
        } else if (control instanceof RemotableTextInput) {
            TextControlDefinition text = new TextControlDefinition();
            text.setSize(((RemotableTextInput) control).getSize());
            return text;
        }
        return null;
    }

        /**
     * Utility method to search a collection of attribute fields and returns
     * a field for a give attribute name.
     *
     * @param attributeName the name of the attribute to search for.  Cannot be blank or null.
     * @param fields cannot be null.
     *
     * @return the attribute field or null if not found.
     */
    public static <T extends KimAttributeField> T findAttributeField(String attributeName, Collection<? extends T> fields) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("attributeName is blank");
        }

        if (fields == null) {
            throw new IllegalArgumentException("fields is null");
        }

        for (T field : fields) {
            if (attributeName.equals(field.getAttributeField().getName())) {
                return field;
            }
        }
        return null;
    }

    public static String createErrorString(KimAttributeField definition) {
        return definition.getAttributeField().getRegexContraintMsg();
    }

     /** will create a string like the following:
     * errorKey:param1;param2;param3;
     *
     * @param errorKey the errorKey
     * @param params the error params
     * @return error string
     */
    public static String createErrorString(String errorKey, String... params) {
        final StringBuilder s = new StringBuilder(errorKey).append(':');
        if (params != null) {
            for (String p : params) {
                if (p != null) {
                    s.append(p);
                    s.append(';');
                }
            }
        }
        return s.toString();
    }

    public static String getAttributeErrorLabel(KimAttributeField definition) {
        String longAttributeLabel = definition.getAttributeField().getLongLabel();
        String shortAttributeLabel = definition.getAttributeField().getShortLabel();
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }
}
