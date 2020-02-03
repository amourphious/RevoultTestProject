package app.money.transfer.di;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import app.money.transfer.activity.Activity;
import app.money.transfer.executor.AccountExecutor;
import app.money.transfer.executor.TransactionExecutor;
import app.money.transfer.executor.TransferExecutor;

public class ActivityModule extends PrivateModule {

	@Override
	protected void configure() {
	}
	
	@Provides
	@Exposed
	@Singleton
	Activity getActivity(final AccountExecutor accountExecutor,
			final TransactionExecutor transactionExecutor,
			final TransferExecutor  transferExecutor) {
		
		return new Activity(transferExecutor, transactionExecutor, accountExecutor);
	}
}
