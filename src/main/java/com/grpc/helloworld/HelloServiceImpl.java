package com.grpc.helloworld;

import com.proto.greet.GreetResponse;
import com.proto.greet.Greeting;
import com.proto.helloworld.GreeterGrpc;
import com.proto.helloworld.HelloReply;
import com.proto.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

        String name = request.getName();
        System.out.println("Receive a request with name="+name);

        String result = "Hello " + name;
        HelloReply helloReply = HelloReply.newBuilder().setMessage(result).build();

        //    send back the response
        responseObserver.onNext(helloReply);
        System.out.println("Sent completely response");

        //    complete the RPC call
        responseObserver.onCompleted();
    }
}
