package rsa;

import android.util.Log;

import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成时间戳
 * 编辑头部
 *
 * 密钥 256  byte
 * 签名 256  byte
 * 时间戳 8   byte
 * 预留 256  byte
 * */
   public class HeadUtils {
	public static final String TAG="HeadUtils";
	public static final String TIME="time";// 时间戳
	public static final String OBLIGATE="obligate";// 预留
	public static final String SIGN="sign";// 签名
	public static final String SECRET="secret";// 密钥密文

	public static final String AESKEY="aeskey";
	public static final String RSAPRIKEY="rsaprikey";
	public static final String RSAPUB="rsapubkey";
	// 公钥
	public static   byte[] publicKey;
	//私钥
	public static   byte[] privateKey;
	// AES 密钥
	public static   String aeskey;
	private SpUtils spUtils;

	public String getAeskey(){
		return  aeskey;
	}

	protected HeadUtils(){
		 spUtils=SpUtils.getSpUtils();
			if(publicKey==null){
				LogUtil.i("HeadUtils","--公钥获取异常！请检查webSocket连接是否异常或者服务端");
				return;
			}
			if(privateKey==null){
				LogUtil.i("HeadUtils","--私钥获取异常！");
				return;
			}

	}

	//获取当前时间戳
	private  byte[] getNowTime(){
		long time=System.currentTimeMillis();
		byte[] b1=long2bytes(time);
		return b1;
	}
	// long转byte[]
	private  byte[] long2bytes(long num) {
		byte[] b = new byte[8];
		for (int i=0;i<8;i++) {
			b[i] = (byte)(num>>>(56-(i*8)));
		}
		return b;
	}

	// 整合头部
	/**
	 * 头部是固定长度
	 * @param time 时间戳 8 byte
	 * @param obligate 预留256 byte
	 * @param sign 签名256 byte
	 * @param secret 密钥密文 256 byte
	 * */
	private  byte[] connectHead(byte[] time,byte[] obligate,byte[] sign,byte[] secret
	){
		// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组；
		// destPos:目的数组放置的起始位置； length:复制的长度。
		//System.arraycopy(src, srcPos, dest, destPos, length);
		byte[] bytehead=new byte[776];
		System.arraycopy(time, 0, bytehead, 0, time.length);
		System.arraycopy(obligate, 0, bytehead, 8, obligate.length);
		System.arraycopy(sign, 0, bytehead, 264, sign.length);
		System.arraycopy(secret, 0, bytehead, 520, secret.length);
		return bytehead;
	}
	// 整合头部和密文
	private  byte[] connectData(byte[] head,byte[] data){
		// head将要放进data里的数组
		byte[] bytedata=new byte[data.length+head.length];
		// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组；
		// destPos:目的数组放置的起始位置； length:复制的长度。
		System.arraycopy(head, 0, bytedata, 0, head.length);

		System.arraycopy(data, 0, bytedata, head.length, data.length);

		return bytedata;
	}


	/**
	 * 将头部传入，分理出各属性
	 * 按顺序存放到MAP中
	 * */

	protected static  Map<String,Object> diamantleHead(byte[] head){
		if(head==null){
			return null;
		}
		Map<String, Object> headMap=new HashMap<String,Object>();
		byte[] time =new byte[8];
		byte[] obligate =new byte[256];
		byte[] sign =new byte[256];
		byte[] secret =new byte[256];

		System.arraycopy(head, 0, time, 0, 8);
		headMap.put(TIME, time);

		System.arraycopy(head, 8, obligate, 0, 256);
		headMap.put(OBLIGATE, obligate);

		System.arraycopy(head, 264, sign, 0, 256);
		headMap.put(SIGN, sign);

		System.arraycopy(head, 520, secret, 0, 256);
		headMap.put(SECRET, secret);

		return headMap;
	}

	// 分离出头部
	private  byte[] factoryByte2(byte[] b1,int i){
		// i 是头部长度
		byte[] b3=new byte[i];
		// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组；
		// destPos:目的数组放置的起始位置； length:复制的长度。
		System.arraycopy(b1, 0, b3, 0, i);// 将b1 头部复制到b3中
		return b3;
	}

	protected byte[] getDataByBigdata(byte[] b1,int i){
		return  factoryByte3(b1,i);
	}

	// 分离出数据
	private  byte[] factoryByte3(byte[] b1,int i){
		// i 是头部长度
		byte[] b3=new byte[b1.length-i];
		// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组；
		// destPos:目的数组放置的起始位置； length:复制的长度。
		System.arraycopy(b1, i, b3, 0, b1.length - i);//
		return b3;
	}
	// 字符串转字节数组
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		hex=hex.toLowerCase();
		int k=0;
		for (int i = 0; i < result.length; i++) {
			byte high = (byte) (Character.digit(hex.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hex.charAt(k + 1), 16) & 0xff);
			result[i] = (byte) (high << 4 | low);
			k += 2;

		}
		return result;
	}
	// 字节数组转字符串
	public  static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	/**
	 * ************************************传入txt数据，进行生成头部和加密*****************************************************
	 * */

	protected  String getSecretData(String message){
		byte[] byte_message=message.getBytes();
		byte[] aeskeybyrom=Secret_Factory.produceAESkey();// AESkey

		try {
			// 产生签名 256
			byte[] signstr=RSACoder.sign(byte_message,privateKey);// 本端私钥
			// 进行AES加密
			byte[] byte_secretData= MyAES.aes_encrypt(message.getBytes(), aeskeybyrom);
			// 利用公钥对AES密钥进行加密处理 ，加密后的密钥 cipherText 256
			byte[] cipherText=RSACoder.encryptByPublicKey(aeskeybyrom,publicKey);// 对端公钥
			// 时间戳 8
			byte[] time=getNowTime();
			byte[] keybytime=Secret_Factory.produceAESkeybytime(time);// 根据时间戳生成密钥
			byte[] enc_aeskeybyrom=MyAES.aes_encrypt(aeskeybyrom,keybytime);// 将加密的密钥进行加密存储
			spUtils.saveByte(bytesToHexString(time),enc_aeskeybyrom);// 储存密钥
			// 预留byte 256
			byte[] obligate=new byte[256];
			// 将 签名和密钥等进行整合成 头部 776
			byte[] head_byte=connectHead(time , obligate, signstr, cipherText);
			// 将头部和密文进行整合
			byte[] con_byte=connectData(head_byte, byte_secretData);
			LogUtil.i(TAG,"--头部长"+head_byte.length);
			LogUtil.i(TAG,"--数据长"+byte_secretData.length);
			LogUtil.i(TAG,"--密钥："+aeskeybyrom);
			//进行进制转换
			return bytesToHexString(con_byte);
		} catch (Exception e) {
			e.printStackTrace();
			return "err,110";
		}
	}

	/**
	 *************************************传入txt数据，进行分解头部和解密*****************************************************
	 **/
	// 本地解密
	protected String getDecodeDatabylocal(String data){
		try {
			//进行进制转换
			byte[] rootData=hexStringToByte(data);
			LogUtil.i(TAG,"--接收到总数据长"+rootData.length);
			// 分离出头部和数据
			byte[] byte_head=factoryByte2(rootData,776);// 分离出头部
			byte[] byte_data=factoryByte3(rootData, 776);// 分离出数据
			LogUtil.i(TAG,"--分离出数据："+byte_data.length);
			// 解析头部
			Map<String, Object> headmap=diamantleHead(byte_head);
			byte[] time=(byte[])headmap.get(HeadUtils.TIME);
			// 根据time 读取本地存储的密钥
			byte[] aeskey_enc=spUtils.readRSAKeyString(bytesToHexString(time));

			byte[] keybytime=Secret_Factory.produceAESkeybytime(time);
			// 利用私钥对密钥密文进行解密  获取AES密钥plainText
			byte[] aeskey=MyAES.aes_decrypt(aeskey_enc,keybytime);
			// 获取到密钥，进行AES解密
			byte[] aes_decData=MyAES.aes_decrypt(byte_data,aeskey);

			return new String(aes_decData);

		} catch (Exception e) {
			e.printStackTrace();
			return "err,111";
		}
	}

	// 对端解密
	protected String getDecodeData(String data){
		try {
		//进行进制转换
		byte[] rootData=hexStringToByte(data);
		LogUtil.i(TAG,"--接收到总数据长"+rootData.length);
		// 分离出头部和数据
		byte[] byte_head=factoryByte2(rootData,776);// 分离出头部
		byte[] byte_data=factoryByte3(rootData, 776);// 分离出数据
		LogUtil.i(TAG,"--分离出数据："+byte_data.length);
		// 解析头部
		Map<String, Object> headmap=diamantleHead(byte_head);
		byte[] sign_str=(byte[]) headmap.get(HeadUtils.SIGN);// 在头部中分理出签名
		byte[] pri_pasword=(byte[]) headmap.get(HeadUtils.SECRET);// 在头部中分理出密钥
		// 利用私钥对密钥密文进行解密  获取AES密钥plainText
		byte[] plainText =RSACoder.decryptByprivateKey(pri_pasword, privateKey);
		// 获取到密钥，进行AES解密
		byte[] aes_decData=MyAES.aes_decrypt(byte_data,plainText);
		//  利用公钥进行签名校对
		boolean is_true=RSACoder.verify(aes_decData,publicKey,sign_str);

			if(is_true){
				return new String(aes_decData);
			}

			return "err,222！";
		} catch (Exception e) {
			e.printStackTrace();
			return "err,111";
		}
	}
	/**
	 * ************************************传入imgFile，进行头部加密和整合*****************************************************
	 * */

	//  生成头部和密文
	protected String getSecretImg(String path ){
		LogUtil.i("HeadUtils","--地址："+path);
		// 在这偷换地址,把加密图片处理
		int last=path.lastIndexOf("/");
		String newPath=path.substring(0,last)+File.separator+"encImage";
		File encFile=new File(newPath);
		encFile.mkdirs();// 创建加密的目录，在原目录基础上
		// 时间戳 8
		byte[] time=getNowTime();
		byte[] aesrom=Secret_Factory.produceAESkey();// 生成AES密钥

		byte[] keybytime=Secret_Factory.produceAESkeybytime(time);
		byte[] enc_aeskeybyrom=MyAES.aes_encrypt(aesrom,keybytime);
		spUtils.saveByte(bytesToHexString(time),enc_aeskeybyrom);// save aeskey

		// 生成新的文件保存加密后的图片
		String tmpFilePath=newPath+File.separator+System.currentTimeMillis()+"_jrenc";
		// 获取到imgData
		byte[] signData=FilesUtils.readFile(path);
		try {
			// 生成签名
			byte[] sign=RSACoder.sign(signData,privateKey);
			// 利用公钥对AES密钥进行加密处理 ，加密后的密钥 cipherText 256
			byte[] cipherText=RSACoder.encryptByPublicKey(aesrom,publicKey);

			// 预留byte 256
			byte[] obligate=new byte[256];

			// 将签名和密钥等进行整合成 头部 776
			byte[] head_byte=connectHead(time, obligate, sign, cipherText);
			// 将头部进行加密 784
			//byte[] enc_head=MyAES.aes_encrypt(head_byte,Secret_Factory.produceAESkey());
			// 将头部和密文进行整合并进行AES加密
			 getCryptoFileObject(path,tmpFilePath,head_byte,aesrom);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpFilePath;
	}

	// enc加密
	protected  void getCryptoFileObject(String path,String tmpFilePath,byte[] head,byte[] aeskeyrom) {

		FileInputStream in = null;
		FileOutputStream out = null;
		//将头部写入
		boolean fist=true;
		try {
			in = new FileInputStream(path);
			out = new FileOutputStream(tmpFilePath);
			byte[] btDataBuf = new byte[2048];// 明文缓冲区
			byte[] btOutBuf;// 密文缓冲区
			//加密
			int len;
			while ((len = in.read(btDataBuf)) > 0) {

				if(fist){
					out.write(head);
					fist=false;
				}
					byte[] btInBuf = null;
					if (len < 2048) {
						btInBuf = new byte[len];
						for (int i = 0; i < len; i++) {
							btInBuf[i] = btDataBuf[i];
						}
					} else {
						btInBuf = btDataBuf;
					}
					btOutBuf= MyAES.aes_encrypt(btInBuf,aeskeyrom); // jr 自定义加密
					// 写入文件
					out.write(btOutBuf, 0, btOutBuf.length);
			}
		}
		catch (IOException e) {
			Log.i("HeadUtils", "--io出错了1？"+e.getMessage() );
		} catch (Exception e) {
			Log.i("HeadUtils", "--io出错了2？"+e.getMessage() );
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				fist=false;
			} catch (Exception e) {
				Log.i("HeadUtils", "--Weord exception while closing stream");
			}
		}

	}

	// 本地解密

	// dec解密 返回头部明文
	protected  byte[] decryptFilebylocal(File encrypt_file) {

		File decrypt_file = new File(encrypt_file.getAbsolutePath() + "_jrdec");

		FileInputStream in = null;
		FileOutputStream out = null;
		byte[] btheadOutBuf=null;// 明文头部缓冲区

		try {
			in = new FileInputStream(encrypt_file);
			out = new FileOutputStream(decrypt_file);
			byte[] btDataBuf = new byte[2064];// 密文缓冲区 加密时按照2048分组，解密时按照2048+16读取
			byte[] bthead=new byte[776];
			byte[] btOutBuf;// 明文缓冲区
			byte[] bbb;
			byte[] bbb2;
			//解密
			int len;

			len=in.read(bthead,0,776);
			bbb2=new byte[len];
			bbb2=bthead;
			// 解密 获取到头部
			// 解析头部
			Map<String, Object> headmap=diamantleHead(bbb2);
			byte[] time =(byte[])headmap.get(HeadUtils.TIME);
			byte[] aeskey_enc=spUtils.readRSAKeyString(bytesToHexString(time));
			byte[] keybytime=Secret_Factory.produceAESkeybytime(time);
			// 利用私钥对密钥密文进行解密  获取AES密钥plainText
			byte[] aeskey=MyAES.aes_decrypt(aeskey_enc,keybytime);
			btheadOutBuf = bbb2;
			LogUtil.i("FilesUtils","--toubu？"+btheadOutBuf.length);
			while ((len = in.read(btDataBuf,0,2064)) != -1) {
				if (len < 2048) {
					bbb = new byte[len];
					for (int i = 0; i < len; i++) {
						bbb[i] = btDataBuf[i];
					}
				} else {
					bbb = btDataBuf;
				}
				// 解密
				btOutBuf = MyAES.aes_decrypt(bbb, aeskey);
				LogUtil.i("FliesUtils", "--解密后长度：" + btOutBuf.length);
				out.write(btOutBuf,0,btOutBuf.length);
			}

			decrypt_file.renameTo(encrypt_file);
			decrypt_file.delete();// delete 缓存的图片
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.i("FilesUtils", "--解密出现问题");
			return btheadOutBuf;
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (Exception e) {
				LogUtil.i("FilesUtils", "--Weord exception while closing stream");
			}
		}
		return btheadOutBuf;
	}




	// dec解密 返回头部明文
	protected  byte[] decryptFile(File encrypt_file) {

		File decrypt_file = new File(encrypt_file.getAbsolutePath() + "_jrdec");

		FileInputStream in = null;
		FileOutputStream out = null;
		byte[] btheadOutBuf=null;// 明文头部缓冲区

		try {
			in = new FileInputStream(encrypt_file);
			out = new FileOutputStream(decrypt_file);
			byte[] btDataBuf = new byte[2064];// 密文缓冲区 加密时按照2048分组，解密时按照2048+16读取
			byte[] bthead=new byte[776];
			byte[] btOutBuf;// 明文缓冲区
			byte[] bbb;
			byte[] bbb2;
			//解密
			int len;

			len=in.read(bthead,0,776);
			bbb2=new byte[len];
			bbb2=bthead;
			// 解密 获取到头部
			// 解析头部
			Map<String, Object> headmap=diamantleHead(bbb2);
			byte[] pri_pasword=(byte[]) headmap.get(HeadUtils.SECRET);// 在头部中分理出密钥
			// 利用私钥对密钥密文进行解密  获取AES密钥plainText
			byte[] plainText =RSACoder.decryptByprivateKey(pri_pasword, privateKey);
			btheadOutBuf = bbb2;
			LogUtil.i("FilesUtils","--toubu？"+btheadOutBuf.length);
			while ((len = in.read(btDataBuf,0,2064)) != -1) {
				if (len < 2048) {
					bbb = new byte[len];
					for (int i = 0; i < len; i++) {
						bbb[i] = btDataBuf[i];
					}
				} else {
					bbb = btDataBuf;
				}
				// 解密
				btOutBuf = MyAES.aes_decrypt(bbb, plainText);
				LogUtil.i("FliesUtils", "--解密后长度：" + btOutBuf.length);
				out.write(btOutBuf,0,btOutBuf.length);
			}

			decrypt_file.renameTo(encrypt_file);
			decrypt_file.delete();// delete 缓存的图片
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.i("FilesUtils", "--解密出现问题");
			decrypt_file.delete();// delete 缓存的图片
			return btheadOutBuf;
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (Exception e) {
				LogUtil.i("FilesUtils", "--Weord exception while closing stream");
			}
		}
		return btheadOutBuf;
	}

	// 将头部传入，进行校验
	protected   boolean verifyImg(byte[] data,byte[] sign)  {

		try {
			return RSACoder.verify(data,publicKey,sign);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.i("HeadUtils","--校验过程出错！");
			return false;
		}
	}

	// MD5 摘要
	protected final static byte[] MD5(byte[] b) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		try {
			byte[] btInput = b;
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



}
