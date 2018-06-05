package project.teachyourself.model;

/**
 * Question entity represents a question for the quiz.
 * Contains: 1 question, 4 answers, 1 correction,
 * and a boolean defining if answers are images url
 */
public class Question {
    private String question;
    private String rep1;
    private String rep2;
    private String rep3;
    private String rep4;
    private int correction;
    private boolean image;

    /**
     * Default empty constructor
     */
    public Question() {
        this.question = "";
        this.rep1 = "";
        this.rep2 = "";
        this.rep3 = "";
        this.rep4 = "";
        this.correction = 0;
        this.image = false;
    }

    /**
     * Constructor
     * @param question a question
     * @param rep1 first answer
     * @param rep2 second answer
     * @param rep3 third answer
     * @param rep4 fourth answer
     * @param correction the good answer
     * @param image true if answer's are image url
     */
    public Question(String question, String rep1, String rep2, String rep3, String rep4,
                    int correction, boolean image) {
        this.question = question;
        this.rep1 = rep1;
        this.rep2 = rep2;
        this.rep3 = rep3;
        this.rep4 = rep4;
        this.correction = correction;
        this.image = image;
    }

    /**
     * Get the question
     * @return question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Set the question
     * @param question question
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Get first answer
     * @return first answer
     */
    public String getRep1() {
        return rep1;
    }

    /**
     * Set first answer
     * @param rep1 first answer
     */
    public void setRep1(String rep1) {
        this.rep1 = rep1;
    }

    /**
     * Get second answer
     * @return second answer
     */
    public String getRep2() {
        return rep2;
    }

    /**
     * Set second answer
     * @param rep2 second answer
     */
    public void setRep2(String rep2) {
        this.rep2 = rep2;
    }

    /**
     * Get third answer
     * @return third answer
     */
    public String getRep3() {
        return rep3;
    }

    /**
     * Set third answer
     * @param rep3 third answer
     */
    public void setRep3(String rep3) {
        this.rep3 = rep3;
    }

    /**
     * Get fourth answer
     * @return fourth answer
     */
    public String getRep4() {
        return rep4;
    }

    /**
     * Set fourth answer
     * @param rep4 fourth answer
     */
    public void setRep4(String rep4) {
        this.rep4 = rep4;
    }

    /**
     * Get the good answer
     * @return the good answer number
     */
    public int getCorrection() {
        return correction;
    }

    /**
     * Set the good answer
     * @param correction the good answer number
     */
    public void setCorrection(int correction) {
        this.correction = correction;
    }

    /**
     * Is question considered as image
     * @return true if question is an image
     */
    public boolean isImage() {
        return image;
    }

    /**
     * Define if question is an image
     * @param image true if question is an image
     */
    public void setImage(boolean image) {
        this.image = image;
    }
}
