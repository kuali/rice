/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.test;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * This annotates that a test method communicates with Workflow via the database, so it should not use a test transaction.
 * Transaction isolation would foil such communication during the test.  (A typical example is a test trying to route
 * a document.  Workflow will fail to load the document until the transaction is committed.  For a test transaction, that
 * would be after the test is finished running.)  This annotation is effective on the setUp(), tearDown(), and test methods
 * of a {@link KualiTestBase} subclass that has the {@link WithTestSpringContext} annotation,
 * to prevent the default test transaction from being created.  Use on other methods has no effect.
 * On the setUp() or tearDown() methods, it applies to every test method in the class.  This annotation only takes effect on
 * the most closely inherited method, not on overrides.  In other words, if an override is missing this annotation,
 * then it will get a test transaction by default of {@link WithTestSpringContext}.
 * Even with this annotation, transactions within the test (i.e., within Spring service methods) still occur, of course.
 * 
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestsWorkflowViaDatabase {
}
