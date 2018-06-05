package project.teachyourself;

import org.junit.Test;

import project.teachyourself.model.Category;
import project.teachyourself.model.Question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing Question model
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class QuestionUnitTest {

    @Test
    public void emptyConstructor() throws Exception {
        Question question = new Question();

        assertEquals("", question.getQuestion());
        assertEquals("", question.getRep1());
        assertEquals("", question.getRep2());
        assertEquals("", question.getRep3());
        assertEquals("", question.getRep4());
        assertEquals(0, question.getCorrection());
        assertFalse(question.isImage());
    }

    @Test
    public void constructor() throws Exception {
        Question question = new Question("question", "rep1", "rep2", "rep3", "rep4", 1, true);

        assertEquals("question", question.getQuestion());
        assertEquals("rep1", question.getRep1());
        assertEquals("rep2", question.getRep2());
        assertEquals("rep3", question.getRep3());
        assertEquals("rep4", question.getRep4());
        assertEquals(1, question.getCorrection());
        assertTrue(question.isImage());
    }

    @Test
    public void testGetterSetter() throws Exception {
        Question question = new Question();

        question.setQuestion("question");
        question.setRep1("rep1");
        question.setRep2("rep2");
        question.setRep3("rep3");
        question.setRep4("rep4");
        question.setCorrection(1);
        question.setImage(true);

        assertEquals("question", question.getQuestion());
        assertEquals("rep1", question.getRep1());
        assertEquals("rep2", question.getRep2());
        assertEquals("rep3", question.getRep3());
        assertEquals("rep4", question.getRep4());
        assertEquals(1, question.getCorrection());
        assertTrue(question.isImage());
    }
}