package com.example.questionnaire.dao;

import com.example.questionnaire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

    @Query("SELECT DISTINCT u FROM User u WHERE u.username = :username")
    User findDistinctByUsername(@Param("username") String username);
}
