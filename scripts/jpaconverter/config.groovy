/**
 * Copyright 2005-2013 The Kuali Foundation
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