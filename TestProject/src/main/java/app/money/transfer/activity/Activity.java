package app.money.transfer.activity;

import app.money.transfer.executor.AccountExecutor;
import app.money.transfer.executor.TransactionExecutor;
import app.money.transfer.executor.TransferExecutor;
import lombok.RequiredArgsConstructor;

/**
 * The application business logic starts here:
 * The application server provides request object
 * to appropriate function.
 * 
 * Currently the Activity does not have any request context,
 * hence it can be singleton, but it is intended to be
 * created for each request.
 * 
 * Though if dependencies of Activity are expensive to create,
 * they can be singleton.
 * @author amourphious
 *
 */
@RequiredArgsConstructor
public class Activity {
	private final TransferExecutor transferExecutor;
	private final TransactionExecutor transactionExecutor;
	private final AccountExecutor accountExecutor;
	
	public TransferResponse transfer(TransferRequest request) {
		try {
			return transferExecutor.transfer(request.getFromAccount(),
					request.getToAccount(),
					request.getAmmount());
		} catch(NotFoundException e) {
			return new TransferResponse(-1L);
		}
	}
	
	public TransactionDetailsResponse getTransactionDetails(TransactionDetailsRequest request) {
		try {
			return transactionExecutor.getTransactionDetails(request.getTransactionId());
		} catch(NotFoundException e) {
			return new TransactionDetailsResponse(-1L, -1L, -1, "Not Found!!");
		}
	}
	
	public AccountDetailsResponse getAccountDetails(AccountDetailsRequest request) {
		try {
			return accountExecutor.getAccountDetails(request.getAccountId());
		} catch (NotFoundException e) {
			return new AccountDetailsResponse(-1);
		}
	}
}
