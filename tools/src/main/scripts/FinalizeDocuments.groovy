/**
 * A script which can be used to generate SQL which will put documents
 * into FINAL status, deactivate all Action Requests, and delete all
 * Action Items
 *
 * @author Eric Westfall
 */

import groovy.text.Template
import groovy.text.SimpleTemplateEngine

if (args.length != 2) {
	println 'usage: groovy FinalizeDocument.groovy -in INPUT_FILE'
	System.exit(1)
}

count = 0
for (arg in args) {
	if (arg == '-in') inputFile = args[count + 1]
	count++
}

def UPDATE_ITEM = 'delete from en_actn_itm_t where doc_hdr_id=${docId} and exists (select doc_hdr_id from en_doc_hdr_t where doc_hdr_id=${docId} and doc_rte_stat_cd=\'R\');'
def UPDATE_REQUEST = 'update en_actn_rqst_t set ACTN_RQST_STAT_CD=\'D\' where doc_hdr_id=${docId} and exists (select doc_hdr_id from en_doc_hdr_t where doc_hdr_id=${docId} and doc_rte_stat_cd=\'R\');'
def UPDATE_DOC = 'update en_doc_hdr_t set DOC_RTE_STAT_CD=\'F\' where doc_hdr_id=${docId} and doc_rte_stat_cd=\'R\';'

def engine = new SimpleTemplateEngine()

def itemTemplate = engine.createTemplate(UPDATE_ITEM)
def requestTemplate = engine.createTemplate(UPDATE_REQUEST)
def docTemplate = engine.createTemplate(UPDATE_DOC)

reader = new BufferedReader(new FileReader(inputFile))
while ((line = reader.readLine()) != null) {
	def binding = ["docId":line]
	def template = itemTemplate.make(binding)
	println template.toString()
	template = requestTemplate.make(binding)
	println template.toString()
	template = docTemplate.make(binding)
	println template.toString()
	println ''
}