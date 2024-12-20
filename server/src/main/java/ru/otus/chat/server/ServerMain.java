package ru.otus.chat.server;

public class ServerMain {
  public static void main(String[] args) {
    Server server = new Server(8189);
    server.start();

  }
}
