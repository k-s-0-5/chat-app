package com.webapp.example.config;

import com.webapp.example.Errors.CryptoException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// https://medium.com/@johnvazna/implementing-local-aes-gcm-encryption-and-decryption-in-java-ac1dacaaa409
@Component
public class CryptoUtils {

  private SecretKey sk;

  public CryptoUtils(@Value("${encryption.key}") String key) {
    byte[] keyDecoded = Base64.getDecoder().decode(key);
    this.sk = new SecretKeySpec(keyDecoded, "AES");
  }

  // private SecretKey generateKey() throws NoSuchAlgorithmException {
  //     KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
  //     keygenerator.init(256);
  //     SecretKey sk = keygenerator.generateKey();
  //     byte[] skEncoded = sk.getEncoded();
  //     String skString = Base64.getEncoder().encodeToString(skEncoded);
  //     return keygenerator.generateKey();
  // }

  public String encrypt(String input) {
    try {
      byte[] iv = new byte[12];
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(iv);

      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
      cipher.init(Cipher.ENCRYPT_MODE, sk, gcmSpec);

      byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

      byte[] combinedIvAndCipher = new byte[iv.length + encryptedBytes.length];
      System.arraycopy(iv, 0, combinedIvAndCipher, 0, iv.length);
      System.arraycopy(encryptedBytes, 0, combinedIvAndCipher, iv.length, encryptedBytes.length);

      return Base64.getEncoder().encodeToString(combinedIvAndCipher);

    } catch (Exception e) {
      throw new CryptoException("Failed to encrypt message");
    }
  }

  public String decrypt(String cipherText) {
    try {
      byte[] decodedCipherText = Base64.getDecoder().decode(cipherText);

      byte[] iv = new byte[12];
      System.arraycopy(decodedCipherText, 0, iv, 0, iv.length);
      byte[] encryptedText = new byte[decodedCipherText.length - 12];
      System.arraycopy(decodedCipherText, 12, encryptedText, 0, encryptedText.length);

      GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, sk, gcmSpec);

      byte[] decryptedBytes = cipher.doFinal(encryptedText);

      return new String(decryptedBytes, StandardCharsets.UTF_8);

    } catch (Exception e) {
      throw new CryptoException("Failed to decrypt message");
    }
  }
}
