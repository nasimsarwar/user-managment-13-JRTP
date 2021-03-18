package com.ashokit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ashokit.entity.UserEntity;
@Repository
public interface UserRegistRepository extends JpaRepository<UserEntity, Long> {
	
	UserEntity findUserByEmail(String email);

}
