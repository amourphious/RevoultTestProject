package app.money.transfer.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import app.money.transfer.middleware.ActivityOrchestrator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceHandler implements HttpHandler {
	private final ActivityOrchestrator orchestrator;
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String response = null;
		Integer statusCode = null;
		try {
			switch(exchange.getRequestMethod()) {
			case "GET":
				response = orchestrator.get(exchange.getRequestURI().getPath().substring(1));
				statusCode = 200;
				break;
			case "POST":
				response = orchestrator.post(exchange.getRequestURI().getPath().substring(1),
						new String(getRequestBody(exchange.getRequestBody())));
				statusCode = 200;
				break;
			default:
				response = "{\"error\": \"HTTP method : "
						+ exchange.getRequestMethod() + " is not supported}\"";
				statusCode = 400;
			}
		} catch (ActivityOrchestrator.BadRequestException e) {
			response = "{\"error\":" + e.getMessage() + "}";
			statusCode = 400;
		} catch (Exception e) {
			response = "{\"error\":" + e.getMessage() + "}";
			statusCode = 500;
		}finally {
			exchange.sendResponseHeaders(statusCode, response.getBytes("UTF-8").length);
			exchange.getResponseHeaders().set("Content-Type", "application/json, charset=UTF-8");
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes("UTF-8"));
			os.close();
		}
		
	}
	
	private String getRequestBody(InputStream requestBody) throws IOException {
		ByteArrayOutputStream requestBodyBytes = new ByteArrayOutputStream();
		byte[] bytesRead = new byte[1024];
		int len;
		while ((len = requestBody.read(bytesRead)) > 0) {
			requestBodyBytes.write(bytesRead, 0, len);
		}
		return new String(requestBodyBytes.toByteArray(), "UTF-8");
	}
	
	
}
