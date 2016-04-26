package rsa;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class FilesUtils {

	// SD
	public static final String PATH_SD = Environment
			.getExternalStorageDirectory().getPath();
	// 根目录
	public static final String PATH_ROOT = PATH_SD + File.separator + "jr_demo";
	// 子目录
	public static final String PATH_MIYUE = PATH_ROOT + File.separator + "miyue";
	// 公钥
	public static final String PATH_GONGYUE = PATH_MIYUE + File.separator + "publicKey.keystore";
	//私钥
	public static final String PATH_SIYUE = PATH_MIYUE + File.separator + "privateKey.keystore";
	// aes密钥
	public static final String PATH_AESKEY = PATH_MIYUE + File.separator + "aes.keystore";
	//初始化文件系统
	public static boolean initDirs(){
		File file=new File(PATH_MIYUE);
		boolean b=file.mkdirs();// 创建此抽象路径名指定的目录，包括创建必需但不存在的父目录。
		return b;
	}
	/**
	 * 从文件中输入流中加载公钥
	 *
	 * @param path
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static String loadPublicKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("--公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("--公钥输入流为空");
		}
	}


	/**
	 * 从文件中加载私钥
	 *
	 * @param path
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public  static String loadPrivateKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("--私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("--私钥输入流为空");
		}
	}



	/**
	 * 读文件获取byte[] 流
	 * */
	protected static byte[] readFile(String path) {

		File file=new File(path);
		byte [] by=null;
		if (!file.isFile()) {
			return null;
		}
		try {
			FileInputStream is=new FileInputStream(file);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buf=new byte[1024];
			int len;
			while ((len=is.read(buf))!=-1) {
				swapStream.write(buf,0,len);
			}
			by=swapStream.toByteArray();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("File_store", "--打开File失败");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("File_store", "--IO读File失败");
		}
		return by;
	}

	/**
	 * 拷贝文件到系统的某个目录
	 *
	 * @param is
	 *            源文件的流
	 * @param destPath
	 *            目标路径
	 * @return
	 */
	public static File copyFile(String oriPath, String destPath) {
		try {
			File desfile = new File(PATH_MIYUE+ File.separator +destPath);
			File orifile=new File(oriPath);
			FileInputStream is=new FileInputStream(orifile);

			FileOutputStream fos = new FileOutputStream(desfile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
			return desfile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}






}
