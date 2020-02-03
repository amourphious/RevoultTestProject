package app.money.transfer.activity;

import lombok.Data;

@Data
public class TransferRequest {
	private final Long fromAccount;
	private final Long toAccount;
	private final Integer ammount;
}
