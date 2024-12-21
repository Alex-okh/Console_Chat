package ru.otus.chat.client;

import java.io.IOException;

public class ClientMain {


  public static void main(String[] args) throws InterruptedException {
    int retrycount = 10;
    System.out.println("Client started.");
    while (retrycount > 0) {
      try {
        new Client();
        break;
      } catch (IOException e) {
        System.out.println("Could not connect to server. Reason: " + e.getMessage());
        retrycount--;
        Thread.sleep(1000);
      }
    }
    System.out.println("Exiting...");
  }
}
