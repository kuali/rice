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
package org.kuali.rice.loadtest

def String SQL = "-- LOAD TEST LOAD_TEST_DESC\n" +
"INSERT INTO `krcr_cmpnt_t` VALUES ('KR-WKFLW','codeLOAD_TEST_DESC','KRCR_CMPNT_OBJ_ID',1,'nameLOAD_TEST_DESC','Y');\n\n" + 

"INSERT INTO `krew_doc_hdr_s` VALUES ('KREW_DOC_HDR_ID');\n" + 
"INSERT INTO `krew_doc_hdr_t` VALUES ('KREW_DOC_HDR_ID','3007','F',0,'2014-03-03 14:09:05','2014-03-03 14:08:42','2014-03-03 14:09:05','2014-03-03 14:09:05','2014-03-03 14:09:05','New ComponentBo - Load test LOAD_TEST_DESC ',NULL,1,'admin',7,'admin',NULL,'KREW_DOC_HDR_OBJ_ID',NULL,NULL);\n\n" +

"INSERT INTO `krew_actn_tkn_s` VALUES ('KREW_ACTN_TKN_ID');\n" +
"INSERT INTO `krew_actn_tkn_t` VALUES ('KREW_ACTN_TKN_ID','KREW_DOC_HDR_ID','admin',NULL,'C','2014-03-03 14:09:05',1,'',1,1,NULL);\n\n" + 

"INSERT INTO `krew_init_rte_node_instn_t` VALUES ('KREW_DOC_HDR_ID','KREW_RTE_NODE_INSTN_ID');\n" + 
"INSERT INTO `krew_rte_node_instn_t` VALUES ('KREW_RTE_NODE_INSTN_ID','KREW_DOC_HDR_ID','2917','KREW_RTE_NODE_INSTN_ID',NULL,0,1,0,2);\n" + 
"INSERT INTO `krns_doc_hdr_t` VALUES ('KREW_DOC_HDR_ID','KRNS_DOC_HDR_OBJ_ID',1,'Load test LOAD_TEST_DESC',NULL,NULL,NULL);\n" + 
"INSERT INTO `krns_maint_doc_t` VALUES ('KREW_DOC_HDR_ID','KRNS_MAINT_DOC_OBJ_ID',4,'QyubAlGJNqixjeNwrY00/9f2We7ofrJSQW0cV45RaxAR43KyuBSRikXF3KMX7owDnI91yLmrylFhOMxGFszjsYmmClr4HjkENqRIzkfwvGY9leBqNxuGfUIS24vlYNYMgo4nSC3Dg1htJ0QBefE15I0sLyRpqurfYLFb767wnifAmmc6tTaYdsUirK6+yFeKy+ylKzSSxeTGnpKNL7jC8UPtedIn8OCnfcYWOM0fpTLzIc792QExYREvQLo93mOdztU1MawAvN3238AvKJ/2xnjVu8dMFeDvYG0kSDPFmxSoXy1qqV+nKkcsazuhEd9SUTSTad6rMl2g7eddbYz0tCoX89jinm6yT+k/6faDwcwgPT1oPh6yZU0XEs5+d6woDP0EIEt5LPgmFuaP/2A6SlH0VIsucZcSYGxRA4QDBB4+zlazYFcW62sWHUy6W32Aw0surap7ja2Jh0Dpw1cb2w2zN8Z+t9VnYTjMRhbM47ER2jQr2luYDRjwZWKEegryopDYG9jft332hnpfBvRK693g0ikwNK62vsSEwjlFC38ySXrLK2Cf0LqQYFnzU+WWBmoYM53yYDcySXrLK2Cf0PGL2ec/sAADkCQPP20HJuo9rJetBii2V3iMVWK8gjsCl4H3luuvooCXFLewR/pEwlV7/UZuG9rxeIxVYryCOwJh11zTTxIh7KRdwa1H7fcfLUKKbedlySwCQD5iRbGg+K4Qae1EmJPBzKtNxlfXlElkEMIwSAyYBMVDCwOkHKLiKPObEFp7fZfOfL7FJ0H5lP9Wp3T6l1+wxy8JtPg3jgO/BYvMN637VhNpsBxlJtxS1gC43MnYpoWSUIY8oOrd7udpSHssOJSWEeNysrgUkYqL7bo5zEz2qFHu8R7XpZOOt7iPl3AxqKBtuQRj+0cfHo2YfsBYEINK9V4DGhMKcLQ=');\n" +
"INSERT INTO `krew_doc_hdr_cntnt_t` VALUES ('KREW_DOC_HDR_ID','<documentContent>\\n<applicationContent>\\n<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer/>\\n</applicationContent>\\n</documentContent>\\n');\n\n\n";

def int loadTestCount = 0;
def int numberToCreate = Integer.parseInt(args[0]);
def int krewDocHdrId = Integer.parseInt(args[1]);
def int krewActnTknId = Integer.parseInt(args[2]);
def int krewRteNodeInsnId = Integer.parseInt(args[3]);
def String sqlGen;
def uid = new java.rmi.server.UID();

while (loadTestCount < numberToCreate) {
	sqlGen = SQL.replace("KREW_DOC_HDR_ID", krewDocHdrId + "");
	sqlGen = sqlGen.replace("KREW_ACTN_TKN_ID", krewActnTknId + "");
	sqlGen = sqlGen.replace("KREW_RTE_NODE_INSTN_ID", krewRteNodeInsnId + "");

	sqlGen = sqlGen.replace("LOAD_TEST_DESC", krewDocHdrId + "");

	sqlGen = sqlGen.replace("KRCR_CMPNT_OBJ_ID", (new java.rmi.server.UID()).toString());
	sqlGen = sqlGen.replace("KREW_DOC_HDR_OBJ_ID", (new java.rmi.server.UID()).toString());
	sqlGen = sqlGen.replace("KRNS_DOC_HDR_OBJ_ID", (new java.rmi.server.UID()).toString());
	sqlGen = sqlGen.replace("KRNS_MAINT_DOC_OBJ_ID", (new java.rmi.server.UID()).toString());

	System.out.println(sqlGen);

	loadTestCount++;
	krewDocHdrId++;
	krewActnTknId++;
	krewRteNodeInsnId++;
}