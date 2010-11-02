// this code needs to go in a Messaging "variable"/code section in the root of the doc in LiveCycle

/**
 * List of names of fields which were previously writable that we have toggled read-only.
 * When the doc is made read-only, writable field names are stashed here, and then when it is
 * toggled to writable again, ONLY these fields are marked writable (otherwise we would end
 * up making pre-existing readonly fields writable).
 */
var writableFields = [];
/**
 * The current read-only-ness state of the doc
 */
var doc_readonly = false;

/**
 * Table of function tables, one per version.  The idea here is that we can easily
 * modify and extend the API without breaking existing forms.  With the JavaScript
 * 'eval' functionality we practically can inject anything we want anyway (e.g. dynamically
 * retrofit old forms, take advantage of APIs in newer versions of Acrobat, etc.).
 */
var function_tables = {
    "1.0": {
        // makes all fields on the form read-only
        "ToggleReadOnly": function(doc, args) {
            var desired_readonly = !doc_readonly;
            if (args.length > 0) {
                if ("on".toLowerCase() == args[0].toLowerCase()) {
                    desired_readonly = true;
                } else {
                    desired_readonly = false;
                }
            }
 
            if (desired_readonly == doc_readonly) {
                // nothing to do, we're already in the right state
                return;
            }

            if (desired_readonly) {
                // make it read-only
                var numFields = doc.numFields;
                for (var i = 0; i < numFields; i++) {
                    var fname = doc.getNthFieldName(i);
                    var field = doc.getField(fname);
                    if (!field.readonly) {
                        writableFields.push(fname);
                        field.readonly = true;
                    }
                }
                doc_readonly = true;
            } else {
                // make the previously writable fields writable again
                while (writableFields.length > 0) {
                    var fname = writableFields.pop();
                    var field = doc.getField(fname);
                    field.readonly = false;                 
                }
                doc_readonly = false;
            }
        },

        "GetDataFields": function(doc, args) {
            var xml = doc.xfa.datasets.data.saveXML('pretty');
            return xml;
        },
       
        "SetDataFields": function(doc, args) {
            //app.alert("doc.xfa name: " + doc.xfa.name);
            //app.alert("doc.xfa model: " + doc.xfa.model);
            //app.alert("doc.xfa nodes: " + doc.xfa.nodes);
            //app.alert("doc.xfa parent: " + doc.xfa.parent);
            //var data = XMLData.parse(message[1], false);
            //app.alert(data);
            //app.alert("New values: " + data.saveXML('pretty'));
            //doc.resetForm();
            // reset the form data
            //doc.xfa.host.resetData();
            // remove the first data child
            doc.xfa.datasets.data.nodes.remove(doc.xfa.datasets.data.nodes.item(0));
            // load in the new data
            doc.xfa.datasets.data.loadXML(args[0]); // strips off the root <datasets> element
        },
        
        "GetDataField": function(doc, args) {
            if (args.length < 1) {
                throw new Error("Must specified field name");
            }
            var field = doc.getField(args[0]);
            if (field == null) {
                throw new Error("No such field: " + args[0]);
            }
            return field.rawValue;
        },
        
        "SetDataField": function(doc, args) {
            if (args.length < 1) {
                throw new Error("Must specified field name");
            }
            var fieldName = args.shift();
            var field = doc.getField(fieldName);
            if (field == null) {
                throw new Error("No such field: " + fieldName);
            }

            var s = Incoming.rawValue;
            if (s == null) s = "";
            for (arg in args) {
                s += arg.toString();
            }
            field.rawValue = s;
        },
        
        "Print": function(doc, args) {
            for (arg in args) {
                console.println(arg);
            }
        },
        
        "Alert": function(doc, args) {
            app.alert(args[0]);
        },
        
        "Eval": function(doc, args) {
            return eval(args[0]);
        }
    }
};

/**
 * Prints some console output about the type of document we are in.
 */
function detectFormType() {
    console.println("Document name: " + this.documentFileName);
    // The xfa object is undefined in an Acrobat form.
    if (typeof xfa == "object") {
        if (this.dynamicXFAForm)
            console.println(" This is a dynamic XML form.");
        else
            console.println(" This is a static XML form.");
        if (this.XFAForeground)
            console.println(" This document has imported artwork");
    }
    else console.println(" This is an Acrobat Form.");
}

/**
 * app.trustedFunction and app.trustedPropagatorFunction are not available at JavaScript initialization time, and if they
 * are used directly in the function_tables structure, the definition will fail and result in an undefined function_tables.
 * We need to dynamically wrap all the handlers at runtime inside an event
 */
function secureHandlerFunctions() {
    app.alert("app.trustPropagatorFunction: " + app.trustPropagatorFunction);
    try {
        for (version_key in function_tables) {
            for (function_key in function_tables[version_key]) {
                raw_function = function_tables[version_key][function_key];
                wrapped_function = app.trustPropagatorFunction(raw_function);
                app.alert("Wrapping " + raw_function + " as ");
                //function_tables[version_key][function_key] = wrapped_function;
            }
        }
    } catch (e) {
        app.alert("Error securing handler functions: " + e);
    }
}

/**
 * Initializes the "hostContainer" messaging setup
 */
function init() {
    detectFormType();
    // secureHandlerFunctions();
    if (typeof(xfa.host.appType) != "undefined") {
        if (xfa.host.appType != "Adobe PDF Library") {
            RegisterMessageHandler();
        }
    }
}

/**
 * This registers a message handler that will handle messages coming from the hostContainer (i.e. browser)
 */
function RegisterMessageHandler() {
	if (!event.target.hostContainer) {
		return;
	}
    event.target.hostContainer.messageHandler = {
        doc: event.target,
        onMessage: function(message) {
            try {
                console.println("Recieved message: " + message);
                if (message.layout < 2) {
                    throw new Error("API version and command arguments must be specified.  E.g. [ '1.0', 'Print', 'this is a test' ]");
                }
                var version = message.shift();
    
                //app.alert("Function tables: " + function_tables);
    
                var func_table = function_tables[version];
                if (func_table == null) {
                    throw new Error("No such API version: " + version);
                }
                
                var command = message.shift();
                var func = func_table[command];
                if (func == null) {
                    throw new Error("No such function: " + command);
                }

                //app.alert("Wrapping function: " + func);
                //var wrapped = func; //app.trustedFunction(func);
                //console.println("Invoking function: " + func);
                //app.alert("Invoking function: " + wrapped);
                //app.beginPriv();
                var result = func(this.doc, message);
                if (result != null) {
                    var resultArray = null;
                    if (result instanceof Array) {
                        resultArray = result;
                    } else {
                        resultArray = [ result ];
                    }
                    console.println("Sending response to client: " + resultArray);
                    sendMessageToClient(this.doc, resultArray);
                }
            } catch(e) {
                app.alert("Failure in message handler: " + e);
                throw e;
            } finally {
                //app.endPriv();
            }
        },
        onError: function(error, message) {
            app.alert('PDF messageHandler onError: ' + error + ' ' + message); 
        },
        onDisclose: function(cURL, cDocumentURL) { return true; }
    };
}

/**
 * Convenience function to send a message to a client,
 * mostly for readability and self-documentation
 */
function sendMessageToClient(doc, message) {
    doc.hostContainer.postMessage(message);
}
