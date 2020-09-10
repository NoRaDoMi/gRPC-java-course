package com.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
  public static void main(String[] args) {
    System.out.println("Hello I'm gRPC client");
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();

    System.out.println("Creating a stub");
    CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient =
        CalculatorServiceGrpc.newBlockingStub(channel);

    //    Unary
    //    SumRequest sumRequest =
    // SumRequest.newBuilder().setFirstNumber(10).setSecondNumber(20).build();
    //
    //    SumResponse sumResponse = calculatorClient.sum(sumRequest);
    //    System.out.println(sumResponse.getSumResult());

    //    Server Streaming
    Long number = 51681239121312L;
    PrimeNumberDecompositionRequest request =
        PrimeNumberDecompositionRequest.newBuilder().setNumber(number).build();
    calculatorClient
        .primeNumberDecomposition(request)
        .forEachRemaining(
            primeNumberDecompositionResponse -> {
              System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
            });

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
