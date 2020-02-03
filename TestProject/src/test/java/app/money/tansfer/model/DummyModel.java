package app.money.tansfer.model;

import app.money.transfer.models.Model;
import lombok.Data;

@Data
public class DummyModel implements Model<String> {
	private final String id;
	private final String value;

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return id;
	}

}
