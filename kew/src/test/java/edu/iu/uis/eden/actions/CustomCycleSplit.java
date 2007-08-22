/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.actions;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;
import edu.iu.uis.eden.util.Utilities;

public class CustomCycleSplit implements SplitNode {

    private static int timesToCycle = 0;
    private static String cycleBranchName = null;
    private static String nonCycleBranchName = null;
    private static int timesCycled = 0;
    
    public SplitResult process(RouteContext context, RouteHelper helper) throws Exception {
        List branchNames = new ArrayList();
        if (Utilities.isEmpty(cycleBranchName) || Utilities.isEmpty(nonCycleBranchName)) {
            throw new Exception("Must specify cycle and non-cycle branch names.");
        }
        if (timesCycled++ == timesToCycle) {
            branchNames.add(nonCycleBranchName); 
        } else {
            branchNames.add(cycleBranchName);
        }
        return new SplitResult(branchNames);
    }
    
    public static void configureCycle(String cycleBranchName, String nonCycleBranchName, int timesToCycle) {
        CustomCycleSplit.cycleBranchName = cycleBranchName;
        CustomCycleSplit.nonCycleBranchName = nonCycleBranchName;
        CustomCycleSplit.timesToCycle = timesToCycle;
        CustomCycleSplit.timesCycled = 0;
    }
    
    public static int getTimesCycled() {
        return timesCycled;
    }

}
