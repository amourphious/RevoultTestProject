package app.money.transfer.executor;

import app.money.transfer.activity.NotFoundException;
import app.money.transfer.activity.TransactionDetailsResponse;
import app.money.transfer.controller.TransactionController;
import app.money.transfer.models.Transaction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionExecutor {
	private final TransactionController transactionController;
	
	public TransactionDetailsResponse getTransactionDetails(Long transactionId) 
			throws NotFoundException {
		Transaction transaction = transactionController.get(transactionId);
		if (transaction == null) {
			throw new NotFoundException("Unable to find transaction: " + transactionId);
		}
		return new TransactionDetailsResponse(transaction.getFromAccount(),
				transaction.getToAccount(),
				transaction.getAmount(),
				transaction.getStatus());
	}
}
