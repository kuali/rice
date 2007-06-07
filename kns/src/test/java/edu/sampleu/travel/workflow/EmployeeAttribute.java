package edu.sampleu.travel.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.GenericRoleAttribute;
import edu.iu.uis.eden.routetemplate.QualifiedRoleName;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * An attribute implementation that can resolve organizational roles
 */
public class EmployeeAttribute extends GenericRoleAttribute {
    private static final Role EMPLOYEE_ROLE = new Role(EmployeeAttribute.class, "employee", "Employee");
    private static final Role SUPERVISOR_ROLE = new Role(EmployeeAttribute.class, "supervisr", "Supervisor");
    private static final Role DIRECTOR_ROLE = new Role(EmployeeAttribute.class, "director", "Dean/Director");
    private static final List<Role> ROLES;
    static {
        List<Role> tmp = new ArrayList<Role>(1);
        tmp.add(EMPLOYEE_ROLE);
        tmp.add(SUPERVISOR_ROLE);
        tmp.add(DIRECTOR_ROLE);
        ROLES = Collections.unmodifiableList(tmp);
    }

	private static String USERID_FORM_FIELDNAME = "userid";

    /**
     * Traveler to be set by client application so that doc content can be generated appropriately
     */
	private String traveler;

	//private AttributeParser _attributeParser = new AttributeParser(ATTRIBUTE_TAGNAME);

	public EmployeeAttribute() {
        super("employee");
	}

	public EmployeeAttribute(String traveler) {
        super("employee");
		this.traveler = traveler;
	}

    /** for edoclite?? */
    public void setTraveler(String traveler) {
        this.traveler = traveler;
    }

	/* RoleAttribute methods */
	public List<Role> getRoleNames() {
        return ROLES;
	}

    protected boolean isValidRole(String roleName) {
        for (Role role: ROLES) {
            if (role.getBaseName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    
	@Override
    protected List<String> getRoleNameQualifiers(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        if (!isValidRole(roleName)) {
            throw new WorkflowRuntimeException("Invalid role: " + roleName);
        }

        List<String> qualifiers = new ArrayList<String>();
        qualifiers.add(roleName);
        // find all traveller inputs in incoming doc
//        List<Map<String, String>> attrs;
//        try {
//            attrs = content.parseContent(documentContent.getAttributeContent());
//        } catch (XPathExpressionException xpee) {
//            throw new WorkflowRuntimeException("Error parsing attribute content: " + XmlHelper.jotNode(documentContent.getAttributeContent()));
//        }
//        for (Map<String, String> props: attrs) {
//            String attrTraveler = props.get("traveler");
//            if (attrTraveler != null) {
//                qualifiers.add(attrTraveler);
//            }
//        }
        return qualifiers;
    }

	@Override
    protected List<Id> resolveRecipients(RouteContext routeContext, QualifiedRoleName qualifiedRoleName) throws EdenUserNotFoundException {
        List<Id> members = new ArrayList<Id>();
        UserId roleUserId = null;
        String roleName = qualifiedRoleName.getBaseRoleName();
        String roleTraveler = qualifiedRoleName.getQualifier();

        /* EMPLOYEE role routes to traveler */
        if (StringUtils.equals(EMPLOYEE_ROLE.getBaseName(), roleName)) {
            roleUserId = new WorkflowUserId(roleTraveler);
        
        /* SUPERVISOR role routes to... supervisor */
        } else if (StringUtils.equals(SUPERVISOR_ROLE.getBaseName(), roleName)) {
            // HACK: need to create an organizational-hierarchy service which
            // has methods like
            // getSupervisor( user ), getDirector( user ), getSupervised( user
            // ), etc.
            // using q.uhuuid() as input
            roleUserId = new EmplId("supervisr");

        /* SUPERVISOR role routes to... director */
        } else if (StringUtils.equals(DIRECTOR_ROLE.getBaseName(), roleName)) {
            // HACK: need to create an organizational-hierarchy service which
            // has methods like
            // getSupervisor( user ), getDirector( user ), getSupervised( user
            // ), etc.
            // using q.uhuuid() as input
            roleUserId = new EmplId("director");
        } else {
            // throw an exception if you get an unrecognized roleName
            throw new WorkflowRuntimeException("unable to process unknown role '" + roleName + "'");
        }
        members.add(roleUserId);

        return members;
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("traveler", traveler);
        return properties;
    }

	/**
	 * Required to support flex routing report
	 * 
	 * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getFieldConversions()
	 */
	public List getFieldConversions() {
		List conversionFields = new ArrayList();
		conversionFields.add(new KeyLabelPair("userid", USERID_FORM_FIELDNAME));
		return conversionFields;
	}

	public List<Row> getRoutingDataRows() {
		List<Row> rows = new ArrayList<Row>();

		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("Traveler username", "", Field.TEXT, false, USERID_FORM_FIELDNAME, "", null, null));
		rows.add(new Row(fields));

		return rows;
	}

	public List validateRoutingData(Map paramMap) {
		List errors = new ArrayList();

		String userid = StringUtils.trim((String) paramMap.get(USERID_FORM_FIELDNAME));
		if (isRequired() && StringUtils.isBlank(userid)) {
			errors.add(new WorkflowServiceErrorImpl("userid is required", "uh.accountattribute.userid.required"));
		}

		WorkflowUser user = null;
		if (!StringUtils.isBlank(userid)) {
			try {
				user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(userid));
			} catch (EdenUserNotFoundException e) {
				errors.add(new WorkflowServiceErrorImpl("unable to retrieve user for userid '" + userid + "'", "uh.accountattribute.userid.invalid"));
			}
		}
		if (errors.size() == 0) {
			traveler = user.getUuId().getUuId();
		}

		return errors;
	}
}