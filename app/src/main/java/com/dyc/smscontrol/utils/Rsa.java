/**
 * 作者：pjp
 * 邮箱：vippjp@163.com
 */
package com.dyc.smscontrol.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class Rsa {
     

    //公钥加密
    public static String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoNFvWjbPuyb69khNL9Ah\n" +
            "yZ/QJpwwZImpKCbFc2lkMKaEYgPkTa2od2LgNcQtJvR53Mu4qpNj2bbQq2I5StRw\n" +
            "na1e+O4+j2lVrYrjkYTuQA6Rl4osCHj8ECSQ+oQvQKsoA3siEf01b+zDYeBkz4DW\n" +
            "4I5QhdmGtr2nb7pgQApdqdZN+GRmXS04F82QB/wKf2x0mvNfj94gi1rhqHjav7I1\n" +
            "LcGdkpGb/d7vYg2Hj5pVOv/lFNAQGPt47aBoog5rgMI7N5MPiBT/i2suEeDOOOBH\n" +
            "FCBYRygTvdzIfV3lL0iD2US9HdCyXqKK3wPBNuHEWtRfpZ7UwrBluDNoZcGcrukg\n" +
            "iwIDAQAB";
    //私钥解密
    public  static String priKey = "MIICXQIBAAKBgQDQiFQ68jaoj9gWgo8JN9bFVaiAjpfEJ/mCalMpnRYcqwNM0JNp\n" +
            "qLpCAY4uoFd2cP8c3yqZbh8e3+nkDHOGnndeaGO/284uIuLlwNxnYT+/EJdcaB9z\n" +
            "8UR2tLSzeH4EXce3VfT8h2GzcoVj1OdT4kPvr9VGVgpVhgW4YUriyxZWxQIDAQAB\n" +
            "AoGAIVsdNgyWZ6ISq48YuB3BcfFAscedSRgn1g+R298vsUg9j+TxH36IxJQhHR4y\n" +
            "v1RVylV8J+ywd6zTadIADLF+YEaWc+mmo+wfE13F9aZj/enxb6nuUWJqPsg6Rqby\n" +
            "In5gp9SomNZaYkxTQTCWPd/MkMugZC5YTZe5s1CwTPBkjoECQQDvKgxX1DuP6vgv\n" +
            "WPz7rO3yD+tE2iUtDiFRQKd8o32pes9BzDTgRv3uN8zoub3JiQvRSKvDFWJFqoQ0\n" +
            "mkYujlDhAkEA3zZFKCRNmFdNcmsIm8u1jNPvv8H3cf9bs6xWx0HQatDFqaBtnZlv\n" +
            "fGbaJ/Lqwu4cGuNAnYsjQunLR3CQ32guZQJBAIiqlIcT5j1lXhFgXqBKv2YVprGf\n" +
            "nqLSckOGGK9mlYZlgU3uLUEEEFMyW8uZaFRkFfav+kbuT0vUFtwgVH6CIMECQQDR\n" +
            "2qUUQ2VMd6/Rhb23M8NBXrRF9aedXrYpazq+5Sp8ckGT48eK5wmAzPYHnwOGNvTn\n" +
            "doZ2V6zUKRg71yHtWHZdAkByvOFMw9Oq3jKyXS0sFz07XVsuQ+sGpVVwLl1A5n45\n" +
            "GNu5kD/3UE1EJrFXUEVVWPuvGKR2rCMYRRRAqliQgcyh";
     
    public static void main(String[] args) {
        Rsa rsa = new Rsa();
        String str = "我要加密这段文字。";
        System.out.println("原文:"+"我要加密这段文字。");
        String crypt = rsa.encryptByPrivateKey(str);
        System.out.println("私钥加密密文:"+crypt);
        String result = rsa.decryptByPublicKey(crypt);
        System.out.println("原文:"+result);
         
        System.out.println("---");
         
        str = "我要加密这段文字。";
        System.out.println("原文:"+"我要加密这段文字。");
        crypt = rsa.encryptByPublicKey(str);
        System.out.println("公钥加密密文:"+crypt);
        result = rsa.decryptByPrivateKey(crypt);
        System.out.println("原文:"+result);
         
        System.out.println("---");
         
        str = "我要签名这段文字。";
        System.out.println("原文："+str);
        String str1 = rsa.signByPrivateKey(str);
        System.out.println("签名结果："+str1);
        if(rsa.verifyByPublicKey(str1, str)){
            System.out.println("成功");
        } else {
            System.out.println("失败");
        }
    }
  
//    public Rsa(){
//        priKey = readStringFromFile("java_private.pem");
//        pubKey = readStringFromFile("java_public.pem");
//    }
     
      /**
       * 使用私钥加密
       * @see
       */
      public String encryptByPrivateKey(String data) {
        // 加密
        String str = "";
        try {
            byte[] pribyte = base64decode(priKey.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
            Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c1.init(Cipher.ENCRYPT_MODE, privateKey);
            str = base64encode(c1.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
             
        }
        return str;
      }
       
      /**
       * 使用私钥解密
       * @see
       */
      public String decryptByPrivateKey(String data) {
        // 加密
        String str = "";
        try {
            byte[] pribyte = base64decode(priKey.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
            Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c1.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] temp = c1.doFinal(base64decode(data));
            str = new String(temp);
        } catch (Exception e) {
            e.printStackTrace();
             
        }
        return str;
      }
  
       
      /**
       * 使用公钥加密
       * @see
       */
      public String encryptByPublicKey(String data) {
        // 加密
        String str = "";
        try {
            byte[] pubbyte = base64decode(pubKey.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c1.init(Cipher.ENCRYPT_MODE, rsaPubKey);
            str = base64encode(c1.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
             
        }
        return str;
      }
       
      /**
       * 使用公钥解密
       * @see
       */
      public String decryptByPublicKey(String data) {
        // 加密
        String str = "";
        try {
            byte[] pubbyte = base64decode(pubKey.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c1.init(Cipher.DECRYPT_MODE, rsaPubKey);
            byte[] temp = c1.doFinal(base64decode(data));
            str = new String(temp);
        } catch (Exception e) {
            e.printStackTrace();
             
        }
        return str;
      }
    /**
     * 本方法使用SHA1withRSA签名算法产生签名
     * @param  src 签名的原字符串
     * @return String 签名的返回结果(16进制编码)。当产生签名出错的时候，返回null。
     */
    public String signByPrivateKey(String src) {
        try {
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            byte[] pribyte = base64decode(priKey.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
            sigEng.initSign(privateKey);
            sigEng.update(src.getBytes());
            byte[] signature = sigEng.sign();
            return base64encode(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
  
    /**
     * 使用共钥验证签名
     * @param sign
     * @param src
     * @return
     */
    public boolean verifyByPublicKey(String sign, String src) {
        try {
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            byte[] pubbyte = base64decode(pubKey.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            sigEng.initVerify(rsaPubKey);
            sigEng.update(src.getBytes());
            byte[] sign1 = base64decode(sign);
            return sigEng.verify(sign1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
  
    /**
     *  base64加密
     * @param bstr
     * @return
     */
    @SuppressWarnings("restriction")
    private String base64encode(byte[] bstr) {
        String str = new String( Base64.encode(bstr,Base64.DEFAULT));
//        String str =  new sun.misc.BASE64Encoder().encode(bstr);
        str = str.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
        return str;
    }
  
    /**
     * base64解密
     * @param str
     * @return byte[]
     */
    @SuppressWarnings("restriction")
    private byte[] base64decode(String str) {
        byte[] bt = null;
        //            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        bt = Base64.decode(str,Base64.DEFAULT);
//            bt = decoder.decodeBuffer(str);

        return bt;
    }
  
    /**
     * 从文件中读取所有字符串
     * @param fileName
     * @return  String
     */
    private String readStringFromFile(String fileName){
        StringBuffer str = new StringBuffer();
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            char[] temp = new char[1024];
            while (fr.read(temp) != -1) {
                str.append(temp);
            }
            fr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
  
        }
        return str.toString();
    }
}