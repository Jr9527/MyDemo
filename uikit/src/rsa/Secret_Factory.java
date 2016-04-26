package rsa;

import com.netease.nim.uikit.common.util.log.LogUtil;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 生成密钥
 * */
public class Secret_Factory {
    public static final String AES_Secret="aesSecret";

    /*
    *实时生成随机密钥
     */

    protected static byte[] produceAESkey(){
        try {
            KeyGenerator kg =  KeyGenerator.getInstance("AES");
            kg.init(256);//要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] key=sk.getEncoded();
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtil.i("Secret_Factory", "--生成aes密钥失败！");
            return null;

        }
    }
 /*   // 根据时间生成密钥
    protected static byte[] produceAESkeybytime(byte[] time){
        try {
            KeyGenerator kg =  KeyGenerator.getInstance("AES");
            kg.init(256,new SecureRandom(time));//要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] key=sk.getEncoded();
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtil.i("Secret_Factory", "--生成aes密钥失败！");
            return null;

        }
    }*/
   // 根据因子生成密钥
 /* protected static byte[] produceAESkeybytime(byte[] strKey) {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance( "AES" );
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(strKey);
            _generator.init(128,secureRandom);
            SecretKey s=   _generator.generateKey();
            return s.getEncoded();
        }  catch (Exception e) {
            throw new RuntimeException(" 初始化密钥出现异常 ");
        }
    }*/


    // 根据strKey生成固定密钥
    protected static byte[] produceAESkeybytime(byte[] strKey) {
        try {
            // 从原始密匙数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(strKey);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(dks);
            return securekey.getEncoded();
        }  catch (Exception e) {
            throw new RuntimeException(" 初始化密钥出现异常 ");
        }
    }
}
