package edu.iu.uis.eden;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.config.NodeSettings;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.workflow.role.RoleService;
import org.kuali.workflow.workgroup.WorkgroupTypeService;
import org.objectweb.jotm.Current;
import org.springframework.transaction.PlatformTransactionManager;

import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actions.ActionRegistry;
import edu.iu.uis.eden.actiontaken.ActionTakenService;
import edu.iu.uis.eden.applicationconstants.ApplicationConstantsService;
import edu.iu.uis.eden.batch.XmlDigesterService;
import edu.iu.uis.eden.batch.XmlIngesterService;
import edu.iu.uis.eden.batch.XmlPollerService;
import edu.iu.uis.eden.cache.RiceCacheAdministrator;
import edu.iu.uis.eden.docsearch.DocumentSearchService;
import edu.iu.uis.eden.doctype.DocumentSecurityService;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.edl.EDocLiteService;
import edu.iu.uis.eden.edl.StyleService;
import edu.iu.uis.eden.edl.extract.ExtractService;
import edu.iu.uis.eden.engine.WorkflowEngine;
import edu.iu.uis.eden.engine.node.BranchService;
import edu.iu.uis.eden.engine.node.RouteNodeService;
import edu.iu.uis.eden.exception.WorkflowDocumentExceptionRoutingService;
import edu.iu.uis.eden.help.HelpService;
import edu.iu.uis.eden.mail.ActionListEmailService;
import edu.iu.uis.eden.mail.EmailContentService;
import edu.iu.uis.eden.mail.EmailService;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.notes.NoteService;
import edu.iu.uis.eden.notification.NotificationService;
import edu.iu.uis.eden.preferences.PreferencesService;
import edu.iu.uis.eden.removereplace.RemoveReplaceDocumentService;
import edu.iu.uis.eden.responsibility.ResponsibilityIdService;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routeheader.WorkflowDocumentService;
import edu.iu.uis.eden.routemodule.RouteModuleService;
import edu.iu.uis.eden.routemodule.RoutingReportService;
import edu.iu.uis.eden.routetemplate.RuleAttributeService;
import edu.iu.uis.eden.routetemplate.RuleDelegationService;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.security.EncryptionService;
import edu.iu.uis.eden.server.WorkflowDocumentActions;
import edu.iu.uis.eden.server.WorkflowUtility;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.useroptions.UserOptionsService;
import edu.iu.uis.eden.web.WebAuthenticationService;
import edu.iu.uis.eden.web.WebAuthorizationService;
import edu.iu.uis.eden.workgroup.WorkgroupRoutingService;
import edu.iu.uis.eden.workgroup.WorkgroupService;
import edu.iu.uis.eden.xml.export.XmlExporterService;

/**
 * Convenience class that holds service names and provide methods to acquire services.  Defaults to
 * GLR for actual service acquisition.  Used to be responsible for loading and holding spring
 * application context (when it was SpringServiceLocator) but those responsibilities have been
 * moved to the SpringLoader.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public final class KEWServiceLocator {

	private static final Logger LOG = Logger.getLogger(KEWServiceLocator.class);

	public static final String DATASOURCE = "enWorkflowDataSource";

	public static final String WORKFLOW_UTILITY_SERVICE = "enWorkflowUtilityService";

	public static final String WORKFLOW_DOCUMENT_ACTIONS_SERVICE = "enWorkflowDocumentActionsService";

	public static final String QUICK_LINKS_SERVICE = "enQuickLinksService";

	public static final String USER_SERVICE = "enUserService";

	public static final String DOCUMENT_SEARCH_SERVICE = "enDocumentSearchService";

	public static final String ACTION_TAKEN_SRV = "enActionTakenService";

	public static final String ACTION_REQUEST_SRV = "enActionRequestService";

	public static final String ACTION_LIST_SRV = "enActionListService";

	public static final String DOC_ROUTE_HEADER_SRV = "enDocumentRouteHeaderService";

	public static final String DOCUMENT_TYPE_GROUP_SERVICE = "enDocumentTypeGroupService";

	public static final String DOCUMENT_TYPE_SERVICE = "enDocumentTypeService";

	public static final String DOCUMENT_SECURITY_SERVICE = "enDocumentSecurityService";

	public static final String WORKGROUP_SRV = "enWorkgroupService";

	public static final String WORKGROUP_ROUTING_SERVICE = "enWorkgroupRoutingService";

	public static final String WORKGROUP_TYPE_SERVICE = "workgroupTypeService";

	public static final String USER_OPTIONS_SRV = "enUserOptionsService";

	public static final String DOCUMENT_CHANGE_HISTORY_SRV = "enDocumentChangeHistoryService";

	public static final String APPLICATION_CONSTANTS_SRV = "enApplicationConstantsService";

	public static final String DOCUMENT_VALUE_INDEX_SRV = "enDocumentValueIndexService";

	public static final String ROUTE_LEVEL_SERVICE = "enRouteLevelService";

	public static final String CONSTANTS_SERVICE = "enApplicationConstantsService";

	public static final String PREFERENCES_SERVICE = "enPreferencesService";

	public static final String ROUTE_LOG_SERVICE = "enRouteLogService";

	public static final String RULE_TEMPLATE_SERVICE = "enRuleTemplateService";

	public static final String RULE_SERVICE = "enRuleService";

	public static final String RULE_ATTRIBUTE_SERVICE = "enRuleAttributeService";

	public static final String RULE_TEMPLATE_ATTRIBUTE_SERVICE = "enRuleTemplateAttributeService";

	public static final String ROLE_SERVICE = "enRoleService";

	public static final String RESPONSIBILITY_ID_SERVICE = "enResponsibilityIdService";

	public static final String STATS_SERVICE = "enStatsService";

	public static final String ROUTE_MANAGER_QUEUE_SERVICE = "enRouteManagerQueueService";

	public static final String ROUTE_MANAGER_CONTROLLER = "enRouteManagerController";

	public static final String RULE_DELEGATION_SERVICE = "enRuleDelegationService";

	public static final String ROUTE_MANAGER_DRIVER = "enRouteManagerDriver";

	public static final String OPTIMISTIC_LOCK_FAILURE_SERVICE = "enOptimisticLockFailureService";

	public static final String WEB_AUTHENTICATION_SERVICE = "enWebAuthenticationService";

	public static final String WEB_AUTHORIZATION_SERVICE = "enWebAuthorizationService";

	public static final String NOTE_SERVICE = "enNoteService";

	public static final String ROUTING_REPORT_SERVICE = "enRoutingReportService";

	public static final String ROUTE_MODULE_SERVICE = "enRouteModuleService";

	public static final String EXCEPTION_ROUTING_SERVICE = "enExceptionRoutingService";

	public static final String ACTION_REGISTRY = "enActionRegistry";

	public static final String BRANCH_SERVICE = "enBranchService";

	public static final String WORKFLOW_MBEAN = "workflowMBean";

	public static final String NODE_SETTINGS = "nodeSettings";

	public static final String APPLICATION_CONSTANTS_CACHE = "applicationConstantsCache";

	public static final String JTA_TRANSACTION_MANAGER = "jtaTransactionManager";

	public static final String USER_TRANSACTION = "userTransaction";

	public static final String CACHE_ADMINISTRATOR = "enCacheAdministrator";

	public static final String SCHEDULER = "enScheduler";

	/**
	 * Polls for xml files on disk
	 */
	public static final String XML_POLLER_SERVICE = "enXmlPollerService";

	/**
	 * Ingests xml docs containers
	 */
	public static final String XML_INGESTER_SERVICE = "enXmlIngesterService";

	/**
	 * Delegates xml doc parsing
	 */
	public static final String XML_DIGESTER_SERVICE = "enXmlDigesterService";

	/**
	 * Exports xml data
	 */
	public static final String XML_EXPORTER_SERVICE = "enXmlExporterService";

	public static final String DB_TABLES_LOADER = "enDbTablesLoader";

	public static final String DB_PLATFORM = "enDbPlatform";

	public static final String ROUTE_NODE_SERVICE = "enRouteNodeService";

	public static final String WORKFLOW_ENGINE = "workflowEngine";

	public static final String EDOCLITE_SERVICE = "enEDocLiteService";

    public static final String STYLE_SERVICE = "enStyleService";

	public static final String ACTION_LIST_EMAIL_SERVICE = "enActionListEmailService";

	public static final String EMAIL_SERVICE = "enEmailService";

    public static final String EMAIL_CONTENT_SERVICE = "enEmailContentService";

	public static final String NOTIFICATION_SERVICE = "enNotificationService";

	public static final String TRANSACTION_MANAGER = "transactionManager";

	public static final String TRANSACTION_TEMPLATE = "transactionTemplate";

	public static final String JOTM = "jotm";

	public static final String HELP_SERVICE = "enHelpService";

	public static final String WORKFLOW_DOCUMENT_SERVICE = "enWorkflowDocumentService";

	public static final String EXTENSION_SERVICE = "enExtensionService";

	public static final String TRANSFORMATION_SERVICE = "enTransformationService";

	public static final String ENCRYPTION_SERVICE = "enEncryptionService";

	public static final String OPTIONAL_EMBEDDED_USER_SERVICE = "enOptionalEmbeddedClientUserService";

	public static final String OPTIONAL_EMBEDDED_WORKGROUP_SERVICE = "enOptionalEmbeddedClientWorkgroupService";

	public static final String REMOVE_REPLACE_DOCUMENT_SERVICE = "enRemoveReplaceDocumentService";

	public static final String EXTRACT_SERVICE = "enExtractService";

	/**
	 * @param serviceName
	 *            the name of the service bean
	 * @return the service
	 */
	public static Object getService(String serviceName) {
		return getBean(serviceName);
	}

	public static Object getBean(String serviceName) {
		LOG.debug("Fetching service " + serviceName);
		return GlobalResourceLoader.getResourceLoader().getService(new QName(serviceName));
	}

	public static WorkflowUtility getWorkflowUtilityService() {
		return (WorkflowUtility) getBean(WORKFLOW_UTILITY_SERVICE);
	}

	public static WorkflowDocumentActions getWorkflowDocumentActionsService() {
		return (WorkflowDocumentActions) getBean(WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
	}

	public static DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) getBean(DOCUMENT_TYPE_SERVICE);
	}

    public static DocumentSecurityService getDocumentSecurityService() {
    	return (DocumentSecurityService) getBean(DOCUMENT_SECURITY_SERVICE);
    }


	public static ActionRequestService getActionRequestService() {
		return (ActionRequestService) getBean(ACTION_REQUEST_SRV);
	}

	public static UserService getUserService() {
		return (UserService) getBean(USER_SERVICE);
	}

	public static ActionTakenService getActionTakenService() {
		return (ActionTakenService) getBean(ACTION_TAKEN_SRV);
	}

	public static WorkgroupService getWorkgroupService() {
		return (WorkgroupService) getBean(WORKGROUP_SRV);
	}

	public static WorkgroupRoutingService getWorkgroupRoutingService() {
		return (WorkgroupRoutingService) getBean(WORKGROUP_ROUTING_SERVICE);
	}

	public static WorkgroupTypeService getWorkgroupTypeService() {
		return (WorkgroupTypeService) getBean(WORKGROUP_TYPE_SERVICE);
	}

	public static ResponsibilityIdService getResponsibilityIdService() {
		return (ResponsibilityIdService) getBean(RESPONSIBILITY_ID_SERVICE);
	}

	public static RouteHeaderService getRouteHeaderService() {
		return (RouteHeaderService) getBean(DOC_ROUTE_HEADER_SRV);
	}

	public static RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) getBean(RULE_TEMPLATE_SERVICE);
	}

	public static RuleAttributeService getRuleAttributeService() {
		return (RuleAttributeService) getBean(RULE_ATTRIBUTE_SERVICE);
	}

	public static WorkflowDocumentService getWorkflowDocumentService() {
		return (WorkflowDocumentService) getBean(WORKFLOW_DOCUMENT_SERVICE);
	}

	public static RouteModuleService getRouteModuleService() {
		return (RouteModuleService) getBean(ROUTE_MODULE_SERVICE);
	}

	public static ApplicationConstantsService getApplicationConstantsService() {
		return (ApplicationConstantsService) getBean(APPLICATION_CONSTANTS_SRV);
	}

	public static RoleService getRoleService() {
		return (RoleService) getBean(ROLE_SERVICE);
	}

	public static RuleService getRuleService() {
		return (RuleService) getBean(RULE_SERVICE);
	}

	public static RuleDelegationService getRuleDelegationService() {
		return (RuleDelegationService) getBean(RULE_DELEGATION_SERVICE);
	}

	public static Current getJotm() {
		Object jotm = getBean(JOTM);
		if (jotm == null)
			throw new RuntimeException("Could not locate JOTM.");
		return (Current) jotm;
	}

	public static HelpService getHelpService() {
		return (HelpService) getBean(HELP_SERVICE);
	}

	public static RoutingReportService getRoutingReportService() {
		return (RoutingReportService) getBean(ROUTING_REPORT_SERVICE);
	}

	public static PreferencesService getPreferencesService() {
		return (PreferencesService) getBean(PREFERENCES_SERVICE);
	}

	public static XmlPollerService getXmlPollerService() {
		return (XmlPollerService) getBean(XML_POLLER_SERVICE);
	}

	public static XmlIngesterService getXmlIngesterService() {
		return (XmlIngesterService) getBean(XML_INGESTER_SERVICE);
	}

	public static XmlDigesterService getXmlDigesterService() {
		return (XmlDigesterService) getBean(XML_DIGESTER_SERVICE);
	}

	public static XmlExporterService getXmlExporterService() {
		return (XmlExporterService) getBean(XML_EXPORTER_SERVICE);
	}

	public static UserOptionsService getUserOptionsService() {
		return (UserOptionsService) getBean(USER_OPTIONS_SRV);
	}

	public static ActionListService getActionListService() {
		return (ActionListService) getBean(ACTION_LIST_SRV);
	}

	public static RouteNodeService getRouteNodeService() {
		return (RouteNodeService) getBean(ROUTE_NODE_SERVICE);
	}

	public static WorkflowEngine getWorkflowEngine() {
		return (WorkflowEngine) getBean(WORKFLOW_ENGINE);
	}

	public static EDocLiteService getEDocLiteService() {
		return (EDocLiteService) getBean(EDOCLITE_SERVICE);
	}

    public static StyleService getStyleService() {
        return (StyleService) getBean(STYLE_SERVICE);
    }

	public static WebAuthenticationService getWebAuthenticationService() {
		return (WebAuthenticationService) getBean(WEB_AUTHENTICATION_SERVICE);
	}

    public static WebAuthorizationService getWebAuthorizationService() {
        return (WebAuthorizationService) getBean(WEB_AUTHORIZATION_SERVICE);
    }

	public static WorkflowDocumentExceptionRoutingService getExceptionRoutingService() {
		return (WorkflowDocumentExceptionRoutingService) getBean(EXCEPTION_ROUTING_SERVICE);
	}

	public static ActionListEmailService getActionListEmailService() {
		return (ActionListEmailService) getBean(KEWServiceLocator.ACTION_LIST_EMAIL_SERVICE);
	}

	public static EmailService getEmailService() {
		return (EmailService) getBean(KEWServiceLocator.EMAIL_SERVICE);
	}

    public static EmailContentService getEmailContentService() {
        return (EmailContentService) getBean(KEWServiceLocator.EMAIL_CONTENT_SERVICE);
    }

    public static NotificationService getNotificationService() {
		return (NotificationService) getBean(KEWServiceLocator.NOTIFICATION_SERVICE);
	}

	public static TransactionManager getTransactionManager() {
		return (TransactionManager) getBean(JTA_TRANSACTION_MANAGER);
	}

	public static UserTransaction getUserTransaction() {
		return (UserTransaction) getBean(USER_TRANSACTION);
	}

	public static NoteService getNoteService() {
		return (NoteService) getBean(NOTE_SERVICE);
	}

	public static ActionRegistry getActionRegistry() {
		return (ActionRegistry) getBean(ACTION_REGISTRY);
	}

	public static NodeSettings getNodeSettings() {
		return (NodeSettings) getBean(NODE_SETTINGS);
	}

	public static RiceCacheAdministrator getCacheAdministrator() {
		return (RiceCacheAdministrator) getBean(CACHE_ADMINISTRATOR);
	}

	public static EncryptionService getEncryptionService() {
		return (EncryptionService) getBean(ENCRYPTION_SERVICE);
	}

    public static BranchService getBranchService() {
        return (BranchService) getBean(BRANCH_SERVICE);
    }

    public static DocumentSearchService getDocumentSearchService() {
    	return (DocumentSearchService) getBean(DOCUMENT_SEARCH_SERVICE);
    }

    public static RemoveReplaceDocumentService getRemoveReplaceDocumentService() {
	return (RemoveReplaceDocumentService) getBean(REMOVE_REPLACE_DOCUMENT_SERVICE);
    }

    public static ExtractService getExtractService() {
	return (ExtractService) getBean(EXTRACT_SERVICE);
    }

    /**
     * For the following methods, we go directly to the SpringLoader because we do NOT want them to
     * be wrapped in any sort of proxy.
     */

    public static DataSource getDataSource() {
	return (DataSource) SpringLoader.getInstance().getBean(DATASOURCE);
    }

    public static PlatformTransactionManager getPlatformTransactionManager() {
	return (PlatformTransactionManager) SpringLoader.getInstance().getBean(TRANSACTION_MANAGER);
    }
}