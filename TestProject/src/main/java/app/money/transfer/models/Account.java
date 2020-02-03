package app.money.transfer.models;

import lombok.Data;

@Data
public class Account implements Model<Long> {
	private final Long id;
	private final int amount;
	
	public Long getKey() {
		return id;
	}
}
