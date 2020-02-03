package app.money.transfer.controller

import java.util.concurrent.ExecutorService

import org.codehaus.groovy.ast.stmt.AssertStatement

import app.money.transfer.dao.Dao
import app.money.transfer.executor.TransferExecutor
import app.money.transfer.models.Transaction
import app.money.transfer.utils.IdGenerator
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class TransactionControllerSpec extends Specification {
	AccountController accountController = Mock(AccountController)
	Dao<Transaction, Long> transactionDao = Mock(Dao)
	IdGenerator idGenerator = Mock(IdGenerator)
	NotificationController notificationController = Mock(NotificationController)
	
	Long accountTo = 1L
	Long accountFrom = 2L
	Integer toAccountAmt = 500
	Integer fromAccountAmt = 600
	Integer transferAmount = 100
	Long transactionId = 1234
	
	Transaction transactionInitialized = new Transaction(1234,
		accountFrom,
		accountTo,
		transferAmount,
		"Initialized")
	
	Transaction transactionExecuting = new Transaction(1234,
		accountFrom,
		accountFrom,
		transferAmount,
		"Executing")
	
	Transaction transactionServcieError = new Transaction(1234,
		accountFrom,
		accountTo,
		transferAmount,
		"Service Error")
	
	Transaction transactionInsufficintFunds = new Transaction(1234,
		accountFrom,
		accountTo,
		transferAmount,
		"Insufficient Funds")
	
	Transaction transactionSuccesful = new Transaction(1234,
		accountFrom,
		accountTo,
		transferAmount,
		"Successful")
	
	
	@Subject
	TransactionController transactionController = new TransactionController(transactionDao,
		accountController,
		notificationController,
		idGenerator)
	
	def setup() {
		_ * idGenerator.generate() >> 1234
	}
	
	def "Test transaction initialization"() {
		when:
			transactionController.create(accountFrom, accountTo, transferAmount)
			
		then:
			{interaction { assertUpdatedStatus(transactionExecuting) }}
			1 * idGenerator.generate() >> 1234
			0 * _
	}
	
	def "Test simple transaction"() {
		when:
			transactionController.transact(transactionInitialized)
			
		then:
			1 * accountController.getAndLock(accountFrom) >> fromAccountAmt
			1 * accountController.getAndLock(accountTo) >> toAccountAmt
			interaction { assertUpdatedStatus(transactionExecuting) }
			1 * accountController.update(accountTo, toAccountAmt + transferAmount)
			1 * accountController.update(accountFrom, fromAccountAmt - transferAmount)
			interaction { assertUpdatedStatus(transactionSuccesful) }
			1 * accountController.unlock(accountTo)
			1 * accountController.unlock(accountFrom)
			0 * _
	}
	
	def "Transaction fails when unable to lock and get account amount"() {
		when:
			transactionController.transact(transactionInitialized)
		then:
			interaction { assertUpdatedStatus(transactionExecuting) }
			1 * accountController.getAndLock(accountFrom) >> fromAccountAmt
			1 * accountController.getAndLock(accountTo) >> {throw new RuntimeException()}
			interaction {assertUpdatedStatus(transactionServcieError)}
			1 * accountController.unlock(accountTo)
			1 * accountController.unlock(accountFrom)
			0 * _
	}
	
	def "Transaction fail when insuffcient funds in account"() {
		given:
			Integer insufficientFund = 0
		when:
			transactionController.transact(transactionInitialized)
			
		then:
			1 * accountController.getAndLock(accountFrom) >> insufficientFund
			1 * accountController.getAndLock(accountTo) >> toAccountAmt
			interaction { assertUpdatedStatus(transactionExecuting) }
			interaction { assertUpdatedStatus(transactionInsufficintFunds) }
			1 * accountController.unlock(accountFrom)
			1 * accountController.unlock(accountTo)
			0 * _
	}
	
	def "Transaction fails and restore consistency when unable to persist amount"() {
		given:
			Integer insufficientFund = 0
		when:
			transactionController.transact(transactionInitialized)
		
		then:
			1 * accountController.getAndLock(accountFrom) >> fromAccountAmt
			1 * accountController.getAndLock(accountTo) >> toAccountAmt
			interaction { assertUpdatedStatus(transactionExecuting) }
			1 * accountController.update(accountFrom, fromAccountAmt - transferAmount) >> {throw new RuntimeException()}
			interaction { assertUpdatedStatus(transactionServcieError) }
			1 * accountController.update(accountTo, toAccountAmt)
			1 * accountController.update(accountFrom, fromAccountAmt)
			1 * accountController.unlock(accountTo)
			1 * accountController.unlock(accountFrom)
			0 * _
	}
	
	def "get calls transactionDao"() {
		given:
			Transaction transaction = Mock(Transaction)
		when:
			Transaction actual = transactionController.get(transactionId)
		then:
			1 * transactionDao.get(transactionId) >> transaction
			actual == transaction
	}
	
	def assertUpdatedStatus(Transaction expected) {
		1 * transactionDao.put(_ as Transaction) >> { actual ->
			expected == actual
		}
		
		1 * notificationController.notify(_ as Long) >> { actual ->
			expected.getId() == actual
		}
	}
}
