package com.github.nagyesta.lowkeyvault.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This example shows how the default Azure client can be used with Lowkey Vault
 * (as long as the trust store has the self-signed Lowkey Vault certificate).
 */
@Profile({"!docker & !process"})
@Configuration
public class AzureAccessTestExternalStartConfiguration {

}
