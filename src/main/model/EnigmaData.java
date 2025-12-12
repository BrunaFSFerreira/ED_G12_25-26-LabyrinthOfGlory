package main.model;

import com.google.gson.annotations.SerializedName;

public class EnigmaData {
    private String question;
    private String answer;
    @SerializedName("wrong_answers")
    private String[] wrongAnswers;

    public EnigmaData () {}

    public EnigmaData(String question, String answer, String[] wrongAnswers) {
        this.question = question;
        this.answer = answer;
        this.wrongAnswers = wrongAnswers;
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }
    public String[] getWrongAnswers() {
        return wrongAnswers;
    }

    public boolean checkAnswer (String playerAnswer) {
        return this.answer.equalsIgnoreCase(playerAnswer.trim());
    }
}
