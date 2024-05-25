package com.example.questionnaire.service.impl;

import com.example.questionnaire.dao.AnswerDao;
import com.example.questionnaire.dao.QuestionnaireDao;
import com.example.questionnaire.dao.QuestionnaireIpDao;
import com.example.questionnaire.model.Answer;
import com.example.questionnaire.model.Questionnaire;
import com.example.questionnaire.model.QuestionnaireIp;
import com.example.questionnaire.service.FillInService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class FillInServiceImpl implements FillInService {
    private final AnswerDao answerDao;
    private final QuestionnaireIpDao questionnaireIpDao;
    private final QuestionnaireDao questionnaireDao;
    private final Gson gson;

    @Autowired
    public FillInServiceImpl(AnswerDao answerDao, QuestionnaireIpDao questionnaireIpDao, Gson gson, QuestionnaireDao questionnaireDao) {
        this.answerDao = answerDao;
        this.questionnaireIpDao = questionnaireIpDao;
        this.gson = gson;
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    @Transactional
    public void submitAnswer(Integer questionnaireId, String answerListJson, String ip) {
        // Save the IP addresses of the survey respondents
        saveQuestionnaireIp(questionnaireId, ip);

        // Check if the survey already closed
        Questionnaire questionnaire = questionnaireDao.findByQuestionnaireId(questionnaireId);
        if ("closed".equals(questionnaire.getStatus())) {
            return;
        }

        // Record the number of participants (+1)
        incrementFillCount(questionnaire);

        // Process answers
        JsonArray answerListArray = gson.fromJson(answerListJson, JsonArray.class);
        answerListArray.forEach(answerElement -> saveAnswer(answerElement.getAsJsonObject(), ip));
    }

    private void saveQuestionnaireIp(Integer questionnaireId, String ip) {
        QuestionnaireIp questionnaireIp = new QuestionnaireIp();
        questionnaireIp.setIp(ip);
        questionnaireIp.setQuestionnaireId(questionnaireId);
        questionnaireIpDao.save(questionnaireIp);
    }

    private void incrementFillCount(Questionnaire questionnaire) {
        questionnaire.setFillCount((questionnaire.getFillCount() == null ? 0 : questionnaire.getFillCount()) + 1);
        questionnaireDao.save(questionnaire);
    }

    private void saveAnswer(JsonObject oneAnswerObject, String ip) {
        Answer answer = new Answer();
        answer.setIp(ip);
        answer.setFillTime(new Date());
        answer.setQuestionId(oneAnswerObject.get("questionId").getAsInt());
        answer.setQuestionTitle(oneAnswerObject.get("questionTitle").getAsString());
        answer.setQuestionType(oneAnswerObject.get("questionType").getAsString());
        answer.setWriteValue(getWriteValue(oneAnswerObject, answer.getQuestionType()));
        answerDao.save(answer);
    }

    private String getWriteValue(JsonObject oneAnswerObject, String questionType) {
        switch (questionType) {
            case "single_check":
                return oneAnswerObject.get("answerSingleCheck").getAsString();
            case "multi_check":
                return oneAnswerObject.get("answerMultiCheck").toString();
            case "single_line_text":
            case "multi_line_text":
                return oneAnswerObject.get("answerText").getAsString();
            case "number":
                return oneAnswerObject.get("answerNumber").toString();
            case "grade":
                return oneAnswerObject.get("answerGrade").toString();
            case "date":
                return oneAnswerObject.get("answerDate").toString();
            default:
                throw new IllegalArgumentException("Unknown question type: " + questionType);
        }
    }

    @Override
    public Boolean checkAlreadySubmit(Integer questionnaireId, String ip) {
        return !questionnaireIpDao.findAllByQuestionnaireIdAndIp(questionnaireId, ip).isEmpty();
    }
}
