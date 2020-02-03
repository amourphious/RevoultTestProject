package app.money.transfer.activity

import org.junit.After

import app.money.transfer.controller.TransactionController
import app.money.transfer.executor.AccountExecutor
import app.money.transfer.executor.TransactionExecutor
import app.money.transfer.executor.TransferExecutor
import spock.lang.Specification
import spock.lang.Subject

class ActivitySpec extends Specification {
	TransferExecutor transferExecutor = Mock(TransferExecutor)
	TransactionExecutor transactionExecutor = Mock(TransactionExecutor)
	AccountExecutor accountExecutor = Mock(AccountExecutor)
	
	@Subject
	Activity activity = new Activity(transferExecutor, transactionExecutor, accountExecutor)
	
	def "Test Transfer"() {
		given:
			TransferRequest transferRequest = Mock(TransferRequest)
			TransferResponse transferResponse = Mock(TransferResponse)
			TransferResponse failedResponse = new TransferResponse(-1L)
		when:
			TransferResponse response = activity.transfer(transferRequest)
		then:
			1 * transferRequest.getFromAccount() >> 1L
			1 * transferRequest.getToAccount() >> 2L
			1 * transferRequest.getAmmount() >> 100
			1 * transferExecutor.transfer(1L, 2L, 100) >> transferResponse
			transferResponse == response
			
		when: "Executor throws exception"
			response = activity.transfer(transferRequest)
			
		then:
			1 * transferRequest.getFromAccount() >> 1L
			1 * transferRequest.getToAccount() >> 2L
			1 * transferRequest.getAmmount() >> 100
			1 * transferExecutor.transfer(1L, 2L, 100) >> {throw new NotFoundException("exception")}
			response == failedResponse
			noExceptionThrown()
	}
	
	def "Test Account Details Activity"() {
		given:
			AccountDetailsRequest request = Mock(AccountDetailsRequest)
			AccountDetailsResponse detailsResponse = Mock(AccountDetailsResponse)
			AccountDetailsResponse failedResponse = new AccountDetailsResponse(-1)
		when:
			AccountDetailsResponse response = activity.getAccountDetails(request)
		then:
			1 * request.getAccountId() >> 1L
			1 * accountExecutor.getAccountDetails(1L) >> detailsResponse
			response == detailsResponse
			
		when: "Executor throws exception"
			response = activity.getAccountDetails(request)
		then:
			1 * request.getAccountId() >> 1L
			1 * accountExecutor.getAccountDetails(1L) >> {throw new NotFoundException("exception")}
			response == failedResponse
	}
	
	def "Test TransactionDetails activity"() {
		given:
			TransactionDetailsRequest request = Mock(TransactionDetailsRequest)
			TransactionDetailsResponse transactionResponse = Mock(TransactionDetailsResponse)
			TransactionDetailsResponse failedResponse = new TransactionDetailsResponse(-1L, -1L, -1, "Not Found!!")
		when:
			TransactionDetailsResponse response = activity.getTransactionDetails(request)
		then:
			1 * request.getTransactionId() >> 1234L
			1 * transactionExecutor.getTransactionDetails(1234L) >> transactionResponse
			response == transactionResponse
			
		when: "Executor throws exception"
			response = activity.getTransactionDetails(request)
		then:
			1 * request.getTransactionId() >> 1234L
			1 * transactionExecutor.getTransactionDetails(1234L) >> {throw new NotFoundException("exception")}
			response == failedResponse
	}
	
}
