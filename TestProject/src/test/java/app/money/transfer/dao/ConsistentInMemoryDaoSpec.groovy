package app.money.transfer.dao

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.BiFunction

import app.money.tansfer.model.DummyModel
import spock.lang.Specification
import spock.lang.Subject

class ConsistentInMemoryDaoSpec extends Specification {
	InMemoryDao<DummyModel, String> delegate = Mock(InMemoryDao)
	ExecutorService executor = Executors.newFixedThreadPool(4);
	DummyModel model = Mock(DummyModel)
	DummyModel model_other = Mock(DummyModel)
	DummyModel model_updated = Mock(DummyModel)
	
	@Subject
	ConsistentInMemoryDao dao = new ConsistentInMemoryDao(delegate)
	
	def "gets and puts from delegate"() {
		when:
			dao.put(model)
			dao.get("model-key")
			dao.put([model, model_other])
			dao.get(["model-key", "model-other-key"])
		then:
			2 * delegate.put(model)
			1 * delegate.put(model_other)
			2 * delegate.get("model-key")
			1 * delegate.get("model-other-key")
	}
	
	def "Multiple Threads can not read or write when writing to table"() {
		when:
			CompletableFuture.runAsync({dao.put(model)}, executor)
			
			CompletableFuture.runAsync({dao.put(model_updated)}, executor)
				
			CompletableFuture.runAsync({dao.get("model-key")}, executor)
			
			CompletableFuture.runAsync({dao.remove(model)}, executor)
				
			executor.awaitTermination(1500, TimeUnit.MILLISECONDS)
			
		then:
			1 * delegate.put(model) >> {
				Thread.sleep(1100)
			}
	}
	
	def "multiple threads can read when no thread is writing"() {
		when:
			CompletableFuture.runAsync({dao.get("model-key")})
			CompletableFuture.runAsync({dao.get("model-key-other")})
			CompletableFuture.runAsync({dao.get("model-key")})
			executor.awaitTermination(1500, TimeUnit.MILLISECONDS)
		then:
			2 * delegate.get("model-key") >> {
				Thread.sleep(1100)
				return model
			}
			
			1 * delegate.get("model-key-other") >> {
				Thread.sleep(1100)
				return model
			}
	}
	
	def "Multiple Threads can not remove when removing from table"() {
		when:
			CompletableFuture.runAsync({dao.remove(model)}, executor)
			
			CompletableFuture.runAsync({dao.put(model_updated)}, executor)
				
			CompletableFuture.runAsync({dao.get("model-key")}, executor)
			
			CompletableFuture.runAsync({dao.remove(model)}, executor)
				
			executor.awaitTermination(1500, TimeUnit.MILLISECONDS)
			
		then:
			1 * delegate.remove(model) >> {
				Thread.sleep(1100)
			}
	}
}
