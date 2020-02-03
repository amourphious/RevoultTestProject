package app.money.transfer.dao

import static org.junit.Assert.*

import org.junit.Test

import app.money.tansfer.model.DummyModel
import spock.lang.Specification

class InMemoryDaoSpec extends Specification {
	HashMap table = Spy(new HashMap())
	InMemoryDao inMemoryDao = new DummyInMemoryDao(table)
	
	def "test put and get"() {
		given:
			DummyModel model = Mock(DummyModel)
		
		when:
			inMemoryDao.put(model)
			DummyModel expected = inMemoryDao.get("test")
			inMemoryDao.remove(model)
		
		then:
			_ * model.getKey() >> "test"
			1 * table.put("test", model)
			1 * table.get("test")
			1 * table.remove("test")
			expected == model
	}	
}
