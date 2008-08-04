/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.routemodule;

/**
 * A {@link RouteModule} that exists purely for backwards compatibility with old documents 
 * (pre 2.1) that have this defined as their Route Module.  Essentially, it is a RouteModule 
 * that does not generate requests since it extends the {@link DefaultRouteModule} and does
 * not augment it's functionality.  Similar in nature to the {@link ExceptionRouteModule} 
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdHocRouteModule extends DefaultRouteModule {}
