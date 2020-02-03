package app.money.transfer.executor;

import app.money.transfer.activity.AccountDetailsResponse;
import app.money.transfer.activity.NotFoundException;
import app.money.transfer.controller.AccountController;
import app.money.transfer.models.Account;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountExecutor {
	private final AccountController accountController;
	
	public AccountDetailsResponse getAccountDetails(Long accountId) throws NotFoundException {
		Account account = accountController.get(accountId);
		if (account == null) {
			throw new NotFoundException("Unable to find account" + accountId);
		}
		return new AccountDetailsResponse(account.getAmount());
	}
}
