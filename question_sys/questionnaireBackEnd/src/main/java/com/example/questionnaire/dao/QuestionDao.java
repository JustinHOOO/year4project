package com.example.questionnaire.dao;

import com.example.questionnaire.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionDao extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.questionnaire.id = :questionnaireId")
    List<Question> findAllByQuestionnaireId(@Param("questionnaireId") Integer questionnaireId);

    @Query("SELECT DISTINCT q FROM Question q WHERE q.id = :questionId")
    Question findDistinctByQuestionId(@Param("questionId") Integer questionId);
}
