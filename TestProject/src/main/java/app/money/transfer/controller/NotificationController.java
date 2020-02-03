package app.money.transfer.controller;

import app.money.transfer.dao.Dao;
import app.money.transfer.models.Account;
import app.money.transfer.models.Transaction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationController {
	private final Dao<Account, Long> accountDao;
	private final Dao<Transaction, Long> transactionDao;
	
	public void notify(Long trancationId) {
		Transaction transaction = transactionDao.get(trancationId);
		StringBuilder notification = new StringBuilder("Notification:" + transaction.toString());
		notification.append("\n");
		notification.append(accountDao.get(transaction.getFromAccount()));
		notification.append("\n");
		notification.append(accountDao.get(transaction.getToAccount()));
		System.out.println(notification.toString());
	}
}
