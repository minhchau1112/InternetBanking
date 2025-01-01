package com.example.backend.helper;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.util.Base64;

public class PGPUtil {

    static {
        // Ensure BouncyCastle is registered as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }

    // Encrypt the data using PGP Public Key Encryption
    public static String encrypt(String data, PGPPublicKey publicKey) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOut = new ArmoredOutputStream(byteOut)) {
            PGPEncryptedDataGenerator encryptor = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                            .setWithIntegrityPacket(true)
                            .setSecureRandom(new SecureRandom())
                            .setProvider("BC"));
            encryptor.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));
            try (OutputStream encryptedOut = encryptor.open(armoredOut, new byte[4096])) {
                encryptedOut.write(data.getBytes());
            }
        }
        return byteOut.toString();
    }

    // Decrypt the data using PGP Private Key Decryption
    public static String decrypt(String encryptedData, PGPPrivateKey privateKey) throws Exception {
        InputStream in = new ByteArrayInputStream(encryptedData.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = pgpObjectFactory.nextObject();

        if (!(object instanceof PGPEncryptedDataList)) {
            object = pgpObjectFactory.nextObject();
        }
        PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) object;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = (PGPPublicKeyEncryptedData) encryptedDataList.get(0);

        try (InputStream clearStream = publicKeyEncryptedData.getDataStream(
                new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey))) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            int ch;
            while ((ch = clearStream.read()) >= 0) {
                byteOut.write(ch);
            }
            return byteOut.toString();
        }
    }

    // Sign the data using PGP Private Key
    public static String signData(String data, PGPPrivateKey privateKey) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOut = new ArmoredOutputStream(byteOut)) {
            PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
                    new JcaPGPContentSignerBuilder(privateKey.getPublicKeyPacket().getAlgorithm(), HashAlgorithmTags.SHA256)
                            .setProvider("BC"));
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
            signatureGenerator.update(data.getBytes());
            signatureGenerator.generate().encode(armoredOut);
        }
        return byteOut.toString();
    }

    // Verify the signature of the data using PGP Public Key
    public static boolean verifySignature(String data, String signatureStr, PGPPublicKey publicKey) throws Exception {
        InputStream sigIn = new ByteArrayInputStream(signatureStr.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(sigIn);
        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        PGPSignatureList signatureList = (PGPSignatureList) pgpObjectFactory.nextObject();
        PGPSignature signature = signatureList.get(0);
        signature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);
        signature.update(data.getBytes());
        return signature.verify();
    }

    // Helper method to decode armored data
    private static InputStream getDecoderStream(InputStream in) throws IOException {
        if (in instanceof ArmoredInputStream) {
            return in;
        }
        return new ArmoredInputStream(in);
    }
}