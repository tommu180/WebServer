package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueueImpl<T> {
	private Queue<T> queue;
	private int limit;
	
	public BlockingQueueImpl(int queueSize) {
		this.queue = new LinkedList<T>();
		this.limit = queueSize;
	}
	
	
	/**
	 * Put element on the queue
	 * @param t object
	 * @throws InterruptedException
	 */
	public void put(T t) throws InterruptedException {
		while (this.queue.size() == this.limit) {
			synchronized (queue) {
				queue.wait();
			}
		}
		synchronized (queue) {
			queue.add(t);
			queue.notify();
		}
	}
	
	
	/**
	 * Dequeue element
	 * @return the first element on the queue
	 * @throws InterruptedException
	 */
	public T take() throws InterruptedException {
		while (queue.isEmpty()) {
			synchronized (queue) {
				queue.wait();
			}
		}
		synchronized (queue) {
			queue.notify();
			return queue.poll();
		}
	}
}
