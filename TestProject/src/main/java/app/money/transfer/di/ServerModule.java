package app.money.transfer.di;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Exposed;
import com.google.inject.Injector;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import app.money.transfer.main.ServiceHandler;
import app.money.transfer.middleware.ActivityOrchestrator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerModule extends PrivateModule {
	public static final String SERVER_EXECUTOR = "serverExecutor";
	private static final String SERVICE_HANDLER = "serviceHandler";
	
	private final Integer httpPort;
	@Override
	protected void configure() {
	}
	
	@Provides
	@Singleton
	ActivityOrchestrator getOrchestrator(final Injector injector) {
		return new ActivityOrchestrator(injector);
	}
	
	@Provides
	@Singleton
	@Named(SERVICE_HANDLER)
	HttpHandler getServiceHandler(final ActivityOrchestrator orchestrator) {
		return new ServiceHandler(orchestrator);
	}
	
	@Provides
	@Singleton
	@Named(SERVER_EXECUTOR)
	@Exposed
	ExecutorService getServerExecutor() {
		return Executors.newFixedThreadPool(10);
	}
	
	@Provides
	@Singleton
	@Exposed
	HttpServer getServer(@Named(SERVICE_HANDLER) 
						 final HttpHandler serviceHandler,
						 @Named(SERVER_EXECUTOR)
						 final ExecutorService executor) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", httpPort), 0);
		server.setExecutor(executor);
		server.createContext("/", serviceHandler);
		return server;
	}
}
