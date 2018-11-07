/*
 * @Title ProtocalData.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-4 上午8:45:30
 * @version 1.0
 */
package com.yunbaba.apitest.ui;

/*
 * @Title：ProtocalData.java
 * @Copyright：Copyright 2010-2013 Careland Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author：Wenap
 * @date：2013-7-20 下午3:36:36
 * @version：1.0
 */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.cld.utils.CldSerializer;

/**
 * @Description 协议数据
 * @author Wenap
 * @date 2013-7-20 下午3:36:36
 */
public class ProtocalData {

	public static class ProtocalHead {
		byte[] szUmsa = new byte[4]; // UMSA标识
		byte[] lDataLen = new byte[4]; // 数据长度
		byte[] unIsZip = new byte[2]; // 是否压缩
		byte[] unCode = new byte[2]; // 业务编码
		byte[] ulCrc32 = new byte[4]; // 校验码

		public ProtocalHead() {
			szUmsa[0] = 'g';
			szUmsa[1] = 'j';
		}

		public byte[] GetData() {
			byte[] result = null;
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			try {
				byteOutStream.write(szUmsa);
				byteOutStream.write(lDataLen);
				byteOutStream.write(unIsZip);
				byteOutStream.write(unCode);
				byteOutStream.write(ulCrc32);
				result = byteOutStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		public void SetData(InputStream inputDataStream) {
			try {
				inputDataStream.read(szUmsa);
				inputDataStream.read(lDataLen);
				inputDataStream.read(unIsZip);
				inputDataStream.read(unCode);
				inputDataStream.read(ulCrc32);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void SetData(byte[] in_byteData) {
			System.arraycopy(in_byteData, 0, szUmsa, 0, 4);
			System.arraycopy(in_byteData, 4, lDataLen, 0, 4);
			System.arraycopy(in_byteData, 8, unIsZip, 0, 2);
			System.arraycopy(in_byteData, 10, unCode, 0, 2);
			System.arraycopy(in_byteData, 12, ulCrc32, 0, 4);
		}

		public int GetDataLen() {
			return (int) (CldSerializer.bytesToUint(lDataLen));
		}

		public void SetDataLen(long indatalen) {
			System.arraycopy(CldSerializer.uintToBytes(indatalen), 0, lDataLen,
					0, 4);
		}

		public int GetCode() {
			return CldSerializer.bytesToUshort(unCode);
		}

		public void SetCode(int code) {
			System.arraycopy(CldSerializer.ushortToBytes(code), 0, unCode, 0, 2);
		}

		public boolean GetisZip() {
			int intZip = CldSerializer.bytesToUshort(unIsZip);
			return (intZip == 1);
		}

		public void SetisZip(int in_isZip) {
			System.arraycopy(CldSerializer.ushortToBytes(in_isZip), 0, unIsZip,
					0, 2);
		}

		public long GetCRC32() {
			return CldSerializer.bytesToUint(ulCrc32);
		}

		public void SetCRC32(long inCRC32) {
			System.arraycopy(CldSerializer.uintToBytes(inCRC32), 0, ulCrc32, 0,
					4);
		}
	}

	// 设备信息
	public static class DeviceInfo {
		public String deviceName; // 设备名称
		public String tcpIP; // TCP IP

		/*
		 * byte[] szName = new byte[30]; // 设备名称 byte[] szIPAddr = new byte[20];
		 * // TCP IP
		 */

		public void SetData(byte[] in_byteData) {
			if (in_byteData.length != 50)
				return;

			int pos = 29;
			while (0 == in_byteData[pos])
				pos--;
			deviceName = new String(in_byteData, 0, pos + 1);

			pos = 49;
			while (0 == in_byteData[pos])
				pos--;
			tcpIP = new String(in_byteData, 30, pos - 29);
		}
	}

	// 热联导航POI信息
	public static class ApPoiInfo {
		public String name; // 名称
		public String addr; // 地址
		public String kcode; // K码
		public int selType; // 选路方式( 1:系统推荐; 2:高速优先; 3:少走高速; 4:距离优先)

		/*
		 * byte[] szName = new byte[100]; // 名称 byte[] szAddr = new byte[200];
		 * // 地址 byte[] szKCode = new byte[20]; // K码 int iSelRoadType; // 选路方式(
		 * 1:系统推荐; 2:高速优先; 3:少走高速; 4:距离优先)
		 */
		public byte[] GetData() {
			byte[] result = null;
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			try {
				byte[] src = null;
				byte[] szName = new byte[100]; // 名称
				byte[] szAddr = new byte[200]; // 地址
				byte[] szKCode = new byte[20]; // K码

				src = name.getBytes();
				System.arraycopy(src, 0, szName, 0, src.length);
				src = addr.getBytes();
				System.arraycopy(src, 0, szAddr, 0, src.length);
				src = kcode.getBytes();
				System.arraycopy(src, 0, szKCode, 0, src.length);
				byteOutStream.write(szName);
				byteOutStream.write(szAddr);
				byteOutStream.write(szKCode);
				byteOutStream.write(CldSerializer.uintToBytes(selType));
				result = byteOutStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		public void SetData(byte[] in_byteData) {
			if (in_byteData.length != 224)
				return;

			int pos = 99;
			while (0 == in_byteData[pos])
				pos--;
			name = new String(in_byteData, 0, pos + 1);

			pos = 199;
			while (0 == in_byteData[pos])
				pos--;
			addr = new String(in_byteData, 100, pos - 99);

			pos = 219;
			while (0 == in_byteData[pos])
				pos--;
			kcode = new String(in_byteData, 200, pos - 199);

			byte[] bSel = new byte[4];
			System.arraycopy(in_byteData, 220, bSel, 0, 4);
			selType = (int) CldSerializer.bytesToUint(bSel);
			;
		}
	}
}
