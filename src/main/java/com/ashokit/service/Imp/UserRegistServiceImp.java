package com.ashokit.service.Imp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.ashokit.constant.MessageConstant;
import com.ashokit.dto.ActivationDto;
import com.ashokit.dto.UserDto;
import com.ashokit.emailservice.EmailSenderService;
import com.ashokit.entity.UserEntity;
import com.ashokit.repository.UserRegistRepository;
import com.ashokit.service.UserRegistService;
import com.ashokit.ui.request.model.ActivationModel;
import com.ashokit.ui.request.model.LoginRequestModel;
import com.ashokit.ui.request.model.PasswordRestModel;
import com.ashokit.ui.response.model.StatusResponseModel;
import com.ashokit.utility.Utility;

@Service
public class UserRegistServiceImp implements UserRegistService {
	@Autowired
	UserRegistRepository userRepository;
	@Autowired
	Utility utility;
	@Autowired
	EmailSenderService emailSenderService;

	@Override
	public UserDto registerUser(UserDto userDetail) {
		UserEntity userEntity = new UserEntity();
		UserDto returnValue = new UserDto();
		UserEntity restUserEntity = userRepository.findUserByEmail(userDetail.getEmail());
		if (restUserEntity != null) {
			userDetail.setUserId(restUserEntity.getUserId());
			userDetail.setStatus(MessageConstant.EMAIL_ID_ALL_READY_REGISTER.name());
			BeanUtils.copyProperties(userDetail, returnValue);
			return returnValue;

		}

		else {

			userDetail.setUserId(utility.generateUserId(20));
			userDetail.setTemppassword(utility.generateTemPassword(20));
			userDetail.setStatus(MessageConstant.LOCK.name());
			BeanUtils.copyProperties(userDetail, userEntity);
			UserEntity registerUserEntity = userRepository.save(userEntity);
			// returnValue.setStatus(MessageConstant.YOU_HAVE_REGISTER_SUCCESSFULLY_PLEASE_CHECK_EMAIL_FOR_ACTIVATION.name());
			BeanUtils.copyProperties(registerUserEntity, returnValue);
			return returnValue;
		}

	}

	@Override
	public ActivationDto activateUser(String email, ActivationDto activateDetails) {
		ActivationDto returnValue = new ActivationDto();
		UserEntity UserEntityByEmail = userRepository.findUserByEmail(email);
		if (UserEntityByEmail.getTemppassword().equals(activateDetails.getTemp_Password())) {
			UserEntityByEmail.setPassword(activateDetails.getNew_Password());
			UserEntityByEmail.setStatus(MessageConstant.UNLOCK.name());
			UserEntityByEmail.setTemppassword(null);

			UserEntity unolockUser = userRepository.save(UserEntityByEmail);
			BeanUtils.copyProperties(unolockUser, returnValue);
			returnValue.setStatus(MessageConstant.YOUR_ACCOUNT_ACTIVATED_SUCCESSFULLY.name());
			return returnValue;
		} else {
			returnValue.setStatus(MessageConstant.PLEASE_CHECK_TEMPORARY_YOU_HAVE_GIVEN_WRONG_PASSWORD.name());
			return returnValue;
		}

	}

	@Override
	public void sendMail(UserDto userDetails) {
		/*
		 * Path filepath = Paths.get("src/main/resources","email-body.txt"); String str
		 * = ""; try { Stream<String> lines = Files.lines(filepath);
		 * 
		 * str = Stream.of(lines).toString(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 */
		UserEntity restUserEntity = userRepository.findUserByEmail(userDetails.getEmail());
		String tem_password = restUserEntity.getTemppassword();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(userDetails.getEmail());
		mailMessage.setSubject("Activate your Account");
		mailMessage.setFrom("infydesign07@gmail.com");
		mailMessage.setText("Welcome to Ashok-IT  \n" + "To activate your account, please click here : "
				+ "http://localhost:8080/users/account-activate?email=" + "\n your Temporary Password is :"
				+ tem_password);

		// mailMessage.setText(str.toString());
		emailSenderService.sendEmail(mailMessage);

	}

	@Override
	public StatusResponseModel userLogin(LoginRequestModel loginRequestModel) {
		StatusResponseModel returnValue = new StatusResponseModel();
		UserEntity userEntity = userRepository.findUserByEmail(loginRequestModel.getEmail());
		if (userEntity.getStatus().equals(MessageConstant.LOCK)) {
			returnValue.setStatus(MessageConstant.BEFORE_LOGING_PLEASE_ACTIVATE_YOUR_ACCOUNT.name());
		} else {
			if (userEntity.getPassword().equals(loginRequestModel.getPassword())) {
				returnValue.setStatus(MessageConstant.YOU_HAVE_SUCCEFULLY_LOGING.name());
			}
		}

		return returnValue;

	}

	

	@Override
	public boolean sendMailforPasswordRestRequest(String email, String temp_Password) {
		/*
		 * Path filepath = Paths.get("src/main/resources","email-body.txt"); String str
		 * = ""; try { Stream<String> lines = Files.lines(filepath);
		 * 
		 * str = Stream.of(lines).toString(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 */
		
		
         
           SimpleMailMessage mailMessage = new SimpleMailMessage();
      		mailMessage.setTo(email);
      		mailMessage.setSubject("Forget_Password_Request");
      		mailMessage.setFrom("infydesign07@gmail.com");
      	    mailMessage.setText("Welcome to Ashok-IT  \n"
      	    		       + "Please user Temporary_Password for reset your password  : "
      		            +"http://localhost:8080/users/forget-password?email="+"\n your Temporary Password is :"+temp_Password);
      	    
      		 // mailMessage.setText(str.toString());
      		 emailSenderService.sendEmail(mailMessage);
      		return true;	
		 
	}

	@Override
	public StatusResponseModel forgetPasswordRequest(String email) {
		StatusResponseModel returnValue = new StatusResponseModel();
		UserEntity restUserEntity = userRepository.findUserByEmail(email);
		String temp_password = utility.generateTemPassword(20);
		restUserEntity.setTemppassword(temp_password);
		userRepository.save(restUserEntity);
		sendMailforPasswordRestRequest(email, temp_password);
		returnValue.setStatus(MessageConstant.TEMP_PASSWORD_HAS_SENT_PLEASE_CHECK_MAIL_FOR_RESET_PASSWORD.name());
		return returnValue;
	}
	
	@Override
	public StatusResponseModel passwordRest(PasswordRestModel passwordRestModel, String email) {
		UserEntity UserEntityByEmail = userRepository.findUserByEmail(email);
		StatusResponseModel returnValue = new StatusResponseModel();
		if (UserEntityByEmail.getTemppassword().equals(passwordRestModel.getTemp_Password())) {
			UserEntityByEmail.setPassword(passwordRestModel.getNew_Password());
			//UserEntityByEmail.setStatus(MessageConstant.UNLOCK.name());
			UserEntityByEmail.setTemppassword(null);

			UserEntity unolockUser = userRepository.save(UserEntityByEmail);
		
			returnValue.setStatus(MessageConstant.YOUR_ACCOUNT_PASSWORD_RESET_SUCCESSFULLY.name());
			return returnValue;
		} 
		else {
			returnValue.setStatus(MessageConstant.PLEASE_CHECK_TEMPORARY_YOU_HAVE_GIVEN_WRONG_PASSWORD.name());
			return returnValue;
		}

	}

}
