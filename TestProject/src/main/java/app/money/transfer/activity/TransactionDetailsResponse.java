package app.money.transfer.activity;

import lombok.Data;

@Data
public class TransactionDetailsResponse {
	private final Long payee;
	private final Long benificiary;
	private final Integer amount;
	private final String status;
}
