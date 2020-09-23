package com.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
  @Override
  public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
    int firstNumber = request.getFirstNumber();
    int secondNumber = request.getSecondNumber();

    int sumResult = firstNumber + secondNumber;
    SumResponse sumResponse = SumResponse.newBuilder().setSumResult(sumResult).build();

    responseObserver.onNext(sumResponse);

    responseObserver.onCompleted();
  }

  @Override
  public void primeNumberDecomposition(
      PrimeNumberDecompositionRequest request,
      StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
    Long number = request.getNumber();
    Long divisor = 2L;
    while (number > 1) {
      if (number % divisor == 0) {
        number /= divisor;
        responseObserver.onNext(
            PrimeNumberDecompositionResponse.newBuilder().setPrimeFactor(divisor).build());
      } else {
        divisor++;
      }
    }
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<ComputeAverageRequest> computeAverage(
      StreamObserver<ComputeAverageResponse> responseObserver) {
    return new StreamObserver<ComputeAverageRequest>() {
      int sum = 0;
      int nums = 0;

      @Override
      public void onNext(ComputeAverageRequest computeAverageRequest) {
        // create a number from client
        sum += computeAverageRequest.getNumber();
        nums++;
      }

      @Override
      public void onError(Throwable throwable) {}

      @Override
      public void onCompleted() {
        // client send done
        double average = (double) (sum) / nums;
        responseObserver.onNext(ComputeAverageResponse.newBuilder().setAverage(average).build());
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public StreamObserver<FindMaximumRequest> findMaximum(
      StreamObserver<FindMaximumResponse> responseObserver) {
    return new StreamObserver<FindMaximumRequest>() {
      int currentMaximum = 0;

      @Override
      public void onNext(FindMaximumRequest findMaximumRequest) {
        int number = findMaximumRequest.getNumber();
        if (currentMaximum < number) {
          currentMaximum = number;
          responseObserver.onNext(FindMaximumResponse.newBuilder().setMaximum(number).build());
        }
      }

      @Override
      public void onError(Throwable throwable) {}

      @Override
      public void onCompleted() {
        //        send current last maximum
        responseObserver.onNext(
            FindMaximumResponse.newBuilder().setMaximum(currentMaximum).build());
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public void squareRoot(
      SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
    int number = request.getNumber();
    if (number > 0) {
      double numberRoot = Math.sqrt(number);
      responseObserver.onNext(SquareRootResponse.newBuilder().setNumberRoot(numberRoot).build());
      responseObserver.onCompleted();
    } else {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("The number being sent is not positive")
              .augmentDescription("Number sent: " + number)
              .asRuntimeException());
    }
  }
}
