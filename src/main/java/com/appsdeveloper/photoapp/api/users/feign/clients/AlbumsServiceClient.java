package com.appsdeveloper.photoapp.api.users.feign.clients;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appsdeveloper.photoapp.api.users.ui.model.AlbumResponseModel;
import com.netflix.client.ClientException;

import feign.hystrix.FallbackFactory;

@FeignClient(name="albums-ws", fallbackFactory=AlbumsFallbackFactory.class)
@RibbonClient(name = "albums-ws")
public interface AlbumsServiceClient {

	@GetMapping("/users/{id}/albums")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}


class AlbumsFallback implements AlbumsServiceClient {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Throwable cause;
	
	public AlbumsFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public List<AlbumResponseModel> getAlbums(String id) {
		List<AlbumResponseModel> response = new ArrayList<>();
		AlbumResponseModel albumResponseModel = new AlbumResponseModel();
		response.add(albumResponseModel);
		if(cause.getCause() instanceof ClientException) {
			logger.error("Load balancer client exception as album service is down.");
			albumResponseModel.setServiceDown(true);
		} else {
			logger.error("Album service is up and running but some other exception.");
			albumResponseModel.setAnyException(true);
		}
		return response;
	}
	
}

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient> {

	@Override
	public AlbumsServiceClient create(Throwable cause) {
		return new AlbumsFallback(cause);
	}
	
}