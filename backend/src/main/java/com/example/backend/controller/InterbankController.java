package com.example.backend.controller;

import com.example.backend.dto.response.RestResponse;
import com.example.backend.dto.response.interbank.*;
import com.example.backend.dto.response.InterbankResponse;
import com.example.backend.helper.HashUtil;
import com.example.backend.helper.JSonUtil;
import com.example.backend.helper.PGPUtil;
import com.example.backend.helper.RSAUtil;
import com.example.backend.service.AccountService;
import com.example.backend.service.KeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interbank/")
public class InterbankController {

    @Autowired
    private AccountService accountService;

    private final KeyService keyService;

    public InterbankController(KeyService keyService) {
        this.keyService = keyService;
    }
    // get all body
//    @GetMapping("/temp-api-to-get-header/")
//    public ResponseEntity<Map<String, String>> getHeader(@RequestBody LinkedHashMap<String, Object> body) throws Exception {
//        // Timestamp without milliseconds
//        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
//
//        // Convert all values to strings to ensure uniformity
//        LinkedHashMap<String, String> payloadMap = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : body.entrySet()) {
//            payloadMap.put(entry.getKey(), String.valueOf(entry.getValue()));
//        }
//
//        // Debug: Print the payload map
//        System.out.println("Payload Map: " + payloadMap);
//
//        // Generate signature
//        String signature = HashUtil.calculateHash(payloadMap, timestamp);
//        // Convert the body to a compact JSON string
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        // Convert body to a map and ensure all values are strings
//        Map<String, Object> originalMap = objectMapper.convertValue(payloadMap, new TypeReference<Map<String, Object>>() {});
//        Map<String, String> stringifiedMap = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
//            stringifiedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
//        }
//
//        // Serialize the stringified map back into a JSON string
//        String jsonBody = objectMapper.writeValueAsString(stringifiedMap);
//        PrivateKey key= keyService.getRSAPrivateKey("WNC");
//        System.out.println("Key: " + key);
//        // Generate RSA signature
//        String RSAKey = RSAUtil.signData(jsonBody, key);
//
//        // Return the response with timestamp and signature
//        Map<String, String> response = new HashMap<>();
//        response.put("timestamp", timestamp);
//        response.put("signature", signature);
//        response.put("RSAKey", RSAKey);
//
//        return new ResponseEntity<>(response, null, HttpStatus.OK);
//    }
    @PostMapping("/temp-api-to-get-pgp-header/")
    public ResponseEntity<Map<String, String>> getPGPHeader(@RequestBody LinkedHashMap<String,
            Object> body) throws Exception {
        // Timestamp without milliseconds
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // Serialize the original body directly into a JSON string
        String jsonBody = JSonUtil.toJson(body);

        // Debug: Print the JSON body
        System.out.println("JSON Body: " + jsonBody);

        // Generate RSA signature
        PGPPrivateKey key = keyService.getPGPPrivateKey("BANK3");
        System.out.println("Key: " + key);
        String RSAKey = PGPUtil.signData(jsonBody, key);

        // Generate hash signature
        String signature = HashUtil.calculateHash(body, timestamp);

        // Return the response with timestamp and signatures
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", timestamp);
        response.put("signature", signature);
        response.put("RSAKey", Base64.getEncoder().encodeToString(RSAKey.getBytes()));

        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }
    @PostMapping("/temp-api-to-get-header/")
    public ResponseEntity<Map<String, String>> getHeader(@RequestBody LinkedHashMap<String, Object> body) throws Exception {
        // Timestamp without milliseconds
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // Serialize the original body directly into a JSON string
        String jsonBody = JSonUtil.toJson(body);

        // Debug: Print the JSON body
        System.out.println("JSON Body: " + jsonBody);

        // Generate RSA signature
        PrivateKey key = keyService.getRSAPrivateKey("WNC");
        System.out.println("Key: " + key);
        String RSAKey = RSAUtil.signData(jsonBody, key);

        // Generate hash signature
        String signature = HashUtil.calculateHash(body, timestamp);

        // Return the response with timestamp and signatures
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", timestamp);
        response.put("signature", signature);
        response.put("RSAKey", RSAKey);

        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }

    @Operation(
            summary = "Get other account information",
            description = "Fetch account information from another bank using the provided account number."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account information retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Account information retrieved successfully.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"firstName\": \"Lam\",\n" +
                                    "    \"lastName\": \"Le\",\n" +
                                    "    \"bankCode\": \"WNC\",\n" +
                                    "    \"accountNumber\": \"1389949681\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Error Example",
                            value = "{\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"error\": \"ACCOUNT_NOT_FOUND\",\n" +
                                    "  \"message\": \"Account not found.\",\n" +
                                    "  \"data\": null\n" +
                                    "}"
                    )
            )
    )
    // Get API for my bank to call the api from other bank to get information
    @PostMapping("/get-account-info/")
    public ResponseEntity<AccountInfoData> getAccountInfo(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Example request body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LinkedHashMap.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\n" +
                                            "  \"account_number\": \"3042934092\"\n" +
                                            "}"
                            )
                    )
            ) LinkedHashMap<String, String> body) throws Exception {
        System.out.println("account_number: " + body.get("account_number"));
        // call localhost:3000/api/shared/user
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:3000/api/shared/user";

        HttpHeaders headers = new HttpHeaders();
        // timestamp not include milliseconds
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        // Header include x-timestamp, x-sign
        headers.set("x-timestamp", timestamp); // Set your headers here

        // payload example {
        //    "account_number": "3042934092"
        //}
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("account_number", body.get("account_number"));
//        payloadMap.put("bank_code", "WNC");

//        System.out.println("Payload: " + payload);
        System.out.println("Timestamp: " + timestamp);

        String signature = HashUtil.calculateHash(payloadMap, timestamp);

        System.out.println("Signature: " + signature);

        headers.set("x-sign", signature); // Set your headers here

        HttpEntity<Map<String,String>> entity = new HttpEntity<>(payloadMap, headers);

        // example of response get successfully
//        {
//            "data": {
//            "firstName": "Lam",
//                    "lastName": "Le",
//                    "bankCode": "WNC",
//                    "accountNumber": "1389949681"
//              },
//            "message": "Get user successfully"
//        }
        ResponseEntity<AccountInfoResponse> response=  restTemplate.exchange(url, HttpMethod.POST,
                entity,
                AccountInfoResponse.class);

        AccountInfoData data = response.getBody().getData();
        System.out.println("Data: " + data);
        return new ResponseEntity<>(data, null, 200);
    }

    @Operation(
            summary = "Query account information",
            description = "Fetch detailed account information using the provided key type and account information request."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account information retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"error\": null,\n" +
                                    "  \"message\": \"Account information retrieved successfully.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"name\": \"John Doe\",\n" +
                                    "    \"bankCode\": \"GROUP2\",\n" +
                                    "    \"accountNumber\": \"3042934092\",\n" +
                                    "  }\n" +
                                    "}"
                    )
            ),
            headers = {
                    @Header(name = "x-timestamp", description = "Timestamp of the response", schema = @Schema(type = "string")),
                    @Header(name = "x-sign", description = "Signature of the response", schema = @Schema(type = "string"))
            }
    )
    // Open API for others bank to call to get account info
    @PostMapping("/{keyType}/account-info")
    public ResponseEntity<UserDetailResponse> queryAccountInfo(
            @PathVariable String keyType,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Example request body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountInfoRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\n" +
                                            "  \"account_number\": \"439030014096\",\n" +
                                            "  \"bank_code\": \"WNC\"\n" +
                                            "}"
                            )
                    )
            ) AccountInfoRequest request,
            @RequestHeader("x-timestamp") String xTimestamp,
            @RequestHeader("x-sign") String xSign) {
        System.out.println("Query Account Info");
        // header include x-timestamp and x-sign
        String timestamp = xTimestamp;
        String signature = xSign;
        // check signature is valid (which is hash based on secret key
        // example of payload
        // {
        //    "account_number": "3042934092",
        //    "bank_code": "WNC"
        //}
        LinkedHashMap<String, String> payloadMap = new LinkedHashMap<>();
        payloadMap.put("account_number", request.getAccountNumber());
        payloadMap.put("bank_code", request.getBankCode());
        if (!HashUtil.checkSignature(timestamp, payloadMap, signature)) {
            throw new SecurityException("Invalid signature");
        }
        return processAccountRequestBasedOnKeytype(keyType, request);
    }

    private ResponseEntity<UserDetailResponse> processAccountRequestBasedOnKeytype(String keyType,
                                                                     AccountInfoRequest request) {
        try {
            String bankCode = request.getBankCode();

            if ("RSA".equalsIgnoreCase(keyType)) {
                // Handle RSA case
                return processAccountRequest(request, bankCode);
            } else if ("PGP".equalsIgnoreCase(keyType)) {
                // Handle PGP case
                return processAccountRequest(request, bankCode);
            } else {
                throw new IllegalArgumentException("Unsupported key type");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing request", e);
        }
    }

    private ResponseEntity<UserDetailResponse> processAccountRequest(AccountInfoRequest request, String bankCode) throws Exception {
//        System.out.println("RSA");
//        PublicKey publicKey = getPublicKey(bankCode);
//        System.out.println("Public key: " + publicKey);
//        PrivateKey privateKey = getPrivateKey(bankCode);
//        System.out.println("Private key: " + privateKey);

        // Simulate fetching account details
        UserDetailResponse responsePayload =
                accountService.getUserDetailByAccountNumber(request.getAccountNumber());
        System.out.println("Response Payload: " + responsePayload);
        return generateAccountResponse(responsePayload);
    }

//    private String getAccountInfoFromDatabase() {
//    }

    // Generate response for RSA
    private ResponseEntity<UserDetailResponse> generateAccountResponse(UserDetailResponse decryptedPayload) throws Exception {

//        // Sign the response payload
//        String signature = RSAUtil.signData(decryptedPayload.toString(), (PrivateKey) privateKey);

        // Construct headers
        HttpHeaders headers = new HttpHeaders();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String hash = HashUtil.calculateHash(decryptedPayload, timestamp);
        headers.add("x-sign", hash);
        headers.add("x-timestamp", timestamp);
//        headers.add("x-signature", signature);

        // Return the response with headers
        return new ResponseEntity<>(decryptedPayload, headers, HttpStatus.OK);
    }

    private String handleBusinessLogic(String action, String decryptedPayload) {
        if ("Query Account Info".equals(action)) {
            // Simulate fetching account details
            return "Account info: {" + decryptedPayload + "}";
        } else if ("Deposit Money".equals(action)) {
            // Simulate deposit action
            return "Deposit successful: {" + decryptedPayload + "}";
        }
        return "Unknown action";
    }

    private PublicKey getPublicKey(String bankCode) throws Exception {
        return keyService.getRSAPublicKey(bankCode);
    }

    private PrivateKey getPrivateKey(String bankCode) throws Exception {
        return keyService.getRSAPrivateKey(bankCode);
    }

    // Example for PGP (implement similarly)
    private PGPPublicKey getPublicPGPKey(String bankCode) throws IOException {
        return keyService.getPGPPublicKey(bankCode);
    }

    private PGPPrivateKey getPrivatePGPKey(String bankCode) throws IOException, PGPException {
        return keyService.getPGPPrivateKey(bankCode);
    }

}
