/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.filter.firewall;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina4.android.log.Mina4Log;

/**
 * A {@link IoFilter} which blocks connections from connecting at a rate faster
 * than the specified interval.
 *
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
public class ConnectionThrottleFilter extends IoFilterAdapter {

	private static final String TAG = ConnectionThrottleFilter.class.getName();

	/** The default delay to wait for a session to be accepted again */
	private static final long DEFAULT_TIME = 1000;

	/**
	 * The minimal delay the sessions will have to wait before being created
	 * again
	 */
	private long allowedInterval;

	/**
	 * The map of created sessiosn, associated with the time they were created
	 */
	private final Map<String, Long> clients;

	/** A lock used to protect the map from concurrent modifications */
	private Lock lock = new ReentrantLock();

	// A thread that is used to remove sessions that have expired since they
	// have
	// been added.
	private class ExpiredSessionThread extends Thread {
		public void run() {

			try {
				// Wait for the delay to be expired
				Thread.sleep(allowedInterval);
			} catch (InterruptedException e) {
				// We have been interrupted, get out of the loop.
				return;
			}

			// now, remove all the sessions that have been created
			// before the delay
			long currentTime = System.currentTimeMillis();

			lock.lock();

			try {
				Iterator<String> sessions = clients.keySet().iterator();

				while (sessions.hasNext()) {
					String session = sessions.next();
					long creationTime = clients.get(session);

					if (creationTime + allowedInterval < currentTime) {
						clients.remove(session);
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * Default constructor. Sets the wait time to 1 second
	 */
	public ConnectionThrottleFilter() {
		this(DEFAULT_TIME);
	}

	/**
	 * Constructor that takes in a specified wait time.
	 *
	 * @param allowedInterval
	 *            The number of milliseconds a client is allowed to wait before
	 *            making another successful connection
	 *
	 */
	public ConnectionThrottleFilter(long allowedInterval) {
		this.allowedInterval = allowedInterval;
		clients = new ConcurrentHashMap<String, Long>();

		// Create the cleanup thread
		ExpiredSessionThread cleanupThread = new ExpiredSessionThread();

		// And make it a daemon so that it's killed when the server exits
		cleanupThread.setDaemon(true);

		// start the cleanuo thread now
		cleanupThread.start();
	}

	/**
	 * Sets the interval between connections from a client. This value is
	 * measured in milliseconds.
	 *
	 * @param allowedInterval
	 *            The number of milliseconds a client is allowed to wait before
	 *            making another successful connection
	 */
	public void setAllowedInterval(long allowedInterval) {
		lock.lock();

		try {
			this.allowedInterval = allowedInterval;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Method responsible for deciding if a connection is OK to continue
	 *
	 * @param session
	 *            The new session that will be verified
	 * @return True if the session meets the criteria, otherwise false
	 */
	protected boolean isConnectionOk(IoSession session) {
		SocketAddress remoteAddress = session.getRemoteAddress();

		if (remoteAddress instanceof InetSocketAddress) {
			InetSocketAddress addr = (InetSocketAddress) remoteAddress;
			long now = System.currentTimeMillis();

			lock.lock();

			try {
				if (clients.containsKey(addr.getAddress().getHostAddress())) {

					Mina4Log.d(TAG, "This is not a new client");
					Long lastConnTime = clients.get(addr.getAddress().getHostAddress());

					clients.put(addr.getAddress().getHostAddress(), now);

					// if the interval between now and the last connection is
					// less than the allowed interval, return false
					if (now - lastConnTime < allowedInterval) {
						Mina4Log.d(TAG, "Session connection interval too short");
						return false;
					}

					return true;
				}

				clients.put(addr.getAddress().getHostAddress(), now);
			} finally {
				lock.unlock();
			}

			return true;
		}

		return false;
	}

	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
		if (!isConnectionOk(session)) {
			Mina4Log.w(TAG, "Connections coming in too fast; closing.");
			session.closeNow();
		}

		nextFilter.sessionCreated(session);
	}
}
