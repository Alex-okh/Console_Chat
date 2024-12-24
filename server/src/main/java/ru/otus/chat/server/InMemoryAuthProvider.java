package ru.otus.chat.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryAuthProvider implements AuthenticateProvider {

  private Server server;
  private List<User> users;

  public InMemoryAuthProvider(Server server) {
    this.server = server;
    users = new CopyOnWriteArrayList<>();
    users.add(new User("john", "password", "john"));
    users.add(new User("jane", "password", "jane"));
    users.add(new User("joe", "password", "joe"));
  }

  @Override
  public void initialize() {
    System.out.println("in memory auth provider init.");
  }

  private String getUserNamebyLogin(String login, String password) {
    for (User user : users) {
      if (user.login.equals(login) && user.password.equals(password)) {
        return user.userName;
      }
    }
    return null;
  }

  @Override
  public boolean authenticate(ClientHandler client, String login, String password) {
    String foundUserName = getUserNamebyLogin(login, password);
    if (foundUserName == null) {
      client.send("Wrong login or password.");
      return false;
    }
    if (server.isUsernameBusy(foundUserName)) {
      client.send("Username is already in use.");
      return false;
    }

    client.setUserName(foundUserName);
    server.subscribe(client);
    client.send("Authentification OK. Username: " + foundUserName);


    return true;
  }

  @Override
  public boolean register(ClientHandler clientHandler, String login, String password, String username) {
    if (login.length() < 3 || password.length() < 5 || username.length() < 3) {
      clientHandler.send("login and username must contain at least 3 characters. Password must contain at least 5 characters.");
      return false;
    }
    if (isLoginBusy(login)) {
      clientHandler.send("Login is already in use.");
      return false;
    }
    if (isUsernameBusy(username)) {
      clientHandler.send("Username is already in use.");
      return false;
    }
    users.add(new User(username, password, username));
    clientHandler.setUserName(username);
    server.subscribe(clientHandler);
    clientHandler.send("Registration OK. Username: " + username);
    return true;
  }

  private boolean isUsernameBusy(String username) {
    for (User user : users) {
      if (user.userName.equals(username)) {
        return true;
      }
    }
    return false;
  }

  private boolean isLoginBusy(String login) {
    for (User user : users) {
      if (user.login.equals(login)) {
        return true;
      }
    }
    return false;
  }

  private class User {
    private String login;
    private String password;
    private String userName;

    public User(String login, String password, String userName) {
      this.login = login;
      this.password = password;
      this.userName = userName;
    }
  }
}
