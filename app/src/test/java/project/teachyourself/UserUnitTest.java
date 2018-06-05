package project.teachyourself;

import org.junit.Test;

import project.teachyourself.model.Question;
import project.teachyourself.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing User model
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTest {

    @Test
    public void constructor1() throws Exception {
        User user = new User(1, "name", "email", "pass", 9);

        assertEquals(1, user.getId());
        assertEquals("name", user.getName());
        assertEquals("email", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(9, user.getAge());
    }

    @Test
    public void constructor2() throws Exception {
        User user = new User(1, "name", "email", "pass", 9, 10);

        assertEquals(1, user.getId());
        assertEquals("name", user.getName());
        assertEquals("email", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(9, user.getAge());
        assertEquals(10, user.getScore());
    }

    @Test
    public void testGetterSetter() throws Exception {
        User user = new User(1, "name", "email", "pass", 9, 10);

        user.setId(2);
        user.setName("test");
        user.setEmail("testEmail");
        user.setPassword("password");
        user.setAge(18);
        user.setScore(42);
        user.setImage("image");

        assertEquals(2, user.getId());
        assertEquals("test", user.getName());
        assertEquals("testEmail", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(18, user.getAge());
        assertEquals(42, user.getScore());
        assertEquals("image", user.getImage());

    }
}