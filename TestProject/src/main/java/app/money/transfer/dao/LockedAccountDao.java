package app.money.transfer.dao;

import java.util.concurrent.locks.ReentrantLock;

import app.money.transfer.models.LockedAccount;
import lombok.RequiredArgsConstructor;

/**
 * Such locking technique will work in multi-server environment as well,
 * where method putIfAbsent are provided by the database.  
 * @author amourphious
 *
 */
@RequiredArgsConstructor
public class LockedAccountDao {
	private final Dao<LockedAccount, Long> delegate;
	ReentrantLock lock = new ReentrantLock();
	
	public Boolean putIfAbsent(LockedAccount model) {
		lock.lock();
		try {
			if (delegate.get(model.getKey()) == null) {
				delegate.put(model);
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	public void remove(LockedAccount model) {
		delegate.remove(model);
	}
}
