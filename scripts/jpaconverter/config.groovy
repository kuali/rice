project {
    homeDirectory = ""
    sourceDirectories = []
}
ojb {
    repositoryFiles = []
}
converterMappings = [
      "OjbCharBooleanConversion" : ""
    , "OjbCharBooleanConversionTF" : "org.kuali.rice.krad.data.jpa.converters.BooleanTFConverter"
    , "OjbKualiDecimalFieldConversion" : ""
    , "OjbKualiEncryptDecryptFieldConversion" : "org.kuali.rice.krad.data.jpa.converters.EncryptionConverter"
    , "OjbKualiHashFieldConversion" : "org.kuali.rice.krad.data.jpa.converters.HashConverter"
    , "OjbKualiIntegerFieldConversion" : ""
    , "OjbKualiPercentFieldConversion" : ""
    , "OjbAccountActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
    , "OjbPendingBCAppointmentFundingActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
    , "OjbCharBooleanFieldInverseConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
    , "OjbBCPositionActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.BooleanAIConverter"
    , "OjbCharBooleanFieldAIConversion" : "org.kuali.rice.krad.data.jpa.converters.BooleanAIConverter"
]