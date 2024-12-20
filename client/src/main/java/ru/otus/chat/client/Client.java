package ru.otus.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;
  private Scanner sc;

  public Client() throws IOException {
    socket = new Socket("localhost", 8189);
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    sc = new Scanner(System.in);

    new Thread(() -> {
      System.out.println("Connected.");
      try {
        while (true) {
          String msg = inputStream.readUTF();
          if (msg.startsWith("/")) {
            if (msg.equalsIgnoreCase("/exitok")) {
              break;
            }
          } else {
            System.out.println(msg);
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        disconnect();
      }
    }).start();
    while (true) {
      String msg = sc.nextLine();
      System.out.print("\b".repeat(msg.length()));
      outputStream.writeUTF(msg);
      if (msg.equalsIgnoreCase("/exit")) {
        break;
      }
    }
  }

  public void disconnect() {
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
}

