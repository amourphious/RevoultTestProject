package app.money.transfer.main;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sun.net.httpserver.HttpServer;

import app.money.transfer.di.ActivityModule;
import app.money.transfer.di.ContollerModule;
import app.money.transfer.di.DaoModule;
import app.money.transfer.di.ExecutorsModule;
import app.money.transfer.di.ServerModule;
import app.money.transfer.di.TablesModule;

public class TransferAppService {
	public static void main(String[] args) throws InterruptedException, IOException {
		
		Injector serviceInjector = Guice.createInjector(new ServerModule(8001),
				new ActivityModule(),
				new ContollerModule(),
				new DaoModule(),
				new ExecutorsModule(),
				new TablesModule());
		
		ExecutorService transferExecutor = serviceInjector.getInstance(Key.get(ExecutorService.class,
				Names.named(ExecutorsModule.TRANSFER_EXECUTOR)));
		ExecutorService serverExecutorPool = serviceInjector.getInstance(Key.get(ExecutorService.class,
				Names.named(ServerModule.SERVER_EXECUTOR)));
		
		HttpServer server = serviceInjector.getInstance(HttpServer.class);
		server.start();
		
		System.out.println("server started: http://localhost:8001");
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				serverExecutorPool.awaitTermination(2, TimeUnit.SECONDS);
				transferExecutor.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));
		
	}	
}
