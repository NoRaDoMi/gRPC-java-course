package com.grpc.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class HelloServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Hello gRPC");

    Server server = ServerBuilder.forPort(50051)
            .addService(ProtoReflectionService.newInstance())
            .addService(new HelloServiceImpl()).build();
    server.start();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("Received Shudown Request");
                  server.shutdown();
                  System.out.println("Successfully stopped the server");
                }));

    server.awaitTermination();
  }
}
