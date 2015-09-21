package com.sourcecard.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;

public class brcps_asynclistener implements AsyncListener{

	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		System.out.println("AppAsyncListener onComplete");
		//it is illegal to call any of the responses after oncomplete is called
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		System.out.println("AppAsyncListener onError");
		//we can return error response to client
		ServletResponse response = event.getAsyncContext().getResponse();
		PrintWriter out = response.getWriter();
		out.write("asynclistener error");
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		System.out.println("AppAsyncListener onStartAsync");
		//we can log the event here
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		System.out.println("AppAsyncListener onTimeout");
		//we can send appropriate response to client
		ServletResponse response = event.getAsyncContext().getResponse();
		PrintWriter out = response.getWriter();
		out.write("timeout on asynclistener");
	}
}
