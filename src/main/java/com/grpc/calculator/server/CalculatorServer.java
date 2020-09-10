package com.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Hello gRPC");
    Server server = ServerBuilder.forPort(5001).addService(new CalculatorServiceImpl()).build();
    server.start();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("Received Shutdown Request");
                  server.shutdown();
                  System.out.println("Successfully stop server");
                }));
    server.awaitTermination();
  }
}
