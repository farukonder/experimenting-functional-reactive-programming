package com.github.farukonder.experimenting.frp.rxjava2.service;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;

import org.example.web4.NewOperationResponse;
import org.example.web4.Web4_Service;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class CallRxJava2_2 {

	static Web4_Service web4 = new Web4_Service();

	public static void main(String[] args) throws InterruptedException {
		
		Flowable.just(5000, 100, 100, 100, 100, 9000, 10000)
		.subscribeOn(Schedulers.single())
		.observeOn(Schedulers.single())
		.flatMap(w -> Flowable.create(emitter -> web4.getService4().newOperationAsync(w, createHandlerJava8(w,emitter)),BackpressureStrategy.BUFFER))
		.blockingSubscribe((i) -> System.out.println("o: " + ((NewOperationResponse) i).getOut()), (e) -> System.out.println("e: " + e));
		
	}

	public static <T> AsyncHandler<NewOperationResponse> createHandlerJava8(int wait, Emitter<Object> emitter) {

		AsyncHandler<NewOperationResponse> back = res -> {
			try {
				
				NewOperationResponse newOperationResponse = res.get();
				System.out.println("wait: " + wait + " newOperationResponse: " + newOperationResponse);
				
				emitter.onNext(newOperationResponse);
				emitter.onComplete();
			
			} catch (InterruptedException | ExecutionException e) {
				
				emitter.onError(e);
			}
		};

		return back;
	}


}
