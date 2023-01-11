package ru.kowaslki;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8989;
    FinanceManager financeManager = new FinanceManager();

    public void start() throws RuntimeException {

        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    System.out.println("Server started!");
                    financeManager.addToPurchaseList(requestManager(in));
                    out.println(financeManager.maxCategory(financeManager.maxCategoryMap).toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Can't start server");
            e.printStackTrace();
        }
    }

    public static Purchase requestManager(BufferedReader in) throws IOException {
        Purchase purchase;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        purchase = gson.fromJson(in.readLine(), Purchase.class);
        return purchase;
    }
}
