package main.model;

public class EnigmaData {
    private String question;
    private String answer;

    public EnigmaData () {}

    public EnigmaData(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }

    public boolean checkAnswer (String playerAnswer) {
        return this.answer.equalsIgnoreCase(playerAnswer.trim());
    }
}
