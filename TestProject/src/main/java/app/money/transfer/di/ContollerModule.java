package app.money.transfer.di;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import app.money.transfer.controller.AccountController;
import app.money.transfer.controller.NotificationController;
import app.money.transfer.controller.TransactionController;
import app.money.transfer.dao.Dao;
import app.money.transfer.dao.LockedAccountDao;
import app.money.transfer.models.Account;
import app.money.transfer.models.Transaction;
import app.money.transfer.utils.IdGenerator;

public class ContollerModule extends PrivateModule {

	@Override
	protected void configure() {	
	}
	
	@Provides
	@Singleton
	@Exposed
	AccountController getAccountController(
			@Named(DaoModule.ACCOUNT_DAO)
			final Dao<Account, Long> accountDao,
			@Named(DaoModule.LOCK_DAO)
			final LockedAccountDao lockDao) {
		
		return new AccountController(accountDao, lockDao);
	}
	
	@Provides
	@Singleton
	@Exposed
	TransactionController getTransactionController(
			@Named(DaoModule.TRANSACTION_DAO)
			final Dao<Transaction, Long> transactionDao,
			AccountController accountController,
			NotificationController notificationController,
			IdGenerator idGenerator) {
		
		return new TransactionController(transactionDao,
				accountController,
				notificationController,
				idGenerator);
	}
	
	@Provides
	@Singleton
	@Exposed
	NotificationController getNotificationController(
			@Named(DaoModule.ACCOUNT_DAO)
			final Dao<Account, Long> accountDao,
			@Named(DaoModule.TRANSACTION_DAO)
			final Dao<Transaction, Long> transactionDao) {
		
		return new NotificationController(accountDao, transactionDao);
	}
}
