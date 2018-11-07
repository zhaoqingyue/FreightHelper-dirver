/*
 * @Title ProtocalCommon.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-4 上午8:46:17
 * @version 1.0
 */
package com.yunbaba.apitest.ui;

/**
 * 协议通用相关源文件
 */
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import com.cld.utils.CldSerializer;

public class ProtocalCommon {

	private static final int PACK_BUF_SIZE = 100 * 1024; // 打包缓冲大小

	private static final int ProtDataHeadSize = 8; // 数据头大小

	/** 业务类型定义 */
	public static final int EProtReportEvt = 103; // 事件上报(报料)
	public static final int EProtDataVer = 500; // 业务版本

	// 协议数据头
	static public class ProtDataHead {
		public long ulDataLen; // 数据内容字节数
		public int unType; // 业务类型[协议业务类型]
		public int unCount; // 记录数量
	}

	// 业务版本
	static public class ProtDataVer {
		public int unCode; // 业务编码
		public byte ucVer; // 版本号
		public byte ucReserve; // 保留

		public ProtDataVer(int in_nCode, int in_nVer) {
			unCode = in_nCode;
			ucVer = (byte) (in_nVer & 0xff);
		}

		public byte[] getByteData() {
			byte[] ret = null;
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			try {
				byteStream.write(CldSerializer.ushortToBytes(unCode));
				byteStream.write(ucVer);
				byteStream.write(ucReserve);
				ret = byteStream.toByteArray();
				byteStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ret;
		}
	}

	// 位置信息
	static public class ProtPosition {
		public long lRegionCode; // 区域编码
		public long lX; // 凯立德坐标X(千分之一秒)
		public long lY; // 凯立德坐标Y(千分之一秒)
		public long lZ; // 高程(米)

		public int unGpsSpeed; // 即时速度(km/h)
		public int unAvgSpeed; // 平均速度(km/h)
		public int unDirection; // 行驶方向(GPS角度, 0~360度)
		public byte ucErrCode; // 错误码(注意是无符号类型0~255)
		public byte ucSataliteNum; // 卫星颗数

		public long ulUTCTime; // 推算出来的服务器时间
		public long ulCellID; // 道路图幅ID,下载时设为0
		public long ulUid; // 道路UID,下载时设为0

		public byte[] getByteData() {
			byte[] ret = null;
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			try {
				byteStream.write(CldSerializer.uintToBytes(lRegionCode));
				byteStream.write(CldSerializer.uintToBytes(lX));
				byteStream.write(CldSerializer.uintToBytes(lY));
				byteStream.write(CldSerializer.uintToBytes(lZ));
				byteStream.write(CldSerializer.ushortToBytes(unGpsSpeed));
				byteStream.write(CldSerializer.ushortToBytes(unAvgSpeed));
				byteStream.write(CldSerializer.ushortToBytes(unDirection));
				byteStream.write(ucErrCode);
				byteStream.write(ucSataliteNum);
				byteStream.write(CldSerializer.uintToBytes(ulUTCTime));
				byteStream.write(CldSerializer.uintToBytes(ulCellID));
				byteStream.write(CldSerializer.uintToBytes(ulUid));
				ret = byteStream.toByteArray();
				byteStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ret;
		}
	}

	// 位置网络状态
	static public class ProtLocStatus {
		public int unCellId; // 手机所在的蜂窝扇区ID(0~0xFFFF,0xFFFF为未知)
		public int unLocationAreaCode; // 手机所在的位置区域号码(LAC)(0~0xFFFF,0xFFFF为未知)

		public byte ucGsmSigStrength; // 手机信号强度(值0~31)
		public byte ucNetSigStrength; // 网络信号强度(值0~100)
		public byte ucNetType; // 网络类型(参考CAL_NetworkType)
		public byte ucReserve; // 保留

		public byte[] getByteData() {
			byte[] ret = null;
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			try {
				byteStream.write(CldSerializer.ushortToBytes(unCellId));
				byteStream.write(CldSerializer
						.ushortToBytes(unLocationAreaCode));
				byteStream.write(ucGsmSigStrength);
				byteStream.write(ucNetSigStrength);
				byteStream.write(ucNetType);
				byteStream.write(ucReserve);
				ret = byteStream.toByteArray();
				byteStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ret;
		}
	}

	// 上报事件信息
	static public class ProtEventInfo {
		public int unEvtDirection; // 事件方向(1: 正向, 2: 反向)
		public int unEvtType; // 事件类型
		public int unEvtSource; // 事件来源(1: 官方, 2: K友)
		public int unEvtRewardNum; // 悬赏金额

		public byte[] szEvtDes = null; // 事件描述
		public String strPhotoFile = null; // 照片文件名(全路径)
		public String strVoiceFile = null; // 照片文件名(全路径)

		private long ulSizeContent = 0; // 事件描述长度
		private long ulSizePhoto = 0; // 图片数据长度
		private long ulSizeVoice = 0; // 语音数据长度

		public byte[] getByteData() {
			byte[] ret = null;
			int readLen = 0;
			byte[] readBuf = new byte[1024];
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			try {
				byteStream.write(CldSerializer.ushortToBytes(unEvtDirection));
				byteStream.write(CldSerializer.ushortToBytes(unEvtType));
				byteStream.write(CldSerializer.ushortToBytes(unEvtSource));
				byteStream.write(CldSerializer.ushortToBytes(unEvtRewardNum));

				if (null != szEvtDes)
					ulSizeContent = szEvtDes.length;
				if (null != strPhotoFile)
					ulSizePhoto = (new FileInputStream(strPhotoFile))
							.available();
				if (null != strVoiceFile)
					ulSizeVoice = (new FileInputStream(strVoiceFile))
							.available();
				byteStream.write(CldSerializer.uintToBytes(ulSizeContent));
				byteStream.write(CldSerializer.uintToBytes(ulSizePhoto));
				byteStream.write(CldSerializer.uintToBytes(ulSizeVoice));

				// 事件描述
				if (null != szEvtDes && ulSizeContent > 0)
					byteStream.write(szEvtDes);

				// 照片
				if (null != strPhotoFile) {
					FileInputStream fin = new FileInputStream(strPhotoFile);
					while (-1 != (readLen = fin.read(readBuf))) {
						byteStream.write(readBuf, 0, readLen);
					}
					fin.close();
				}

				// 语音
				if (null != strVoiceFile) {
					FileInputStream fin = new FileInputStream(strVoiceFile);
					while (-1 != (readLen = fin.read(readBuf))) {
						byteStream.write(readBuf, 0, readLen);
					}
					fin.close();
				}

				ret = byteStream.toByteArray();
				byteStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ret;
		}
	}

	/** 成员 */
	private ByteArrayOutputStream byteData = null;
	private byte[] packBuffer = null;

	/**
	 * 构造
	 */
	public ProtocalCommon() {
		packBuffer = new byte[PACK_BUF_SIZE];
		byteData = new ByteArrayOutputStream();
	}

	/** JNI函数声明. */
	/*
	 * ======================================================== 功能: 打包数据 参数:
	 * in_data [输入] => 需要打包的数据 in_dataLen[输入] => 需要打包的数据长度(字节数) out_data [输出
	 * 
	 * ] => 打包后的数据 in_bufLen [输入] => 打包缓冲长度(字节数) in_isZip [输入] => 是否需要压缩 返回:
	 * 
	 * (>0) => 打包后的数据长度 other => 失败(0: 参数错误; -1: 输出缓冲不够; -2: 压缩失败; -3: 生成检验码失败
	 * 
	 * ) ========================================================
	 */
	private native int packPostData(byte[] in_data, int in_dataLen,
			byte[] out_data, int in_bufLen, int in_isZip);

	/*
	 * ======================================================== 功能: 附加数据头 参数:
	 * in_nType [输入] => 数据业务类型 in_nCount [输入] => 数量 in_lDataLen[输入] => 数据长度
	 * ========================================================
	 */
	public void appendDataHeader(int in_nType, int in_nCount, long in_lDataLen) {
		try {
			byteData.write(CldSerializer.uintToBytes(in_lDataLen
					+ ProtDataHeadSize));
			byteData.write(CldSerializer.ushortToBytes(in_nType));
			byteData.write(CldSerializer.ushortToBytes(in_nCount));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================================== 功能: 附加数据 参数:
	 * in_data[输入] => 数据========================================================
	 */
	public void appendData(byte[] in_data) {
		try {
			byteData.write(in_data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================================== 功能: 获取Post打包数据
	 * 参数: in_isZip[输入] => 是否压缩(0: 不压缩; 1: 压缩) 返回: Post打包数据
	 * ========================================================
	 */
	public byte[] getPostPackData(int in_isZip) {
		int ret = 0;
		byte[] srcData = byteData.toByteArray();

		ret = packPostData(srcData, srcData.length, packBuffer, PACK_BUF_SIZE,
				in_isZip);
		if (ret > 0) {
			byteData.reset();
			byteData.write(packBuffer, 0, ret);
		}

		return byteData.toByteArray();
	}
}
