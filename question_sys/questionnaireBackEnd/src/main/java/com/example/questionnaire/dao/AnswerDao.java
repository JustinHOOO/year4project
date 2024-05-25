package com.example.questionnaire.dao;

import com.example.questionnaire.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerDao extends JpaRepository<Answer, Integer> {

    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId")
    List<Answer> findAllByQuestionId(@Param("questionId") Integer questionId);
}
