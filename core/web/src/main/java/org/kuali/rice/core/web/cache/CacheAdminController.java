package org.kuali.rice.core.web.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.core.impl.cache.CacheManagerRegistry;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.factory.NamedBean;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Controller
@RequestMapping(value = "/core/admin/cache")
public final class CacheAdminController extends UifControllerBase {

    private static final String GET_NAME = "getName";
    private static final Log LOG = LogFactory.getLog(CacheAdminController.class);
    private static final String GET_NAME_MSG = "unable to get the getName method on the cache manager";

    private CacheManagerRegistry registry;

    public synchronized CacheManagerRegistry getRegistry() {
        if (registry == null) {
            registry = CoreImplServiceLocator.getCacheManagerRegistry();
        }
        return registry;
    }

    @Override
    protected Class<CacheAdminForm> formType() {
        return CacheAdminForm.class;
    }

    @Override
	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

        final Tree<String, String> cacheTree = new Tree<String,String>();

        Node<String,String> root = new Node<String,String>("Root", "Root");

        for (final CacheManager cm : getRegistry().getCacheManagers()) {
            final String name = getName(cm);
            final Node<String, String> cmNode = new Node<String, String>(name, name);

            for (final String c : cm.getCacheNames()) {
                final Node<String, String> cNode = new Node<String, String>(c, c);
                cmNode.addChild(cNode);
            }

            root.addChild(cmNode);
        }

        cacheTree.setRootElement(root);
        ((CacheAdminForm) form).setCacheTree(cacheTree);

        return super.start(form, result, request, response);
    }

    private String getName(CacheManager cm) {
        if (cm instanceof NamedBean) {
            return ((NamedBean) cm).getBeanName();
        }

        String v = "Unnamed CacheManager " + cm.hashCode();
        try {
            final Method nameMethod = cm.getClass().getMethod(GET_NAME, new Class[] {});
            if (nameMethod != null && nameMethod.getReturnType() == String.class) {
                v = (String) nameMethod.invoke(cm, new Object[] {});
            }
        } catch (NoSuchMethodException e) {
            LOG.warn(GET_NAME_MSG, e);
        } catch (InvocationTargetException e) {
            LOG.warn(GET_NAME_MSG, e);
        } catch (IllegalAccessException e) {
            LOG.warn(GET_NAME_MSG, e);
        }

        return v;
    }
}
