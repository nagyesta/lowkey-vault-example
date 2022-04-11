package com.github.nagyesta.lowkeyvault.example;

public interface AzureKeyRepository {

    String decrypt(byte[] cipher);

    byte[] encrypt(String clearText);

}
