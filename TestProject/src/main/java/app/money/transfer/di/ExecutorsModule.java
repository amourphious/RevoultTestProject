package app.money.transfer.di;

import java.util.concurrent.Executors;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import app.money.transfer.controller.AccountController;
import app.money.transfer.controller.TransactionController;
import app.money.transfer.executor.AccountExecutor;
import app.money.transfer.executor.TransactionExecutor;
import app.money.transfer.executor.TransferExecutor;

public class ExecutorsModule extends PrivateModule {

	@Override
	protected void configure() {
	}
	
	@Provides
	@Singleton
	@Exposed
	TransferExecutor getTransferExecutor(final AccountController accountController,
			final TransactionController transactionController) {
		
		return new TransferExecutor(accountController,
				transactionController,
				Executors.newFixedThreadPool(5)); 
	}
	
	@Provides
	@Singleton
	@Exposed
	TransactionExecutor getTransactionExecutor(final TransactionController transactionController) {
		return new TransactionExecutor(transactionController);
	}
	
	@Provides
	@Singleton
	@Exposed
	AccountExecutor getAccountExecutor(final AccountController accountController) {
		return new AccountExecutor(accountController);
	}
}
