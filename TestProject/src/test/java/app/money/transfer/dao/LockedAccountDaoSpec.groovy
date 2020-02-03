package app.money.transfer.dao

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import app.money.transfer.models.LockedAccount
import spock.lang.Specification

class LockedAccountDaoSpec extends Specification{
	Dao<LockedAccount, Long> delegate = Mock(Dao)
	LockedAccount model = Mock(LockedAccount)
	
	LockedAccountDao dao = new LockedAccountDao(delegate)
	
	def "Returns true when doesnot find model and removes"() {
		when:
			Boolean result = dao.putIfAbsent(model)
		
		then:
			1 * model.getKey() >> 1234
			1 * delegate.get(1234) >> null
			1 * delegate.put(model)
			0 * _
			result
	}
	
	def "Returns false when doesnot find model and removes"() {
		when:
			Boolean result = dao.putIfAbsent(model)
		
		then:
			1 * model.getKey() >> 1234
			1 * delegate.get(1234) >> model
			0 * _
			!result
	}
	
	def "Only one thread succeds when multiple tries"() {
		given:
			ExecutorService executor = Executors.newFixedThreadPool(3)
		
		when:
			for(int i = 0; i < 3; i++)
				CompletableFuture.runAsync({dao.putIfAbsent(model)}, executor)
			
			executor.awaitTermination(100, TimeUnit.MILLISECONDS)			
		then:
			_ * model.getKey() >> 1234
			1 * delegate.get(1234) >> null
			2 * delegate.get(1234) >> model
			1 * delegate.put(model)
	}
	
	def "remove calls delegeate"() {
		when:
			dao.remove(model)
		
		then:
			1 * delegate.remove(model)
	}
}
