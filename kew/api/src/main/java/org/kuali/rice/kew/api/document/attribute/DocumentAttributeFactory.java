package org.kuali.rice.kew.api.document.attribute;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * A factory that helps with creation of new {@link DocumentAttribute} instances as well as translation to concrete
 * instances from a {@link DocumentAttributeContract}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentAttributeFactory {

    public static DocumentAttributeString createStringAttribute(String name, String value) {
        DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(name);
        builder.setValue(value);
        return builder.build();
    }

    public static DocumentAttributeDateTime createDateTimeAttribute(String name, DateTime value) {
        DocumentAttributeDateTime.Builder builder = DocumentAttributeDateTime.Builder.create(name);
        builder.setValue(value);
        return builder.build();
    }

    public static DocumentAttributeDateTime createDateTimeAttribute(String name, Date value) {
        return createDateTimeAttribute(name, new DateTime(value));
    }

    public static DocumentAttributeDateTime createDateTimeAttribute(String name, long instant) {
        return createDateTimeAttribute(name, new DateTime(instant));
    }

    public static DocumentAttributeDateTime createDateTimeAttribute(String name, Calendar calendar) {
        return createDateTimeAttribute(name, new DateTime(calendar));
    }

    public static DocumentAttributeDecimal createDecimalAttribute(String name, BigDecimal value) {
        DocumentAttributeDecimal.Builder builder = DocumentAttributeDecimal.Builder.create(name);
        builder.setValue(value);
        return builder.build();
    }

    public static DocumentAttributeDecimal createDecimalAttribute(String name, Number number) {
        return createDecimalAttribute(name, BigDecimal.valueOf(number.doubleValue()));
    }

    public static DocumentAttributeInteger createIntegerAttribute(String name, BigInteger value) {
        DocumentAttributeInteger.Builder builder = DocumentAttributeInteger.Builder.create(name);
        builder.setValue(value);
        return builder.build();
    }

    public static DocumentAttributeInteger createIntegerAttribute(String name, Number number) {
        return createIntegerAttribute(name, BigInteger.valueOf(number.longValue()));
    }

    /**
     * Loads the given {@link DocumentAttributeContract} into the appropriate builder instance based on the type of the
     * given contract implementation.
     *
     * @param contract the contract to load into a builder
     * @return an implementation of {@link DocumentAttribute.AbstractBuilder} which handles instance of the given
     * contract
     *
     * @throws IllegalArgumentException if the given contract is null
     * @throws IllegalArgumentException if a builder implementation could not be determined into which to load the given
     * contract implementation
     */
    public static DocumentAttribute.AbstractBuilder<?> loadContractIntoBuilder(DocumentAttributeContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("contract was null");
        }
        if (contract instanceof DocumentAttributeString) {
            DocumentAttributeString attribute = (DocumentAttributeString)contract;
            DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(attribute.getName());
            builder.setValue(attribute.getValue());
            return builder;
        } else if (contract instanceof DocumentAttributeDateTime) {
            DocumentAttributeDateTime attribute = (DocumentAttributeDateTime)contract;
            DocumentAttributeDateTime.Builder builder = DocumentAttributeDateTime.Builder.create(attribute.getName());
            builder.setValue(attribute.getValue());
            return builder;
        } else if (contract instanceof DocumentAttributeInteger) {
            DocumentAttributeInteger attribute = (DocumentAttributeInteger)contract;
            DocumentAttributeInteger.Builder builder = DocumentAttributeInteger.Builder.create(attribute.getName());
            builder.setValue(attribute.getValue());
            return builder;
        } else if (contract instanceof DocumentAttributeDecimal) {
            DocumentAttributeDecimal attribute = (DocumentAttributeDecimal)contract;
            DocumentAttributeDecimal.Builder builder = DocumentAttributeDecimal.Builder.create(attribute.getName());
            builder.setValue(attribute.getValue());
            return builder;
        }
        throw new IllegalArgumentException("Given document attribute class could not be converted: " + contract.getClass());
    }


}
