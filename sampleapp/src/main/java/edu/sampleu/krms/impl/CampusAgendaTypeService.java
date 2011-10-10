package edu.sampleu.krms.impl;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableAttributeLookupSettings;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.shareddata.impl.campus.CampusBo;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sample AgendaTypeService that creates a RemotableAttributeField for specifying the campus
 */
public class CampusAgendaTypeService implements AgendaTypeService {

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {

        List<RemotableAttributeField> remoteFields = new ArrayList<RemotableAttributeField>();

        String campusBoClassName = CampusBo.class.getName();

        String baseLookupUrl = KRADServiceLocatorWeb.getRiceApplicationConfigurationMediationService()
                .getBaseLookupUrl(campusBoClassName);

        RemotableQuickFinder.Builder quickFinderBuilder =
                RemotableQuickFinder.Builder.create(baseLookupUrl, campusBoClassName);

        quickFinderBuilder.setFieldConversions(Collections.singletonMap("code","Campus"));

        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(30);
        controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(Integer.valueOf(40));

        RemotableAttributeLookupSettings.Builder lookupSettingsBuilder = RemotableAttributeLookupSettings.Builder.create();
        lookupSettingsBuilder.setCaseSensitive(Boolean.TRUE);
        lookupSettingsBuilder.setInCriteria(true);
        lookupSettingsBuilder.setInResults(true);
        lookupSettingsBuilder.setRanged(false);

        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create("Campus");
        builder.setAttributeLookupSettings(lookupSettingsBuilder);
        builder.setRequired(true);
        builder.setDataType(DataType.STRING);
        builder.setControl(controlBuilder);
        builder.setLongLabel("Campus");
        builder.setShortLabel("Campus");
        builder.setMinLength(Integer.valueOf(1));
        builder.setMaxLength(Integer.valueOf(40));
        builder.setWidgets(Collections.<RemotableAbstractWidget.Builder>singletonList(quickFinderBuilder));

        remoteFields.add(builder.build());

        return remoteFields;
    }

    @Override
    public List<RemotableAttributeError> validateAttributes(@WebParam(name = "krmsTypeId") String krmsTypeId,
            @WebParam(name = "attributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> attributes) throws RiceIllegalArgumentException {
        // TODO
        return Collections.emptyList();
    }

    @Override
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "krmsTypeId") String krmsTypeId, @WebParam(name = "newAttributes") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> oldAttributes) throws RiceIllegalArgumentException {
        // TODO
        return Collections.emptyList();
    }
}
