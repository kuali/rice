package freemarker.core;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class was reverse engineer from bytecode for freemarker 2.3.20-patch2 which was a customized
 * version implemented by the KRAD team. Unfortunately, the source code for this was in svn.kuali.org
 * which no longer exists. So this class contains the decompiled bytecode for the InlineTemplateUtils
 * class from that version, udpated to work with the most recent version of Freemarker.
 */
public final class InlineTemplateUtils {
    private InlineTemplateUtils() {
    }

    public static void invokeMacro(Environment env, Macro macro, Map args, String body) {
        HashMap wrappedArgs = new HashMap();
        Iterator argEntryIterator = args.entrySet().iterator();

        while(argEntryIterator.hasNext()) {
            Entry e = (Entry)argEntryIterator.next();

            try {
                wrappedArgs.put(e.getKey(), new InlineTemplateUtils.WrappedObjectExpression(e.getValue()));
            } catch (TemplateModelException var10) {
                throw new RuntimeException("Error wrapping argument as a FreeMarker model element", var10);
            }
        }

        try {
            env.invoke(macro, wrappedArgs, (List)null, (List)null, body == null?null: new TemplateElement[] { new TextBlock(body) });
        } catch (TemplateException var8) {
            throw new RuntimeException("Error invoking macro " + macro.getCanonicalForm(), var8);
        } catch (IOException var9) {
            throw new RuntimeException("Error invoking macro " + macro.getCanonicalForm(), var9);
        }
    }

    private static class WrappedObjectExpression extends Expression {
        private final Object wrappedObject;
        private final TemplateModel model;

        private WrappedObjectExpression(Object wrappedObject) throws TemplateModelException {
            this.wrappedObject = wrappedObject;
            if(wrappedObject instanceof TemplateModel) {
                this.model = (TemplateModel)wrappedObject;
            } else {
                this.model = ObjectWrapper.DEFAULT_WRAPPER.wrap(wrappedObject);
            }

        }

        TemplateModel _eval(Environment env) throws TemplateException {
            return this.model;
        }

        boolean isLiteral() {
            return false;
        }

        protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
            try {
                return new InlineTemplateUtils.WrappedObjectExpression(this.wrappedObject);
            } catch (TemplateModelException var5) {
                throw new RuntimeException("Error cloning wrapped object expression", var5);
            }
        }

        public String getCanonicalForm() {
            return "-inline-wrapped-object-expression-" + (this.wrappedObject == null?"null":this.wrappedObject.getClass().getName());
        }

        String getNodeTypeSymbol() {
            return "-inline-wrapped-object-expression";
        }

        int getParameterCount() {
            return 0;
        }

        Object getParameterValue(int idx) {
            return null;
        }

        ParameterRole getParameterRole(int idx) {
            return null;
        }
    }
}
