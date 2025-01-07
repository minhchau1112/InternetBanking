package com.example.backend.helper;

import com.example.backend.dto.request.InterbankRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class HashUtil {

    private static final String secretKey = "wnck21";

    public static String calculateHash(Object body, String timestamp) throws Exception {
        // Serialize the stringified map back into a JSON string
        String jsonBody = JSonUtil.toJson(body);
        String data = jsonBody + timestamp + secretKey;

        System.out.println("Data: " + data);

        // Compute SHA-256 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        // Convert hash bytes to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static boolean checkSignature(String timestamp, Object request, String signature) {
        try {
            String hash = calculateHash(request, timestamp);
            System.out.println("Hash: " + hash);
            boolean isVerified = hash.equals(signature);
            System.out.println("Is verified: " + isVerified);
            return isVerified;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
