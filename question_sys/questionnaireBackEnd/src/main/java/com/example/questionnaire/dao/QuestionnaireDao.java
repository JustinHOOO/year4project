package com.example.questionnaire.dao;

import com.example.questionnaire.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionnaireDao extends JpaRepository<Questionnaire, Integer> {
    
    @Query("SELECT q FROM Questionnaire q WHERE q.username = :username")
    List<Questionnaire> findAllByUsername(@Param("username") String username);

    @Query("SELECT q FROM Questionnaire q WHERE q.id = :questionnaireId")
    Questionnaire findByQuestionnaireId(@Param("questionnaireId") Integer questionnaireId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Questionnaire q WHERE q.id = :questionnaireId")
    void deleteDistinctByQuestionnaireId(@Param("questionnaireId") Integer questionnaireId);
}
