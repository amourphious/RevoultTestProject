package app.money.transfer.controller

import org.junit.Test

import app.money.transfer.dao.Dao
import app.money.transfer.dao.LockedAccountDao
import app.money.transfer.models.Account
import app.money.transfer.models.LockedAccount
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class AccountControllerSpec extends Specification {
	Dao<Account, Long> accountDao = Mock(Dao)
	LockedAccountDao lockDao = Mock(LockedAccountDao)
	
	@Subject
	AccountController controller = new AccountController(accountDao, lockDao)
	
	def "Test isPresent"() {
		given:
			Long accountId = 1234
			Account account = Mock()
		
		when: "account exists"
			boolean present = controller.isPresent(accountId)
		
		then:
			1 * accountDao.get(accountId) >> account
			present
		
		when: "account doesnot exists"
			present = controller.isPresent(accountId)
		
		then:
			1 * accountDao.get(accountId) >> null
			!present
			
		when: "Dao throws Exception"
			controller.isPresent(accountId)
		
		then:
			1 * accountDao.get(accountId) >> {throw new RuntimeException()}
			thrown RuntimeException
	}
	
	def "Test unlock put False in LockAccount"() {
		given:
			Long accountId = 1234
		when:
			controller.unlock(accountId)
		
		then:
			1 * lockDao.remove(_) >> {args ->
				LockedAccount accountLock = (LockedAccount) args[0]
				accountLock.getAccountId() == 1234
			}
		
		when:
			controller.unlock(accountId)
		
		then:
			1 * lockDao.remove(_) >> {throw new RuntimeException()}
			noExceptionThrown()
	}
	
	def "Update puts account with new amount"() {
		given:
			Long accountId = 1234
			Integer amount = 100
			
		when:
			controller.update(accountId, amount)
		
		then:
			1 * accountDao.put(_) >> { args -> 
				Account account = (Account) args[0]
				account.getId() >> accountId
				account.getAmount() >> amount
			}
		
		when: "throws exception when unable to put"
			controller.update(accountId, amount)
			
		then:
			1 * accountDao.put(_) >> {throw new RuntimeException()}
			thrown RuntimeException
	}
	
	def "Test getAndLock gets account's amount when able to commit lock"() {
		given:
			Long accountId = 1234
			LockedAccount lock = Mock(LockedAccount)
			Account account = Mock(Account)
		when:
			Integer amount = controller.getAndLock(accountId)
		
		then:
			4 * lockDao.putIfAbsent(_) >> {
				Thread.sleep(500) 
				return false} >> false >> false >> true
			
			1 * accountDao.get(accountId) >> account
			1 * account.getAmount() >> 100
			amount == 100
	}
	
	def "Test getAndLock throws exception when unable to commit lock"() {
		given:
			Long accountId = 1234
			LockedAccount lock = Mock(LockedAccount)
			Account account = Mock(Account)
		when:
			Integer amount = controller.getAndLock(accountId)
		
		then:
			1 * lockDao.putIfAbsent(_) >> { 
				Thread.sleep(1100) 
				return false
			}
			thrown RuntimeException
	}
	
	def "Test throws exception when unable to write"() {
		given:
		Long accountId = 1234
		LockedAccount lock = Mock(LockedAccount)
		Account account = Mock(Account)
	when:
		Integer amount = controller.getAndLock(accountId)
	
	then:
		1 * lockDao.putIfAbsent(_) >> true
		1 * accountDao.get(accountId) >> {throw new RuntimeException()}
		thrown RuntimeException
	}
}
