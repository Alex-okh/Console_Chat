package ru.otus.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
  private final List<ClientHandler> clients;
  int port;
  AuthenticateProvider authProvider;

  public Server(int port) {
    this.port = port;
    clients = new CopyOnWriteArrayList<>();
    authProvider = new InMemoryAuthProvider(this);
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server started at port:  " + port);
      authProvider.initialize();
      while (true) {
        Socket socket = serverSocket.accept();
        new ClientHandler(socket, this);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void subscribe(ClientHandler clientHandler) {
    clients.add(clientHandler);
  }

  public void unsubscribe(ClientHandler client) {
    if (clients.remove(client) && client.getUserName() != null) {
      broadcast(client.getUserName() + " has left chat...");
    }
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

  public boolean isUsernameBusy(String username) {
    for (ClientHandler clientHandler : clients) {
      if (clientHandler.getUserName().equals(username)) {
        return true;
      }
    }
    return false;
  }

  public AuthenticateProvider getAuthProvider() {
    return authProvider;
  }
}
