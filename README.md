# mina4-android description
Android TCP框架(基于MINA 2.0.0-M3)、增加Bytes工厂、无需依赖log4(新增Mina4Log打印输出日志)，
<br>处理Bytes粘包、半包、断包(ByteArrayDecoder)，需配置自己的首尾标识符，
<br>如果与首尾标识符相同的数据出现在首尾标识符以内的范围，建议将该数据进行转义，
<br>如这样配置转义规则(假设首尾标识符是0x7e)：

0x7e = 0x7d,0x02 如果首尾以内出现标识符，将其转换为0x7d后面紧跟一个0x02
<br>0x7d = 0x7d,0x01 如果首尾以内出现标识符转义字符，不进行转换，在其后面紧跟一个0x01

# mina4-android use
	private void initMain4Android() {

		final ConnectionConfiguration config = new ConnectionConfiguration.Builder(this).setIp("192.168.0.102")
				.setPort(10086).setReadBufferSize(10240).setConnectionTimeout(5000)
				.setFactory(new ByteArrayCodecFactory()).setBridge(new KeepAliveMessageBridge() {

					@Override
					public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
						// 心跳超时
					}

					@Override
					public boolean isResponse(IoSession session, Object message) {
						// 字节数组编码
						if (config.getFactory() instanceof ByteArrayCodecFactory) {
						    // 判断服务端响应消息是否是心跳反馈包
							return isKeepAliveResponse((byte[]) message);
						}
						return false;
					}

					@Override
					public boolean isRequest(IoSession session, Object message) {
						return false;
					}

					@Override
					public Object getResponse(IoSession session, Object request) {
						return null;
					}

					@Override
					public int getRequestTimeout() {
					    // 心跳超时时间，10s
						return 10;
					}

					@Override
					public int getRequestInterval() {
					    // 心跳请求频率，5s
						return 5;
					}

					@Override
					public Object getRequest(IoSession session) {
					    // 发送心跳数据包，byte[]
						return IoBuffer.wrap(Bytes);
					}

				}).bulid();

		// ConnectionManager manager;
		manager = ConnectionManager.getManagerInstance();
		manager.loadConfig(config);
		manager.setOnConnectionListener(new ConnectionListener() {

			@Override
			public void receiveData(Object data) {
				// 字节数组编码
				if (config.getFactory() instanceof ByteArrayCodecFactory) {
					MessageCheck((byte[]) data);
				}
			}

			@Override
			public void disConnect() {

			}

			@Override
			public void connectOk() {

			}

			@Override
			public void connectError(Exception e) {
			}

			@Override
			public void exceptionCaught(Throwable cause) {

			}
		});
	}
	
# mina4-android Suggest
如果接收方与发送方的数据类型不一致时，会出现类型转换异常，需要服务端与客户端的收发数据类型保持一致，
<br>如果服务端是在不可控范围，就是说服务端不是自己公司的，是第三方的，你无法决定它的收发数据类型，
<br>请实现ProtocolCodecFactory，进行业务的拓展

# mina4-android feedback
Email：engineer.jsp.hn@gmail.com

# mina4-android jar
*  [mina4-android.jar](https://github.com/Mr-Jiang/mina4-android/blob/master/bin/mina4-android.jar)