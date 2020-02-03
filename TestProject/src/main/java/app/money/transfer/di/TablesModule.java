package app.money.transfer.di;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import app.money.transfer.models.Account;
import app.money.transfer.models.Transaction;

public class TablesModule extends PrivateModule {
	
	static final String TRANSACTION_TABLE = "transactionTable";
	static final String ACCOUNT_TABLE = "accountTable";
	
	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	@Exposed
	@Named(ACCOUNT_TABLE)
	Map<Long, Account> getAccountTable() {
		Map<Long, Account> table = new HashMap<>();
		table.put(1L, new Account(1L, 2000));
		table.put(2L, new Account(2L, 1000));
		table.put(3L, new Account(3L, 500));
		return table;
	}
	
	@Provides
	@Singleton
	@Exposed
	@Named(TRANSACTION_TABLE)
	Map<Long, Transaction> getTransactionTable() {
		return new HashMap<Long, Transaction>();
	}
}
