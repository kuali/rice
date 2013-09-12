/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.objectweb.jotm;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rmi.PortableRemoteObject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/**
 * TransactionImpl is the implementation of the Transaction interface, defined in JTA specifications. This object is
 * intended to be used by the EJBServer for transaction management. It is used indirectly by the UserTransaction
 * implementation too, i.e. the Current object. The goal is to use the JTA interface to hide the JTM interface to the
 * caller (EJBServer, Bean or Client).
 */

public class TransactionImpl implements Transaction, TimerEventListener {

	// ------------------------------------------------------------------
	// Private data
	// ------------------------------------------------------------------
	private SubCoordinator subcoord = null;
	private TransactionContext myCtx = null;
	private Xid myXid = null;
	private boolean genXidhashcode = false;
	private boolean genXidtostring = false;
	private int myXidhashcode = 0;
	private String myXidtostring = null;
	private Date txDate = null;
	private TimerEvent timer = null; // keep this to unvalidate timer
	private RecoveryCoordinator recoveryCoord = null;
	// / store enlisted resources
	private List<XAResource> enlistedXARes = Collections.synchronizedList(new ArrayList<XAResource>());
	// / store suspended resources
	private List<XAResource> delistedXARes = null;

	/**
	 * propagate context or not. No need to propagate Context when accessing TM for example TODO Add a synchro on this
	 * object.
	 */
	private boolean propagateCtx = true;
	private List<javax.transaction.xa.Xid> enlistedJavaxXid = Collections
			.synchronizedList(new ArrayList<javax.transaction.xa.Xid>());
	private Map<Object, Object> userResourceMap = null;

	/**
	 * Actual Status is kept inside SubCoordinator. This one is used only when SubCoordinator does not exist.
	 */
	private int localstatus = Status.STATUS_ACTIVE;

	private boolean toremove = false;

	// ------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------

	/**
	 * New transaction (begin).
	 * 
	 * @param xid
	 *            transaction Xid
	 * @param timeout
	 *            The value of the timeout in seconds.
	 * @throws SystemException
	 *             could not build Transaction Context
	 */
	public TransactionImpl(Xid xid, int timeout) throws SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("xid= " + xid);
			TraceTm.jta.debug("timeout= " + timeout);
		}

		// Build a propagation context local (no ref to JTM yet)
		myXid = xid;
		myCtx = new InternalTransactionContext(timeout, null, xid);
	}

	/**
	 * New Transaction for this thread (setPropagationContext)
	 * 
	 * @param pctx
	 *            propagation context
	 * 
	 */
	public TransactionImpl(TransactionContext pctx) {

		if (pctx == null) {
			TraceTm.jotm.error("TransactionImpl: null PropagationContext");
			return;
		}
		myCtx = pctx;
		myXid = pctx.getXid();

		// Make interposition in any case, to solve problems of memory leaks
		// and bad transaction count, in case no resource will be implied.
		// -> this make sure that commit will remove this object.
		try {
			makeSubCoord(true, true);
		} catch (RollbackException e) {
			toremove = true;
			TraceTm.jotm.debug("already rolled back");
			localstatus = Status.STATUS_ROLLEDBACK;
		} catch (SystemException e) {
			toremove = true;
			TraceTm.jotm.error("cannot make subcoordinator");
			localstatus = Status.STATUS_ROLLEDBACK;
		}
	}

	// ------------------------------------------------------------------
	// Transaction Synchronization Registry implementation
	// ------------------------------------------------------------------

	/**
	 * Save User Resource
	 * 
	 * @param key
	 *            object
	 * 
	 * @param value
	 *            object
	 * 
	 */
	public synchronized void putUserResource(Object key, Object value) {

		if (userResourceMap == null) {
			userResourceMap = Collections.synchronizedMap(new HashMap<Object, Object>());
		}
		userResourceMap.put(key, value);
	}

	/**
	 * Get User Resource
	 * 
	 * @return Object object
	 * 
	 * @param key
	 *            object
	 * 
	 */
	public synchronized Object getUserResource(Object key) {

		if (userResourceMap == null) {
			return null;
		}

		return userResourceMap.get(key);
	}

	/**
	 * Register InterposedSynchronization
	 * 
	 * @param sync
	 *            synchronization
	 * @throws IllegalStateException
	 *             could not register synchronization
	 */
	public void registerInterposedSynchronization(Synchronization sync) throws IllegalStateException {
		try {
			registerSynchronization(sync);
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	// ------------------------------------------------------------------
	// Transaction implementation
	// ------------------------------------------------------------------

	/**
	 * Complete the transaction represented by this Transaction object The calling thread is not required to have the
	 * same transaction associated with the thread. (JTA 3.3.3)
	 * 
	 * @exception RollbackException
	 *                Thrown to indicate that the transaction has been rolled back rather than committed.
	 * 
	 * @exception HeuristicMixedException
	 *                Thrown to indicate that a heuristic decision was made and that some relevant updates have been
	 *                committed while others have been rolled back.
	 * 
	 * @exception HeuristicRollbackException
	 *                Thrown to indicate that a heuristic decision was made and that some relevant updates have been
	 *                rolled back.
	 * 
	 * @exception SecurityException
	 *                Thrown to indicate that the thread is not allowed to commit the transaction.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the current thread is not associated with a transaction.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 */
	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, SystemException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.commit (tx= " + this + ")");
		}

		// *** Distributed transaction.
		Terminator term = myCtx.getTerminator();

		if (term != null) {
			// Commits the Transaction, with heuristic report
			try {
				propagateCtx = false;
				term.commit(true);
			} catch (TransactionRolledbackException e) {
				Current.getCurrent().forgetTx(getXid());
				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("Commit distributed transaction -> rolled back!");
				}
				localstatus = Status.STATUS_ROLLEDBACK;
				// KULRICE-9919 : Updated to include the rollback cause
				RollbackException ex = new RollbackException();
				ex.initCause(e);
				throw ex;
				// END KULRICE-9919
			} catch (RemoteException e) {

				if (TraceTm.jta.isWarnEnabled()) {
					TraceTm.jta.warn("got a RemoteException", e);
				}

				if (e.detail instanceof TransactionRolledbackException) {
					Current.getCurrent().forgetTx(getXid());
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("Commit distributed transaction -> rolled back!");
					}
					localstatus = Status.STATUS_ROLLEDBACK;
					// KULRICE-9919 : Updated to include the rollback cause
					RollbackException ex = new RollbackException();
					ex.initCause(e.detail);
					throw ex;
					// END KULRICE-9919
				}

				if (e.detail instanceof HeuristicMixed) {
					TraceTm.jotm.info("Commit distributed transaction -> Heuristic mixed!");
					throw new HeuristicMixedException();
				} else {
					// KULRICE-9919 : Updated to include the rollback cause
					SystemException ex = new SystemException("Unexpected RemoteException on commit:"
							+ e.detail.getMessage());
					ex.initCause(e.detail);
					throw ex;
					// END KULRICE-9919
				}
			} catch (Exception e) {
				TraceTm.jotm.error("Unexpected Exception on commit:", e);
				// KULRICE-9919 : Updated to include the rollback cause
				SystemException ex = new SystemException("Unexpected Exception on commit");
				ex.initCause(e);
				throw ex;
				// END KULRICE-9919
			} finally {
				propagateCtx = true;
				if (subcoord == null) {
					// if no coordinator, timer will not be unset by JTM.
					unsetTimer();
				}
			}
			Current.getCurrent().forgetTx(getXid());
			localstatus = Status.STATUS_COMMITTED;
			return;
		}

		// *** Local transaction
		// commit_one_phase may raise remote exceptions. We must rethrow local exceptions.

		if (subcoord != null) {
			try {
				subcoord.commit_one_phase();
			} catch (TransactionRolledbackException e) {
				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("Commit local transaction -> rolled back!");
				}
				Current.getCurrent().forgetTx(getXid());
				localstatus = Status.STATUS_ROLLEDBACK;
				// KULRICE-9919 : Updated to include the rollback cause
				RollbackException ex = new RollbackException();
				ex.initCause(e);
				throw ex;
				// END KULRICE-9919
			} catch (RemoteException e) {
				TraceTm.jotm.error("Unexpected Exception on commit_one_phase:", e);
				Current.getCurrent().forgetTx(getXid());
				localstatus = Status.STATUS_UNKNOWN;
				// KULRICE-9919 : Updated to include the rollback cause
				SystemException ex = new SystemException("Unexpected Exception on commit_one_phase");
				ex.initCause(e);
				throw ex;
				// END KULRICE-9919
			}
		} else {
			// if no coordinator, just unset the timer and release this object.
			unsetTimer();
			Current.getCurrent().forgetTx(getXid());
			localstatus = Status.STATUS_COMMITTED;
		}
	}

	/**
	 * Delist the resource specified from the current transaction associated with the calling thread.
	 * 
	 * @param xares
	 *            The XAResource object representing the resource to delist
	 * 
	 * @param flag
	 *            One of the values of TMSUCCESS, TMSUSPEND, or TMFAIL.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is inactive.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 * @return true if the dissociation of the Resource is successful; false otherwise.
	 */
	@Override
	public boolean delistResource(XAResource xares, int flag) throws IllegalStateException, SystemException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.delistResource");
			TraceTm.jta.debug("xares= " + xares + ", flag= " + flag);
		}

		if (enlistedXARes == null) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.error("No XA resources enlisted by JOTM");
			}
			return false;
		}

		// Verify that the XAResource to be delisted was enlisted earlier.

		if (!enlistedXARes.contains(xares)) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.error("XAResouce " + xares + " not enlisted by JOTM");
			}
			return false;
		}

		javax.transaction.xa.Xid javaxxid = subcoord.getJavaxXid(subcoord.getXaresIndex(xares));

		if (!enlistedJavaxXid.contains(javaxxid)) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.error("XAResouce " + xares + " not enlisted by JOTM");
			}
			return false;
		}

		int javaxxidindex = enlistedJavaxXid.indexOf(javaxxid);
		javax.transaction.xa.Xid myjavaxxid = enlistedJavaxXid.get(javaxxidindex);

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("delisted with resource= " + xares);
			TraceTm.jta.debug("end myjavaxxid= " + myjavaxxid);
		}

		// Send the XA end to the XAResource
		try {
			xares.end(myjavaxxid, flag);
		} catch (XAException e) {
			String error = "Cannot send XA end:" + e + " (error code = " + e.errorCode + ") --" + e.getMessage();
			TraceTm.jotm.error(error);
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jotm.debug("xares.end= " + xares);
			}
			throw new SystemException(error);
		}

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("enlistedXAres.remove xares= " + xares);
		}

		// / remove from enlisted list
		enlistedXARes.remove(xares);
		enlistedJavaxXid.remove(javaxxid);
		return true;
	}

	/**
	 * Enlist the resource specified with the current transaction context of the calling thread
	 * 
	 * @param xares
	 *            The XAResource object representing the resource to enlist
	 * 
	 * @return <i>true</i> if the resource was enlisted successfully; otherwise false.
	 * 
	 * @exception RollbackException
	 *                Thrown to indicate that the transaction has been marked for rollback only.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 */
	@Override
	public boolean enlistResource(XAResource xares) throws RollbackException, IllegalStateException, SystemException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.enlistResource");
			TraceTm.jta.debug("xares= " + xares);
		}

		// Check trivial cases
		if (xares == null) {
			TraceTm.jotm.error("enlistResource: null argument");
			throw new SystemException("enlistResource: null argument");
		}

		if (myCtx == null) {
			throw new SystemException("enlistResource: no Transactional Context");
		}

		// make a subCoordinator object if not existing yet
		if (subcoord == null) {
			makeSubCoord(false, true);
			if (subcoord == null) {
				TraceTm.jotm.error("enlistResource: could not create subcoordinator");
				throw new SystemException("enlistResource: could not create subcoordinator");
			}
		}

		boolean found;

		try {
			found = subcoord.addResource(xares);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("enlistResource: could not addResource " + xares);
		}
		// Transaction may have been set rollback only.
		// Enlist is done, but RollbackException will be thrown anyway.

		// Send the XA start to the XAResource
		// A new Xid branch should be generated in case of new RM (if !found)
		// See JTA Specifications, page 12/13.
		int flag = found ? XAResource.TMJOIN : XAResource.TMNOFLAGS;

		if ((delistedXARes != null) && delistedXARes.contains(xares)) {
			flag = XAResource.TMRESUME;
		}

		Xid resXid = new XidImpl(getXid(), subcoord.getXaresIndex(xares));
		javax.transaction.xa.Xid javaxxid = new JavaXidImpl(resXid);

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("enlisted with resource= " + xares);
			TraceTm.jta.debug("start javaxxid= " + javaxxid);
		}

		if (!found) {
			subcoord.addJavaxXid(javaxxid);
		}

		try {
			xares.start(javaxxid, flag);
		} catch (XAException e) {
			String error = "Cannot send XA(" + xares + ") start:" + e + " (error code = " + e.errorCode + ") --"
					+ e.getMessage();
			TraceTm.jotm.error(error);
			throw new SystemException(error);
		}

		if (!enlistedXARes.contains(xares)) {
			// / add to enlisted list
			enlistedXARes.add(xares);
			enlistedJavaxXid.add(javaxxid);
		}

		int status = this.getStatus();

		switch (status) {
		case Status.STATUS_ACTIVE:
		case Status.STATUS_PREPARING:
			break;
		case Status.STATUS_PREPARED:
			throw new IllegalStateException("Transaction already prepared.");
		case Status.STATUS_COMMITTING:
			// throw new IllegalStateException("Transaction already started committing.");
			break;
		case Status.STATUS_COMMITTED:
			throw new IllegalStateException("Transaction already committed.");
		case Status.STATUS_MARKED_ROLLBACK:
			throw new RollbackException("Transaction already marked for rollback");
		case Status.STATUS_ROLLING_BACK:
			throw new RollbackException("Transaction already started rolling back.");
		case Status.STATUS_ROLLEDBACK:
			throw new RollbackException("Transaction already rolled back.");
		case Status.STATUS_NO_TRANSACTION:
			throw new IllegalStateException("No current transaction.");
		case Status.STATUS_UNKNOWN:
			throw new IllegalStateException("Unknown transaction status");
		default:
			throw new IllegalStateException("Illegal transaction status: " + status);
		}

		return true;
	}

	/**
	 * delist all enlisted resources and move to suspended
	 */
	public void doDetach(int flag) throws SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.doDetach flag= " + XAResourceHelper.getFlagName(flag));
			TraceTm.jta.debug("number of enlisted= " + enlistedXARes.size());
		}

		// always copy enlisted to suspended resource list
		// since jonas may resume the transaction in beforecompletion
		delistedXARes = new ArrayList<XAResource>(enlistedXARes);
		for (XAResource xar : delistedXARes) {
			delistResource(xar, flag);
		}
	}

	/**
	 * enlist/clear all suspended resource
	 */
	public void doAttach(int flag) throws SystemException, RollbackException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.doAttach flag= " + XAResourceHelper.getFlagName(flag));
			TraceTm.jta.debug("number of enlisted= " + enlistedXARes.size());
		}

		boolean rollbackonenlist = false;
		RollbackException mye = null;

		// we attach suspended transactions

		if (flag == XAResource.TMRESUME) {
			// we may be calling resume from beforecompletion on transaction that are not suspended!

			for (int i = 0; (delistedXARes != null) && (i < delistedXARes.size()); i++) {

				try {
					enlistResource(delistedXARes.get(i));
				} catch (RollbackException e) {
					if (!rollbackonenlist) {
						rollbackonenlist = true;
						mye = e;
					}
				}
			}
		}

		delistedXARes = null;

		if (rollbackonenlist) {
			throw new RollbackException(mye.getMessage());
		}
	}

	/**
	 * get a copy of the list of currently enlisted resource
	 */
	public List getEnlistedXAResource() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("getEnlistedXAResource size= " + enlistedXARes.size());
		}
		return new ArrayList<XAResource>(enlistedXARes);
	}

	/**
	 * Obtain the status of the transaction associated with the current thread.
	 * 
	 * @return The transaction status. If no transaction is associated with the current thread, this method returns the
	 *         Status.NoTransaction value.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 */
	@Override
	public int getStatus() throws SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.getStatus()");
		}

		// *** Distributed transaction
		Coordinator coord = myCtx.getCoordinator();

		if (coord != null) {
			// Ask the transaction status to JTM
			int ret;
			try {
				propagateCtx = false;
				ret = coord.get_status();
			} catch (Exception e) {
				TraceTm.jotm.error("cannot reach JTM:", e);
				return Status.STATUS_NO_TRANSACTION;
			} finally {
				propagateCtx = true;
			}
			return ret;
		}

		// *** Local transaction
		// The status is kept in the subcoordinator
		if (subcoord != null) {
			return subcoord.getStatus();
		} else {
			return localstatus;
		}
	}

	/**
	 * Register a synchronization object for the transaction currently associated with the calling thread. The
	 * transction manager invokes the beforeCompletion method prior to starting the transaction commit process. After
	 * the transaction is completed, the transaction manager invokes the afterCompletion method.
	 * 
	 * @param sync
	 *            The javax.transaction.Synchronization object for the transaction associated with the target object
	 * 
	 * @exception RollbackException
	 *                Thrown to indicate that the transaction has been marked for rollback only.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 */
	@Override
	public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException,
			SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("Synchro=" + sync);
		}

		// It's time to make the subcoordinator, if not existing yet.
		if (subcoord == null) {
			makeSubCoord(false, true);
		}

		// Add Synchronization to the list.
		// may raise exceptions
		subcoord.addSynchronization(sync);
	}

	/**
	 * Rollback the transaction represented by this Transaction object.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 */
	@Override
	public void rollback() throws IllegalStateException, SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.rollback(tx= " + this + ")");
		}

		// *** Distributed transaction.
		Terminator term = myCtx.getTerminator();

		if (term != null) {
			// Rollback the Transaction
			try {
				propagateCtx = false;
				term.rollback();
			} catch (java.rmi.ServerException e) {
				// HeuristicCommit ????
				throw new IllegalStateException("Exception on rollback:" + e);
			} catch (Exception e) {
				Current.getCurrent().forgetTx(getXid());
				localstatus = Status.STATUS_UNKNOWN;

				clearUserResourceMap();

				throw new SystemException("Unexpected Exception on rollback");
			} finally {
				propagateCtx = true;
			}

			if (subcoord == null) {
				// if no coordinator, timer will not be unset by JTM.
				unsetTimer();
			}

			// release this object.
			Current.getCurrent().forgetTx(getXid());
			localstatus = Status.STATUS_ROLLEDBACK;

			clearUserResourceMap();

			return;
		}

		// *** Local transaction.
		// if no coordinator, nothing to do.

		if (subcoord != null) {
			try {
				subcoord.rollback();
			} catch (RemoteException e) {
				Current.getCurrent().forgetTx(getXid());
				localstatus = Status.STATUS_UNKNOWN;

				clearUserResourceMap();

				throw new IllegalStateException("Exception on rollback:" + e);
			}

		} else {
			// if no coordinator, just unset the timer.
			unsetTimer();
		}

		// release this object.
		Current.getCurrent().forgetTx(getXid());
		localstatus = Status.STATUS_ROLLEDBACK;

		clearUserResourceMap();
	}

	/**
	 * Prepare the transaction represented by this Transaction object.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 * @return prepare status
	 */
	public int prepare() throws IllegalStateException, SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.prepare(tx= " + this + ")");
		}

		int ret = 0;
		if (subcoord != null) {
			try {
				ret = subcoord.prepare();
			} catch (RemoteException e) {
				TraceTm.jotm.error("Unexpected Exception on prepare:", e);
				throw new SystemException("Unexpected Exception on prepare");
			}
		}
		return ret;
	}

	/**
	 * Modify the transaction associated with the current thread such that the only possible outcome of the transaction
	 * is to roll back the transaction.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the current thread is not associated with any transaction.
	 * 
	 * @exception SystemException
	 *                Thrown if the transaction manager encounters an unexpected error condition
	 * 
	 */
	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("Tx=" + this);
		}

		Coordinator coord = myCtx.getCoordinator();

		if (coord != null) {
			// Distributed transaction
			try {
				propagateCtx = false;
				coord.rollback_only();
			} catch (RemoteException e) {
				TraceTm.jotm.error("Cannot perform coordinator rollback only", e);
			} finally {
				propagateCtx = true;
			}
		}

		// perform this even for distributed transactions:
		// resolves bugs 300077, 300078
		if (subcoord == null) {
			// make a subCoordinator object if not existing yet
			try {
				makeSubCoord(false, false);
			} catch (RollbackException e) {
				TraceTm.jotm.debug("already rolled back");
				return;
			}
		}
		subcoord.setRollbackOnly();
	}

	// ------------------------------------------------------------------
	// TimerEventListener implementation
	// ------------------------------------------------------------------

	/**
	 * timeout for that transaction has expired
	 */

	@Override
	public void timeoutExpired(Object arg) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.timeoutExpired");
		}

		// increment counter for management
		Current.getCurrent().incrementExpiredCounter();

		// make the subcoordinator object, if not existing yet.

		if (subcoord == null) {
			// if this is a proxy, just forget this object. The JTM will
			// rollback transaction with its own timer.
			Terminator term = myCtx.getTerminator();

			if (term != null) {
				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("forget tx (tx=" + this + ")");
				}
				Current.getCurrent().forgetTx(getXid());
				localstatus = Status.STATUS_ROLLEDBACK;
				return;
			}
			try {
				makeSubCoord(false, false);
			} catch (RollbackException e) {
				TraceTm.jotm.debug("already rolled back");
				localstatus = Status.STATUS_ROLLEDBACK;
				return;
			} catch (SystemException e) {
				TraceTm.jotm.error("cannot make subcoordinator");
				localstatus = Status.STATUS_ROLLEDBACK;
				return;
			}
		}

		// Try to set it "rollback only"
		// avoids a rollback while SQL requests are in progress
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("set rollback only (tx=" + this + ")");
		}

		try {
			subcoord.setRollbackOnly();
		} catch (Exception e) {
			TraceTm.jotm.error("cannot rollbackonly:" + e);
		}
	}

	// ------------------------------------------------------------------
	// This object is used as an HashTable index
	// ------------------------------------------------------------------

	/**
	 * return true if objects are identical
	 */
	@Override
	public boolean equals(Object obj2) {
		if (obj2 instanceof TransactionImpl) {
			TransactionImpl tx2 = (TransactionImpl) obj2;

			// trivial case
			if (tx2 == this) {
				return true;
			}

			// compare otids
			return getXid().equals(tx2.getXid());
		} else {
			return false;
		}
	}

	/**
	 * return a hashcode value for this object
	 */
	@Override
	public int hashCode() {
		if (!genXidhashcode) {
			genXidhashcode = true;
			myXidhashcode = getXid().hashCode();
		}

		return myXidhashcode;
	}

	// ------------------------------------------------------------------
	// Other methods
	// ------------------------------------------------------------------

	/**
	 * string form
	 */
	@Override
	public String toString() {
		if (!genXidtostring) {
			genXidtostring = true;
			myXidtostring = getXid().toString();
		}
		return myXidtostring;
	}

	/**
	 * Return associated PropagationContext Used for implicit Context propagation.
	 * 
	 * @param hold
	 *            true if must increment the count to hold the object (not used!)
	 * @return PropagationContext associated with the transaction.
	 */
	public synchronized TransactionContext getPropagationContext(boolean hold) {
		if (propagateCtx) {
			return myCtx;
		} else {
			return null;
		}
	}

	/**
	 * set a timer for the transaction
	 * 
	 * @param timer
	 *            the timer event to set
	 */
	public void setTimer(TimerEvent timer) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("set timer for tx (timer=" + timer + ", tx=" + this + ")");
		}
		this.timer = timer;
	}

	/**
	 * unset the timer
	 */
	public void unsetTimer() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("unset timer for tx (timer=" + timer + ", tx=" + this + ")");
		}
		if (timer != null) {
			timer.unset();
			timer = null;
		}
	}

	/**
	 * set the date time stamp for the transaction
	 * 
	 * @param date
	 *            the Date to set for the transaction
	 */
	public void setTxDate(Date date) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("set date for tx (data=" + date + ", tx=" + this + ")");
		}
		txDate = (Date) date.clone();
	}

	/**
	 * get the date time stamp for the transaction
	 * 
	 * @return the timestamp
	 */
	public Date getTxDate() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("get date for tx (date=" + txDate + ", tx=" + this + ")");
		}
		return (Date) txDate.clone();
	}

	/**
	 * update the propagation context We should be inside the reply of a request involved in a tx here!
	 * 
	 * @param pctx
	 *            propagation context
	 */
	public synchronized void updatePropagationContext(TransactionContext pctx) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("TransactionImpl.updatePropagationContext");
		}

		Coordinator remoteCoord = pctx.getCoordinator();

		if (remoteCoord == null && myCtx.getCoordinator() != null) {
			TraceTm.jotm.error("setPropagationContext: Bad Coordinator");
			TraceTm.jotm.error("remoteCoord = " + remoteCoord);
			TraceTm.jotm.error("myCtx.getCoordinator()= " + myCtx.getCoordinator());
			return;
		}

		// Interpose subCoordinator if newly distributed Tx
		if (remoteCoord != null && myCtx.getCoordinator() == null) {
			myCtx.setCoordinator(pctx.getCoordinator());

			if (subcoord != null) {
				// register the subCoordinator as a Resource.
				TraceTm.jta.debug("register the subCoordinator as a Resource");
				try {
					propagateCtx = false;
					recoveryCoord = remoteCoord.register_resource(subcoord);
				} catch (RemoteException e) {
					TraceTm.jotm.warn("Cannot make interposition :" + e.getCause());
					return;
				} finally {
					propagateCtx = true;
				}
			}
		}

		if (pctx.getTerminator() != null) {
			myCtx.setTerminator(pctx.getTerminator());
		}
	}

	/**
	 * Get the Xid of the transaction
	 * 
	 * @return the Xid
	 */
	public Xid getXid() {
		return myXid;
	}

	/**
	 * make a SubCoordinator for this Transaction object
	 * 
	 * @param interpose
	 *            Make interposition if not already done.
	 * @param active
	 *            transaction still active.
	 */
	private void makeSubCoord(boolean interpose, boolean active) throws RollbackException, SystemException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("make subcoordinator");
		}

		// Build the SubCoordinator object
		try {
			subcoord = new SubCoordinator(this, getXid());
		} catch (RemoteException e) {
			// should never go here.
			TraceTm.jotm.error("new SubCoordinator raised exception: ", e);
			return;
		}
		if (!active) {
			// Just create the subCoordinator to store some state locally.
			// Any attempt to register will fail if transaction is rolled back.
			return;
		}

		// If interposition must be done: do it now!
		// Each time we have a remoteCoord + a subCoord, we must interpose.
		Coordinator remoteCoord = myCtx.getCoordinator();

		// First of all, create the Control object on JTM
		// if it was not created before, if interpose flag is set.
		if (interpose && remoteCoord == null) {
			try {
				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("Creating a remote Control on JTM for a distributed transaction");
				}
				propagateCtx = false;
				// Control coord = Current.getJTM().create(myCtx.getTimeout());
				// Control coord = Current.getJTM().create(myCtx.getTimeout(), getXid());
				Control coord = Current.getJTM().recreate(myCtx);
				remoteCoord = (Coordinator) javax.rmi.PortableRemoteObject.narrow(coord, Coordinator.class);

			} catch (RemoteException e) {
				TraceTm.jotm.error("Cannot create distributed transaction:", e);
				cleanup();
				return;
			} finally {
				propagateCtx = true;
			}

			myCtx.setCoordinator(remoteCoord);

			// fix for transaction context propagation with
			// the Jeremie protocol

			if (myCtx.getTerminator() == null) {
				myCtx.setTerminator((Terminator) remoteCoord);
			}
		}

		// Achieve interposition if not already done:
		// - register the subCoordinator as a Resource.
		if (remoteCoord != null && recoveryCoord == null) {
			try {
				propagateCtx = false;
				recoveryCoord = remoteCoord.register_resource(subcoord);
			} catch (RemoteException e) {
				// If cannot be registered, destroy it.
				// transaction status will be only local.
				cleanup();
				if (e.getCause() instanceof TransactionRolledbackException) {
					TraceTm.jotm.warn("Cannot Make Interposition: rolled back occured");
					throw new RollbackException("Cannot Make Interposition");
				} else {
					TraceTm.jotm.warn("Cannot make Interposition:" + e.getCause());
					throw new SystemException("Cannot Make Interposition");
				}
			} finally {
				propagateCtx = true;
			}
		}
		// increment counter for management
		Current.getCurrent().incrementBeginCounter();
	}

	public boolean toRemove() {
		return toremove;
	}

	public void cleanup() {
		if (subcoord != null) {
			TraceTm.jta.debug("unexport SubCoordinator");
			try {
				PortableRemoteObject.unexportObject(subcoord);
			} catch (NoSuchObjectException e) {
				TraceTm.jta.debug("Cannot unexport subcoord:" + e);
			}
			subcoord = null;
		}
	}

	/**
	 * clear userResourceMap
	 */
	private synchronized void clearUserResourceMap() {

		if ((userResourceMap != null) && !(userResourceMap.isEmpty())) {
			userResourceMap.clear();
			userResourceMap = null;
		}
	}

}
