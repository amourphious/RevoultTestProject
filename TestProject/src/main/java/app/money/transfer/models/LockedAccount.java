package app.money.transfer.models;

import lombok.Data;

@Data
public class LockedAccount implements Model<Long> {
	private final Long accountId;
	@Override
	public Long getKey() {
		return this.accountId;
	}
	
}
