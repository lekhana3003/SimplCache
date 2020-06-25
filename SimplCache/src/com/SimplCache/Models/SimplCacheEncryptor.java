package com.SimplCache.Models;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public interface SimplCacheEncryptor {
    String encrypt(String message);

    String decrypt(String encryptedValue);
}
