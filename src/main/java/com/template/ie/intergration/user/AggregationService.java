package com.template.ie.intergration.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.template.ie.intergration.model.User;

import rx.Observable;

@Service
public class AggregationService {

	private Logger logger = LoggerFactory.getLogger(AggregationService.class); 
	
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "fallback")
	public Observable<User> getUserById(Long id){
		// 创建一个被观察者
		logger.info("用户");
		return Observable.create(observer -> {
				// 请求用户组件的/{id}端点
				User user = restTemplate.getForObject("http://user/ant/{id}", User.class, id);
				observer.onNext(user);
				observer.onCompleted();
				});
	}
	
	@HystrixCommand(fallbackMethod = "fallback")
	public Observable<User> getMovieUserById(Long id){
		// 创建一个被观察者
		logger.info("电影");
		return Observable.create(observer -> {
				// 请求电影组件的/{id}端点
				User movieUser = restTemplate.getForObject("http://movie/ant/{id}", User.class, id);
				observer.onNext(movieUser);
				observer.onCompleted();
				});
	}
	
	public User fallback(Long id) {
		User user = new User();
		user.setId(-1L);
		return user;
	}
}