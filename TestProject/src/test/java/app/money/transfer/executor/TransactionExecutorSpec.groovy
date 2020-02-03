package app.money.transfer.executor

import app.money.transfer.activity.NotFoundException
import app.money.transfer.activity.TransactionDetailsRequest
import app.money.transfer.activity.TransactionDetailsResponse
import app.money.transfer.controller.TransactionController
import app.money.transfer.models.Transaction
import spock.lang.Specification
import spock.lang.Subject

class TransactionExecutorSpec extends Specification {
	TransactionController controller = Mock(TransactionController)
	
	@Subject
	TransactionExecutor transactionExecutor = new TransactionExecutor(controller);
	
	def"Throws Not found Exception when transactionId not present"() {
		when:
			transactionExecutor.getTransactionDetails(1234L)
			
		then:
			1 * controller.get(1234L) >> null
			thrown NotFoundException
	}
	
	def "Gets transaction details"() {
		given:
			Transaction transaction = Mock(Transaction)
			TransactionDetailsResponse expected = new TransactionDetailsResponse(1L, 2L, 100, "Test")
		when:
			TransactionDetailsResponse actual = transactionExecutor.getTransactionDetails(1234L)
			
		then:
			1 * transaction.getFromAccount() >> 1L
			1 * transaction.getToAccount() >> 2L
			1 * transaction.getAmount() >> 100
			1 * transaction.status >> "Test"
			1 * controller.get(1234L) >> transaction
			actual == expected
			
	}
}
