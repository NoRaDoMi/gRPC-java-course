package com.grpc.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
  public static void main(String[] args) {
    System.out.println("Hello I'm a gRPC client");
//    usePlaintext(): for disable grpc's security
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
    System.out.println("Creating stub");

    //    create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);

    //  create a protocol buffer greeting message
    Greeting greeting = Greeting.newBuilder().setFirstName("Noradomi").setLastName("Phuc").build();

    //    do the same for a GreetRequest
    GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

    //    call  the RPC and get back a GreetResponse (protocol buffers)
    GreetResponse greetResponse = greetClient.greet(greetRequest);

    System.out.println(greetResponse.getResult());

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
