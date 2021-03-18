package com.ashokit.service;

import com.ashokit.dto.ActivationDto;
import com.ashokit.dto.UserDto;
import com.ashokit.ui.request.model.LoginRequestModel;
import com.ashokit.ui.request.model.PasswordRestModel;
import com.ashokit.ui.response.model.StatusResponseModel;


public interface UserRegistService {
	
	UserDto registerUser(UserDto userDetail);
	ActivationDto activateUser(String email,ActivationDto activateDetails);
    void sendMail(UserDto userDto);
    StatusResponseModel userLogin( LoginRequestModel loginRequestModel);
    StatusResponseModel passwordRest(PasswordRestModel passwordRestModel,String email);
    boolean sendMailforPasswordRestRequest(String email,String temp_Password);
    StatusResponseModel forgetPasswordRequest(String email);
    
	
}
