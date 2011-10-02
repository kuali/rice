package org.kuali.rice.kew.impl.document;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.DocumentProcessingOptions;
import org.kuali.rice.kew.api.document.DocumentProcessingQueue;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.engine.OrchestrationConfig;
import org.kuali.rice.kew.engine.WorkflowEngine;
import org.kuali.rice.kew.engine.WorkflowEngineFactory;

import javax.jws.WebParam;

/**
 * Reference implementation of the {@code DocumentProcessingQueue}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentProcessingQueueImpl implements DocumentProcessingQueue {

    private static final Logger LOG = Logger.getLogger(DocumentProcessingQueueImpl.class);

    private WorkflowEngineFactory workflowEngineFactory;
    private DocumentAttributeIndexingQueue documentAttributeIndexingQueue;

    @Override
    public void process(@WebParam(name = "documentId") String documentId) {
        processWithOptions(documentId, null);
    }

    @Override
    public void processWithOptions(@WebParam(name = "documentId") String documentId,
            @WebParam(name = "options") DocumentProcessingOptions options) {
        if (StringUtils.isBlank(documentId)) {
            throw new RiceIllegalArgumentException("documentId was a null or blank value");
        }
        if (options == null) {
            options = getDefaultOptions();
        }
        OrchestrationConfig config = new OrchestrationConfig(OrchestrationConfig.EngineCapability.STANDARD, options.isRunPostProcessor());
        WorkflowEngine engine = getWorkflowEngineFactory().newEngine(config);
        try {
			engine.process(documentId, null);
		} catch (Exception e) {
			LOG.error("Failed to process document through the workflow engine", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
			throw new WorkflowRuntimeException(e);
		}
        if (options.isIndexSearchAttributes()) {
            getDocumentAttributeIndexingQueue().indexDocument(documentId);
        }
    }

    protected DocumentProcessingOptions getDefaultOptions() {
        return new DocumentProcessingOptions(true, true);
    }

    public WorkflowEngineFactory getWorkflowEngineFactory() {
        return workflowEngineFactory;
    }

    public void setWorkflowEngineFactory(WorkflowEngineFactory workflowEngineFactory) {
        this.workflowEngineFactory = workflowEngineFactory;
    }

    public DocumentAttributeIndexingQueue getDocumentAttributeIndexingQueue() {
        return documentAttributeIndexingQueue;
    }

    public void setDocumentAttributeIndexingQueue(DocumentAttributeIndexingQueue documentAttributeIndexingQueue) {
        this.documentAttributeIndexingQueue = documentAttributeIndexingQueue;
    }

}
