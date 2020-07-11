package com.dyc.smscontrol.utils;


import com.blankj.utilcode.util.LogUtils;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class EncryptionUtils {


    //公钥加密
    public static String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQiFQ68jaoj9gWgo8JN9bFVaiA\n" +
            "jpfEJ/mCalMpnRYcqwNM0JNpqLpCAY4uoFd2cP8c3yqZbh8e3+nkDHOGnndeaGO/\n" +
            "284uIuLlwNxnYT+/EJdcaB9z8UR2tLSzeH4EXce3VfT8h2GzcoVj1OdT4kPvr9VG\n" +
            "VgpVhgW4YUriyxZWxQIDAQAB";
    //私钥解密
    public  static String privateKeyString = "MIICWgIBAAKBgGxNapCU54dA1dA2OENqrWYhEntUnYp9qGvqT39WjTL1Zf2yGFUm\n" +
            "TaNnQ2EeByFmPotI2Ux+pfKnbSxY7hIPSZtkatsi5LCUPklvKnrCb+qrv1bc8tLI\n" +
            "8CjpFmCKstsSUWVf6as5mo3C8y015ey2vevd6C/gfIUCTvEwBk+T9LMhAgMBAAEC\n" +
            "gYBkD6qzZj/7oJVrR4z4DKmkQE4ZEHZ1q99dxSAp4EeYm03d9RuyIC9/FzsTkXC5\n" +
            "FQQYH/hUSnb8GGAvpwJeQS2ZXfjFX0FpgCIVBWVt06n0zkRe5Wre79G0zZtIw6Cb\n" +
            "npRmxWM4rDwpZ90sBrNcLvjdMJJ94iCC8HJ9qFDpOdwgAQJBANL4ODwGs4XrXNKV\n" +
            "ffJ/VcYlTWQBqe0Nuo8IIUY4i//wN5uLJGiuKY0hWpCbK8X2Zugbv6Zrp3BZ/pjw\n" +
            "pLrDw6ECQQCDa0QAhM9p0VNRebiG4eRSS9umtuFt/5WHz0q/I/yQNem/1Bia8Brl\n" +
            "7TQIKyRfA3QcLlAGQWFoxVuZgCdKGr+BAkBxUYbTJz5SlqObMVUfZioqbmrtZr9h\n" +
            "Z1hn75P/5eu9I8iasdhyqeoDtoCw38hZYwrPbeg5eeXMQWqxt8Cj2PdBAkAH44J7\n" +
            "fX1t3rDfrSzUe+7WudL/mO7DSZpSQrE79A8PIQ1dBIBNnKggsTJ0YxKV2YE4x9bp\n" +
            "6TbNsTskZbr2CdEBAkBtnWP+LvesVgZFpNwGJNLuBIbyflEOQTbeqaxzQeMKJ9D/\n" +
            "+A2jnFtpJzouFwLCUSGoUQjnA9Yw+ydX9cqOg6LK";


//    public static void main(String[] args) throws Exception {
//        // TODO Auto-generated method stub
//
//        // 用于封装随机产生的公钥与私钥
//        { // 生成公钥和私钥
//            genKeyPair();
//            // 加密字符串
//            String message = "df723820";
//            System.out.println("随机生成的公钥为:" +publicKey);
//            System.out.println("随机生成的私钥为:" + privateKey);
//            String messageEn = encrypt(message,publicKey);
//            System.out.println(message + "\t加密后的字符串为:" + messageEn);
//            String messageDe = decrypt(messageEn, privateKey);
//            System.out.println("还原后的字符串为:" + messageDe);
//        }
//    }

    /**
     * 生成 公钥  私钥方法
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException { // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 得到公钥
        publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到
        LogUtils.w("dyc","公钥："+publicKeyString);
        LogUtils.w("dyc","私钥："+privateKeyString);
//        keyMap.put(0, publicKeyString);
        // 0表示公钥
//        keyMap.put(1, privateKeyString);
        // 1表示私钥
    }
 
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA", "BC");;
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }
 
    public static String decrypt(String str, String privateKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        // base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA", "BC");;
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
 
}