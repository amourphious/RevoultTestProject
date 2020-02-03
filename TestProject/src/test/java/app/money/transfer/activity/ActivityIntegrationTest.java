package app.money.transfer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import app.money.transfer.di.ActivityModule;
import app.money.transfer.di.ContollerModule;
import app.money.transfer.di.DaoModule;
import app.money.transfer.di.ExecutorsModule;
import app.money.transfer.di.TablesModule;

public class ActivityIntegrationTest {
	
	@Test
	public void testActivity() throws InterruptedException, ExecutionException {
		Injector serviceInjector = Guice.createInjector(new ActivityModule(),
				new ContollerModule(),
				new DaoModule(),
				new ExecutorsModule(),
				new TablesModule());
		
		Activity activity = serviceInjector.getInstance(Activity.class);
		
		
		List<Long> transactions = new ArrayList<Long>();
		
		CompletableFuture.allOf(createTask(activity, 100, transactions),
				createTask(activity, 200, transactions),
				createTask(activity, 300, transactions),
				createTask(activity, 400, transactions),
				createTask(activity, 500, transactions),
				createTask(activity, 7000, transactions)).get();
		
		Integer amountDeducted = transactions.stream().map((transactionId) -> {
				return activity.getTransactionDetails(
					new TransactionDetailsRequest(transactionId));
			})
			.filter(response -> response.getStatus() == "Successful")
			.map((successfulTransaction) -> {return successfulTransaction.getAmount();})
			.mapToInt(Integer::valueOf)
			.sum();
		
		System.out.println("amountDeducted: " + amountDeducted);
		
		
		Integer payeeBalance = activity
				.getAccountDetails(new AccountDetailsRequest(1L))
				.getAmount();
		
		Integer beneficiaryBalance = activity
				.getAccountDetails(new AccountDetailsRequest(2L))
				.getAmount();
		
		if (payeeBalance == 2000 - amountDeducted && 
				beneficiaryBalance == 1000 + amountDeducted) {
			System.out.println("Test passed");
		} else {
			throw new RuntimeException("test Failed");
		}
		
	}
	
	private static CompletableFuture<TransferResponse> createTask(final Activity activity,
			final Integer amount,
			final List<Long> transactions) {
		return CompletableFuture.supplyAsync(() -> 
			activity.transfer(new TransferRequest(1L, 2L, amount)))
				.whenComplete((response, error) -> {
					if (error == null) {
						transactions.add(response.getTransactionId());
					} else {
						System.out.println(error.toString());
					}
				});
	}
}
