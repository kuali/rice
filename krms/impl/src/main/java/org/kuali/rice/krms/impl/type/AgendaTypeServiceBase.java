package org.kuali.rice.krms.impl.type;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextarea;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for {@link org.kuali.rice.krms.framework.type.AgendaTypeService} implementations, providing
 * boilerplate for attribute building and merging from various sources.
 */
public abstract class AgendaTypeServiceBase implements AgendaTypeService {


    private final ModelObjectUtils.Transformer<KrmsTypeAttribute, RemotableAttributeField> attributeTransformer =
            new TypeAttributeToFieldTransformer();

    public static final AgendaTypeService defaultAgendaTypeService = new AgendaTypeServiceBase() {
        @Override
        public List<RemotableAttributeField> getCustomAttributeFields(String krmsTypeId) {
            return Collections.emptyList();
        }
    };

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {

        if (StringUtils.isBlank(krmsTypeId)) {
            throw new RiceIllegalArgumentException("krmsTypeId must be non-null and non-blank");
        }

        List<RemotableAttributeField> results =
                new ArrayList<RemotableAttributeField>(getCustomAttributeFields(krmsTypeId));

        KrmsTypeDefinition krmsType =
                KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().getTypeById(krmsTypeId);

        if (krmsType == null) {
            throw new RiceIllegalArgumentException("krmsTypeId must be a valid type id for a KRMS type");
        } else {
            String serviceName = krmsType.getServiceName();

            // need to merge in type attributes that the service doesn't create RemotableAttributeFields for.
            // iterate through attribute fields and build list of attribute names.
            Set<String> serviceDefinedAttributes = new HashSet<String>();
            for (RemotableAttributeField field : results) {
                serviceDefinedAttributes.add(field.getName());
            }

            List<KrmsTypeAttribute> typeAttributes = krmsType.getAttributes();
            if (!CollectionUtils.isEmpty(typeAttributes)) {
                List<RemotableAttributeField> typeAttributeFields = ModelObjectUtils.transform(typeAttributes,
                        attributeTransformer);

                // add fields for any attributes not yet provided by the AgendaTypeService
                for (RemotableAttributeField field : typeAttributeFields) {
                    if (!serviceDefinedAttributes.contains(field.getName())) {
                        results.add(field);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Plug point for subclasses to add hand-rolled custom attributes. May return an empty list, must not return null;
     * @param krmsTypeId
     * @return
     */
    public abstract List<RemotableAttributeField> getCustomAttributeFields( String krmsTypeId );

    @Override
    public List<RemotableAttributeError> validateAttributes(@WebParam(name = "krmsTypeId") String krmsTypeId,
            @WebParam(name = "attributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> attributes) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "krmsTypeId") String krmsTypeId, @WebParam(name = "newAttributes") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> oldAttributes) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }

    private static class TypeAttributeToFieldTransformer implements  ModelObjectUtils.Transformer<KrmsTypeAttribute, RemotableAttributeField> {

        @Override
        public RemotableAttributeField transform(KrmsTypeAttribute input) {

            KrmsTypeRepositoryService typeRepositoryService = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();

            KrmsAttributeDefinition attributeDefinition =
                    typeRepositoryService.getAttributeDefinitionById(input.getAttributeDefinitionId());

            RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(attributeDefinition.getName());

            RemotableTextarea.Builder controlBuilder = RemotableTextarea.Builder.create();
            controlBuilder.setCols(80);
            controlBuilder.setRows(5);


            controlBuilder.setWatermark(attributeDefinition.getDescription());

            builder.setLongLabel(attributeDefinition.getName());
            builder.setName(attributeDefinition.getName());
            builder.setHelpSummary("helpSummary: " + attributeDefinition.getDescription());
            builder.setHelpDescription("helpDescription: " + attributeDefinition.getDescription());
            builder.setControl(controlBuilder);
            builder.setMaxLength(400);

            return builder.build();
        }
    };

}
