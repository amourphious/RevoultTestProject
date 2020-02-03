package app.money.transfer.executor;

import java.util.concurrent.ExecutorService;

import app.money.transfer.activity.NotFoundException;
import app.money.transfer.activity.TransferResponse;
import app.money.transfer.controller.AccountController;
import app.money.transfer.controller.TransactionController;
import app.money.transfer.models.Transaction;

public class TransferExecutor {
	private final AccountController accountController;
	private final TransactionController transactionController;
	private final ExecutorService executor;
	
	public TransferExecutor(AccountController accountController,
			TransactionController transactionController,
			ExecutorService executor) {
		this.accountController = accountController;
		this.transactionController = transactionController;
		this.executor = executor;
	}
	
	public TransferResponse transfer(Long fromAccount, Long toAccount, int amount) throws NotFoundException {
		if (accountController.isPresent(fromAccount) && accountController.isPresent(toAccount)) {
			Transaction transaction = transactionController.create(fromAccount, toAccount, amount);
			executor.execute(() -> this.transactionController.transact(transaction));
			return new TransferResponse(transaction.getId());
		} else {
			throw new NotFoundException("Invalid Accounts provided for transfer!!");
		}
	}
}
