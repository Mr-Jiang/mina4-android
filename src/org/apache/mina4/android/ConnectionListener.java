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
/**
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
package org.apache.mina4.android;

/**
 * ClassName :ConnectionListener
 * <p>
 * Description: ConnectionListener连接监听器
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
 * @date 2018-03-22 10:13
 */
public abstract interface ConnectionListener {

	public abstract void receiveData(Object data);

	public abstract void disConnect();

	public abstract void connectOk();

	public abstract void connectError(Exception e);

	public abstract void exceptionCaught(Throwable cause);

}
