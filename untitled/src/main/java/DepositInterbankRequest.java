import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

    private Integer amount;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("fee_payer")
    private String feePayer;

    @JsonProperty("fee_amount")
    private Integer feeAmount;

    private String description;

    private String status;
}

