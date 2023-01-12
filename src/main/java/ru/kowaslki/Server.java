package ru.kowaslki;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8989;
    public static final String BIN_FILE_NAME = "data.bin";

    File binFile = new File(BIN_FILE_NAME);
    FinanceManager financeManager = new FinanceManager();

    public void start() throws RuntimeException {

        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            if (binFile.exists()) {
                financeManager.loadFromBin(binFile);
                System.out.println("BinFile loaded");
            } else {
                binFile.createNewFile();
                System.out.println("BinFile created");
            }
            System.out.println("Server started!");
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    financeManager.saveToBin(binFile, requestManager(in));
                    out.println(financeManager.maxCategory(financeManager.getMaxCategoryMap()).toString());
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
