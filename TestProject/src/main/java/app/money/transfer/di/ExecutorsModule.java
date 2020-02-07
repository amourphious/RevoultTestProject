package app.money.transfer.di;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import app.money.transfer.controller.AccountController;
import app.money.transfer.controller.TransactionController;
import app.money.transfer.executor.AccountExecutor;
import app.money.transfer.executor.TransactionExecutor;
import app.money.transfer.executor.TransferExecutor;

public class ExecutorsModule extends PrivateModule {

	public static final String TRANSFER_EXECUTOR = "transferExecutor";

	@Override
	protected void configure() {
	}
	
	@Provides
	@Singleton
	@Exposed
	@Named(TRANSFER_EXECUTOR)
	ExecutorService getExecutor() {
		return Executors.newFixedThreadPool(5);
	}
	
	@Provides
	@Singleton
	@Exposed
	TransferExecutor getTransferExecutor(final AccountController accountController,
			final TransactionController transactionController,
			@Named(TRANSFER_EXECUTOR) final ExecutorService executor) {
		
		return new TransferExecutor(accountController,
				transactionController,
				executor); 
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
