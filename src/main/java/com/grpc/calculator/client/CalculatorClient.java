package com.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
  public static void main(String[] args) {
    System.out.println("Hello I'm gRPC client");

    CalculatorClient main = new CalculatorClient();
    main.run();
  }

  public void run() {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();

    System.out.println("Creating a stub");
    //    doUnaryCall(channel);
    //    doServerStreamingCall(channel);
    //    doClientStreamingCall(channel);
    //    doBiDiStreamingCall(channel);
    doErrorCall(channel);

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  private void doUnaryCall(ManagedChannel channel) {

    CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient =
        CalculatorServiceGrpc.newBlockingStub(channel);

    //    Unary
    SumRequest sumRequest = SumRequest.newBuilder().setFirstNumber(10).setSecondNumber(20).build();

    SumResponse sumResponse = calculatorClient.sum(sumRequest);
    System.out.println(sumResponse.getSumResult());
  }

  private void doServerStreamingCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient =
        CalculatorServiceGrpc.newBlockingStub(channel);

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
  }

  private void doClientStreamingCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceStub asyncClient =
        CalculatorServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<ComputeAverageRequest> requestObserver =
        asyncClient.computeAverage(
            new StreamObserver<ComputeAverageResponse>() {
              @Override
              public void onNext(ComputeAverageResponse computeAverageResponse) {
                // create a message from server
                System.out.println("Received a message from server");
                System.out.println("average = " + computeAverageResponse.getAverage());
              }

              @Override
              public void onError(Throwable throwable) {}

              @Override
              public void onCompleted() {
                System.out.println("Server has completed sending us something");
                latch.countDown();
              }
            });

    for (int i = 1; i <= 1000; i++) {
      requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(i).build());
    }

    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void doBiDiStreamingCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceStub asyncClient =
        CalculatorServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<FindMaximumRequest> requestObserver =
        asyncClient.findMaximum(
            new StreamObserver<FindMaximumResponse>() {
              @Override
              public void onNext(FindMaximumResponse findMaximumResponse) {
                System.out.println(
                    "Got a new current maximum: " + findMaximumResponse.getMaximum());
              }

              @Override
              public void onError(Throwable throwable) {}

              @Override
              public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
              }
            });

    for (int i = 0; i < 100; i++) {
      Random r = new Random();
      int number = r.nextInt((1000 - 10) + 1) + 10;
      requestObserver.onNext(FindMaximumRequest.newBuilder().setNumber(number).build());
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void doErrorCall(ManagedChannel channel) {

    CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient =
        CalculatorServiceGrpc.newBlockingStub(channel);

    try {
      SquareRootResponse squareRootResponse =
          calculatorClient.squareRoot(SquareRootRequest.newBuilder().setNumber(-10).build());

      System.out.println(squareRootResponse.getNumberRoot());
    } catch (StatusRuntimeException e) {
      System.out.println("Got an exception for square root!");
      e.printStackTrace();
    }
  }
}
