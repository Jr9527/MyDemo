package rsa;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public  class RSACoder {
    // 非对称加密密钥算法
    public static final String KEY_ALGORITHM="RSA";

    // 公钥
    public static final String PUBLIC_KEY="RSAPublicKey";
    // 私钥
    public static final String PRIVATE_KEY="RSAPrivateKey";

    /**
     *数字签名
     * 签名/验证算法
     * */
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";


    /**
     * RSA密钥长度
     * 默认1024位
     * 密钥长度必须是64的倍数
     * 范围在512~65536位之间
     * */
    private static final int KEY_SIZE=2048;
    /**
     * 私钥解密
     * @param data 待解密数据
     * @param key 私钥
     * @return byte[] 解密数据
     * @throws Exception
     * */
    protected static byte[] decryptByprivateKey(byte[] data,byte[] key) throws Exception{
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(key);
        KeyFactory keyfactory =KeyFactory.getInstance(KEY_ALGORITHM);
        // 生成私钥
        PrivateKey privateKey=keyfactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher =Cipher.getInstance(keyfactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     * @param data 待加密的数据
     * @param key 公钥
     * @return byte[] 加密数据
     * @throws Exception
     * */
    protected static byte[] encryptByPublicKey(byte[] data,byte[] key) throws Exception{
        //取得的公钥
        X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(key);
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey=keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher =Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /*
     * 初始化密钥
     * @return Map 密钥Map
     * */
    protected static Map<String, Object>  initKey(  ) throws Exception{
        //实例化密钥对生成器
        KeyPairGenerator keyPairGen=KeyPairGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE);
        // 生成密钥对
        KeyPair keyPair=keyPairGen.generateKeyPair();
        //公钥
        RSAPublicKey publicKey=(RSAPublicKey)keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey=(RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥字符串
        String publicKeyString = Base64.encode(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.encode(privateKey.getEncoded());

          /*
           try {
           // 将密钥对写入到文件
            FileWriter pubfw = new FileWriter(filePath +File.separator+ "publicKey.keystore");
            FileWriter prifw = new FileWriter(filePath +File.separator+ "privateKey.keystore");
            BufferedWriter pubbw = new BufferedWriter(pubfw);
            BufferedWriter pribw = new BufferedWriter(prifw);
            pubbw.write(publicKeyString);
            pribw.write(privateKeyString);
            pubbw.flush();
            pubbw.close();
            pubfw.close();
            pribw.flush();
            pribw.close();
            prifw.close();
             } catch (Exception e) {
            e.printStackTrace();
        }
            */
            // 封装密钥
            Map<String, Object> keyMap=new HashMap<String,Object>(2);
            keyMap.put(PUBLIC_KEY, publicKeyString);
            keyMap.put(PRIVATE_KEY, privateKeyString);

            return keyMap;

    }

    /**
     * 签名
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return byte[] 数字签名
     * @throws Exception
     * */

    protected static byte[] sign(byte[] data,byte[] privateKey) throws Exception{
        //转换私钥材料
        PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(privateKey);
        // 实例化密钥工厂
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥对象
        PrivateKey priKey=keyFactory.generatePrivate(pkcs8KeySpec);
        // 实例化Signature
        Signature signature=Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initSign(priKey);
        // 更新
        signature.update(data);
        // 签名
        return signature.sign();
    }
    /**
     * 校验
     * @param data 待校验数据
     * @param publicKey 公钥
     * @param sign 数字签名
     * @return boolean 校验成功 返回true 失败返回false
     * @throws Exception
     * */
    protected static boolean verify(byte[] data,byte[] publicKey,byte[] sign) throws Exception{
        // 转换公钥材料
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(publicKey);
        // 实例化密钥工厂
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        // 生成公钥
        PublicKey pubkey=keyFactory.generatePublic(keySpec);
        // 实例化Signature
        Signature signature=Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initVerify(pubkey);
        // 更新
        signature.update(data);
        // 验证
        return signature.verify(sign);
    }
}