package app.money.transfer.utils

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

import app.money.transfer.di.ExecutorsModule
import spock.lang.Specification
import spock.lang.Subject

class IdGeneratorSpec extends Specification {
	@Subject
	IdGenerator idGenerator = new IdGenerator()
	
	def "Id generator gets id"() {
		when:
			Long id = idGenerator.generate()
			
		then:
			id
	}
	
	def "concurrent threads get different id"() {
		given:
			ExecutorService executor = Executors.newFixedThreadPool(2)
		when:
			Future<Long> gen1 = executor.submit({return idGenerator.generate()} as Callable<Long>)
			Future<Long> gen2 = executor.submit({return idGenerator.generate()} as Callable<Long>)
			
			Long id1 = gen1.get()
			Long id2 = gen2.get()
		then:
			id1 != id2
	}
}
