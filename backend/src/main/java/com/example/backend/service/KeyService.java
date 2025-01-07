package com.example.backend.service;

import com.example.backend.model.LinkedBank;
import com.example.backend.repository.LinkedBankRepository;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class KeyService {

    private final LinkedBankRepository linkedBankRepository;

    public KeyService(LinkedBankRepository linkedBankRepository) {
        this.linkedBankRepository = linkedBankRepository;
    }

    public PublicKey getRSAPublicKey(String bankCode) throws Exception {
        LinkedBank bank = linkedBankRepository.findByBankCodeAndType(bankCode,"RSA")
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
        System.out.println(bank.getTheirPublicKey());
        String publicKeyEncoded = bank.getTheirPublicKey().replace("-----BEGIN PUBLIC KEY-----",
                "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "").replace(
                        "\n", ""); // Assuming you store publicKey securely.
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyEncoded);


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    public PrivateKey getRSAPrivateKey(String bankCode) throws Exception {
        LinkedBank bank = linkedBankRepository.findByBankCodeAndType(bankCode,"RSA")
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
        System.out.println(bank.getPrivateKey());
        String privateKeyEncoded = bank.getPrivateKey().replace("-----BEGIN RSA PRIVATE KEY-----",
                "").replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "").replaceAll(
                        "\n",""); //
        // Assuming
        // you store privateKey securely.
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyEncoded);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    public PGPPublicKey getPGPPublicKey(String bankCode) throws IOException {
        // Implement fetching and parsing of PGP public key
        LinkedBank bank = linkedBankRepository.findByBankCodeAndType(bankCode,"PGP")
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
//        String publicKeyEncoded = bank.getPublicKey().replace("-----BEGIN PGP PUBLIC KEY " +
//                                "BLOCK-----",
//                        "").replace("-----END PGP PUBLIC KEY BLOCK-----", "")
//                .replaceAll("\\s", "").replaceAll(
//                        "\n","");
        String publicKeyEncoded = bank.getTheirPublicKey();
        System.out.println("Public key: " + publicKeyEncoded);
        InputStream keyInputStream = new ByteArrayInputStream(publicKeyEncoded.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(keyInputStream);
        PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = objectFactory.nextObject();
        if (object instanceof PGPPublicKeyRing) {
            PGPPublicKeyRing publicKeyRing = (PGPPublicKeyRing) object;
            return publicKeyRing.getPublicKey();
        }
        throw new IllegalArgumentException("Invalid PGP public key string");
    }

    public PGPPrivateKey getPGPPrivateKey(String bankCode) throws IOException, PGPException {
        // Implement fetching and parsing of PGP private key
        LinkedBank bank = linkedBankRepository.findByBankCodeAndType(bankCode,"PGP")
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
        String privateKeyEncoded = bank.getPrivateKey();
        InputStream keyInputStream = new ByteArrayInputStream(privateKeyEncoded.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(keyInputStream);
        PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = objectFactory.nextObject();
        if (object instanceof PGPSecretKeyRing) {
            PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) object;
            PGPSecretKey secretKey = secretKeyRing.getSecretKey();
            return secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider(
                    "BC").build("BaOfBu".toCharArray()));
        }
        throw new IllegalArgumentException("Invalid PGP private key string");
    }
}

