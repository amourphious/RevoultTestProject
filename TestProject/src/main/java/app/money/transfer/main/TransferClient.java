package app.money.transfer.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;

import app.money.transfer.di.ActivityModule;
import app.money.transfer.di.ContollerModule;
import app.money.transfer.di.DaoModule;
import app.money.transfer.di.ExecutorsModule;
import app.money.transfer.di.TablesModule;

public class TransferClient {
	private final static ExecutorService PARALLEL_TRANSFER_EXECUTOR = Executors.newFixedThreadPool(5);
	public static void main(String[] args) throws InterruptedException {
		Injector activityInjector = Guice.createInjector(new ActivityModule(),
				new ContollerModule(),
				new DaoModule(),
				new ExecutorsModule(),
				new TablesModule());
		
		TransferService service = new TransferService(activityInjector);
		
		
		List<String> transactions = new ArrayList<String>();
		
		CompletableFuture.allOf(makePostRequest(service, 100, transactions),
				makePostRequest(service, 200, transactions),
				makePostRequest(service, 300, transactions),
				makePostRequest(service, 400, transactions),
				makePostRequest(service, 500, transactions),
				makePostRequest(service, 7000, transactions)).join();		
		
		transactions.stream().forEach((transactionId) -> {
				System.out.println("TransactionDetails: " + service.get("transaction/" + transactionId));
			});
		
		System.out.println("AccountDetails for no. 1: " + service.get("account/1"));
		System.out.println("AccountDetails for no. 2: " + service.get("account/2"));
		
	}
	
	private static CompletableFuture<String> makePostRequest(final TransferService service,
			final Integer amount,
			final List<String> transactions) {
		return CompletableFuture.supplyAsync(() -> 
			service.post("transfer", "payer:1,beneficiary:2,amount:"+amount), PARALLEL_TRANSFER_EXECUTOR)
				.whenComplete((response, error) -> {
					if (error == null) {
						transactions.add(response);
					} else {
						System.out.println(error.toString());
					}
				});
	}
}
