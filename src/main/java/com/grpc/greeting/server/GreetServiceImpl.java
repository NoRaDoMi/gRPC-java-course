package com.grpc.greeting.server;

import com.proto.greet.*;
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

  @Override
  public void greetManyTimes(
      GreetManyTimeRequest request, StreamObserver<GreetManyTimeResponse> responseObserver) {
    try {
      String firstName = request.getGreeting().getFirstName();

      for (int i = 0; i < 10; i++) {
        String result = "Hello " + firstName + ", response number: " + i;
        GreetManyTimeResponse response =
            GreetManyTimeResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        Thread.sleep(1000L);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      responseObserver.onCompleted();
    }
  }

  @Override
  public StreamObserver<LongGreetRequest> longGreet(
      StreamObserver<LongGreetResponse> responseObserver) {
    StreamObserver<LongGreetRequest> streamObserverRequest =
        new StreamObserver<LongGreetRequest>() {

          String result = "";

          @Override
          public void onNext(LongGreetRequest longGreetRequest) {
            // client send a message
            result += "Hello " + longGreetRequest.getGreeting().getFirstName() + " !";
          }

          @Override
          public void onError(Throwable throwable) {
            // client send an error
          }

          @Override
          public void onCompleted() {
            // client is done
            responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
            // this is when we return a response using responseObserver
            responseObserver.onCompleted();
          }
        };
    return streamObserverRequest;
  }
}
