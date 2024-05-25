package com.example.questionnaire.dao;

import com.example.questionnaire.model.QuestionnaireIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import java.util.List;

public interface QuestionnaireIpDao extends JpaRepository<QuestionnaireIp, Integer> {

    default List<QuestionnaireIp> findAllByQuestionnaireIdAndIp(Integer questionnaireId, String ip) {
        QuestionnaireIp probe = new QuestionnaireIp();
        probe.setQuestionnaireId(questionnaireId);
        probe.setIp(ip);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("questionnaireId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("ip", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<QuestionnaireIp> example = Example.of(probe, matcher);
        return findAll(example);
    }
}
