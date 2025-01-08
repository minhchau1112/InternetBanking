package com.example.backend.dto.response.interbank;

import com.example.backend.helper.FloatOrIntegerSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "sender_account_number",
        "sender_bank_code",
        "recipient_account_number",
        "amount",
        "transaction_type",
        "fee_payer",
        "fee_amount",
        "description",
        "status"
})
public class DepositInterbankRequest {
    @JsonProperty("sender_account_number")
    private String senderAccountNumber;

    @JsonProperty("sender_bank_code")
    private String senderBankCode;

    @JsonProperty("recipient_account_number")
    private String recipientAccountNumber;

    @JsonSerialize(using = FloatOrIntegerSerializer.class)
    private Float amount;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("fee_payer")
    private String feePayer;

    @JsonProperty("fee_amount")
    @JsonSerialize(using = FloatOrIntegerSerializer.class)
    private Float feeAmount;

    private String description;

    private String status;
}
