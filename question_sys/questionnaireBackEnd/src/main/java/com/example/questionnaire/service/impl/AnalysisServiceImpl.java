package com.example.questionnaire.service.impl;

import com.example.questionnaire.dao.AnswerDao;
import com.example.questionnaire.dao.QuestionDao;
import com.example.questionnaire.dao.QuestionnaireDao;
import com.example.questionnaire.model.Answer;
import com.example.questionnaire.model.Question;
import com.example.questionnaire.service.AnalysisService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final Gson gson;

    public AnalysisServiceImpl(QuestionnaireDao questionnaireDao, QuestionDao questionDao, AnswerDao answerDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
        this.gson = new Gson();
    }

    @Override
    public String getQuestionnairesByUsername(String username) {
        List<?> questionnaires = questionnaireDao.findAllByUsername(username);
        JsonObject res = new JsonObject();
        res.add("questionnaires", gson.toJsonTree(questionnaires));
        return gson.toJson(res);
    }

    @Override
    public String getQuestionnaireById(Integer questionnaireId) {
        return gson.toJson(questionnaireDao.findByQuestionnaireId(questionnaireId));
    }

    @Override
    public String getWriteValue(Integer questionId) {
        return gson.toJson(answerDao.findAllByQuestionId(questionId));
    }

    @Transactional
    @Override
    public String getQuestionValueList(Integer questionId) {
        List<Answer> answerList = answerDao.findAllByQuestionId(questionId);
        Question question = questionDao.findDistinctByQuestionId(questionId);
        String questionType = question.getQuestionType();

        switch (questionType) {
            case "single_check":
                return processSingleCheck(question, answerList);
            case "multi_check":
                return processMultiCheck(question, answerList);
            case "number":
            case "grade":
                return processNumberGrade(answerList);
            default:
                return null;
        }
    }

    private String processSingleCheck(Question question, List<Answer> answerList) {
        Map<String, Integer> resValueMap = initOptionMap(question);
        answerList.forEach(answer -> 
            resValueMap.merge(answer.getWriteValue(), 1, Integer::sum)
        );
        return gson.toJson(resValueMap);
    }

    private String processMultiCheck(Question question, List<Answer> answerList) {
        Map<String, Integer> resValueMap = initOptionMap(question);
        answerList.forEach(answer -> {
            JsonArray valueList = gson.fromJson(answer.getWriteValue(), JsonArray.class);
            valueList.forEach(value -> 
                resValueMap.merge(value.getAsString(), 1, Integer::sum)
            );
        });
        return gson.toJson(resValueMap);
    }

    private String processNumberGrade(List<Answer> answerList) {
        List<Double> values = answerList.stream()
                .map(answer -> gson.fromJson(answer.getWriteValue(), Double.class))
                .sorted()
                .collect(Collectors.toList());

        Map<String, Double> resValueMap = new HashMap<>();
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        int size = values.size();

        resValueMap.put("Maximum", size > 0 ? values.get(size - 1) : 0.0);
        resValueMap.put("Minimum", size > 0 ? values.get(0) : 0.0);
        resValueMap.put("Average", size > 0 ? sum / size : 0.0);
        resValueMap.put("Median", size > 0 ? values.get(size / 2) : 0.0);

        return gson.toJson(resValueMap);
    }

    private Map<String, Integer> initOptionMap(Question question) {
        JsonObject temp = gson.fromJson(question.getDetails(), JsonObject.class);
        JsonArray questionOptionsJsonArray = temp.get("questionOptions").getAsJsonArray();
        Map<String, Integer> resValueMap = new HashMap<>();
        questionOptionsJsonArray.forEach(option -> 
            resValueMap.put(option.getAsString(), 0)
        );
        return resValueMap;
    }
}
