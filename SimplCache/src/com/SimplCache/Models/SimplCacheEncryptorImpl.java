package com.SimplCache.Models;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class SimplCacheEncryptorImpl implements SimplCacheEncryptor {
    private  String code;
    StandardPBEStringEncryptor encryptor;

    public SimplCacheEncryptorImpl() throws Exception {
        this.encryptor = new StandardPBEStringEncryptor();
        this.code=fetchCode();
       if (this.code != null)
        encryptor.setPassword(this.code);
       else
           throw new Exception("Manifest File Error");
    }
private  String fetchCode()
{
    Enumeration resEnum;
    try {
        resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
        while (resEnum.hasMoreElements()) {
            try {
                URL url = (URL)resEnum.nextElement();
                InputStream is = url.openStream();
                if (is != null) {
                    Manifest manifest = new Manifest(is);
                    Attributes mainAttribs = manifest.getMainAttributes();
                    String code = mainAttribs.getValue("Code");
                    if(code != null) {
                        return code;
                    }
                }
            }
            catch (Exception e) {

            }
        }
    } catch (IOException e1) {

    }
return null;
}

    @Override
    public String encrypt(String message) {

       return encryptor.encrypt(message);
    }

    @Override
    public String decrypt(String encryptedValue) {

        return encryptor.decrypt(encryptedValue);
    }
}
