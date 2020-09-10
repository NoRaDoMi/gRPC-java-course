package com.grpc.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
  @Override
  public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    //   extract the fields we need
    Greeting greeting = request.getGreeting();
    String firstName = greeting.getFirstName();

    String result = "Hello " + firstName;
    GreetResponse greetResponse = GreetResponse.newBuilder().setResult(result).build();

    //    Now we should return reponse to client, but we can write return response
    //    because server are async -> using StreamObserver to return response

    //    send back the response
    responseObserver.onNext(greetResponse);

    //    complete the RPC call
    responseObserver.onCompleted();
  }
}
