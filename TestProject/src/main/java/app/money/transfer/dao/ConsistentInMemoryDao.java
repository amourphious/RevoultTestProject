package app.money.transfer.dao;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import app.money.transfer.models.Model;

/**
 * Decorates {@link InMemoryDao} to provide consitent read and writes.
 * @author amourphious
 *
 * @param <T>
 * @param <K>
 */
public class ConsistentInMemoryDao<T extends Model<K>, K> implements Dao<T, K> {
	private final InMemoryDao<T, K> delegate;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConsistentInMemoryDao(InMemoryDao<T, K> delegate) {
		this.delegate = delegate;
	} 

	@Override
	public T get(K keyValue) {
		return this.get(ImmutableList.of(keyValue)).get(0);
	}

	@Override
	public void put(T object) {
		this.put(ImmutableList.of(object));
	}
	
	public List<T> get(Collection<K> keyValues) {
		try {
			if (lock.readLock().tryLock(1000, TimeUnit.MILLISECONDS)) {
				try {
					return keyValues.stream()
							.map(keyValue -> delegate.get(keyValue))
							.collect(Collectors.toList());
				} finally {
					lock.readLock().unlock();
				}
			} else {
				throw new UnableToAcquireLockException();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void put(Collection<T> objects) {
		try {
			if (lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS)) {
				try {
					objects.stream().forEach(object -> delegate.put(object));
				} finally {
					lock.writeLock().unlock();
				}
			} else {
				throw new UnableToAcquireLockException();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(T object) {
		try {
			if (lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS)) {
				try {
					delegate.remove(object);
				} finally {
					lock.writeLock().unlock();
				}
			} else {
				throw new UnableToAcquireLockException();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
