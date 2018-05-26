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
package org.apache.mina.filter.codec.bytes;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina4.android.log.Mina4Log;

/**
 * ClassName :ByteArrayEncoder
 * <p>
 * Description: ByteArray编码
 * <p>
 * Copyright: Copyright (c) 2018
 * <p>
 * GitHub and Blog URL:
 * <p>
 * GitHub:<a href="https://github.com/Mr-Jiang">https://github.com/Mr-Jiang</a>
 * <p>
 * Blog:
 * <a href="https://blog.csdn.net/jspping?viewmode=contents">https://blog.csdn.
 * net/jspping?viewmode=contents</a>
 * 
 * @author <a href="https://github.com/Mr-Jiang">mina4-android</a>
 * @date 2018-03-22 10:00
 */
public class ByteArrayEncoder extends ProtocolEncoderAdapter {

	private static final String TAG = ByteArrayEncoder.class.getName();

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
		try {
			out.write(message);
		} catch (Exception e) {
			Mina4Log.e(TAG, e.toString());
		} finally {
			out.flush();
		}
	}
}
