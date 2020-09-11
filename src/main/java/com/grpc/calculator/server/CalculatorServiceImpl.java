package com.grpc.calculator.server;

import com.proto.calculator.*;
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
    StreamObserver<ComputeAverageRequest> requestObserver =
        new StreamObserver<ComputeAverageRequest>() {
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
            responseObserver.onNext(
                ComputeAverageResponse.newBuilder().setAverage(average).build());
            responseObserver.onCompleted();
          }
        };
    return requestObserver;
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
}
