package com.sourcecard.servlets;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public class brcps_contextlistener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		//getting  thread pool executor attribute for the servelet context
		ThreadPoolExecutor executor = (ThreadPoolExecutor) servletContextEvent
			.getServletContext().getAttribute("executor");
		System.out.println("ThreadpoolExecutor Destroyed");
		//shutdown the executor		
		executor.shutdown();
		
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// create the thread pool
		ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 50000L,
			TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));
				 
		//setting a threadpool executor attribute
		servletContextEvent.getServletContext().setAttribute("executor",executor);
		System.out.println("ThreadpoolExecutor Created");
	}

}
