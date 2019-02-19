package com.template.ie.intergration.user;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.collect.Maps;
import com.template.ie.intergration.model.User;

import rx.Observable;
import rx.Observer;

@RestController
public class AggregationController {

	private Logger logger = LoggerFactory.getLogger(AggregationController.class); 
	
	@Autowired
	private AggregationService aggregationService;

	/**
	 * feign - 调用测试
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DeferredResult<HashMap<String, User>> aggregate(@PathVariable Long id) {
		logger.info("begin " + "id" + " end");
		Observable<HashMap<String, User>> result = this.aggregateObservable(id);
		return this.toDeferredResult(result);
	}
	public Observable<HashMap<String, User>> aggregateObservable(Long id){
		return Observable.zip(
				this.aggregationService.getUserById(id),
				this.aggregationService.getMovieUserById(id),
				(user, movieUser) -> {
					HashMap<String, User> map = Maps.newHashMap();
					map.put("user", user);
					map.put("movieUser", movieUser);
					return map;
				}
		);
	}
	public DeferredResult<HashMap<String, User>> toDeferredResult(Observable<HashMap<String, User>> details){
		DeferredResult<HashMap<String, User>> result = new DeferredResult<>();
		// 订阅
		details.subscribe(new Observer<HashMap<String, User>>(){
			
			@Override
			public void onCompleted() {
				logger.info("完成...");
			}
			
			@Override
			public void onError(Throwable throwable) {
				logger.info("发生错误...", throwable);
			}
			
			@Override
			public void onNext(HashMap<String, User> movieDetails) {
				result.setResult(movieDetails);
			}
		});
		return result;
	}
}