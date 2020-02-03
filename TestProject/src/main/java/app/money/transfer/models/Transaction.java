package app.money.transfer.models;

import lombok.Data;

@Data
public class Transaction implements Model<Long> {
	private final long id;
	private final long fromAccount;
	private final long toAccount;
	private final int amount;
	private final String status;
	
	public Long getKey() {
		return id;
	}
}
