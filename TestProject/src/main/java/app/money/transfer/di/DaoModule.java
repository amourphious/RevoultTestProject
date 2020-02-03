package app.money.transfer.di;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import app.money.transfer.dao.ConsistentInMemoryDao;
import app.money.transfer.dao.InMemoryDao;
import app.money.transfer.dao.LockedAccountDao;
import app.money.transfer.dao.Dao;
import app.money.transfer.models.Account;
import app.money.transfer.models.LockedAccount;
import app.money.transfer.models.Transaction;

public class DaoModule extends PrivateModule {
	
	static final String TRANSACTION_DAO = "transactionDao";
	static final String LOCK_DAO = "lockDao";
	static final String ACCOUNT_DAO = "accountDao";
	
	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	@Exposed
	@Named(ACCOUNT_DAO)
	Dao<Account, Long> getAccountDao(
			@Named(TablesModule.ACCOUNT_TABLE)
			final Map<Long, Account> accountTable) {
		
		return new ConsistentInMemoryDao<Account, Long>(
				new InMemoryDao<Account, Long>(accountTable));
	}
	
	@Provides
	@Singleton
	@Exposed
	@Named(LOCK_DAO)
	LockedAccountDao getLockDao() {
		return new LockedAccountDao(
				new InMemoryDao<LockedAccount, Long>(new HashMap<Long, LockedAccount>()));
	}
	
	@Provides
	@Singleton
	@Exposed
	@Named(TRANSACTION_DAO)
	Dao<Transaction, Long> getLockDao(
			@Named(TablesModule.TRANSACTION_TABLE)
			final Map<Long, Transaction> transactionTable) {
		
		return new ConsistentInMemoryDao<Transaction, Long>(
				new InMemoryDao<Transaction, Long>(transactionTable));
	}
}
