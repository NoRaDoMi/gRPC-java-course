package com.grpc.helloworld;

import com.proto.helloworld.GreeterGrpc;
import com.proto.helloworld.HelloReply;
import com.proto.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
  @Override
  public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
    String name = request.getName();
    System.out.println("Received a request with name=" + name);

    String result = "Hello " + name;
    HelloReply greetResponse = HelloReply.newBuilder().setMessage(result).build();

    responseObserver.onNext(greetResponse);
    System.out.println("Done response");

    responseObserver.onCompleted();
  }
}
