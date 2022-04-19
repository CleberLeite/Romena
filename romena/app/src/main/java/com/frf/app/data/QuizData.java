package com.frf.app.data;

import java.util.List;

public class QuizData {

    String id;
    String date;
    String reward;
    String question;
    String idAnswer;
    String img;
    String title;
    int idAnswered;
    List<Answers> answers;

    public QuizData(String id, String date, String reward, String question, String idAnswer, List<Answers> answers, int idAnswered, String img) {
        this.id = id;
        this.date = date;
        this.reward = reward;
        this.question = question;
        this.idAnswer = idAnswer;
        this.answers = answers;
        this.idAnswered = idAnswered;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getIdAnswer() {
        return idAnswer;
    }

    public void setIdAnswer(String idAnswer) {
        this.idAnswer = idAnswer;
    }

    public List<Answers> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }

    public int getIdAnswered() {
        return idAnswered;
    }

    public void setIdAnswered(int idAnswered) {
        this.idAnswered = idAnswered;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
