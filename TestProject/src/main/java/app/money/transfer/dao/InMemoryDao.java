package app.money.transfer.dao;

import java.util.Map;

import app.money.transfer.models.Model;
import lombok.RequiredArgsConstructor;

/**
 * As the database is in memory, this DAO reads and writes to a map.
 * @author amourphious
 *
 * @param <T>
 * @param <K>
 */
@RequiredArgsConstructor
public class InMemoryDao<T extends Model<K>, K> implements Dao<T, K> {
	private final Map<K, T> table;
	
	@Override
	public T get(K key) {
		return table.get(key);
	}
	
	@Override
	public void put(T object) {
		table.put(object.getKey(), object);
	}
	
	@Override
	public void remove(T object) {
		table.remove(object.getKey());
	}
}
