package app.money.transfer.utils;

public class IdGenerator {
	private static long id = System.currentTimeMillis();
	
	
	synchronized public Long generate() {
		return id++;
	}
}
