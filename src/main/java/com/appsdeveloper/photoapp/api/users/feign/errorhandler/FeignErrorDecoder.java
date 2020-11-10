package com.appsdeveloper.photoapp.api.users.feign.errorhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

	@Autowired
	private Environment env;
	
	@Override
	public Exception decode(String methodKey, Response response) {
		switch(response.status()) {
		case 404:
			if(methodKey.contains("getAlbums")) {
				return new ResponseStatusException(HttpStatus.valueOf(response.status()),env.getProperty("albums.exception.albums-not-found"));
				/*NOTE: Below HystrixBadRequestException is used if we don't want to call fallback method when there is an exception.
				 * When we have feign error decoder and hystrix fallback, then even for exceptions fallback will
				 * be served and if you do not want that behavior, in the sense when album service is down then
				 * call fallback method and when album service is up and for any exception in feign client call
				 * and you do not want to call fall back in this case, then throw below exception.*/
				//return new HystrixBadRequestException(env.getProperty("albums.exception.albums-not-found"));
			}
		}
		return null;
	}
}
