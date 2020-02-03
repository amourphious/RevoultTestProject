package app.money.transfer.dao;

import java.util.Map;

import app.money.tansfer.model.DummyModel;

class DummyInMemoryDao extends InMemoryDao <DummyModel, String>{
	public DummyInMemoryDao(Map<String, DummyModel> table) {
		super(table);
	}
	
	
}
