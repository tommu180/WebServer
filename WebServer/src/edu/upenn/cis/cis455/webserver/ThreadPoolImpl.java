package edu.upenn.cis.cis455.webserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis.cis455.webserver.api.ServletEngine;

public class ThreadPoolImpl {
	private static List<WorkerThreadImpl> workerThreads = new ArrayList<WorkerThreadImpl>();
	
	public ThreadPoolImpl(int NumOfThreads, BlockingQueueImpl<Socket> reqQueue, String rootDir, ServletEngine appServe) {
		for (int i = 0; i < NumOfThreads; i++) {
			workerThreads.add(new WorkerThreadImpl(reqQueue, rootDir, appServe));
		}
		for (WorkerThreadImpl workerThread : workerThreads) {
			workerThread.start();
		}
	}
	
	/**
	 * Stop worker threads
	 */
	public void interruptWorkerThread() {
		for (WorkerThreadImpl thread : workerThreads) {
			if (thread.getUrl() == null) {
				thread.interrupt();
			}
			thread.shutDownThread();
		}
	}
	
	/**
	 * @return a list of states of the threads
	 */
	public static ArrayList<String> getStates() {
		ArrayList<String> states = new ArrayList<String>();
		for (WorkerThreadImpl thread : workerThreads) {
			long id = thread.getId() - 7;
			Thread.State threadState = thread.getState();
			System.out.println(String.valueOf(id) + ":" + threadState.toString() + thread.getUrl());
			states.add(String.valueOf(id + ":" + threadState.toString() + " " +thread.getUrl()));
		}
		return states;
	}
}
