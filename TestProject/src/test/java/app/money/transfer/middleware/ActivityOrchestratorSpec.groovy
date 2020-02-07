package app.money.transfer.middleware

import org.json.JSONObject

import com.google.inject.Injector

import app.money.transfer.activity.AccountDetailsRequest
import app.money.transfer.activity.AccountDetailsResponse
import app.money.transfer.activity.Activity
import app.money.transfer.activity.TransactionDetailsRequest
import app.money.transfer.activity.TransactionDetailsResponse
import app.money.transfer.activity.TransferRequest
import app.money.transfer.activity.TransferResponse
import app.money.transfer.middleware.ActivityOrchestrator.BadRequestException
import app.money.transfer.middleware.ActivityOrchestrator.ServiceInternalFailure
import spock.lang.Specification
import spock.lang.Subject

class ActivityOrchestratorSpec extends Specification {
	Injector activityInjector = Mock(Injector)
	Activity activity = Mock()
	
	@Subject
	ActivityOrchestrator orchestrator = new ActivityOrchestrator(activityInjector)
	
	def "Get throws bad request with invalid path"() {
		when:
			orchestrator.get("blah/1234")
		
		then:
			thrown BadRequestException
		
		when:
			orchestrator.get("/transaction/1234")
		
		then:
			thrown BadRequestException
		
		when:
			orchestrator.get("transaction/blah")
			
		then:
			thrown BadRequestException
	}
	
	def "Get throws Service error when activity fails"() {
		when:
			orchestrator.get("transaction/1234")
		
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.getTransactionDetails(_ as TransactionDetailsRequest) >> {throw new RuntimeException()}
			thrown ServiceInternalFailure
		
		when:
			orchestrator.get("account/1234")
			
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.getAccountDetails(_ as AccountDetailsRequest) >> {throw new RuntimeException()}
			thrown ServiceInternalFailure
	}
	
	def "Get returns JSON representation of activity response"() {
		given:
			AccountDetailsResponse responseAaccountDetails = new AccountDetailsResponse(100)
			TransactionDetailsResponse responseTransactionDetails = 
				new TransactionDetailsResponse(1L, 2L, 100, "Successful")
		when:
			String response = orchestrator.get("transaction/1234")
		
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.getTransactionDetails(_ as TransactionDetailsRequest) >> { args ->
				((TransactionDetailsRequest)args[0]).getTransactionId() == 1234L
				return responseTransactionDetails
			}
			response == new JSONObject(responseTransactionDetails).toString()
			
		when:
			response = orchestrator.get("account/1234")
			
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.getAccountDetails(_ as AccountDetailsRequest) >> { args -> 
				((AccountDetailsRequest) args[0]).getAccountId() == 1234L
				return responseAaccountDetails
			}
			response == new JSONObject(responseAaccountDetails).toString();	
	}
	
	def "Post throws bad request exception on invalid payload/path"() {
		when:
			orchestrator.post("blah", "{\"1\": \"2\"}")
		
		then:
			thrown BadRequestException
			
		when:
			orchestrator.post("transfer", "{\"payload")
		
		then:
			thrown BadRequestException	
	}
	
	def "Post throws Service Internal failure when activity throws exception"() {
		when:
			orchestrator.post("transfer", "{\"payer\": 1, \"beneficiary\": 2, \"amount\": 100}")
		
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.transfer(_ as TransferRequest) >> {throw new RuntimeException()}
			thrown ServiceInternalFailure 
	}
	
	def "Post return JSON represntation of Activity response"() {
		given:
			TransferResponse transferResponse = new TransferResponse(1234L)
			TransferRequest request = new TransferRequest(1L, 2L, 100)
		when:
			String response = orchestrator.post("transfer",
				"{\"payer\": 1, \"beneficiary\": 2, \"amount\": 100}")
			
		then:
			1 * activityInjector.getInstance(Activity.class) >> activity
			1 * activity.transfer(_ as TransferRequest) >> transferResponse
			response == new JSONObject(transferResponse).toString()
	}
}
