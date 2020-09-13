package com.appsdeveloper.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloper.photoapp.api.users.shareddto.UserDto;

public interface UsersService extends UserDetailsService {
	UserDto createUser(UserDto userDetails);
	
	UserDto getUserDetailsByEmail(String email);
	
	UserDto getUserByUserId(String userId);
}
