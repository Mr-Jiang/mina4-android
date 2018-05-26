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
package org.apache.mina.util;

import org.apache.mina.core.service.IoService;
import org.apache.mina4.android.log.Mina4Log;

/**
 * A default {@link ExceptionMonitor} implementation that logs uncaught
 * exceptions using {@link Logger}.
 * <p>
 * All {@link IoService}s have this implementation as a default exception
 * monitor.
 *
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
public class DefaultExceptionMonitor extends ExceptionMonitor {

	private static final String TAG = DefaultExceptionMonitor.class.getName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(Throwable cause) {
		if (cause instanceof Error) {
			throw (Error) cause;
		}

		Mina4Log.w(TAG, "Unexpected exception.\n" + cause.toString());
	}
}