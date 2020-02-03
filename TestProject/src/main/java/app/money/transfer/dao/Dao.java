package app.money.transfer.dao;

import app.money.transfer.models.Model;

/**
 * 
 * @author amourphious
 *
 * @param <T> Model Type
 * @param <K> Key Type
 */
public interface Dao<T extends Model<K>, K> {
	public T get(K keyValue);
	public void put(T object);
	public void remove(T object);
}
