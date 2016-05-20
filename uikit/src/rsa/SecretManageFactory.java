package rsa;

import com.netease.nim.uikit.common.util.log.LogUtil;

import org.alexandria.client.AlexCipherClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wall on 2016/3/23.
 *
 * 管理加解密等的管理者
 *
 *
 */
public class SecretManageFactory {

    int anInt;// 区别各算法
    HeadUtils headUtils;
    Object object;
    // 传入需要加密的类型
    public SecretManageFactory(int i,Object object ){
            headUtils=new HeadUtils();

        if(i<0||object==null){
            return;
        }
        if(i==Arithmetic.TAEX){
            // 文本 获取文本密钥
            anInt=i;
            this.object=object;
        }else if(i==Arithmetic.IMAGE){
            // 图片 获取图片密钥
            anInt=i;
            this.object=object;
        }else if(i==Arithmetic.FILE){
            // 文件 获取文件密钥
            anInt=i;
            this.object=object;
        }else if(i==Arithmetic.AUDIO){
            // 文件 获取文件密钥
            anInt=i;
            this.object=object;
        }
    }

    // 加密
    public  String  doEncryptManage(){
        if(anInt==Arithmetic.TAEX){
            // 执行文本加密
            String data=(String)object;
            String s= headUtils.getSecretData(data);
            return s;
        }else if(anInt==Arithmetic.IMAGE){
            // 执行图片加密
            String path=(String)object;

            String newPath=headUtils.getSecretImg(path);
            return newPath;
        }else  if(anInt==Arithmetic.FILE){
            // 执行文件加密
            String path=(String)object;

            String newPath=headUtils.getSecretImg(path);
            return newPath;
        }else  if(anInt==Arithmetic.AUDIO){
            // 执行语音加密
            String path=(String)object;

            String newPath=headUtils.getSecretImg(path);
            return newPath;
        }else{
            // err
        }
        return null;
    }
    // 本地解密
    public Object doDecodebylocal(){
        if(anInt==Arithmetic.TAEX){
            // 执行文本解密
            String data=(String)object;

            String s= headUtils.getDecodeDatabylocal(data);
            return s;
        }else if(anInt==Arithmetic.IMAGE){
            // 执行图片解密
            File f=(File)object;
            boolean dec_ok=false;
            byte[] bhead= headUtils.decryptFilebylocal(f);// 解密并获取到头部
            // 本地解密没有做头部的判断，所以先设置false，后续进行改动
            return dec_ok;
        }else{
            // err
        }
        return null;
    }
    // 解密
    public Object doDecode(){
        if(anInt==Arithmetic.TAEX){
            // 执行文本解密
            String data=(String)object;

            String s= headUtils.getDecodeData(data);
            return s;
        }else if(anInt==Arithmetic.IMAGE){
            // 执行图片解密
            File f=(File)object;
            boolean dec_ok=false;
            byte[] bhead= headUtils.decryptFile(f);// 解密并获取到头部
            if(bhead!=null){
                   HashMap<String,Object> map= (HashMap<String, Object>) headUtils.diamantleHead(bhead);
                   byte[] sign= (byte[]) map.get(headUtils.SIGN);// 截取签名
                  byte[] datafile= FilesUtils.readFile(f.getAbsolutePath());// 获取文件byte[]流
                // 校验
                  dec_ok= headUtils.verifyImg(datafile, sign);
            }else{
                LogUtil.i("SecretManageFactory","--bhead is null");
            }
            return dec_ok;
        }else{
            // err
        }
        return null;
    }
    // 执行webSocket 连接
    public static AlexCipherClient getconnectSocket(){
        AlexCipherClient alexandria = InitAlexandria.getAlexandriaInstance(InitAlexandria.test_txt);
        return  alexandria;
    }
    // 生成密钥
    public static Map<String,Object> initRSAkey(){
        try {
            return RSACoder.initKey();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("SecretManageFactory","--密钥生成失败！");
            return null;
        }
    }
}
