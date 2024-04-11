package ru.test.grpc.server;

import ru.test.grpc.NumberRequest;
import ru.test.grpc.NumberResponse;
import ru.test.grpc.NumberServiceGrpc.NumberServiceImplBase;
import io.grpc.stub.StreamObserver;

public class NumberServiceImpl extends NumberServiceImplBase {

  @Override
  public StreamObserver<NumberRequest> start(
      StreamObserver<NumberResponse> responseObserver) {
    return new StreamObserver<NumberRequest>() {
      @Override
      public void onNext(NumberRequest request) {

        for (int i = request.getFirstValue(); i <= request.getLastValue(); i++) {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          responseObserver.onNext(NumberResponse.newBuilder()
              .setNewValue(i + 1)
              .build());

        }
        responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }

      //handle OnError() ...
    };

  }
}
