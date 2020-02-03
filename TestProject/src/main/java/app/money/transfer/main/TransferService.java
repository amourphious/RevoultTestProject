package app.money.transfer.main;

import com.google.inject.Injector;

import app.money.transfer.activity.AccountDetailsRequest;
import app.money.transfer.activity.Activity;
import app.money.transfer.activity.TransactionDetailsRequest;
import app.money.transfer.activity.TransferRequest;
import app.money.transfer.executor.AccountExecutor;
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
public class TransferService {
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
				return activityInjector
					.getInstance(Activity.class)
					.getTransactionDetails(new TransactionDetailsRequest(Long.valueOf(params[1])))
					.toString();
			} catch(Exception e) {
				throw new ServiceInternalFailure("500: ServiceInternalFailure: ", e);
			}
		case "account":
			try {
				return activityInjector
					.getInstance(Activity.class)
					.getAccountDetails(new AccountDetailsRequest(Long.valueOf(params[1])))
					.getAmount().toString();
			} catch (Exception e){
				throw new ServiceInternalFailure("500: ServiceInternalFailure", e);
			}
		default: throw new BadRequestException("404: not found");
		}
	}
	
	/**
	 * 
	 * @param path valid path: transfer
	 * @param payload valid payload: "payer:<Long_Id>,beneficiary:<Long_Id>,amount:<Int_amount>"
	 * @return
	 */
	public String post(String path, String payload) {
		if (path == "transfer") {
			TransferRequest request = parsePayload(payload);
			try {
				return activityInjector
						.getInstance(Activity.class)
						.transfer(request)
						.getTransactionId().toString();
			} catch(Exception e) {
				throw new ServiceInternalFailure("500: ServiceInternalFailure", e);
			}
		}
		throw new BadRequestException("404: not found");
	}
	
	private TransferRequest parsePayload(String payload) {
		String[] params = payload.split(",");
		if (params.length == 3) {
			Long fromAccount = null;
			Long toAccount = null;
			Integer amount = null;
			for (String param: params) {
				String[] keyValue = param.split(":");
				try {
					switch(keyValue[0]) {
					case "payer": 
						fromAccount = Long.valueOf(keyValue[1]);
						break;
					case "beneficiary":
						toAccount = Long.valueOf(keyValue[1]);
						break;
					case "amount":
						amount = Integer.valueOf(keyValue[1]);
						break;
					default: throw new BadRequestException("Invalid payload!!");
					}
				} catch (NumberFormatException e) {
					throw new BadRequestException("400: Invalid payload");
				}
			}
			return new TransferRequest(fromAccount, toAccount, amount);
		}
		throw new BadRequestException("404: not forund");
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
	
	static class BadRequestException extends RuntimeException {

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
