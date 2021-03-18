package com.ashokit.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ashokit.constant.MessageConstant;
import com.ashokit.dto.ActivationDto;
import com.ashokit.dto.UserDto;
import com.ashokit.service.UserRegistService;
import com.ashokit.ui.request.model.ActivationModel;
import com.ashokit.ui.request.model.LoginRequestModel;
import com.ashokit.ui.request.model.PasswordRestModel;
import com.ashokit.ui.request.model.UserDetailsRequest;
import com.ashokit.ui.response.model.StatusResponseModel;
import com.ashokit.ui.response.model.UserRest;



@RestController
@RequestMapping("/users")
public class UserRegistController {
	@Autowired
	UserRegistService userService;
	
	//http://localhost:8080/users/registration
	@PostMapping(path = "/registration")
	public UserRest userRegistation(@RequestBody UserDetailsRequest userDetails) {
		UserRest returnValue = new UserRest();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto registerUserDto= userService.registerUser(userDto);
		//SimpleMailMessage mailMessage = new SimpleMailMessage();
		if(registerUserDto.getStatus().equals(MessageConstant.EMAIL_ID_ALL_READY_REGISTER.name())) {
			BeanUtils.copyProperties(registerUserDto, returnValue); 
			return returnValue;
		}
		else {
			userService.sendMail(userDto);
			registerUserDto.setStatus(MessageConstant.YOU_HAVE_REGISTER_SUCCESSFULLY_PLEASE_CHECK_EMAIL_FOR_ACTIVATION.name());
			BeanUtils.copyProperties(registerUserDto, returnValue); 
			//returnValue.setStatus(MessageConstant.PLEASE_CHECK_EMAIL_FOR_ACTIVATION.name());
			
			return returnValue;
		}
		
	}
	//http://localhost:8080/users/email-verification?email=test@test.com
	 @PostMapping(path = "/email-verification")
	public StatusResponseModel activateUser(@RequestParam(value = "email") String email, @RequestBody ActivationModel activateDetails) {
		 StatusResponseModel returnValue = new StatusResponseModel();
		ActivationDto activeDto = new ActivationDto();
		BeanUtils.copyProperties(activateDetails, activeDto); 
		ActivationDto activeSucces=userService.activateUser(email,activeDto);
		returnValue.setStatus(MessageConstant.YOUR_ACCOUNT_ACTIVATED_SUCCESSFULLY.name());
		return returnValue;	
		
	}
	
	//http://localhost:8080/users/user-login
		 @PostMapping(path = "/user-login")
		public StatusResponseModel userLogin(@RequestBody LoginRequestModel loginRequestModel) {
			
			 StatusResponseModel returnValue = userService.userLogin(loginRequestModel);
			return returnValue;	
			
		}
		 
		 
		//http://localhost:8080/users/forget-password?email=test@test.com
		 @GetMapping(path = "/forget-password")
		public StatusResponseModel forgetPasswordRequest(@RequestParam(value = "email") String email) {
	
			 StatusResponseModel returValue =  userService.forgetPasswordRequest(email);
			
			 return returValue;	
			
		}	 	 
		 
		//http://localhost:8080/users/reset-password?email=test@test.com
		 @PutMapping(path = "/reset-password")
		public StatusResponseModel resetPassword(@RequestParam(value = "email") String email,@RequestBody PasswordRestModel passwordRestModel) {
			// StatusResponseModel returnValue = new StatusResponseModel();
			 StatusResponseModel returnValue=userService.passwordRest(passwordRestModel,email);
			return returnValue;	
			
		}	 
	 @GetMapping("/hello0/{name}")
	 public String hello0(@PathVariable("name") String name)
	 {
		 System.out.println(name);
	     return "Hello " + name;
	 }
	

}
