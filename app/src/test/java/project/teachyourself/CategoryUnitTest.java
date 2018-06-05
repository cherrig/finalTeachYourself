package project.teachyourself;

import org.junit.Test;

import project.teachyourself.model.Category;

import static org.junit.Assert.assertEquals;

/**
 * Testing Category model
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CategoryUnitTest {

    @Test
    public void emptyConstructor() throws Exception {
        Category category = new Category();

        assertEquals("", category.getTitle());
        assertEquals("", category.getDescription());
        assertEquals(0, category.getIcon());
        assertEquals(0, category.getColor());
        assertEquals(0, category.getShadow());
    }

    @Test
    public void constructor() throws Exception {
        Category category = new Category("title", "description", 1, 2, 3);

        assertEquals("title", category.getTitle());
        assertEquals("description", category.getDescription());
        assertEquals(1, category.getIcon());
        assertEquals(2, category.getColor());
        assertEquals(3, category.getShadow());
    }

    @Test
    public void testGetterSetter() throws Exception {
        Category category  = new Category();

        category.setTitle("title");
        category.setDescription("description");
        category.setIcon(1);
        category.setColor(2);
        category.setShadow(3);
        category.setScore(4);
        category.setQuestions(5);
        category.setTimeAvg(6.7f);

        assertEquals("title", category.getTitle());
        assertEquals("description", category.getDescription());
        assertEquals(1, category.getIcon());
        assertEquals(2, category.getColor());
        assertEquals(3, category.getShadow());
        assertEquals(4, category.getScore());
        assertEquals(5, category.getQuestions());
        assertEquals(6.7f, category.getTimeAvg(), 0.01f);
    }
}