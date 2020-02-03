package app.money.transfer.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import app.money.transfer.activity.NotFoundException
import app.money.transfer.activity.TransferRequest
import app.money.transfer.activity.TransferResponse
import app.money.transfer.controller.AccountController
import app.money.transfer.controller.TransactionController
import app.money.transfer.models.Transaction
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class TransferExecutorSpec extends Specification {
	TransactionController transactionController = Mock(TransactionController)
	AccountController accountController = Mock(AccountController)
	ExecutorService executor = Executors.newFixedThreadPool(1)
	
	@Subject
	TransferExecutor transferExecutor = new TransferExecutor(accountController,
		transactionController,
		executor)
	
	@Unroll
	def "Transaction does not start when accountId is invalid"() {
		when:
			transferExecutor.transfer(1, 2, 100)
			
		then:
			accountController.isPresent(1) >> fromAccountValidity
			accountController.isPresent(2) >> toAccountValidity
			thrown NotFoundException
			0 * _
			
		where:
			fromAccountValidity << [true, false]
			toAccountValidity << [false, true]
	}
	
	def "Transaction does not start when unable to create transaction"() {
		when:
			transferExecutor.transfer(1, 2, 100)
	
		then:
			1 * accountController.isPresent(1) >> true
			1 * accountController.isPresent(2) >> true
			1 * transactionController.create(1, 2, 100) >> {throw new RuntimeException()}
			thrown RuntimeException
			0 * _
	}
	
	def "Transaction executor executes transaction"() {
		given:
			Transaction transaction = Mock(Transaction)
			TransferResponse expected = new TransferResponse(1234L)
			
		when:
			TransferResponse actual = transferExecutor.transfer(1, 2, 100)
			executor.awaitTermination(10, TimeUnit.MILLISECONDS)
		then:
			1 * transaction.getId() >> 1234L
			1 * accountController.isPresent(1) >> true
			1 * accountController.isPresent(2) >> true
			1 * transactionController.create(1, 2, 100) >> transaction
			1 * transactionController.transact(transaction)
			actual == expected
			0 * _
	}
}
