package com.sudha.tasktracker.repository;

import com.sudha.tasktracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
