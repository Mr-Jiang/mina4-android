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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina4.android.log.Mina4Log;

/**
 * ClassName :ConnectionManager
 * <p>
 * Description: ConnectionManager连接管理
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
 * @date 2018-03-22 10:15
 */
public class ConnectionManager {

	private static final String TAG = ConnectionManager.class.getName();

	private static final int CONNECTION_STATE_CONNECT = 0xf1;
	private static final int CONNECTION_STATE_RECONNECT = 0xf2;
	private IoConnector connector;
	private IoSession session;
	private SocketAddress address;
	private ConnectFuture future;
	private ConnectionConfiguration Config;
	private ConnectionListener mConnectionListener;
	private Thread mThread;

	/**
	 * 静态内部类加载(Java类加载机制)，高效率单例模式，集饿汉单例模式的安全、懒汉单例模式的延时加载、优于双判断的同步单例模式效率、
	 * 优于同步单例方法效率
	 */
	private static final class ConnectionFactory {
		private static final ConnectionManager mConnectionManager = new ConnectionManager();
	}

	/** 获取ConnectionManager实例 ,静态内部类模式 */
	public static ConnectionManager getManagerInstance() {
		return ConnectionFactory.mConnectionManager;
	}

	/** 保证只有一个ConnectionManager实例 */
	private ConnectionManager() {

	}

	public void loadConfig(ConnectionConfiguration Config) {
		this.Config = Config;
		initConnection();
	}

	public void setOnConnectionListener(ConnectionListener c) {
		mConnectionListener = c;
	}

	private void initConnection() {

		if (Config == null)
			throw new IllegalArgumentException("ConnectionConfiguration is cannot be empty");

		connector = new NioSocketConnector();
		// 设置链接超时时间
		connector.setConnectTimeoutMillis(Config.getConnectionTimeout());

		if (Config.getFactory() == null)
			throw new IllegalArgumentException("ProtocolCodecFactory  is cannot be empty");

		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(Config.getFactory()));

		connector.getSessionConfig().setReadBufferSize(Config.getReadBufferSize());
		/******************************************* mina4心跳 **********************************************/
		if (Config.getBridge() != null) {
			// 初始化过滤器
			KeepAliveFilter KeepAlive = new KeepAliveFilter(Config.getBridge());
			// 设置心跳频率
			KeepAlive.setRequestInterval(Config.getBridge().getRequestInterval());
			// 设置心跳超时时间
			KeepAlive.setRequestTimeout(Config.getBridge().getRequestTimeout());
			// 设置心跳超时回调
			KeepAlive.setRequestTimeoutHandler(Config.getBridge());
			// 添加Filter
			connector.getFilterChain().addLast("KeepAlive", KeepAlive);

			// 是否传递空闲事件,true传递；false不传递；默认false
			// KeepAlive.setForwardEvent(true);
		}
		/****************************************** mina4心跳 ***********************************************/

		connector.setHandler(new ConnectionHandler());
		connector.addListener(new IoServiceListener() {

			@Override
			public void sessionDestroyed(IoSession arg0) throws Exception {
				if (mConnectionListener != null) {
					mConnectionListener.disConnect();
				}
			}

			@Override
			public void sessionCreated(IoSession arg0) throws Exception {
				if (mConnectionListener != null) {
					mConnectionListener.connectOk();
				}
			}

			@Override
			public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception {
				Mina4Log.d(TAG, "serviceIdle() >>> ");
			}

			@Override
			public void serviceDeactivated(IoService arg0) throws Exception {
				Mina4Log.d(TAG, "serviceDeactivated() >>> ");
			}

			@Override
			public void serviceActivated(IoService arg0) throws Exception {
				Mina4Log.d(TAG, "serviceActivated() >>> ");
			}

			@Override
			public void sessionClosed(IoSession arg0) throws Exception {
				Mina4Log.d(TAG, "sessionClosed() >>> ");
			}
		});

		if (null == Config.getIp() || "".equals(Config.getIp()))
			throw new IllegalArgumentException("Ip  is cannot be empty");

		if (Config.getPort() < 0)
			throw new IllegalArgumentException("Port is illegal");

		// 创建链接对象
		address = new InetSocketAddress(Config.getIp(), Config.getPort());
	}

	/**
	 * 设置TCP超时时间
	 * 
	 * @param RequestTimeout
	 *            TCP超时时间
	 */
	public void setRequestTimeout(int RequestTimeout) {
		if (connector != null)
			// 以毫秒为单位设置连接超时,默认值是1分钟
			connector.setConnectTimeoutMillis(RequestTimeout);
	}

	public void connect() {
		if (mThread != null)
			// throw new
			// IllegalStateException(mConnectionThread.getClass().getName() + "
			// can only be started once.");
			return;
		mThread = new Thread(new Connection(CONNECTION_STATE_CONNECT), "mina4-android");
		mThread.start();
	}

	private void sconnect() {
		try {
			// 连接
			future = connector.connect(address);
			// 等待连接创建完成
			future.awaitUninterruptibly();
			session = future.getSession();
		} catch (Exception e) {
			if (mConnectionListener != null) {
				mConnectionListener.connectError(e);
			}
		}
	}

	private Lock lock = new ReentrantLock();

	public void write(byte[] data) {
		if (session == null)
			return;
		lock.lock();
		try {
			session.write(IoBuffer.wrap(data));
		} catch (Exception e) {
			Mina4Log.e(TAG, e.toString());
		} finally {
			lock.unlock();
		}
	}

	public boolean isConnect() {
		if (session == null) {
			return false;
		}
		return session.isConnected();
	}

	@SuppressWarnings("deprecation")
	public void close() {
		if (session == null) {
			return;
		}
		session.close();
		session = null;
	}

	void reconnect() {
		if (mThread != null)
			// throw new
			// IllegalStateException(mConnectionThread.getClass().getName() + "
			// can only be started once.");
			return;
		mThread = new Thread(new Connection(CONNECTION_STATE_RECONNECT));
		mThread.start();
	}

	private void sreconnect() {
		try {
			while (true) {
				// 连接
				future = connector.connect(address);
				// 等待连接创建完成
				future.awaitUninterruptibly();
				session = future.getSession();
				if (session != null)
					break;
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			if (mConnectionListener != null) {
				mConnectionListener.connectError(e);
			}
		}
	}

	private void closeConnectionThread() {
		if (mThread != null)
			mThread.interrupt();
		mThread = null;
	}

	public class ConnectionHandler extends IoHandlerAdapter {

		// 接收消息
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			if (mConnectionListener != null) {
				mConnectionListener.receiveData(message);
			}
		}

		// 发送消息
		@Override
		public void messageSent(IoSession session, Object message) throws Exception {

		}

		// 断开连接
		@Override
		public void sessionClosed(IoSession session) throws Exception {
			Mina4Log.d(TAG, "断开连接 >>> ");
		}

		// 创建连接
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			Mina4Log.d(TAG, "创建连接 >>> ");
		}

		// 连接打开
		@Override
		public void sessionOpened(IoSession session) throws Exception {
			Mina4Log.d(TAG, "连接打开 >>> ");
		}

		// 进入空闲状态
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			Mina4Log.d(TAG, "进入空闲状态 >>> ");
		}

		// 发生异常
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			if (mConnectionListener != null)
				mConnectionListener.exceptionCaught(cause);
		}
	}

	public class Connection implements Runnable {

		private int state = CONNECTION_STATE_CONNECT;

		public Connection(int flags) {
			state = flags;
		}

		@Override
		public void run() {
			if (state == CONNECTION_STATE_CONNECT)
				sconnect();
			else if (state == CONNECTION_STATE_RECONNECT)
				sreconnect();

			closeConnectionThread();
		}

	}

}