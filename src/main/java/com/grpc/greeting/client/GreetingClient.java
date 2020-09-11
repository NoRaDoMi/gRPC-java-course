package com.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
  public static void main(String[] args) {
    System.out.println("Hello I'm a gRPC client");

    GreetingClient main = new GreetingClient();
    main.run();
  }

  public void run() {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    System.out.println("Creating stub");
    //    doUnaryCall(channel);
    //    doServerStreamingCall(channel);
    //    doClientStreamingCall(channel);
    doBiDiStreamingCall(channel);

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  private void doUnaryCall(ManagedChannel channel) {
    //    create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);

    //    Unary
    //  create a protocol buffer greeting message
    Greeting greeting = Greeting.newBuilder().setFirstName("Noradomi").setLastName("Phuc").build();

    //    do the same for a GreetRequest
    GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

    //    call  the RPC and get back a GreetResponse (protocol buffers)
    GreetResponse greetResponse = greetClient.greet(greetRequest);

    System.out.println(greetResponse.getResult());
  }

  private void doServerStreamingCall(ManagedChannel channel) {
    //    create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);
    //    Server Streaming
    //    prepare request
    GreetManyTimeRequest greetManyTimeRequest =
        GreetManyTimeRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Noradomi").setLastName("Phuc").build())
            .build();

    //    stream the response (in a blocking manner)
    greetClient
        .greetManyTimes(greetManyTimeRequest)
        .forEachRemaining(
            greetManyTimeResponse -> {
              System.out.println(greetManyTimeResponse.getResult());
            });
  }

  private void doClientStreamingCall(ManagedChannel channel) {
    // create an asynchronous client
    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<LongGreetRequest> requestStreamObserver =
        asyncClient.longGreet(
            new StreamObserver<LongGreetResponse>() {
              @Override
              public void onNext(LongGreetResponse longGreetResponse) {
                // we get a response from the server
                // onNext will be called only once
                System.out.println("Received a response from the server");
                System.out.println(longGreetResponse.getResult());
              }

              @Override
              public void onError(Throwable throwable) {
                // we get an error from the server
              }

              @Override
              public void onCompleted() {
                // the server is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
              }
            });

    // streaming message #1
    System.out.println("sending message 1");
    requestStreamObserver.onNext(
        LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Noradomi").build())
            .build());
    // streaming message #2
    System.out.println("sending message 2");
    requestStreamObserver.onNext(
        LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Java").build())
            .build());
    // streaming message #3
    System.out.println("sending message 3");
    requestStreamObserver.onNext(
        LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("React").build())
            .build());

    // we tell the server that the client is done sending data
    requestStreamObserver.onCompleted();

    // Wait until done received response from server.
    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void doBiDiStreamingCall(ManagedChannel channel) {
    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<GreetEveryoneRequest> requestObserver =
        asyncClient.greetEveryone(
            new StreamObserver<GreetEveryoneResponse>() {
              @Override
              public void onNext(GreetEveryoneResponse greetEveryoneResponse) {
                System.out.println("Response from server: " + greetEveryoneResponse.getResult());
              }

              @Override
              public void onError(Throwable throwable) {}

              @Override
              public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
              }
            });

    Arrays.asList("Ronaldo", "Messi", "Neymar", "Kaka")
        .forEach(
            name -> {
              System.out.println("Sending: " + name);
              requestObserver.onNext(
                  GreetEveryoneRequest.newBuilder()
                      .setGreeting(Greeting.newBuilder().setFirstName(name).build())
                      .build());
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
