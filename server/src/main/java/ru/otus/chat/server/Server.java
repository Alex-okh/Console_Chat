package ru.otus.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
  int port;
  private List<ClientHandler> clients;

  public Server(int port) {
    this.port = port;
    clients = new CopyOnWriteArrayList<>();
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server started at port:  " + port);
      while (true) {
        Socket socket = serverSocket.accept();
        subscribe(new ClientHandler(socket, this));

      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void subscribe(ClientHandler clientHandler) {
    clients.add(clientHandler);
  }

  public void unsubscribe(ClientHandler client) {
    clients.remove(client);
    broadcast(client.getUserName() + " has left chat...");
  }

  public void broadcast(String message) {
    for (ClientHandler clientHandler : clients) {
      clientHandler.send(message);
    }
  }

  public ClientHandler findClientByName(String name) {
    for (ClientHandler clientHandler : clients) {
      if (clientHandler.getUserName().equals(name)) {
        return clientHandler;
      }
    }
    return null;
  }
}
