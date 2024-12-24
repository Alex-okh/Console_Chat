package ru.otus.chat.server;

public interface AuthenticateProvider {
  void initialize();
  boolean authenticate(ClientHandler client, String login, String password);
  boolean register(ClientHandler clientHandler, String login, String password, String username);
}
