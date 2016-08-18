package com.github.emailtohl.frame.util;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyExecutorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecute() {
		// 执行器中生成3个线程
		Executor exec = new MyExecutor(3);
		// 传入100个任务给执行器
		for (int i = 0; i < 100; i++) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						for (int j = 0; j < 100; j++) {
							System.out.println(Thread.currentThread().getName() + " : " + j);
							TimeUnit.MILLISECONDS.sleep(5);
						}
					} catch (InterruptedException e) {
						System.out.println(Thread.currentThread().getName() + " : 睡眠中被中断 ");
						// e.printStackTrace();
					}
				}
			});
		}
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((MyExecutor) exec).shutdown();
	}
}
