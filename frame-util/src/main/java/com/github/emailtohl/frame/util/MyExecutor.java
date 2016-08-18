package com.github.emailtohl.frame.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
/**
 * 实验性质，学习之用，并不可靠
 * 线程池的执行器
 * 
 * @author helei
 * 2016.05.18
 */
public class MyExecutor implements Executor {
	private Thread[] ts;
	private volatile boolean shutdown = false;
	private BlockingQueue<Runnable> q;

	public MyExecutor(int count) {
		// q = new LinkedBlockingQueue<Runnable>();
		q = new MyBlockingQueue<Runnable>();
		ts = new Thread[count];
		for (int i = 0; i < count; i++) {
			ts[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					task();
				}
			});
			// ts[i].setDaemon(true);
			ts[i].start();
		}
	}

	private void task() {
		try {
			while (!Thread.interrupted() && !shutdown) {
				Runnable r = q.take();
				r.run();
			}
		} catch (InterruptedException e) {
			// e.printStackTrace();
			System.out.println(Thread.currentThread().getName() + " 中断退出");
		}
	}

	public void shutdown() {
		shutdown = true;
		for (int i = 0; i < ts.length; i++) {
			ts[i].interrupt();
		}
	}

	@Override
	public void execute(Runnable command) {
		try {
			q.put(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

class MyBlockingQueue<T> extends LinkedList<T> implements BlockingQueue<T> {
	private static final long serialVersionUID = 1959746295531999932L;
	private Object lock = new Object();

	@Override
	public void put(T e) throws InterruptedException {
		synchronized (lock) {
			super.add(e);
			lock.notifyAll();
		}
	}

	@Override
	public T take() throws InterruptedException {
		synchronized (lock) {
			while (peek() == null) {
				lock.wait();
			}
			return super.poll();
		}
	}
	
	@Override
	public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		throw new UnsupportedOperationException();
	}

}