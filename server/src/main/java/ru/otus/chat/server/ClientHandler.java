package ru.otus.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
  private Socket socket;
  private Server server;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;
  private String userName;
  private boolean authenticated;

  public ClientHandler(Socket socket, Server server) throws IOException {
    this.socket = socket;
    this.server = server;
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    authenticated = false;


    new Thread(() -> {
      try {
        System.out.println("Client connected at port:  " + socket.getLocalPort());
        System.out.println("Client port :" + socket.getPort());
        //цикл аутентификации
        while (true) {
          send("""
                  Please authenticate or register to start messaging.
                  Use /auth login password to authenticate.
                  Use /reg login password username to register.""");
          String msg = inputStream.readUTF();
          if (msg.startsWith("/")) {
            if (msg.equalsIgnoreCase("/exit")) {
              System.out.println("Client exited");
              send("/exitok");
              break;
            }
            if (msg.startsWith("/auth")) {
              String[] element = msg.split(" ");
              if (element.length != 3) {
                System.out.println("/auth failed. Wrong format.");
                continue;
              }
              if (server.getAuthProvider().authenticate(this, element[1], element[2])) {
                authenticated = true;
                break;
              }
            }

            if (msg.startsWith("/reg")) {
              String[] element = msg.split(" ");
              if (element.length != 4) {
                System.out.println("/reg failed. Wrong format.");
                continue;
              }
              if (server.getAuthProvider().register(this, element[1], element[2], element[3])) {
                authenticated = true;
                break;
              }
            }
          }
        }


        //цикл работы
        while (authenticated) {
          String msg = inputStream.readUTF();
          if (msg.startsWith("/")) {
            if (msg.equalsIgnoreCase("/exit")) {
              System.out.println("Client exited");
              send("/exitok");
              break;
            }
            if (msg.startsWith("/w")) {
              String[] words = msg.split(" ", 3);
              if (words.length == 3) {
                ClientHandler target = server.findClientByName(words[1]);
                if (target != null) {
                  target.send("Private from " + userName + " : " + words[2]);
                } else {
                  this.send("Name " + words[1] + " not found. Nothing sent.");
                }
              } else {
                this.send("No message found. Nothing sent.");
              }
            }

          } else {
            System.out.println(msg);
            server.broadcast(userName + ": " + msg);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        disconnect();
      }
    }).start();
  }

  public void send(String msg) {
    try {
      outputStream.writeUTF(msg);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void disconnect() {
    server.unsubscribe(this);
    try {
      if (inputStream != null) {
        inputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      if (outputStream != null) {
        outputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
