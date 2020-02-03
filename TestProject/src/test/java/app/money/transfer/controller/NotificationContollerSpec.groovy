package app.money.transfer.controller

import app.money.transfer.dao.Dao
import app.money.transfer.models.Account
import app.money.transfer.models.Transaction
import spock.lang.Specification
import spock.lang.Subject

class NotificationContollerSpec extends Specification {
	Dao<Account, Long> accountDao = Mock(Dao)
	Dao<Transaction, Long> transactionDao = Mock(Dao)
	
	@Subject
	NotificationController controller = new NotificationController(accountDao, transactionDao)
	
	def "Test notificationContoller happy case"() {
		given:
			Transaction transaction = Mock(Transaction)
			Account payeeAccount = Mock(Account)
			Account benificiaryAccount = Mock(Account)
		
		when:
			controller.notify(1234L)
			
		then:
			1 * transaction.getFromAccount() >> 1L
			1 * transaction.getToAccount() >> 2L
			1 * transaction.toString() >> "stringified transaction"
			1 * payeeAccount.toString() >> "stringified payee account"
			1 * benificiaryAccount.toString() >> "stringified benificiary account"
			1 * transactionDao.get(1234L) >> transaction
			1 * accountDao.get(1L) >> payeeAccount
			1 * accountDao.get(2L) >> benificiaryAccount
	}
	
	def "Test notificationContoller throws exception when unable to get details"() {
		given:
			Transaction transaction = Mock(Transaction)
			Account payeeAccount = Mock(Account)
			Account benificiaryAccount = Mock(Account)
		
		when: "Transaction Dao Fails"
			controller.notify(1234L)
			
		then:
			1 * transactionDao.get(1234L) >> {throw new RuntimeException()}
			
			thrown RuntimeException
		
		when: "Account Dao Fails for payee Account"
			controller.notify(1234L)
			
		then:
			1 * transaction.getFromAccount() >> 1L
			1 * transaction.toString() >> "stringified transaction"
			1 * transactionDao.get(1234L) >> transaction
			1 * accountDao.get(1L) >> {throw new RuntimeException()}
			
			thrown RuntimeException
			
		when: "Account Dao Fails for benificiary Account"
			controller.notify(1234L)
			
		then:
			1 * transaction.getFromAccount() >> 1L
			1 * transaction.getToAccount() >> 2L
			1 * transaction.toString() >> "stringified transaction"
			1 * payeeAccount.toString() >> "stringified payee account"
			1 * transactionDao.get(1234L) >> transaction
			1 * accountDao.get(1L) >> payeeAccount
			1 * accountDao.get(2L) >> {throw new RuntimeException()}
			
			thrown RuntimeException
	}
}
