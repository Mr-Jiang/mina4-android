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
package org.apache.mina4.android;

import org.apache.mina.core.session.IoSession;
/**
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

/**
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
import android.content.Context;

/**
 * ClassName :ConnectionConfiguration
 * <p>
 * Description: ConnectionConfiguration连接配置
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
 * @date 2018-03-22 10:11
 */
public class ConnectionConfiguration {

	// Ip
	private String Ip;
	// 端口
	private int Port;
	// 缓存大小
	private int ReadBufferSize;
	// 以毫秒为单位设置连接超时
	private long ConnectionTimeout = 5000;
	// 上下文
	private Context mContext;
	// 协议编解码器工厂
	private ProtocolCodecFactory factory;
	// 心跳桥梁
	private KeepAliveMessageBridge bridge;

	public String getIp() {
		return Ip;
	}

	public int getPort() {
		return Port;
	}

	public int getReadBufferSize() {
		return ReadBufferSize;
	}

	public long getConnectionTimeout() {
		return ConnectionTimeout;
	}

	public Context getContext() {
		return mContext;
	}

	public ProtocolCodecFactory getFactory() {
		return factory;
	}

	public KeepAliveMessageBridge getBridge() {
		return bridge;
	}

	public static class Builder {
		// Ip
		private String Ip = "";
		// 端口
		private int Port = 8080;
		// 缓存大小
		private int ReadBufferSize = 10240;
		// 连接超时时间
		private long ConnectionTimeout = 5000;
		// 上下文
		private Context mContext;
		// 协议编解码器工厂
		private ProtocolCodecFactory factory;
		// 心跳桥梁
		private KeepAliveMessageBridge bridge;

		public Builder(Context mContext) {
			this.mContext = mContext;
		}

		public Builder setConnectionTimeout(long connectionTimeout) {
			this.ConnectionTimeout = connectionTimeout;
			return this;
		}

		public Builder setIp(String ip) {
			this.Ip = ip;
			return this;
		}

		public Builder setPort(int port) {
			this.Port = port;
			return this;
		}

		public Builder setReadBufferSize(int readBufferSize) {
			this.ReadBufferSize = readBufferSize;
			return this;
		}

		public Builder setFactory(ProtocolCodecFactory factory) {
			this.factory = factory;
			return this;
		}

		public Builder setBridge(KeepAliveMessageBridge bridge) {
			this.bridge = bridge;
			return this;
		}

		public ConnectionConfiguration bulid() {
			ConnectionConfiguration Config = new ConnectionConfiguration();
			Config.ConnectionTimeout = this.ConnectionTimeout;
			Config.Ip = this.Ip;
			Config.Port = this.Port;
			Config.ReadBufferSize = this.ReadBufferSize;
			Config.mContext = this.mContext;
			Config.factory = this.factory;
			Config.bridge = this.bridge;
			return Config;
		}
	}

	/**
	 * 心跳工厂与客户端桥梁
	 */
	public static abstract interface KeepAliveMessageBridge
			extends KeepAliveMessageFactory, KeepAliveRequestTimeoutHandler {

		// default 30 (seconds)
		public abstract int getRequestTimeout();

		// default 60 (seconds)
		public abstract int getRequestInterval();

		@Override
		public boolean isRequest(IoSession session, Object message);

		@Override
		public boolean isResponse(IoSession session, Object message);

		@Override
		public Object getRequest(IoSession session);

		@Override
		public Object getResponse(IoSession session, Object request);

	}

}