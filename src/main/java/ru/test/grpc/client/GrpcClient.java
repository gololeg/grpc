package ru.test.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.test.grpc.NumberRequest;
import ru.test.grpc.NumberResponse;
import ru.test.grpc.NumberServiceGrpc;
import ru.test.grpc.NumberServiceGrpc.NumberServiceStub;

public class GrpcClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcClient.class.getName());

  static int currentValue, lastValue,newValue;
  public static void main(String[] args) throws InterruptedException {


    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build();

    NumberServiceStub stub
        = NumberServiceGrpc.newStub(channel);

    StreamObserver<NumberResponse> responseObserver = new StreamObserver<NumberResponse>() {
      @Override
      public void onNext(NumberResponse helloResponse) {
        lastValue = helloResponse.getNewValue();
        LOG.info("newValue: " + lastValue);
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        LOG.info("Completed");
      }

    };
    StreamObserver<NumberRequest> requestObserver = stub.start(responseObserver);

    try {
      for (int i = 0; i < 50; i++) {
        requestObserver.onNext(NumberRequest.newBuilder()
                .setFirstValue(0)
            .setLastValue(30)
            .build());
        Thread.sleep(1000);
        currentValue = currentValue + (lastValue == newValue ? 0 : lastValue) + 1;
        newValue = lastValue;
        LOG.info("currentValue:" + currentValue);
      }

    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }
    requestObserver.onCompleted();
  }


}
