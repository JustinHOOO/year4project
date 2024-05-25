package com.example.questionnaire.service.impl;

import com.example.questionnaire.dao.QuestionDao;
import com.example.questionnaire.dao.QuestionnaireDao;
import com.example.questionnaire.model.Question;
import com.example.questionnaire.model.Questionnaire;
import com.example.questionnaire.service.CreateService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CreateServiceImpl implements CreateService {

    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;
    private final Integer QUESTION_ID_DIGIT = 1000;
    private final Gson gson = new Gson();

    public CreateServiceImpl(QuestionnaireDao questionnaireDao, QuestionDao questionDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
    }

    @Override
    public String createQuestionnaire(String username) {
        Questionnaire newQuestionnaire = new Questionnaire();
        newQuestionnaire.setUsername(username);
        newQuestionnaire.setCreateTime(new Date());
        newQuestionnaire.setStatus("editing");
        newQuestionnaire.setTitle("no title");
        newQuestionnaire.setDescription("no description");
        questionnaireDao.save(newQuestionnaire);

        JsonObject res = new JsonObject();
        res.addProperty("id", newQuestionnaire.getQuestionnaireId());
        return gson.toJson(res);
    }

    @Override
    public String deleteQuestionnaire(Integer questionnaireId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Questionnaire questionnaire = questionnaireDao.findByQuestionnaireId(questionnaireId);

        if (currentUsername.equals(questionnaire.getUsername())) {
            questionnaireDao.deleteById(questionnaireId);
            return "success";
        } else {
            return "denied!";
        }
    }

    @Override
    public String getQuestionList(Integer questionnaireId) {
        List<Question> questionList = questionDao.findAllByQuestionnaireId(questionnaireId);
        JsonArray resList = questionList.stream()
                .map(this::convertQuestionToJson)
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        
        JsonObject res = new JsonObject();
        res.add("questionList", resList);
        return gson.toJson(res);
    }

    private JsonObject convertQuestionToJson(Question question) {
        JsonObject questionJson = gson.fromJson(gson.toJson(question), JsonObject.class);
        questionJson.addProperty("isBoxSelected", false);
        questionJson.addProperty("questionTitle", question.getQuestionTitle());
        questionJson.addProperty("questionDescription", question.getQuestionDescription());
        questionJson.addProperty("questionIndex", question.getQuestionId() % QUESTION_ID_DIGIT);
        questionJson.addProperty("questionNullable", question.getQuestionNullable());
        questionJson.addProperty("questionType", question.getQuestionType());
        
        JsonObject detailsJson = gson.fromJson(question.getDetails(), JsonObject.class);
        processDetails(questionJson, detailsJson);
        return questionJson;
    }

    private void processDetails(JsonObject questionJson, JsonObject detailsJson) {
        if (detailsJson != null) {
            detailsJson.entrySet().forEach(entry -> questionJson.add(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public String saveQuestionnaireOutline(String questionnaire) {
        JsonObject questionnaireObject = gson.fromJson(questionnaire, JsonObject.class);
        Integer questionnaireId = questionnaireObject.get("questionnaireId").getAsInt();
        Questionnaire questionnaireEntity = questionnaireDao.findById(questionnaireId).orElseThrow();
        
        questionnaireEntity.setTitle(questionnaireObject.get("questionnaireTitle").getAsString());
        questionnaireEntity.setDescription(questionnaireObject.get("questionnaireDescription").getAsString());
        questionnaireDao.save(questionnaireEntity);
        return null;
    }

    @Override
    public String saveQuestionnaire(String questionnaire, String questionList) {
        saveQuestionnaireOutline(questionnaire);
        JsonArray questionListArray = gson.fromJson(questionList, JsonArray.class);
        Integer questionnaireId = gson.fromJson(questionnaire, JsonObject.class).get("questionnaireId").getAsInt();
        
        questionListArray.forEach(questionJson -> saveOneQuestion(questionnaireId, questionJson.getAsJsonObject()));
        return null;
    }

    @Override
    public String getQuestionnaireOutline(Integer questionnaireId) {
        Questionnaire questionnaire = questionnaireDao.findById(questionnaireId).orElseThrow();
        JsonObject res = new JsonObject();
        res.add("questionnaire", gson.toJsonTree(questionnaire));
        return gson.toJson(res);
    }

    @Override
    public String saveOneQuestion(String question, Integer questionnaireId) {
        saveOneQuestion(questionnaireId, gson.fromJson(question, JsonObject.class));
        return null;
    }

    @Override
    public String releaseQuestionnaire(Integer questionnaireId) {
        Questionnaire questionnaire = questionnaireDao.findById(questionnaireId).orElseThrow();
        questionnaire.setStatus("collecting");
        questionnaire.setFillCount(0);
        questionnaireDao.save(questionnaire);
        return null;
    }

    @Override
    public String closeQuestionnaire(Integer questionnaireId) {
        Questionnaire questionnaire = questionnaireDao.findById(questionnaireId).orElseThrow();
        questionnaire.setStatus("closed");
        questionnaire.setFillCount(0);
        questionnaireDao.save(questionnaire);
        return null;
    }

    private void saveOneQuestion(Integer questionnaireId, JsonObject questionJson) {
        Question question = new Question();
        question.setQuestionnaireId(questionnaireId);
        question.setQuestionDescription(questionJson.get("questionDescription").getAsString());
        question.setQuestionTitle(questionJson.get("questionTitle").getAsString());
        question.setQuestionId(questionnaireId * QUESTION_ID_DIGIT + questionJson.get("questionIndex").getAsInt());
        question.setQuestionType(questionJson.get("questionType").getAsString());
        question.setQuestionNullable(questionJson.get("questionNullable").getAsBoolean());

        JsonObject detailsJson = new JsonObject();
        processDetails(detailsJson, questionJson);

        question.setDetails(detailsJson.toString());
        questionDao.save(question);
    }
}
