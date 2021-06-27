package com.huanghe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AspectLogDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspectLogDemoApplication.class, args);
	}
}
