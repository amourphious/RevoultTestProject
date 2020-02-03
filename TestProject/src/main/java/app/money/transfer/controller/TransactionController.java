package app.money.transfer.controller;

import app.money.transfer.dao.Dao;
import app.money.transfer.models.Transaction;
import app.money.transfer.utils.IdGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionController {
	private static final String SUCCESFUL = "Successful";
	private static final String SERVICE_ERROR = "Service Error";
	private static final String FAILED_INSUFFICIENT_FUNDS = "Insufficient Funds";
	private static final String EXECUTING = "Executing";
	private static final String INITIALIZED_STATUS = "Initialized";
	
	private final Dao<Transaction, Long> transactionDao;
	private final AccountController accountController;
	private final NotificationController notificationController;
	private final IdGenerator idGenerator;
	
	public void transact(final Transaction transaction) {
		try {
			Integer payeeAmount = null;
			Integer benificiaryAmount = null;
			
			try {
				this.updateStatus(transaction, EXECUTING);
				payeeAmount = accountController.getAndLock(transaction.getFromAccount());
				benificiaryAmount = accountController.getAndLock(transaction.getToAccount());
			} catch (Exception e) {
				this.updateStatus(transaction, SERVICE_ERROR);
				System.out.println("Error Transaction Failed");
				return;
			}
			
			try {
				if (payeeAmount < transaction.getAmount()) {
					this.updateStatus(transaction, FAILED_INSUFFICIENT_FUNDS);
					return;
				}
				int payeeBalance = payeeAmount - transaction.getAmount();
				int benificiaryBalance = benificiaryAmount + transaction.getAmount();
				accountController.update(transaction.getFromAccount(), payeeBalance);
				accountController.update(transaction.getToAccount(), benificiaryBalance);
				this.updateStatus(transaction, SUCCESFUL);
			} catch (Exception e) {
				this.updateStatus(transaction, SERVICE_ERROR);
				accountController.update(transaction.getFromAccount(), payeeAmount);
				accountController.update(transaction.getToAccount(), benificiaryAmount);
				System.out.println("Error transafer failed");
				return;
			}
		} finally {
			accountController.unlock(transaction.getToAccount());
			accountController.unlock(transaction.getFromAccount());
		}
	}
	
	public Transaction create(final Long fromAccount,
			final Long toAccount, 
			final Integer amount) {
		Transaction transaction = new Transaction(idGenerator.generate(),
				fromAccount,
				toAccount,
				amount,
				INITIALIZED_STATUS);
		
		this.updateStatus(transaction, INITIALIZED_STATUS);
		return transaction;
	}
	
	public Transaction get(Long transactionId) {
		return transactionDao.get(transactionId);
	}
	
	private void updateStatus(final Transaction transaction, final String status) {
		final Transaction updatedTransaction = new Transaction(transaction.getId(),
				transaction.getFromAccount(),
				transaction.getToAccount(),
				transaction.getAmount(),
				status);
		
		transactionDao.put(updatedTransaction);
		notificationController.notify(transaction.getId());
	}
}
