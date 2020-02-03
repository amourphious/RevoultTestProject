# TransferMoneyApp
## API
### GET: transaction/<transaction_id>
*returns the details of transaction*
See usage in TransferClient.java
### GET account/<account_id>
*returns account balance*
See usage in TransferClient.java
### POST transfer
*payload: "payer:<account_id>,beneficiary:<account_id>,amount:<amount_to_transfer>"*
*returns transaction-Id*
initializes the transfer

###Exceptions
#### BadRequestException
*request is invalid*
#### ServiceInternalFailure
*service failed to process request*

## Notes
* The database is in memory, each table is a Map.
* Import the project in eclipse and run ActivityIntegrationTest.java and run the TransferClient.java to interact with service. Service is actually a java program which implements Service Client framework.
* All unit tests are in spock.
* Using Guice for dependency injection.
* Guice modules and Model definitions are assumed to be out of scope of unit-testing as they do not contain any business logic.
* Project depends on lombok as well.
* Database is initialized with two accounts *1* and *2* with amount 2000 and 1000 respectively, more accounts can only be added by adding entries into map in TablesModule.java.
* Client has no timeout as it is standalone.
