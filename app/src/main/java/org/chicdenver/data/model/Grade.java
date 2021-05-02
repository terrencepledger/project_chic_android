package org.chicdenver.data.model;

public class Grade {

    private Float percentage;
    private Character letter;

    public Grade(Float percentage) {

        this.percentage = percentage;
        setLetter();

    }

    private void setLetter() {

        if(percentage > 89)
            letter = 'A';
        else if(percentage > 79)
            letter = 'B';
        else if(percentage > 69)
            letter = 'C';
        else
            letter = 'F';

    }

    public String getLetter() {
        return String.valueOf(letter);
    }

    public Float getPercentage() {
        return percentage;
    }

}
