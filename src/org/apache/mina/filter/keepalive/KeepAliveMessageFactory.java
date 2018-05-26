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
package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;

/**
 * Provides keep-alive messages to {@link KeepAliveFilter}.
 * 
 * @author <a href="https://github.com/Mr-Jiang/mina4-android">mina4-android</a>
 */
public interface KeepAliveMessageFactory {

	/**
	 * 1.假设当前场景(客户端主动推送心跳，服务端被动响应)
	 * <p>
	 * 客户端会定时发送心跳请求（注意定时时间必须小于，服务器端的IDLE监控时间）
	 * <p>
	 * 同时需要监听心跳反馈，以此来判断是否与服务器丢失连接
	 * <p>
	 * 服务器不会给客户端发送请求包，因此不关注请求包，isRequest方法直接返回false，不做任何处理
	 * <p>
	 * 2.如果当前场景属于活跃型连接(客户端主动推送心跳，服务端也同时发送心跳)
	 * <p>
	 * isRequest方法需要配置一个规则，用于检测服务端请求是否是心跳请求
	 * <p>
	 * 如是，则isRequest方法需返回true，表示请求是来自服务端的心跳数据
	 * 
	 * @return <tt>true</tt> if and only if the specified message is a
	 *         keep-alive request message.
	 * 
	 * @param session
	 *            The current session
	 * @param message
	 *            teh message to check
	 */
	boolean isRequest(IoSession session, Object message);

	/**
	 * 1.假设当前场景(客户端主动推送心跳，服务端被动响应)
	 * <p>
	 * 客户端需要关注请求反馈，以此判断该消息是否是心跳反馈包
	 * <p>
	 * 配置自己的解析规则，判断消息是否是心跳反馈包，是返回true，不是返回false
	 * 
	 * @return <tt>true</tt> if and only if the specified message is a
	 *         keep-alive response message;
	 * 
	 * @param session
	 *            The current session
	 * @param message
	 *            teh message to check
	 */
	boolean isResponse(IoSession session, Object message);

	/**
	 * 1.假设当前场景(客户端主动推送心跳，服务端被动响应)
	 * <p>
	 * getRequest返回需要发送给服务器的心跳数据包
	 * 
	 * @return a (new) keep-alive request message or <tt>null</tt> if no request
	 *         is required.
	 * 
	 * @param session
	 *            The current session
	 */
	Object getRequest(IoSession session);

	/**
	 * 1.假设当前场景属于半活跃型连接(客户端主动推送心跳，服务端被动响应)
	 * <p>
	 * 服务器不会给客户端发送心跳请求，客户端当然也不用反馈，getResponse方法直接返回null
	 * <p>
	 * 2.如果当前场景属于活跃型连接(客户端主动推送心跳，服务端也同时发送心跳)
	 * <p>
	 * isRequest方法需要配置一个规则，用于检测服务端请求是否是心跳请求
	 * <p>
	 * 如是，则isRequest方法需返回true，表示请求是来自服务端的心跳数据
	 * <p>
	 * getResponse方法返回需要响应给服务端的心跳包数据
	 * <p>
	 * 
	 * @return a (new) response message for the specified keep-alive request, or
	 *         <tt>null</tt> if no response is required.
	 * 
	 * @param session
	 *            The current session
	 * @param request
	 *            The request we are lookig for
	 */
	Object getResponse(IoSession session, Object request);
}
