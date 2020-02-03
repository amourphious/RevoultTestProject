package app.money.transfer.controller;

import app.money.transfer.dao.Dao;
import app.money.transfer.dao.LockedAccountDao;
import app.money.transfer.models.Account;
import app.money.transfer.models.LockedAccount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountController {
	private final Dao<Account, Long> accountDao;
	private final LockedAccountDao lockDao;
	
	public Integer getAndLock(final Long accountId) {
		tryLocking(accountId);
		try {
			return this.get(accountId).getAmount();
		} catch (Exception e) {
			System.out.println("Unable to get amount for accountId: " + accountId);
			throw new RuntimeException("Unable to get amount for accoount Id: " + accountId, e);
		}
	}
	
	public void update(final Long accountId, final Integer amount) {
		try {
			accountDao.put(new Account(accountId, amount));
		} catch(Exception e) {
			throw new RuntimeException("Unable to update account: " + accountId, e);
		}
	}
	
	public void unlock(final Long accountId) {
		try {
			lockDao.remove(new LockedAccount(accountId));
		} catch (Exception e) {
			System.out.println("Error: unable to unlock account: " + accountId);
		}
	}
	
	public Boolean isPresent(final Long accountId) {
		try {
			return this.get(accountId) != null;
		} catch (Exception e){
			System.out.println("Warn: Unable to retrieve account information: " + accountId);
			throw new RuntimeException("Unable to retrieve account information: " + accountId, e);
		}
	}
	
	public Account get(final Long accountId) {
		return accountDao.get(accountId);
	}
	
	private void tryLocking(final Long accountId) {
		long startTime = System.currentTimeMillis();
		while (!lockDao.putIfAbsent(new LockedAccount(accountId))) {
			if (System.currentTimeMillis() - startTime < 1000L) {
				continue;
			}
			throw new RuntimeException("Starved while commiting lock");
		}
	}
}
