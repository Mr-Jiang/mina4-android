package org.apache.mina4.android.log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * ClassName :Mina4Log
 * <p>
 * Description: mina4-android控制日志打印输出类
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
public class Mina4Log {

	/**
	 * 写日志开关，默认不写日志
	 */
	private static boolean canWrite = false;

	/**
	 * 打印日志开关，默认打印
	 */
	private static boolean DeBug = true;

	/**
	 * 日志文件名
	 */
	private static final String LOGNAME = "mina4log.txt";

	/**
	 * 时间格式
	 */
	private static final String INFORMAT = "YYYY-MM-dd HH:mm:ss";

	/**
	 * VERBOSE标识
	 */
	private static final int VERBOSE = 5;

	/**
	 * DEBUG标识
	 */
	private static final int DEBUG = 4;

	/**
	 * INFO标识
	 */
	private static final int INFO = 3;

	/**
	 * WARN标识
	 */
	private static final int WARN = 2;

	/**
	 * ERROR标识
	 */
	private static final int ERROR = 1;

	/**
	 * 默认的日志存储路径
	 */
	private static final String DEFAULT_LOG_DIRPATH = Environment.getExternalStorageDirectory() + File.separator
			+ "mina4log";

	/**
	 * 存放日志文件的路径
	 */
	private static File MINA4LOG_FILE;

	public static void Mina4LogPrintf(Context context) {
		MINA4LOG_FILE = new File(context.getExternalFilesDir("mina4log"), LOGNAME);
	}

	/**
	 * @param tag
	 *            日志标识
	 * @param throwable
	 *            抛出的异常
	 * @param type
	 *            日志类型
	 */
	public static void log(String tag, Throwable throwable, int type) {
		log(tag, exToString(throwable), type);
	}

	/**
	 * @param tag
	 *            日志标识
	 * @param msg
	 *            要输出的内容
	 * @param type
	 *            日志类型
	 */
	public static void log(String tag, String msg, int type) {
		switch (type) {
		case VERBOSE:
			v(tag, msg);
			break;
		case DEBUG:
			d(tag, msg);
			break;
		case INFO:
			i(tag, msg);
			break;
		case WARN:
			w(tag, msg);
			break;
		case ERROR:
			e(tag, msg);
			break;
		default:
			break;
		}
	}

	/**
	 * verbose日志输出
	 * 
	 * @param tag
	 *            日志标识
	 * @param msg
	 *            要输出的内容
	 */
	public static void v(String tag, String msg) {
		// 是否打印日志
		if (DeBug) {
			Log.v(tag, msg);
		}
		// 是否写日志
		if (canWrite) {
			write(tag, msg);
		}
	}

	/**
	 * debug日志输出
	 * 
	 * @param tag
	 *            标识
	 * @param msg
	 *            内容
	 */
	public static void d(String tag, String msg) {
		if (DeBug) {
			Log.d(tag, msg);
		}
		if (canWrite) {
			write(tag, msg);
		}
	}

	/**
	 * info日志输出
	 * 
	 * @param tag
	 *            标识
	 * @param msg
	 *            内容
	 */
	public static void i(String tag, String msg) {
		if (DeBug) {
			Log.i(tag, msg);
		}
		if (canWrite) {
			write(tag, msg);
		}
	}

	/**
	 * warn日志输出
	 * 
	 * @param tag
	 *            标识
	 * @param msg
	 *            内容
	 */
	public static void w(String tag, String msg) {
		if (DeBug) {
			Log.w(tag, msg);
		}
		if (canWrite) {
			write(tag, msg);
		}
	}

	/**
	 * error日志输出
	 * 
	 * @param tag
	 *            标识
	 * @param msg
	 *            内容
	 * @return void 返回类型
	 */
	public static void e(String tag, String msg) {
		if (DeBug) {
			Log.e(tag, msg);
		}
		if (canWrite) {
			write(tag, msg);
		}
	}

	/**
	 * 把日志写入文件
	 * 
	 * @param tag
	 *            标识
	 * @param msg
	 *            要输出的内容
	 */
	public static void write(String tag, String msg) {
		String log = DateFormat.format(INFORMAT, System.currentTimeMillis()) + "\n" + tag + "  ========>>  " + msg
				+ "\n\n";
		if (MINA4LOG_FILE == null) {
			String path = Mina4WriteLog.createMkdirsAndFiles(DEFAULT_LOG_DIRPATH, LOGNAME);
			if (TextUtils.isEmpty(path)) {
				throw new RuntimeException("The file path cannot empty!");
			}
			Mina4WriteLog.write2File(path, log, true);
		} else {
			Mina4WriteLog.write2File(MINA4LOG_FILE.getAbsolutePath(), log, true);
		}
	}

	/**
	 * 写异常
	 * 
	 * @param ex
	 *            异常
	 */
	public static void write(Throwable ex) {
		write("", exToString(ex));
	}

	/**
	 * 异常信息转化为String
	 * 
	 * @param ex
	 *            异常信息
	 * @return String 异常信息字符串
	 */
	private static String exToString(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		printWriter.close();
		String result = writer.toString();
		return result;
	}
}
