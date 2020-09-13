package com.appsdeveloper.photoapp.api.users.ui.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloper.photoapp.api.users.service.UsersService;
import com.appsdeveloper.photoapp.api.users.shareddto.UserDto;
import com.appsdeveloper.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdeveloper.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.appsdeveloper.photoapp.api.users.ui.model.UserResponseModel;

@RestController
@RequestMapping(path = "/users")
public class UsersController {

	@Autowired
	private Environment env;

	@Autowired
	private UsersService usersService;

	@GetMapping("/status/check")
	public String status() {
		return "working on port: " + env.getProperty("local.server.port")+" and token = "+env.getProperty("token.secret");
	}

	@PostMapping(
			consumes =  { 
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE
					}, 
					produces =  { 
							MediaType.APPLICATION_XML_VALUE,
							MediaType.APPLICATION_JSON_VALUE
							} 
			)
	public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = usersService.createUser(userDto);
		
		CreateUserResponseModel response = modelMapper.map(createdUser, CreateUserResponseModel.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PreAuthorize("principal == #userId")
	@GetMapping(path = "/{userId}",produces =  { 
							MediaType.APPLICATION_XML_VALUE,
							MediaType.APPLICATION_JSON_VALUE
							} 
			)
	public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId) {
		UserDto userDto = usersService.getUserByUserId(userId);
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserResponseModel returnValue = modelMapper.map(userDto, UserResponseModel.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);	
	}
}
