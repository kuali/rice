/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.util;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RiceDebugUtils {

	public static StringBuffer getTruncatedStackTrace( boolean excludeCaller ) {
    	// temporary throwable to be able to obtain a stack trace
    	Throwable t = new Throwable();
    	t.fillInStackTrace();
    	StringBuffer sb = new StringBuffer();
    	// loop over the stack trace, only including kuali classes
    	// and excluding certain prefixes that are on almost all traces
    	// within a web application
    	// TODO: add exclusion paths for JUnit base classes
    	boolean firstRecord = true;
    	for ( StackTraceElement ste : t.getStackTrace() ) {
    		if ( ste.getClassName().startsWith( "org.kuali" ) 
    				&& !ste.getClassName().endsWith( "RiceDebugUtils" )
    				&& !ste.getClassName().startsWith( "org.kuali.rice.kns.web.filter" ) 
    				&& !ste.getMethodName().equals( "doFilter" )
    				&& !ste.getMethodName().equals( "doInTransaction" )
    				) {
        		if ( excludeCaller && firstRecord ) {
        			firstRecord = false;
        			continue;
        		}
    			sb.append( "    " );
    			sb.append( ste.getClassName() ).append( '.' ).append( ste.getMethodName() );
    			sb.append( " - line " ).append( ste.getLineNumber() );
    			sb.append( '\n' );
    		}
    	}
		return sb;
	}
}
