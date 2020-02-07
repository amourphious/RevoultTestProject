package app.money.transfer.middleware;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Injector;

import app.money.transfer.activity.AccountDetailsRequest;
import app.money.transfer.activity.Activity;
import app.money.transfer.activity.TransactionDetailsRequest;
import app.money.transfer.activity.TransferRequest;
import lombok.RequiredArgsConstructor;

/**
 * This is generally done by application server eg: jesrsey-guice etc.,
 * but as the task does not use any framework,
 * hence implementing the similar functionality here but,
 * very specific to the application.
 * 
 * Also as no authentication and application context is required,
 * it does not support headers or cookies.
 * 
 * @author amourphious
 *
 */
@RequiredArgsConstructor
public class ActivityOrchestrator {
	private final Injector activityInjector;
	
	/**
	 * 
	 * @param path valid path: transaction/<Long_Id> or account/<Long_Id>
	 * @return
	 */
	public String get(String path) {
		String[] params = path.split("/");
		validateParams(params);
		switch(params[0]) {
		case "transaction":
			try {
				return new JSONObject(activityInjector.getInstance(Activity.class)
						.getTransactionDetails(new TransactionDetailsRequest(Long.valueOf(params[1]))))
						.toString();
			} catch(Exception e) {
				throw new ServiceInternalFailure("500: ServiceInternalFailure: ", e);
			}
		case "account":
			try {
				return new JSONObject(activityInjector.getInstance(Activity.class)
						.getAccountDetails(new AccountDetailsRequest(Long.valueOf(params[1]))))
						.toString();
			} catch (Exception e){
				throw new ServiceInternalFailure("500: ServiceInternalFailure", e);
			}
		default: throw new BadRequestException("404: not found");
		}
	}
	
	/**
	 * 
	 * @param path valid path: transfer
	 * @param payload valid payload: "{payer:<Long_Id>,beneficiary:<Long_Id>,amount:<Int_amount>}"
	 * @return
	 */
	public String post(String path, String payload) {
		if ("transfer".equals(path)) {
			TransferRequest request = parsePayload(payload);
			try {
				return new JSONObject(activityInjector.getInstance(Activity.class)
						.transfer(request)).toString();
				
			} catch(Exception e) {
				throw new ServiceInternalFailure("500: ServiceInternalFailure", e);
			}
		}
		throw new BadRequestException("404: not found");
	}
	
	private TransferRequest parsePayload(String payload) {
		try {
			JSONObject params = new JSONObject(payload);
			if (params.has("payer") && params.has("beneficiary") && params.has("amount")) {
				Long fromAccount = params.getLong("payer");
				Long toAccount = params.getLong("beneficiary");
				Integer amount = params.getInt("amount");
				return new TransferRequest(fromAccount, toAccount, amount);
			}
		} catch (JSONException e) {
			throw new BadRequestException("Please provide valid parameters");
		} catch (Exception e) {
			throw new ServiceInternalFailure("Something went wrong", e);
		}
		throw new BadRequestException("Please provide valid parameters");
	}

	private  void validateParams(String[] params) {
		if (params.length == 2) {
			try {
				Long.valueOf(params[1]);
			} catch (NumberFormatException e) {
				throw new BadRequestException("404: Invalid Id: should be a number");
			}
		} else {
			throw new BadRequestException("404: not found");
		}
	}
	
	public static class BadRequestException extends RuntimeException {

		public BadRequestException(String string) {
			super(string);
		}
	}
	
	static class ServiceInternalFailure extends RuntimeException {

		public ServiceInternalFailure(String string, Throwable e) {
			super(string, e);
		}
	}
}
