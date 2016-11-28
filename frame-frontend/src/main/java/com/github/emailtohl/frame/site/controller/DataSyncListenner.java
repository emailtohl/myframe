package com.github.emailtohl.frame.site.controller;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.emailtohl.frame.cdi.Context;
import com.github.emailtohl.frame.site.service.SupplierDataSync;
import com.github.emailtohl.frame.site.service.impl.SupplierDataSyncImpl;
import com.github.emailtohl.frame.transition.TransitionProxy;

/**
 * Application Lifecycle Listener implementation class DataSyncListenner
 *
 */
@WebListener
public class DataSyncListenner implements ServletContextListener {
	private final static Logger logger = Logger.getLogger(DataSyncListenner.class.getName());
	private Context context;
	private SupplierDataSync supplierDataSyncProxy;

	/**
	 * 示例中演示了三种线程启动的方法，其中一条线程以Future返回执行结果
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		final ServletContext sc = event.getServletContext();
		context = (Context) sc.getAttribute("context");
		supplierDataSyncProxy = TransitionProxy.getProxy(context.getInstance(SupplierDataSyncImpl.class));
		String dataSyncTimeDelayTime = sc.getInitParameter("DataSyncTimeDelayTime");
		final long time = Long.parseLong(dataSyncTimeDelayTime);

		class Task implements Runnable {
			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						supplierDataSyncProxy.syncSupplierData();
						Thread.sleep(time);
					}
				} catch (InterruptedException e) {
					// e.printStackTrace();
					logger.finest("中断退出");
				}
			}
		}
		// 传统方式，使用线程对象执行任务
		Thread thread = new Thread(new Task());
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.log(Level.SEVERE, "未捕获的异常" + t + "线程结束", e);
			}
		});
		thread.start();
		sc.setAttribute("syncThread", thread);

		// 高级层次，使用用Executor管理线程
		final ExecutorService exec = Executors.newCachedThreadPool();
		Timer t1 = new Timer(), t2 = new Timer();
		
		t1.schedule(new TimerTask() {
			@Override
			public void run() {
				// 启动线程的第二种方式，推荐使用
				exec.execute(new Task());
			}
		}, 6000);
		sc.setAttribute("exec", exec);

		t2.schedule(new TimerTask() {
			@Override
			public void run() {
				// 下面是返回Future结果的方式
				Future<Integer> future = exec.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						int count = 0;
						try {
							while (!Thread.interrupted()) {
								supplierDataSyncProxy.syncSupplierData();
								count++;
								TimeUnit.MILLISECONDS.sleep(time);
							}
						} catch (InterruptedException e) {
							// e.printStackTrace();
							logger.finest("中断退出");
						}
						return count;
					}
				});
				sc.setAttribute("future", future);
			}
		}, 12000);
		
		sc.setAttribute("t1", t1);
		sc.setAttribute("t2", t2);
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
		Thread thread = (Thread) sc.getAttribute("syncThread");
		thread.interrupt();

		ExecutorService exec = (ExecutorService) sc.getAttribute("exec");
		exec.shutdownNow();
		
		Timer t1 = (Timer) sc.getAttribute("t1");
		Timer t2 = (Timer) sc.getAttribute("t2");
		t1.cancel();
		t2.cancel();
		
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver d = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(d);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			@SuppressWarnings("unchecked")
			Future<Integer> future = (Future<Integer>) sc.getAttribute("future");
			if (future != null) {
				Integer count = future.get();
				logger.info("The result of Future is : " + count);
			}
		} catch (InterruptedException | ExecutionException e) {
			// e.printStackTrace();
			logger.log(Level.INFO, "Future结果获取失败", e);
		}
	}

}
