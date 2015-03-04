package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.upenn.cis.cis455.webserver.api.ServletEngine;

class HttpServer extends Thread {

	private BlockingQueueImpl<Socket> reqQueue;
	private ThreadPoolImpl workerThreads;
	private static ServerSocket serverSocket;
	private static boolean isStop = false;
	private ServletEngine appServe;

	/**
	 * Start a server with 3 parameters
	 * @param portNumber
	 * @param rootDir
	 * @param webxmlLoc
	 */
	public HttpServer(int portNumber, String rootDir, String webxmlLoc) {
		try {
			serverSocket = new ServerSocket(portNumber);
			serverSocket.setSoTimeout(1000);
			this.appServe = new ServletEngine(webxmlLoc);
			LogRecorder.addMessage("Started Server");	
		} catch (IOException e) {
			System.out.println("Server cannot be created");
			LogRecorder.addErrorMessage(e, "Server Cannot created");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("App server cannot be created");
			LogRecorder.addErrorMessage(e, "App server cannot be created");
			e.printStackTrace();
		}
		this.reqQueue = new BlockingQueueImpl<Socket>(1000);
		this.workerThreads = new ThreadPoolImpl(10, reqQueue, rootDir, appServe);
		
	}

	public static ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * server started and handle requests
	 */
	public void run() {
		while (!isStop) {
			try {
				//this.workerThreads.getStates();
				reqQueue.put(serverSocket.accept());
			} catch (SocketTimeoutException e) {
				
			} catch (IOException e) {
				isStop = true;
			} catch (InterruptedException e) {
				System.out.println("InterruptedException when enqueue request");
				isStop = true;
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Server cannot be shut");
			LogRecorder.addErrorMessage(e, "Server cannot be shut");
			e.printStackTrace();
		}
		System.out.println("Server is shutting down");
		workerThreads.interruptWorkerThread();
		appServe.shutdown();
	}

	/**
	 * Server shut down.
	 */
	public static void shutdownServer() {
		isStop = true;
	}

	public static void main(String args[]) {
		if (args.length < 3) {
			System.out.println("Name: Chunxiao Mu \nLogin: chunxmu");
			return;
		}
		
		int portNumber = Integer.parseInt(args[0]);
		String rootDirectory = args[1];
		String webxmlLoc = args[2];
		
		HttpServer webServer = new HttpServer(portNumber, rootDirectory, webxmlLoc);
		webServer.start();
	}
}
