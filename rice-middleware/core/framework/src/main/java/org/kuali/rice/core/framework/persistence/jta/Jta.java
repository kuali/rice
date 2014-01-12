/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.framework.persistence.jta;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * Provides static access to the JTA {@link TransactionManager} and {@link UserTransaction} in use by this Rice-based
 * application if they exist. If the Rice core module is configured properly for JTA (using the CoreConfigurer),
 * these should be set automatically and this class will be "frozen". The default state of this class is that both the
 * TransactionManager and UserTransaction are "null" which indicates that JTA is not enabled for the application.
 *
 * <p>If one of the TransactionManager or UserTransaction is set, then they *both* must be set. Otherwise this class
 * will throw an IllegalArgumentException when
 * {@link #configure(javax.transaction.TransactionManager, javax.transaction.UserTransaction)} is invoked.</p>
 *
 * <p>All methods on this class are thread-safe.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Jta {

    private static final Object initLock = new Object();

    private static boolean frozen = false;

    private static TransactionManager transactionManager;
    private static UserTransaction userTransaction;

    private Jta() {}

    /**
     * Sets the global JTA TransactionManager and UserTransaction. If this class is already frozen when this method is
     * called an attempt is made to configure it with a different TransactionManager or UserTransaction, an
     * IllegalStateException will be thrown. If an attempt is made to configure it multiple same with the same JTA
     * object instances, the request will silently be ignored since the requested configuration is in place. This allows
     * for multiple component to attempt to register JTA as long as they are attempting to register the exact same
     * UserTransaction and TransactionManager.
     *
     * <p>This method requires that both of the arguments be either null or non-null. It is not valid to have a null
     * TransactionManager but non-null UserTransaction since this represents only a partial JTA configuration. If such
     * an attempt is made, this method will throw an IllegalArgumentException.</p>
     *
     * <p>Once this method has been invoked, this class becomes "frozen". It can be unfrozen by calling the
     * {@link @reset} method which will also discard the existing JTA configuration.</p>
     *
     * @param transactionManager the TransactionManager to set, may be null if JTA is not configured or enabled
     * @param userTransaction the UserTransaction to set, may be null if JTA is not configured or enabled
     * @throws IllegalStateException if this class is frozen
     * @throws IllegalArgumentException if an incomplete JTA configuration is provided
     */
    public static void configure(TransactionManager transactionManager, UserTransaction userTransaction) {
        synchronized (initLock) {
            if (Jta.frozen) {
                if (transactionManager != Jta.transactionManager || userTransaction != Jta.userTransaction) {
                    throw new IllegalStateException("JTA configured is frozen and attempt was made to reconfigure it.");
                }
            }
            if (userTransaction == null && transactionManager != null) {
                throw new IllegalArgumentException("TransactionManager has a value but UserTransaction is null, please provide a complete JTA configuration.");
            }
            if (userTransaction != null && transactionManager == null) {
                throw new IllegalArgumentException("UserTransaction has a value but TransactionManager is null, please provide a complete JTA configuration.");
            }
            Jta.transactionManager = transactionManager;
            Jta.userTransaction = userTransaction;
            Jta.frozen = true;
        }
    }

    /**
     * Resets this class by discarding the internal JTA configuration and "unfreezing" the class so that the
     * {@link #configure(javax.transaction.TransactionManager, javax.transaction.UserTransaction)} method can be called
     * again.
     */
    public static void reset() {
        synchronized (initLock) {
            Jta.transactionManager = null;
            Jta.userTransaction = null;
            Jta.frozen = false;
        }
    }

    /**
     * Returns true if this class is frozen for changes.
     *
     * @return true if frozen, false if not
     */
    public static boolean isFrozen() {
        synchronized (initLock) {
            return Jta.frozen;
        }
    }

    /**
     * Indicates whether or not JTA is enabled according to the configuration of this class. JTA will be enabled if the
     * TransactionManager and UserTransaction configured on this class are non-null.
     *
     * @return true if JTA is enabled, false if not
     */
    public static boolean isEnabled() {
        synchronized (initLock) {
            return Jta.transactionManager != null;
        }
    }

    /**
     * Returns the TransactionManager that is set on this class. Since this class is an unreliable source of information
     * prior to being "frozen", this method will throw an IllegalStateException if attempting to retrieve the
     * TransactionManager before the class has been frozen.
     *
     * @return the TransactionManager set on this class, may be null if JTA is not enabled
     * @throws IllegalStateException if this class is not frozen
     */
    public static TransactionManager getTransactionManager() {
        synchronized (initLock) {
            if (!Jta.frozen) {
                throw new IllegalStateException("JTA configuration must be frozen before retrieving TransactionManager from this class.");
            }
        }
        return Jta.transactionManager;
    }

    /**
     * Returns the UserTransaction that is set on this class. Since this class is an unreliable source of information
     * prior to being "frozen", this method will throw an IllegalStateException if attempting to retrieve the
     * UserTransaction before the class has been frozen.
     *
     * @return the UserTransaction set on this class, may be null if JTA is not enabled
     * @throws IllegalStateException if this class is not frozen
     */
    public static UserTransaction getUserTransaction() {
        synchronized (initLock) {
            if (!Jta.frozen) {
                throw new IllegalStateException("JTA configuration must be frozen before retrieving UserTransaction from this class.");
            }
        }
        return Jta.userTransaction;
    }

}
