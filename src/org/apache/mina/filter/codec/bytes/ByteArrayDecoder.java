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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * ClassName :ByteArrayDecoder
 * <p>
 * Description: ByteArray解码
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
 * @date 2018-03-22 09:59
 */
public class ByteArrayDecoder extends CumulativeProtocolDecoder {

	// private static final String TAG = ByteArrayDecoder.class.getName();

	/**
	 * 重写次方法用于处理tcp的断包、半包、粘包问题
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// 缓冲区中剩余的字节 < 1 时
		if (in.remaining() < 1) {
			// 不处理
			return false;
		}
		// 标记缓冲区的位置
		in.mark();
		// 创建字节数组，长度 = 缓冲区中剩余的字节长度
		byte[] data = new byte[in.remaining()];
		// 将缓冲区的字节从0开始，读取data.lengrh个到data字节数组
		in.get(data);
		// 定义起始位置
		int pos = 0;
		// 将此缓冲区的标记位置重置
		in.reset();
		// 缓冲区中剩余的字节 > 0时
		while (in.remaining() > 0) {
			// 标记缓冲区的位置
			in.mark();
			// 当前位置的字节，间接调用java.nio.ByteBuffer.get()方法，每调用一次get()方法时，get()方法返回当前位置的字节并将上次调用get()方法时的下标位置增加1
			byte tag = in.get();
			// 包的起始位置为0x7e标识符 && 缓冲区中剩余的字节 >0
			if (tag == 0x7e && in.remaining() > 0) {
				// 获取当前位置的字节
				tag = in.get();
				// 假设0x7e标识符下一个字节不是0x7e标识符，说明下一个字节属于当前数据包，数据还没有接收完毕
				while (tag != 0x7E) {
					// 缓冲区中剩余的字节 <= 0时
					if (in.remaining() <= 0) {
						// 没有找到结束包，等待下一个数据包
						// 将此缓冲区的标记位置重置
						in.reset();
						return false;
					}
					// 获取当前位置的字节
					tag = in.get();
				}
				// 假设0x7e标识符下一个字节依旧是0x7e标识符，说明下一个字节是新数据包的起始位置
				// 返回此缓冲区当前位置的值
				pos = in.position();
				// 当前标记的位置。 如果未设置标记，则此方法返回-1
				int packetLength = pos - in.markValue();
				// 包长度 > 1
				if (packetLength > 1) {
					// 定义packetLength长度的字节数组
					byte[] tmp = new byte[packetLength];
					// 将此缓冲区的标记位置重置
					in.reset();
					// 将缓冲区的字节从0开始，读取tmp.lengrh个到tmp字节数组
					in.get(tmp);
					// 写tmp
					out.write(tmp);
				} else {
					// 说明是两个0x7E
					in.reset();
					// 两个7E说明前面是包尾，后面是包头
					in.get();
				}
			}
		}
		return false;
	}

}