package rsa;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加解密
 *
 * */
public class MyAES {
    private static String ivKey = "fedcba9876543210";
    /**
     * 加密
     * @param strIn
     * @return
     */
    protected static byte[] aes_encrypt(byte[] strIn,byte[] aeskey) {
        try {
            SecretKeySpec skeySpec = getKey(aeskey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(strIn);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param strIn
     * @return
     */
    protected static byte[] aes_decrypt(byte[] strIn,byte[] aeskey) {
        try {
            SecretKeySpec skeySpec = getKey(aeskey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(strIn);
            return original;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取适配密钥
     * @param strKey
     * @return
     * @throws Exception
     */
    protected static SecretKeySpec getKey(byte[] arrBTmp) throws Exception {
        //byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }


}