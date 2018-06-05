package project.teachyourself.model;

/**
 * A category represent a question category with it's graphic styling and statistics
 */
public class Category {
    private String title;
    private String description;
    private int icon;
    private int color;
    private int shadow;
    private int score;
    private int questions;
    private float timeAvg;

    /**
     * Empty constructor
     */
    public Category() {
        title = "";
        description = "";
        icon = 0;
        color = 0;
        shadow = 0;
    }

    /**
     * Constructor
     * @param title title
     * @param description description
     * @param icon icon displayed in menu
     * @param color background color
     * @param shadow shadow color
     */
    public Category(String title, String description, int icon, int color, int shadow) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.shadow = shadow;
    }

    /**
     * Constructor for statistics
     * @param title title
     * @param questions questions number
     * @param score score
     * @param timeAvg average response time
     */
    public Category(String title, int questions, int score, float timeAvg) {
        this.title = title;
        this.questions = questions;
        this.score = score;
        this.timeAvg = timeAvg;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get Icon
     * @return icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * Set icon
     * @param icon icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * Get background color
     * @return background color
     */
    public int getColor() {
        return color;
    }

    /**
     * Set background color
     * @param color background color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Get Shadow color
     * @return shadow color
     */
    public int getShadow() {
        return shadow;
    }

    /**
     * Set shadow color
     * @param shadow shadow color
     */
    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    /**
     * Get score
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set score
     * @param score score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get question number
     * @return question number
     */
    public int getQuestions() {
        return questions;
    }

    /**
     * Set question number
     * @param questions question number
     */
    public void setQuestions(int questions) {
        this.questions = questions;
    }

    /**
     * Get average response time
     * @return average response time
     */
    public float getTimeAvg() {
        return timeAvg;
    }

    /**
     * Set average response time
     * @param timeAvg average response time
     */
    public void setTimeAvg(float timeAvg) {
        this.timeAvg = timeAvg;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", icon=" + icon +
                ", color=" + color +
                ", shadow=" + shadow +
                ", score=" + score +
                ", questions=" + questions +
                ", timeAvg=" + timeAvg +
                '}';
    }
}