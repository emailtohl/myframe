package com.github.emailtohl.frame.ioc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.frame.ioc.testsite.service.OtherService;
import com.github.emailtohl.frame.ioc.testsite.service.SomeService;

public class WatchSpring {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		System.out.println(context.getBean("someController"));
//		System.out.println(context.getBean("baseRepository"));
		System.out.println(context.getBean("someRepository"));
		System.out.println(context.getBean("someOneUtil"));
		System.out.println(context.getBean(SomeService.class));
		System.out.println(context.getBean(OtherService.class));
	}
}
