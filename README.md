# TransferMoneyApp
## API
### GET: transaction/<transaction_id>
*returns the details of transaction*

Usage:
```curl http://localhost:8001/transaction/<transaction_id>```

result:
```{"payee":<account_id>, "amount":<amount_int>, "benificiary":<account_id>, "status":"Successful"|"InsuffcientFunds!!"|"Service Failure!!"}```

OR

```{"payee":-1,"amount":-1,"benificiary":-1,"status":"Not Found!!"}```


### GET: account/<account_id>
*returns account balance*

```curl http://localhost:8001/transaction/<account_id>```

result:
```{"amount": <amount_int>}```

OR

```{"amount": -1}```

### POST: transfer
Usage: ```curl -d '{"payer": <account_id>, "beneficiary": <account_id>, "amount": <amount_int>}' -X POST http://localhost:8001/transfer```

*payload:* ```{"payer":<account_id>,"beneficiary":<account_id>,"amount":<amount_to_transfer>}```

*returns:* ```{"transactionId": <id>}```

initializes the transfer and invokes notifications when transaction status changes (for now prints new status to output stream)

### Exceptions

#### BadRequestException
*request is invalid*

#### ServiceInternalFailure
*service failed to process request*

## Notes
* The database is in memory, each table is a Map.
* Import the project in eclipse and run ActivityIntegrationTest.java
* run the TransferAppService.java to run server, it runs on http://localhost:8001, if port unavailable pass different parameter to serverModule constructor in same class.
* All unit tests are in spock.
* Using Guice for dependency injection.
* Guice modules and Model definitions are assumed to be out of scope of unit-testing as they do not contain any business logic.
* Project depends on lombok as well.
* Database is initialized with two accounts *1* and *2* with amount *2000* and *1000* respectively, more accounts can only be added by adding entries into map in TablesModule.java.
* Use curl to make calls.
