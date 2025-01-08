import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPKeyRing;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.io.StringReader;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.*;
import java.security.*;
import java.security.interfaces.*;
import java.util.Base64;
import java.util.Iterator;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

public class RSACrypto {
    public static void main(String[] args) throws Exception {
//        // Add BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
//
//        // data should be something like JSON
//        String data = "Account Number: 1234567890, Amount: 1000, Transaction Type: Deposit, Bank Code: 001";
//
//        // Load RSA public and private keys
//        String publicKeyStr = "-----BEGIN PUBLIC KEY-----\n" +
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCDUR4eGiHDQZJru66/cVGMEjXK\n" +
//                "Rf0Dw9lKvWzi/xcWG652DR/EHUBXlVlzfaT2de25JK5q1rFGiTAZpbM6JSIcjv0q\n" +
//                "DGU46EqOkuCoLJR2ExloxOY31O5BPs5xxg6T2YEYodrdp4PxIbVnm88R9wCLJaMh\n" +
//                "2tFeyFjA/gJSFAL4UwIDAQAB\n" +
//                "-----END PUBLIC KEY-----";
//        String privateKeyStr = "-----BEGIN RSA PRIVATE KEY-----\n" +
//                "MIICXAIBAAKBgQCDUR4eGiHDQZJru66/cVGMEjXKRf0Dw9lKvWzi/xcWG652DR/E\n" +
//                "HUBXlVlzfaT2de25JK5q1rFGiTAZpbM6JSIcjv0qDGU46EqOkuCoLJR2ExloxOY3\n" +
//                "1O5BPs5xxg6T2YEYodrdp4PxIbVnm88R9wCLJaMh2tFeyFjA/gJSFAL4UwIDAQAB\n" +
//                "AoGAQMehfeQSR8pvOupJWHPEcL8C/hfsqxDQfshXkNuaPfPF6sKqqwprFUm96PVQ\n" +
//                "jay7axwnVtk2dr9ylRRjCAoNeyfFwo3oe7/awY/LlOb+SAlaJ51BgmpxehhQEAJy\n" +
//                "4anqzu6R1ww42pK1qfG5zktskMaDUyo/Q4CuwIifHNGfNYECQQDFM90CfNAAx+Rs\n" +
//                "r8W5pCWhEzoBT3sIjBBn2wMdoUuZzc+QN+Nt5MFA2QPvprC6czggjRWqjrD7ldMC\n" +
//                "sfs3YgzhAkEAqnhPPhz5dXt2rPO7zYv/HHDyKWTawwJTusKnGIcYWjULY9KqK49D\n" +
//                "zesYsMzDswT8FXHS4QKjKHQuE+UpOwPXswJAQl+EYZP0xm6BGXvHxwXSjHZWLZ68\n" +
//                "wFG0K+BxGgXx1iCJXfcRmylFSjhAtOab0QV6vKn/wtooA2WYEe4pqahcwQJARBzy\n" +
//                "PLVDD6gXUR/KKGUJuAmBGP0NU+H4JqVkMq64EkMjm+0uxVznEL4hDXOjtkIFn2Pt\n" +
//                "iK6PFrjlOFnWUZhFdQJBAIuriIdG5XiZsWoW06z6NO1kTODBNcP6Nh0e9F4dwWo3\n" +
//                "ZEctESkNcca5D7yAMvXI4UicPNdpOog1PCac/+bSaQM=\n" +
//                "-----END RSA PRIVATE KEY-----";
//
//// Convert to RSA PublicKey and PrivateKey
//        PublicKey rsaPublicKey = getPublicKey(publicKeyStr);
//        PrivateKey rsaPrivateKey = getPrivateKey(privateKeyStr);
//
////        // Generate AES key
////        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
////        keyGen.init(128);
////        SecretKey aesKey = keyGen.generateKey();
////
////        // Encrypt data with AES key
////        String encryptedData = encryptDataWithAES(data, aesKey);
////        System.out.println("Encrypted Data: " + encryptedData);
////
////        // Encrypt AES key with RSA public key
////        String encryptedAESKey = encryptAESKeyWithRSA(aesKey, rsaPublicKey);
////        System.out.println("Encrypted AES Key: " + encryptedAESKey);
//
//        String encryptedAESKey =
//                "TDcJah9lzqd33toNNfnp9HtQ7jRlsWdwfbJra1oN6WNmBJrH0hTHLV0X7A0ekOePdak4L2lgcfmgExuqz3d4FsG44OhHWMIlmsIx05MW+GalBViXmYNSEkXDQpPspEEnna01RIlonKMTnTxwNF2Bn4RlwXziAccFfi76KOum4F0=";
//        String encryptedData = "SIUcbd9wpZdzB7TNrvNi+LOmmdm/iV06VwZz87/b5kth31lfnJuz1YxNZ" +
//                "+jln6tJgxDNLcHfuJBR88U8fpoLPfdDqb7AQ+CkIsYDBfr5peC4odkk0Nz90wT4vj57VABfJq7K7+U8trWfjrmluFd+7A==";
//        String signature = "U9iGzMI0AH3sD65R5H6rOxsBAhcC1IUTSyFeq5ia9xb+dpgRHbLgqDlmV5dmsvlkmm3VR35DQ2iZy1Vp3ZRonEqir8b74UJ8M1ib8oARXaW9TyyOSzOOfKgEj2VW5KXDvdRhusXkNnVEcjwPp0JHWmg5+MmT1oR5AvZGc77eiVE=";
//        // Decrypt AES key with RSA private key
//        SecretKey decryptedAESKey = decryptAESKeyWithRSA(encryptedAESKey, rsaPrivateKey);
//
//        // Decrypt data with AES key
//        String decryptedData = decryptDataWithAES(encryptedData, decryptedAESKey);
//        System.out.println("Decrypted Data: " + decryptedData);
//
//        boolean isVerified = verifySignature(decryptedData, signature, rsaPublicKey);
//        System.out.println("Is Verified: " + isVerified);
//        // when data is lower than 117 bytes
////        String EncryptedData = encryptDataWithRSA(data, rsaPublicKey);
////        System.out.println("Encrypted Data: " + EncryptedData);
////
////        String DecryptedData = decryptDataWithRSA(EncryptedData, rsaPrivateKey);
////        System.out.println("Decrypted Data: " + DecryptedData);
////
////        String signature = signData(data, rsaPrivateKey);
////        System.out.println("Signature: " + signature);
////
////        boolean isVerified = verifySignature(DecryptedData, signature, rsaPublicKey);
////        System.out.println("Is Verified: " + isVerified);
//
        String publicKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: Keybase OpenPGP v1.0.0\n" +
                "Comment: https://keybase.io/crypto\n" +
                "\n" +
                "xsBNBGd060kBCADhV9z3UfTkw7I9PKsOSrCZM1I/v+CBCNxb1tDcSZlRF6jTpUnl\n" +
                "ySzozjOVT0Te7YQ6AX7LRUORQ+Yp7UTKl8gFNjqKxMTKINjt8wmXDVOf4mxspIIj\n" +
                "YFEz/Rm9UhW7oWlcAGF8Tz2KojenISUv0bquP/AEddzgzJ4w751D+ai/diaEmzCf\n" +
                "nSAdFk0NNJ3jHieHoJRMgGhN0zBBGjX7wn0wwr2uTfUELugl70l3B1oyLUwBIe1Z\n" +
                "q5wQsLzC5KpoVJQAXbMl/WkTwLXBNdN9Cchp3tiXYuWgfg2KRabecxvT+zqqNRv4\n" +
                "mymM7+j5h2+3sfeq1ahqluQ4JWEZDnqBrZhdABEBAAHNKlF14buRYyBLaMOhbmgg\n" +
                "PHF1b2NraGFuaDIwMDMucWtAZ21haWwuY29tPsLAbQQTAQoAFwUCZ3TrSQIbLwML\n" +
                "CQcDFQoIAh4BAheAAAoJEHOhZwAK6pVCQtgH/1gpWWK0dHWYifboYJNfcQItzAzJ\n" +
                "OGXAlZSyICJglsYUAfZ9me2I3hyad4h/SwZwLdzmTBVHIqv29Zd6ehI4e9DmMj1V\n" +
                "HwW/SmkyNx/aNqKGLvOS0vMb5DljFfNSLkqAmmUoaZN2VxFY3Xg0gXxHFp7uesc/\n" +
                "3w1BzjfrIABHvhz0+VRDfZZlZRhpCMpZSXVKread/5hruB/WbvlcnMwMbdgXXw/u\n" +
                "I8cTCO/rIeDT5U5W2lOIm04fOQvR1O6kr3GjKcJUhrYSFcagdb3ENh+HgHWH84Pc\n" +
                "Mx9HmKjGdOUuedcLJhA2xaseVqIWKykEEjj6kocnsyLS41rcbU394qGiCkDOwE0E\n" +
                "Z3TrSQEIALoyViCQs014AcRRpZnw717J7aepqKf0QmDsfEnUfjV4n2c3nQM9vpti\n" +
                "sb22eSzGTk3Bt9wR/sP2mTwEjKGq8iIBwL601Te1Qzwiml5qhe5W16sz3lUrsI7R\n" +
                "iV1ZKLpPWaLI3LrPyvsXXznmZX/oQdMg/QysQJpUhqWnOPGweMR1p7b73Ay0mwj3\n" +
                "E7Yp7K2Uyz85Jk4ailPW8xkmYgyPmwiLfAFQXQPEDrsDXpVyW/OjzAjeQSvXE4dK\n" +
                "GHEg5ncJp5d3V9XMPHfXpTVp6AYza22shjLqROmNOI75MGzWkuxeTQSp6+w5QFyW\n" +
                "PccjwFCAiuDtoIKDkMIox0Mx6LHCAlUAEQEAAcLBhAQYAQoADwUCZ3TrSQUJDwmc\n" +
                "AAIbLgEpCRBzoWcACuqVQsBdIAQZAQoABgUCZ3TrSQAKCRAjC5LhbO/bXbIBCACi\n" +
                "SkENoLxwUTbxsoeL62RXep6r5Nnq1YDbT5MyO9iGvZAM3rajzSDI6/bNYI+vvdrD\n" +
                "z8LcUgAiT1hjR+dVXYbicZF/J8HVF8LcLYrzMfS7yyVGbC/Ol1crKHMPG2vUayu4\n" +
                "AozF8J2oKobkX6o8dWJzSZQKS6lx4TB4GsXCkn0gvR6GNOuZpbMObeAezkfnGQc1\n" +
                "eM14aIgx5nb4GkCVHS364fq7oCApma4h2JrGiP0OHhbniTa9mSNkpv0fzN64qnwe\n" +
                "GAOEGGSkTu0NUmZlhCTB99cas7dO83FUvS5Sap8GDvFzYN1V6QBiFtHLAHvwOHuu\n" +
                "NoIq529pjqS5dAbiHXgIC9YIAJmBzhiIQjLZylvwPsnAyzviTFX6EaU8i6OF1rEX\n" +
                "tCK35h2E/YMkTk4b8YzHS0fKFfU7xItJ9NWjZvWo0SfGgj3+L6vfZcfJLmKyyCje\n" +
                "0iEgnZi48LshMndIVW0BUOdZ0afn3mkiWZPoSbcuf/OHWBUFse7DEgK+GHltw+zA\n" +
                "jUpPoMtq/xco8k3ezWmLGsT9pDNS2T34eNefpf7cBB1yw0LtHK/CTtPAXcmF172p\n" +
                "s+/eexwnWHeCy5F5dXkTELMnIDzg5IbBr4K8uS7XKFNndRtMiEfO7L3Nf+PGrJva\n" +
                "xN+cEHMs7Fy6UBNHfTb9Ucy6dIHbGSqg2urOzFKPPJGG8vvOwE0EZ3TrSQEIAO5X\n" +
                "rjm6HFXj72MVILMHtuxUqM0z1EnipGUaaJD3Ty7wuJdbdPj2ra931SGvq5NCE2A5\n" +
                "c8RWyu9CLJR7HxV7YvydXdBAh8oy4ik1SIUtT5rBsDxBcv4Xsb4EJI3ryVllr863\n" +
                "qNYgShN5rZuLCjK493jgKULDP17vJYoZB5GMFRO0Ga3817ahJvsJoypmrOAgoI3+\n" +
                "v2ER7E/X+AKs8vmjtULb4bEzyB741ce/dm8YoVPIW/7KEBQ8RVhkMfUGad7kYS1j\n" +
                "G7ANls3eGNexJssiMw0VIRodxdmNgDAvktim790lrUBD2iQzCs2XbnyEvJ8Y5k++\n" +
                "ixNduI4gHnQ5DkiHI3kAEQEAAcLBhAQYAQoADwUCZ3TrSQUJDwmcAAIbLgEpCRBz\n" +
                "oWcACuqVQsBdIAQZAQoABgUCZ3TrSQAKCRB5PwbVQAHesfnBB/9JLB2OZ4yBSiyb\n" +
                "jbofPlKw0BvMwxY7JUBQaIjHzJIJgZzxMtttM4Tz7j/NJdinUiqcho+ZHp7uAujw\n" +
                "5THpeI5f0Hda351zXPqvsUMcTWXOMR7JKAEiRoG/m2DoYjlYWXaxg8+PFGrTIUgy\n" +
                "MgpMXFubJTe27wDcn3fF3RriBBXRyP2sxekh1C94RWUH9Nn0TtlKJdlZM5xcTOH5\n" +
                "3qdHkVnzgcE/U5mI6Ol/KfTk1In5BOVNqyq7GJ3B0kxM+vWB+WWz5Fz3KCa2QjpU\n" +
                "t7E6Y7T+sn/kguiSEoBCsHeFYtbx8P/VKGbXkeVdZBrN2WW45HtP2fTeay/nWCB+\n" +
                "la6CPt+JjKsIAKsGWnJBdLb52lDCL7z6rq9o4HuOgmf1LR+nCj42o0swgaL/TCjI\n" +
                "wkx5LD0XCxoFbHknL2yY4umBq/h8enyd0luAyvSA1t9pv1N74UvLHqz0qdSwmEoX\n" +
                "7NBJNccT9PXe7RcyqObl7ySGr5Rzn7zOCAWc8pMuJPE5yyLIFAjpukVxSxszzdB+\n" +
                "A8viBJ49sAMM6SPT0aPjB+d13KLN3F6iVlxH0euiB4wS+AvDxEybrH+T/pjpAX7r\n" +
                "R2c9HYl3w+4C6RWDkKfPZVrZ4lO97V9P9HW6XhAwPlTG4ywaiaohJM5AsJLQZb+R\n" +
                "5duikGYTeV4j8dmQILYgHLMXXyKygtEAyDg=\n" +
                "=E10s\n" +
                "-----END PGP PUBLIC KEY BLOCK-----\n";
        String privateKey = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                "Version: Keybase OpenPGP v1.0.0\n" +
                "Comment: https://keybase.io/crypto\n" +
                "\n" +
                "xcMFBGd060kBCADhV9z3UfTkw7I9PKsOSrCZM1I/v+CBCNxb1tDcSZlRF6jTpUnl\n" +
                "ySzozjOVT0Te7YQ6AX7LRUORQ+Yp7UTKl8gFNjqKxMTKINjt8wmXDVOf4mxspIIj\n" +
                "YFEz/Rm9UhW7oWlcAGF8Tz2KojenISUv0bquP/AEddzgzJ4w751D+ai/diaEmzCf\n" +
                "nSAdFk0NNJ3jHieHoJRMgGhN0zBBGjX7wn0wwr2uTfUELugl70l3B1oyLUwBIe1Z\n" +
                "q5wQsLzC5KpoVJQAXbMl/WkTwLXBNdN9Cchp3tiXYuWgfg2KRabecxvT+zqqNRv4\n" +
                "mymM7+j5h2+3sfeq1ahqluQ4JWEZDnqBrZhdABEBAAH+CQMI7FJBawyrkwRgevD2\n" +
                "4Xhh5QCgHqQmXV2oNszsWHrVQ6eUixPelMErcS/gLmdt8cpfnurpya9mOMqAjn+6\n" +
                "maa0BkCsDwa9JLY9ru8AxoUArMKV82Y0X33kNnT/0GqSzIoXMmkRw8F5llZw1JAS\n" +
                "4SdWFjNN2EVnXgWtPAeGaymOqm2ICIjq202daEYNWCPl+A+w9LpCrzGPB+h1m9Vi\n" +
                "yBW9lMUqc+SadgeGtcinqBMjO9beN1DcVS2GEmsKc051fJWcB/29eM/nTt8Y6aKw\n" +
                "kQhxhIOZVaG7YE+v02lD8at3gHX0e9pr8LDACnwhacarsWjZBpWq2TxeRoFKb8pd\n" +
                "JJlIyQ/t0Ld0sx8ssOVq2DUTv3T3oQuQV9/4lIAFt/ywDGAFTHlxjJ3x9SKeRHnG\n" +
                "ZdAKUS7PD4WTFOji7tY/KPyovooTypBxjNxLzRQskjLrpPl9XlBbPm51RvGXpMmT\n" +
                "whYy/7UrS3pyIWUIR1ValqKe2yLoeX6+Jfx/1nU2qkEhrQAacMESpQ8Srj9eQqcM\n" +
                "U5QcWCmKWFhfriGhA6L5YXyf2mFLvCaTT07jFsMDUCabG265EkN0gSF3f+gcTsuN\n" +
                "ZmB6uwQUJmtO4uraftds+0SZv9uDbkBlGj5PFbMNG8Rzinei6Ju1f5hegJuikanN\n" +
                "a9+qwRw8HEFAc6n5if3g0f4X4fRk9iykAuKvSMFpKM8mrYgDzhsPIdZh85LDMZOc\n" +
                "vfEWFIbvczl6G0xxWRcCfwBBjAL9nK8n2wYMww6N2sFZs/EhKwyFII9GGsCSvwJz\n" +
                "vLqzGcR2E58x4eEfYvx93Ga7b3BDEPlIr10hW+2I6T79p6g15zxT0T0Jxvp1wx3g\n" +
                "HnxsTVieJTq0oXdEjEDVkOQakElFpi9ijCn35ni1xeh0yiMi9m4IIXp4fPElvq16\n" +
                "pn4WKnedPcjNKlF14buRYyBLaMOhbmggPHF1b2NraGFuaDIwMDMucWtAZ21haWwu\n" +
                "Y29tPsLAbQQTAQoAFwUCZ3TrSQIbLwMLCQcDFQoIAh4BAheAAAoJEHOhZwAK6pVC\n" +
                "QtgH/1gpWWK0dHWYifboYJNfcQItzAzJOGXAlZSyICJglsYUAfZ9me2I3hyad4h/\n" +
                "SwZwLdzmTBVHIqv29Zd6ehI4e9DmMj1VHwW/SmkyNx/aNqKGLvOS0vMb5DljFfNS\n" +
                "LkqAmmUoaZN2VxFY3Xg0gXxHFp7uesc/3w1BzjfrIABHvhz0+VRDfZZlZRhpCMpZ\n" +
                "SXVKread/5hruB/WbvlcnMwMbdgXXw/uI8cTCO/rIeDT5U5W2lOIm04fOQvR1O6k\n" +
                "r3GjKcJUhrYSFcagdb3ENh+HgHWH84PcMx9HmKjGdOUuedcLJhA2xaseVqIWKykE\n" +
                "Ejj6kocnsyLS41rcbU394qGiCkDHwwYEZ3TrSQEIALoyViCQs014AcRRpZnw717J\n" +
                "7aepqKf0QmDsfEnUfjV4n2c3nQM9vptisb22eSzGTk3Bt9wR/sP2mTwEjKGq8iIB\n" +
                "wL601Te1Qzwiml5qhe5W16sz3lUrsI7RiV1ZKLpPWaLI3LrPyvsXXznmZX/oQdMg\n" +
                "/QysQJpUhqWnOPGweMR1p7b73Ay0mwj3E7Yp7K2Uyz85Jk4ailPW8xkmYgyPmwiL\n" +
                "fAFQXQPEDrsDXpVyW/OjzAjeQSvXE4dKGHEg5ncJp5d3V9XMPHfXpTVp6AYza22s\n" +
                "hjLqROmNOI75MGzWkuxeTQSp6+w5QFyWPccjwFCAiuDtoIKDkMIox0Mx6LHCAlUA\n" +
                "EQEAAf4JAwjYYGxTr9thQmDRh2Ktb1he+vKg5O1cwwWhi0h84JGDBLrT96rgtI0+\n" +
                "9fchPC6X9iZaPIEQnTrgH+bVjEx0H5Fg+IyuYRvwd3Y9xhMXz4VfRzyFAXCqIzqi\n" +
                "GuYLafExU1azivBbUHQZluNyhkEC9XlMY7tULGq4xNy6ipTbap+Bf6se7u0Fqp+x\n" +
                "uOdUnFybGl1rFgEiKiDtveUmZySwcTE8CAKCc/n6xgKPN6MmjTnXwsw1cPtoaSaW\n" +
                "yVli5kidFpuHuY5/tCqF1LXnCAc82ZXvIcNF1WPlhDzEk8y5tNTjvy5KsHbPbhUJ\n" +
                "VNclKt1ftW6oWHeAgcRBJDGEWXTCjZs4Op8GJw37kK7rPKPnbZ7KsMWZyzwaOjJo\n" +
                "3B+j7kDraRPPTRzN+S5uzJE7zX/p42AJEdPmfnyGE1rn2loGUbCPhbrzgb/D1ViY\n" +
                "UFq2Qf8Jlf8+XZjj9Hy5ecv3cKg2RTQ8IHYoYftRLMN6FvlUZD1J5PbrY/v7YWzn\n" +
                "D6OsbfBudcC5d4zLSWZOU+Vy2I5NpaU/lYIdjrjbQ1FeSjIQSrWfbiR3ZGuMFYj9\n" +
                "R+oEBD5wzvEmBz2WEQyYRJpYytK5Rb1cnTr2mZa4x7m1sDfR4SA3aXBDKAulJQSw\n" +
                "brEzrjepz66s/7FQ1AvW2+dFz8Xurbd+GtnmjvBkzTm43zI0IbNMVhXC0qmu60zc\n" +
                "I2AHFmIrgGLq8HpHveBVxH67To2SYcvT2QJYpHalh0HNpd5c+AjNB+Op6bVWetz8\n" +
                "dz0dlYBK/FA1IENRU54zwy8qVLVSyC5RX8BSEXNvwP5V23D9kKwnc98vHhMujG0v\n" +
                "Xu38ozkVGGY3CojJ6xWp3BJHNtMb9EpGbbHEqti1mzGfCnoseRUl8MLUDQbM+nrB\n" +
                "RDGqHll0rlqe8jNP/uNAmRmuZMmS3EhoM7lyvRLCwYQEGAEKAA8FAmd060kFCQ8J\n" +
                "nAACGy4BKQkQc6FnAArqlULAXSAEGQEKAAYFAmd060kACgkQIwuS4Wzv212yAQgA\n" +
                "okpBDaC8cFE28bKHi+tkV3qeq+TZ6tWA20+TMjvYhr2QDN62o80gyOv2zWCPr73a\n" +
                "w8/C3FIAIk9YY0fnVV2G4nGRfyfB1RfC3C2K8zH0u8slRmwvzpdXKyhzDxtr1Gsr\n" +
                "uAKMxfCdqCqG5F+qPHVic0mUCkupceEweBrFwpJ9IL0ehjTrmaWzDm3gHs5H5xkH\n" +
                "NXjNeGiIMeZ2+BpAlR0t+uH6u6AgKZmuIdiaxoj9Dh4W54k2vZkjZKb9H8zeuKp8\n" +
                "HhgDhBhkpE7tDVJmZYQkwffXGrO3TvNxVL0uUmqfBg7xc2DdVekAYhbRywB78Dh7\n" +
                "rjaCKudvaY6kuXQG4h14CAvWCACZgc4YiEIy2cpb8D7JwMs74kxV+hGlPIujhdax\n" +
                "F7Qit+YdhP2DJE5OG/GMx0tHyhX1O8SLSfTVo2b1qNEnxoI9/i+r32XHyS5issgo\n" +
                "3tIhIJ2YuPC7ITJ3SFVtAVDnWdGn595pIlmT6Em3Ln/zh1gVBbHuwxICvhh5bcPs\n" +
                "wI1KT6DLav8XKPJN3s1pixrE/aQzUtk9+HjXn6X+3AQdcsNC7Ryvwk7TwF3Jhde9\n" +
                "qbPv3nscJ1h3gsuReXV5ExCzJyA84OSGwa+CvLku1yhTZ3UbTIhHzuy9zX/jxqyb\n" +
                "2sTfnBBzLOxculATR302/VHMunSB2xkqoNrqzsxSjzyRhvL7x8MGBGd060kBCADu\n" +
                "V645uhxV4+9jFSCzB7bsVKjNM9RJ4qRlGmiQ908u8LiXW3T49q2vd9Uhr6uTQhNg\n" +
                "OXPEVsrvQiyUex8Ve2L8nV3QQIfKMuIpNUiFLU+awbA8QXL+F7G+BCSN68lZZa/O\n" +
                "t6jWIEoTea2biwoyuPd44ClCwz9e7yWKGQeRjBUTtBmt/Ne2oSb7CaMqZqzgIKCN\n" +
                "/r9hEexP1/gCrPL5o7VC2+GxM8ge+NXHv3ZvGKFTyFv+yhAUPEVYZDH1Bmne5GEt\n" +
                "YxuwDZbN3hjXsSbLIjMNFSEaHcXZjYAwL5LYpu/dJa1AQ9okMwrNl258hLyfGOZP\n" +
                "vosTXbiOIB50OQ5IhyN5ABEBAAH+CQMIWoHETsQLT8xgXvMYOS+TynrH4VU+ukhg\n" +
                "Za3y+urQb4Fb0rKSYD8zV6VZ+jX5v5GBoAOtlKJgB6QHc5Bw6BRpopcf8N/YNWUz\n" +
                "NIrnGkT1t00nL1u8iW4oiMlyJQUv4UgNQUOM0c57gwKebve0eHTMVO8H7EdNYZS8\n" +
                "yScYnDMAGTuMOj5EyPl8Tj/n7FJDnTyzZjNycjnYFg5fWIqzYbU1KnzjoFunvOvI\n" +
                "DktiCAy0tZM1YgftIPHWj6zadPVAR7CRlFJSkkqJF2fFoYTngt9u9eqpdnc4/UZ1\n" +
                "gzU5UIKJZLgPKRIDpWGA0p0piHyC3xhSNW49Hi6PSBClBl34r1zXBviEeB9IqNAg\n" +
                "uvgRRP2OZ44d6udSGh3nsS7Ggdo5IDd2R+vxj0aH5Es6ehLdep4SHkc5kLGhQuuS\n" +
                "hB+l4A2M+C+HAaTHPb21pp7jKmG0ajIgjcmkk/0R56/QiReLZpmiKBkTQ0mqxL3k\n" +
                "6U37ncFGaxGmOnbRg2dd/Np9HGwQbFKJreTWN4iph8a7vmzIbvy0aGtFm6VQLLkX\n" +
                "18gp4/xcVEzV9tEXt2zXK7abFRscSy8wFZSTbUpCjIJ7veixAr+nJK1HQUH1OmcJ\n" +
                "yV4/Ros6U4urdID608v5slqCmetmieRmmLNDm51eMckPfBJkmWSfl+4bb6Ke1BJG\n" +
                "5O1B7RacdyJTiJe7bLB+m+FDrptwC1rrPi0zAh+zi1SO7a4fud8jNgovAWJtH/mf\n" +
                "gpS+eueIi7L3ilpNoaREr878WhqD3LkBOqSsxijsO+Ic/Fol1WX+nWAk/z6Pe4e0\n" +
                "ORFj+B7Knc0CQnl4S66oQplqRg5l1yDokmWW/CHE6XHUx6btHPTM0az2u9C2Bn+v\n" +
                "Xmpw4K5Qt4Du1Stbd3i1/lyBxzAhIDTwhuV9zN7BmcS4h8dNOF/RnGfBfSmbwsGE\n" +
                "BBgBCgAPBQJndOtJBQkPCZwAAhsuASkJEHOhZwAK6pVCwF0gBBkBCgAGBQJndOtJ\n" +
                "AAoJEHk/BtVAAd6x+cEH/0ksHY5njIFKLJuNuh8+UrDQG8zDFjslQFBoiMfMkgmB\n" +
                "nPEy220zhPPuP80l2KdSKpyGj5kenu4C6PDlMel4jl/Qd1rfnXNc+q+xQxxNZc4x\n" +
                "HskoASJGgb+bYOhiOVhZdrGDz48UatMhSDIyCkxcW5slN7bvANyfd8XdGuIEFdHI\n" +
                "/azF6SHUL3hFZQf02fRO2Uol2VkznFxM4fnep0eRWfOBwT9TmYjo6X8p9OTUifkE\n" +
                "5U2rKrsYncHSTEz69YH5ZbPkXPcoJrZCOlS3sTpjtP6yf+SC6JISgEKwd4Vi1vHw\n" +
                "/9UoZteR5V1kGs3ZZbjke0/Z9N5rL+dYIH6VroI+34mMqwgAqwZackF0tvnaUMIv\n" +
                "vPqur2jge46CZ/UtH6cKPjajSzCBov9MKMjCTHksPRcLGgVseScvbJji6YGr+Hx6\n" +
                "fJ3SW4DK9IDW32m/U3vhS8serPSp1LCYShfs0Ek1xxP09d7tFzKo5uXvJIavlHOf\n" +
                "vM4IBZzyky4k8TnLIsgUCOm6RXFLGzPN0H4Dy+IEnj2wAwzpI9PRo+MH53Xcos3c\n" +
                "XqJWXEfR66IHjBL4C8PETJusf5P+mOkBfutHZz0diXfD7gLpFYOQp89lWtniU73t\n" +
                "X0/0dbpeEDA+VMbjLBqJqiEkzkCwktBlv5Hl26KQZhN5XiPx2ZAgtiAcsxdfIrKC\n" +
                "0QDIOA==\n" +
                "=EbT9\n" +
                "-----END PGP PRIVATE KEY BLOCK-----\n";
        String passphrase = "BaOfBu";
        String json = "{\n" +
                "  \"sender_account_number\": \"1234567890\",\n" +
                "  \"sender_bank_code\": \"BANK3\",\n" +
                "  \"recipient_account_number\": \"439030014096\",\n" +
                "  \"amount\": 1000000,\n" +
                "  \"transaction_type\": \"interbank\",\n" +
                "  \"fee_payer\": \"RECEIVER\",\n" +
                "  \"fee_amount\": 10000,\n" +
                "  \"description\": \"\",\n" +
                "  \"status\": \"pending\"\n" +
                "}";
        String encodedSignature = Base64.getEncoder().encodeToString(json.getBytes());
        System.out.println("Encoded Signature: " + encodedSignature);
        // decode signature
        byte[] decodedSignature = Base64.getDecoder().decode(encodedSignature);
        String signature = new String(decodedSignature);
        System.out.println("Signature: " + signature);

        ObjectMapper objectMapper = new ObjectMapper();
        DepositInterbankRequest request = objectMapper.readValue(json, DepositInterbankRequest.class);

        System.out.println("Sender Account Number: " + request.getSenderAccountNumber());
        System.out.println("Sender Bank Code: " + request.getSenderBankCode());
        System.out.println("Recipient Account Number: " + request.getRecipientAccountNumber());
        System.out.println("Amount: " + request.getAmount());
        System.out.println("Transaction Type: " + request.getTransactionType());
        System.out.println("Fee Payer: " + request.getFeePayer());
        System.out.println("Fee Amount: " + request.getFeeAmount());
        System.out.println("Description: " + request.getDescription());
        System.out.println("Status: " + request.getStatus());

        String message = toJson(request);

        PGPPublicKey pgpPublicKey = getPGPPublicKeyFromString(publicKey);
        PGPPrivateKey pgpPrivateKey = getPGPPrivateKeyFromString(privateKey, passphrase);

        String PGPSignature = signDataWithPGP(message, pgpPrivateKey);
        System.out.println("PGP Signature: " + PGPSignature);

        String encrypted = encryptDataWithPGP(message, pgpPublicKey);
        System.out.println("Encrypted: " + encrypted);

        // decrypt response
//        String encrypted = "-----BEGIN PGP MESSAGE-----\\r\\nVersion: BCPG v1.70\\r\\n\\r\\nhQEMA3OhZwAK6pVCAQf/aEsDg5rvW5LuIcnwkilQilh2PKC5YVg5+oTmXY008I3v\\r\\n5mZqaGMvS4jGumYnw7qAZTVfxNxDFhN551g7j1U8RISMNWxtFH3cuxPa0f/DadCI\\r\\nz1e5NTxnqWMIQ8v7IbN+DbVOExQAGKVyFDq6UroeT+O7oJSi6WVllDUEzRCHWSFI\\r\\nH7gcRFDV0/6YD+iTVRaG3sBjYKNW3goIhVSih8D74XgNqjWBBodN1rW1O/JTE2x7\\r\\nNfeFCYmZXXwY2JwUt7mPt0tJlGpz/XDj1ZswOojqJT3CTjKzyUF/YRIpg+udQO8I\\r\\nm93/+lEey4sy6sOvzDXVUISo7qb5b7BN03OFdO64A9LAqAGlxuL8eL7ullaosQki\\r\\nYXEBnpzcT6r2MCjYvJ0xAW+Mvp3XypfbQc5D9dtDjCLWgOj6ZNmzyoWqfNjBcmjv\\r\\ndMO9SKxG9JF+wV1hRhxe6CjRHEIQDLoQ79WcPTOpurd9ec3lrCeVclvv8/+Ykux1\\r\\n56ifxLhH7z8sXTcsqiKeZBccCsMgbvSewxH9PHgoSPCOoy0SjNFXjMrQpsAI1aME\\r\\n+AMEzG4aQGAvH9PyHAMBy+y+e2fICbu20Kv3dAUHoGnReL3QxkJfyafMLZbE9DKO\\r\\nO5thZLQTfW0d4dcM9XkoBzmVM2HXX/t0yufV0T4VlBGuYxXM4VZi10ftvrvFeenk\\r\\nruXwRtcgW8n5MOjAwKYTalTNXy3eNZasfdyr+Nm5AT+KI3cRZPOjuOsq7u9ZuMM4\\r\\n4BOheQU7pHHhA+i+XFWB35q1aywXdb01Cgh9c4QgEzsG+i64a5QZqSVhn+aZyFC0\\r\\n/p2+juvIuIkE1Q==\\r\\n=QJww\\r\\n-----END PGP MESSAGE-----\\r\\n";
//        String PGPSignature = "-----BEGIN PGP SIGNATURE-----\\r\\nVersion: BCPG v1" +
//                ".70\\r\\n\\r\\niQEcBAABCAAGBQJndkddAAoJEHOhZwAK6pVCqLcIAMYl47O4YDdCsHWI2IkvrNvh\\r\\naFjEnB8CxOL2vgG8oPwKzThjTP5m+YWtoVcX3lgmAfPubUWmQkf/xQYNgGVqGN4x\\r\\nfR8uZWbvsvw9/feky2aA1AmXbxWJGuxCDeuyJR2Ephg3lHwHG+4mqsn142t1zYiB\\r\\nlSPsPNGyH9SOk39Tc3x6rqQ7tKXeXDaq//SMDwF61XypkV6UtgTA1C8vlskct0H6\\r\\nJAzhi9edEgaMqDM+A4Q/wPOEh5Qrbn01fSJQuXBYKyYziPr/ZLR5QnpxD5j8D6O3\\r\\nzByheEqIAqdIo9fGHfT8v/AbGPX/AaksLNUWaIkbO7bJ59jczSgug5Oa8xuYPBs=\\r\\n=6WX5\\r\\n-----END PGP SIGNATURE-----\\r\\n";

//        encrypted = encrypted.replaceAll("\\\\r\\\\n", "\n");
        String decrypted = decryptDataWithPGP(encrypted, pgpPrivateKey);
        System.out.println("Decrypted: " + decrypted);

        boolean verified = verifyPGPSignature(message, PGPSignature, pgpPublicKey);
        System.out.println("Verified: " + verified);

    }
//    public static void main(String[] args) throws Exception {
//        String secretKey = "wnck21";
//        String timestamp = "1735898712"; // Example timestamp
//        Object body = Map.of("account_number", "3042934092"); // Example body
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonBody = objectMapper.writeValueAsString(body);
//
//        String data = jsonBody + timestamp + secretKey;
//        System.out.println("Data in Java: " + data);
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
//
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : hash) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) hexString.append('0');
//            hexString.append(hex);
//        }
//
//        System.out.println("Hash in Java: " + hexString);
//    }

    private static PublicKey getPublicKey(String key) throws Exception {
        String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }

    private static PrivateKey getPrivateKey(String key) throws Exception {
        String privateKeyPEM = key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    private static String signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    private static boolean verifySignature(String data, String signatureStr, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }

    private static String encryptDataWithRSA(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static String decryptDataWithRSA(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

//    private static PGPKeyRingCollection getPGPKeyRingCollection(String publicKeyStr, String privateKeyStr) throws Exception {
//        // Use a PEMParser to read the public and private keys and create PGP key rings
//        PEMParser pemParser = new PEMParser(new StringReader(publicKeyStr));
//        PGPPublicKeyRing publicKeyRing = (PGPPublicKeyRing) pemParser.readObject();
//
//        pemParser = new PEMParser(new StringReader(privateKeyStr));
//        PGPPrivateKeyRing privateKeyRing = (PGPPrivateKeyRing) pemParser.readObject();
//
//        return new PGPKeyRingCollection(publicKeyRing, privateKeyRing);
//    }

    public static String encryptDataWithPGP(String data, PGPPublicKey publicKey) throws Exception {
        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOut = new ArmoredOutputStream(encryptedOut)) {
            PGPEncryptedDataGenerator encryptor = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                            .setWithIntegrityPacket(true)
                            .setSecureRandom(new SecureRandom())
                            .setProvider("BC"));
            encryptor.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));
            try (OutputStream encryptedStream = encryptor.open(armoredOut, new byte[1024])) {
                encryptedStream.write(data.getBytes());
            }
        }
        return encryptedOut.toString();
    }

    public static String decryptDataWithPGP(String encryptedData, PGPPrivateKey privateKey) throws Exception {
//        InputStream encryptedInputStream = PGPUtil.getDecoderStream(new ByteArrayInputStream(encryptedData.getBytes()));
//        PGPObjectFactory objectFactory = new PGPObjectFactory(encryptedInputStream, new JcaKeyFingerprintCalculator());
//        Object object = objectFactory.nextObject();
//        if (object instanceof PGPEncryptedDataList) {
//            PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) object;
//            PGPPublicKeyEncryptedData encryptedDataObject = (PGPPublicKeyEncryptedData) encryptedDataList.get(0);
//            InputStream decryptedInputStream = encryptedDataObject.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));
//            ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();
//            int ch;
//            while ((ch = decryptedInputStream.read()) >= 0) {
//                decryptedOut.write(ch);
//            }
//            return decryptedOut.toString();
//        }
//        throw new IllegalArgumentException("Invalid encrypted data");
        InputStream in = new ByteArrayInputStream(encryptedData.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = pgpObjectFactory.nextObject();
        System.out.println(object);
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

    public static PGPPrivateKey getPGPPrivateKeyFromString(String privateKeyStr, String passphrase) throws Exception {
        InputStream keyInputStream = new ByteArrayInputStream(privateKeyStr.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(keyInputStream);
        PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = objectFactory.nextObject();
        if (object instanceof PGPSecretKeyRing) {
            PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) object;
            PGPSecretKey secretKey = secretKeyRing.getSecretKey();
            return secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray()));
        }
        throw new IllegalArgumentException("Invalid PGP private key string");
    }

    public static PGPPublicKey getPGPPublicKeyFromString(String publicKeyStr) throws Exception {
        InputStream keyInputStream = new ByteArrayInputStream(publicKeyStr.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(keyInputStream);
        PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = objectFactory.nextObject();
        if (object instanceof PGPPublicKeyRing) {
            PGPPublicKeyRing publicKeyRing = (PGPPublicKeyRing) object;
            return publicKeyRing.getPublicKey();
        }
        throw new IllegalArgumentException("Invalid PGP public key string");
    }

    private static String encryptDataWithAES(String data, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static String decryptDataWithAES(String encryptedData, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

    private static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey rsaPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    private static SecretKey decryptAESKeyWithRSA(String encryptedAESKey, PrivateKey rsaPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decodedKey = Base64.getDecoder().decode(encryptedAESKey);
        byte[] decryptedKey = cipher.doFinal(decodedKey);
        return new SecretKeySpec(decryptedKey, "AES");
    }

    public static String signDataWithPGP(String data, PGPPrivateKey privateKey) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOut = new ArmoredOutputStream(out);

        PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
                new JcaPGPContentSignerBuilder(privateKey.getPublicKeyPacket().getAlgorithm(), PGPUtil.SHA256)
                        .setProvider("BC"));
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);

        signatureGenerator.update(data.getBytes());

        PGPSignature signature = signatureGenerator.generate();
        signature.encode(armoredOut);

        armoredOut.close();
        return out.toString();
    }

    public static boolean verifyPGPSignature(String data, String signatureStr, PGPPublicKey publicKey) throws Exception {
        InputStream in = new ByteArrayInputStream(signatureStr.getBytes());
        InputStream decoderStream = PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = pgpFactory.nextObject();

        if (object instanceof PGPSignatureList) {
            PGPSignatureList signatureList = (PGPSignatureList) object;
            PGPSignature signature = signatureList.get(0);

            signature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);
            signature.update(data.getBytes());

            return signature.verify();
        }
        return false;
    }

    private static String toJson(Object object) throws JsonProcessingException {
        // Convert the body to a compact JSON string without altering value types
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Serialize the original body directly into a JSON string
        String jsonBody = objectMapper.writeValueAsString(object);
        return jsonBody;
    }

}

