package com.example.backend.controller;



import com.example.backend.dto.request.InterbankRequest;
import com.example.backend.dto.request.OtpVerificationRequest;
import com.example.backend.dto.response.interbank.*;
import com.example.backend.dto.response.InterbankResponse;
import com.example.backend.helper.HashUtil;
import com.example.backend.helper.JSonUtil;
import com.example.backend.helper.PGPUtil;
import com.example.backend.helper.RSAUtil;
import com.example.backend.model.InterbankTransaction;
import com.example.backend.service.InterbankService;
import com.example.backend.service.KeyService;
import com.example.backend.service.TransactionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class InterbankTransferController {

    @Autowired
    private InterbankService interbankService;

    @Autowired
    private TransactionService transactionService;

    private final KeyService keyService;

    public InterbankTransferController(KeyService keyService) {
        this.keyService = keyService;
    }

    @PostMapping("/create")
    public ResponseEntity<InterbankResponse> createInterbankTransaction(@RequestBody DepositInterbankRequest request) {
        request.setSenderBankCode("WNC");
        InterbankTransaction transaction = interbankService.createTransaction(request);
        InterbankResponse response = new InterbankResponse();
        response.setPayload(String.valueOf(transaction.getId()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest otpRequest) {
        boolean isVerified = interbankService.verifyOtpAndCompleteTransaction(otpRequest); // Verify OTP

        if (isVerified) {
            return ResponseEntity.ok("Transaction completed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or expired.");
        }
    }

    @Operation(
            summary = "Transfer money",
            description = "Transfer money to another bank using the provided transfer request."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Transfer successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransferInterbankResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"transactionId\": \"123456\",\n" +
                                    "  \"status\": \"SUCCESS\",\n" +
                                    "  \"message\": \"Transaction successful\"\n" +
                                    "}"
                    )
            ),
            headers = {
                    @Header(name = "x-timestamp", description = "Timestamp of the response", schema = @Schema(type = "string")),
                    @Header(name = "x-sign", description = "Signature of the response", schema = @Schema(type = "string")),
                    @Header(name = "x-signature", description = "RSA or PGP signature of the response", schema = @Schema(type = "string"))
            }
    )
    @PostMapping("/transfer")
    public ResponseEntity<Object> depositMoney(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Example request body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DepositInterbankRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\n" +
                                            "  \"sender_account_number\": \"074311598537\",\n" +
                                            "  \"sender_bank_code\": \"GROUP2\",\n" +
                                            "  \"recipient_account_number\": \"1609200303\",\n" +
                                            "  \"amount\": 50,\n" +
                                            "  \"transaction_type\": \"interbank\",\n" +
                                            "  \"fee_payer\": \"sender\",\n" +
                                            "  \"fee_amount\": 2,\n" +
                                            "  \"description\": \"paying for groceries\",\n" +
                                            "  \"status\": \"pending\"\n" +
                                            "}"
                            )
                    )
            ) DepositInterbankRequest request) throws Exception {
        // call localhost:3000/api/shared/transfer
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:3000/api/shared/transfer";

        HttpHeaders headers = new HttpHeaders();
        // timestamp not include milliseconds
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        // Header include x-timestamp, x-sign
        headers.set("x-timestamp", timestamp); // Set your headers here

        // Serialize the stringified map back into a JSON string
        String jsonBody = JSonUtil.toJson(request);
        System.out.println("Timestamp: " + timestamp);

        String signature = HashUtil.calculateHash(request, timestamp);

        System.out.println("Signature: " + signature);

        headers.set("x-sign", signature); // Set your headers here

        PrivateKey privateKey = getPrivateKey("WNC");
        String RSASignature = RSAUtil.signData(jsonBody, privateKey);
        headers.set("x-signature", RSASignature);

        System.out.println("RSA Signature: " + RSASignature);

        HttpEntity<DepositInterbankRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);

        request.setSenderBankCode("WNC");

        return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Transfer money",
            description = "Transfer money to another bank using the provided key type and transfer request."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Transfer successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransferInterbankResponse.class),
                    examples = @ExampleObject(
                            name = "Success Example",
                            value = "{\n" +
                                    "  \"transactionId\": \"123456\",\n" +
                                    "  \"status\": \"SUCCESS\",\n" +
                                    "  \"message\": \"Transaction successful\"\n" +
                                    "}"
                    )
            ),
            headers = {
                    @Header(name = "x-timestamp", description = "Timestamp of the response", schema = @Schema(type = "string")),
                    @Header(name = "x-sign", description = "Signature of the response", schema = @Schema(type = "string")),
                    @Header(name = "x-signature", description = "RSA or PGP signature of the response", schema = @Schema(type = "string"))
            }
    )
    @PostMapping("/{keyType}/transfer")
    public ResponseEntity<TransferInterbankResponse> depositMoney(
            @PathVariable String keyType,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Example request body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DepositInterbankRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\n" +
                                            "  \"account_number\": \"3042934092\",\n" +
                                            "  \"amount\": 1000,\n" +
                                            "  \"currency\": \"USD\",\n" +
                                            "  \"receiver_bank_code\": \"ABC\",\n" +
                                            "  \"receiver_account_number\": \"1234567890\"\n" +
                                            "}"
                            )
                    )
            ) DepositInterbankRequest request,
            @RequestHeader("x-timestamp") String xTimestamp,
            @RequestHeader("x-sign") String xSign,
            @RequestHeader("x-signature") String xSignature) throws Exception {
        System.out.println("Transfer Money");
        System.out.println("Headers: x-timestamp=" + xTimestamp + ", x-sign=" + xSign + ", x-signature=" + xSignature);
        // header include x-timestamp and x-sign
        String timestamp = xTimestamp;
        String signature = xSign;
        // check signature is valid (which is hash based on secret key)
        if (!HashUtil.checkSignature(timestamp, request, signature)) {
            throw new SecurityException("Invalid signature");
        }
        if (!keyType.equals("RSA") && !keyType.equals("PGP")) {
            throw new IllegalArgumentException("Unsupported key type");
        }
        if (keyType.equalsIgnoreCase("RSA")) {
            // verify RSA signature
            String jsonBody = JSonUtil.toJson(request);
            if (!RSAUtil.verifySignature(jsonBody, xSignature, getPublicKey(request.getSenderBankCode()))) {
                throw new SecurityException("Invalid RSA signature");
            }
        } else {
            // verify PGP signature
            String PGPKey = new String(Base64.getDecoder().decode(xSignature));
            String jsonBody = JSonUtil.toJson(request);
            if (!PGPUtil.verifySignature(jsonBody, PGPKey, getPublicPGPKey(request.getSenderBankCode()))) {
                throw new SecurityException("Invalid PGP signature");
            }
        }
        return processTransferRequestBasedOnKeytype(keyType, request);
    }

    private ResponseEntity<TransferInterbankResponse> processTransferRequestBasedOnKeytype(String keyType,
                                                                                          DepositInterbankRequest request) {
        try {
            String bankCode = request.getSenderBankCode();
            System.out.println("Bank code: " + bankCode);
            if ("RSA".equalsIgnoreCase(keyType)) {
                // Handle RSA case
                return processTransferRequest(request, bankCode);
            } else if ("PGP".equalsIgnoreCase(keyType)) {
                // Handle PGP case
                return processPGPTransferRequest(request, bankCode);
            } else {
                throw new IllegalArgumentException("Unsupported key type");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing request", e);
        }
    }

    private ResponseEntity<TransferInterbankResponse> processTransferRequest(DepositInterbankRequest request, String bankCode) throws Exception {
        System.out.println("RSA");
//        PublicKey publicKey = getPublicKey(bankCode);
//        System.out.println("Public key: " + publicKey);
        PrivateKey privateKey = getPrivateKey(bankCode);
//        System.out.println("Private key: " + privateKey);

        InterbankTransaction transaction = interbankService.createTransaction(request);

        // Construct response payload
        TransferInterbankResponse responsePayload = new TransferInterbankResponse();
        responsePayload.setTransactionId(String.valueOf(transaction.getId()));
        responsePayload.setStatus("SUCCESS");
        responsePayload.setMessage("Transaction successful");

        return generateRSATransferResponse(responsePayload, privateKey);
    }

    // PGP-specific processing
    private ResponseEntity<TransferInterbankResponse> processPGPTransferRequest(DepositInterbankRequest request,
                                                                 String bankCode) throws Exception {
        System.out.println("PGP");
        PGPPublicKey pgpPublicKey = getPublicPGPKey(bankCode);
//        System.out.println("Public key: " + pgpPublicKey);
        PGPPrivateKey pgpPrivateKey = getPrivatePGPKey(bankCode);
//        System.out.println("Private key: " + pgpPrivateKey);

        InterbankTransaction transaction = interbankService.createTransaction(request);

        // Construct response payload
        TransferInterbankResponse responsePayload = new TransferInterbankResponse();
        responsePayload.setTransactionId(String.valueOf(transaction.getId()));
        responsePayload.setStatus("SUCCESS");
        responsePayload.setMessage("Transaction successful");

        return generatePGPTransferResponse(responsePayload, pgpPrivateKey);
    }

    // Generate response for RSA
    private ResponseEntity<TransferInterbankResponse> generateRSATransferResponse(TransferInterbankResponse decryptedPayload,
                                                                                  Object privateKey) throws Exception {

        // Sign the response payload
        System.out.println("TransferInterbankResponse about to be encrypted Payload: " + decryptedPayload.toString());
        String signature = RSAUtil.signData(JSonUtil.toJson(decryptedPayload), (PrivateKey) privateKey);

        // Construct headers
        HttpHeaders headers = new HttpHeaders();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String hash = HashUtil.calculateHash(decryptedPayload, timestamp);
        headers.add("x-sign", hash);
        headers.add("x-timestamp", timestamp);
        headers.add("x-signature", signature);

        // Return the response with headers
        return new ResponseEntity<>(decryptedPayload, headers, HttpStatus.OK);
    }

    // Generate response for PGP
    private ResponseEntity<TransferInterbankResponse> generatePGPTransferResponse(TransferInterbankResponse responsePayload,
                                                                  PGPPrivateKey privateKey) throws Exception {

        String signature = PGPUtil.signData(JSonUtil.toJson(responsePayload), privateKey);
        System.out.println("Signature: " + signature);

        // Encode the signature to Base64
        String encodedSignature = Base64.getEncoder().encodeToString(signature.getBytes());

        // Construct headers
        HttpHeaders headers = new HttpHeaders();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String hash = HashUtil.calculateHash(responsePayload, timestamp);
        headers.add("x-sign", hash);
        headers.add("x-timestamp", timestamp);
        headers.add("x-signature", encodedSignature);

        // Return the response with headers
        return new ResponseEntity<>(responsePayload, headers, HttpStatus.OK);
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

