package app.money.transfer.dao;

public class UnableToAcquireLockException extends RuntimeException {
	public UnableToAcquireLockException() {
		super("Thread starved to acquire lock");
	}
}
