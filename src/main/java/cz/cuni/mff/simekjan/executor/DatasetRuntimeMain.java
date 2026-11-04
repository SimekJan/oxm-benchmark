package cz.cuni.mff.simekjan.executor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

public final class DatasetRuntimeMain {

    private DatasetRuntimeMain() {
    }

    static void main(String[] args) throws Exception {
        Map<String, String> arguments = arguments(args);
        int port = Integer.parseInt(required(arguments, "port"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/health", exchange -> respond(exchange, 200, "{\"status\":\"READY\"}"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        server.start();
    }

    private static Map<String, String> arguments(String[] args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Expected --name value arguments");
        }
        Map<String, String> result = new java.util.HashMap<>();
        for (int index = 0; index < args.length; index += 2) {
            String name = args[index];
            if (!name.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + name);
            }
            result.put(name.substring(2), args[index + 1]);
        }
        return result;
    }

    private static String required(Map<String, String> arguments, String name) {
        String value = arguments.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing --" + name);
        }
        return value;
    }

    private static void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (var output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
