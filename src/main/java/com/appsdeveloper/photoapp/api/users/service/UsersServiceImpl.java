package com.appsdeveloper.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloper.photoapp.api.users.data.UserEntity;
import com.appsdeveloper.photoapp.api.users.data.UsersRepository;
import com.appsdeveloper.photoapp.api.users.feign.clients.AlbumsServiceClient;
import com.appsdeveloper.photoapp.api.users.shareddto.UserDto;
import com.appsdeveloper.photoapp.api.users.ui.model.AlbumResponseModel;

@Service
public class UsersServiceImpl implements UsersService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private AlbumsServiceClient albumsServiceClient;
	
	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncyrptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

		usersRepository.save(userEntity);
		
		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(username);
		if(!userEntityOptional.isPresent()) {
			throw new UsernameNotFoundException(username);
		}
		UserEntity userEntity = userEntityOptional.get();
		return new User(userEntity.getEmail(),userEntity.getEncyrptedPassword(),true,true,true,true,new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
		if(!userEntityOptional.isPresent()) {
			throw new UsernameNotFoundException(email);
		}
		UserEntity userEntity = userEntityOptional.get();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		Optional<UserEntity> userEntityOptional = usersRepository.findByUserId(userId);
		if(!userEntityOptional.isPresent()) {
			throw new UsernameNotFoundException("User not found");
		}
		UserEntity userEntity = userEntityOptional.get();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto userDto = modelMapper.map(userEntity, UserDto.class);
		
		logger.info("Before calling albums microservice");
		List<AlbumResponseModel> albums = albumsServiceClient.getAlbums(userId);
		logger.info("After calling albums microservice");

		userDto.setAlbums(albums);
		return userDto;
	}

}
