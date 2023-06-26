package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WineServer {

    private static List<Wine> wineList;

    public static void main(String[] args) throws IOException {
        initializeWineList();

        HttpServer server = HttpServer.create(new InetSocketAddress(6969), 0);
        server.createContext("/", new MainHandler());
        server.createContext("/wine", new WineHandler());
        server.start();
        System.out.println("Server avviato sulla porta 6969");
    }

    private static void initializeWineList() {
        wineList = new ArrayList<>();
        wineList.add(new Wine("Chianti", "red", 20.99));
        wineList.add(new Wine("Pinot Noir", "red", 35.99));
        wineList.add(new Wine("Sauvignon Blanc", "white", 18.99));
        wineList.add(new Wine("Chardonnay", "white", 25.99));
        wineList.add(new Wine("Merlot", "red", 22.99));
        wineList.add(new Wine("Riesling", "white", 28.99));
    }

    static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body><h1>Wine Server</h1>" +
                    "<table>" +
                    "<tr><th>Comando</th><th>Descrizione</th></tr>" +
                    "<tr><td style=\"border-right: 1px solid black;\">wine/red</td><td>Mostra tutti i vini rossi</td></tr>" +
                    "<tr><td colspan=\"2\"><hr></td></tr>" +
                    "<tr><td style=\"border-right: 1px solid black;\">wine/white</td><td>Mostra tutti i vini bianchi</td></tr>" +
                    "<tr><td colspan=\"2\"><hr></td></tr>" +
                    "<tr><td style=\"border-right: 1px solid black;\">wine/sorted_by_name</td><td>Mostra i vini ordinati per nome</td></tr>" +
                    "<tr><td colspan=\"2\"><hr></td></tr>" +
                    "<tr><td style=\"border-right: 1px solid black;\">wine/sorted_by_price</td><td>Mostra il vino piu costoso</td></tr>" +
                    "</table></body></html>";


            exchange.getResponseHeaders().set("Content-Type", "text/html");
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    static class WineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String response = "";

            if (requestMethod.equalsIgnoreCase("GET")) {
                String command = exchange.getRequestURI().getPath().substring(6);
                response = processCommand(command);
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String processCommand(String command) {
            switch (command) {
                case "red":
                    return filterByType("red");
                case "white":
                    return filterByType("white");
                case "sorted_by_name":
                    return sortByName();
                case "sorted_by_price":
                    return sortByPrice();
                default:
                    return "Comando non valido";
            }
        }

        private String filterByType(String type) {
            StringBuilder result = new StringBuilder();
            for (Wine wine : wineList) {
                if (wine.getType().equalsIgnoreCase(type)) {
                    result.append(wine.toString()).append("\n");
                }
            }
            return result.toString();
        }

        private String sortByName() {
            List<Wine> sortedList = new ArrayList<>(wineList);
            sortedList.sort(Comparator.comparing(Wine::getName));
            return listToString(sortedList);
        }

        private String sortByPrice() {
            Wine mostExpensiveWine = wineList.stream()
                    .max(Comparator.comparing(Wine::getPrice))
                    .orElse(null);

            if (mostExpensiveWine != null) {
                return mostExpensiveWine.toString();
            } else {
                return "Nessun vino trovato";
            }
        }

        private String listToString(List<Wine> wineList) {
            StringBuilder result = new StringBuilder();
            for (Wine wine : wineList) {
                result.append(wine.toString()).append("\n");
            }
            return result.toString();
        }
    }
}
