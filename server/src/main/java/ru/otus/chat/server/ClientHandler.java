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
  private static int userCount = 0;

  public ClientHandler(Socket socket, Server server) throws IOException {
    this.socket = socket;
    this.server = server;
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    userCount++;
    userName = "User" + userCount;


    new Thread(() -> {
      try {
        System.out.println("Client connected at port:  " + socket.getLocalPort());
        System.out.println("Client port :" + socket.getPort());

        while (true) {
          String msg = inputStream.readUTF();
          if (msg.startsWith("/")) {
            if (msg.equalsIgnoreCase("/exit")) {
              System.out.println("Client exited");
              send("/exitok");
              break;
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
}
