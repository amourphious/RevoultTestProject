package app.money.transfer.executor

import app.money.transfer.activity.AccountDetailsResponse
import app.money.transfer.activity.NotFoundException
import app.money.transfer.controller.AccountController
import app.money.transfer.models.Account
import spock.lang.Specification
import spock.lang.Subject

class AccountExecutorSpec extends Specification {
	AccountController accountController = Mock(AccountController)
	
	@Subject
	AccountExecutor accountExecutor = new AccountExecutor(accountController)
	
	def "Throws Exception when account not found"() {
		when:
			accountExecutor.getAccountDetails(1234L)
		then:
			1 * accountController.get(1234L) >> null
			thrown NotFoundException
	}
	
	def "Gets account information"() {
		given:
			Account account = Mock()
			AccountDetailsResponse expected = new AccountDetailsResponse(100)
		
		when:
			AccountDetailsResponse actual = accountExecutor.getAccountDetails(1234L)
		
		then:
			1 * account.getAmount() >> 100
			1 * accountController.get(1234L) >> account
			actual == expected
	}
}
