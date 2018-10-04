package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.beans.NameConverterBean;

@Configuration
public class RedisConfig {

	@Bean
	public NameConverterBean nameConverter() {
		return new NameConverterBean() ;
	}
	
}
