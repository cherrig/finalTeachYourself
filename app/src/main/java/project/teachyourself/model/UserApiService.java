package project.teachyourself.model;

import java.util.List;

import project.teachyourself.model.bodies.BodyAddQuestion;
import project.teachyourself.model.bodies.BodyConnect;
import project.teachyourself.model.bodies.BodyInsert;
import project.teachyourself.model.bodies.BodySave;
import project.teachyourself.model.bodies.BodySaveImage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit user service to communicate with user api
 */
public interface UserApiService {

    /**
     * Retrieves user with email
     * @param email user's email
     * @return a response containing a user
     */
    @GET("user")
    Call<User> getUserByEmail(@Query("email") String email);

    /**
     * Creates a new user
     * @param email new user's email
     * @param name new user's name
     * @param age new user's age
     * @param password new user's password
     * @return a response containing a potential error message
     */
    @POST("new")
    Call<User> insert(@Body BodyInsert bodyInsert);

    /**
     * Tries to connect a user
     * @param email user's email
     * @param password user's password
     * @return a response containing a potential error message
     */
    @POST("connect")
    Call<String> connect(@Body BodyConnect bodyConnect);

    /**
     * Retrieves statistics of a user or globally
     * @param email user's email or nothing for global statistics
     * @return a response containing a list a categories with statistics
     */
    @GET("stat")
    Call<List<Category>> getStatistics(@Query("email") String email);


    /**
     * Saves user's statistics for a quiz
     * @param email user's email
     * @param category category of the quiz played
     * @param score number of good answer
     * @param questionNumber number of questions
     * @param avgResponseTime average response time
     * @return a response containing a potential error message
     */
    @POST("save_score")
    Call<Question> save(@Body BodySave bodySave);


    /**
     * Updates the user profile picture
     * @param email user's email
     * @param image user's profile picture
     * @return a response containing user's information
     */
    @PUT("save_image")
    Call<User> saveImage(@Body BodySaveImage bodySaveImage);

    /**
     * Retrieves all questions for a given category and level
     * @param category question's category
     * @param level question's level
     * @return list of all question
     */
    @GET("questions/{category}/{level}")
    Call<List<Question>> getQuestions(@Path("category") String category,
                                      @Path("level") int level);

    /**
     * Create a new question
     * @param category question's category
     * @param level question's level
     * @param question question
     * @param rep1 answer 1
     * @param rep2 answer 2
     * @param rep3 answer 3
     * @param rep4 answer 4
     * @param correction correction
     * @return OK
     */
    @POST("questions/{category}/{level}")
    Call<Question> addQuestion(@Path("category") String category,
                             @Path("level") int level,
                             @Body BodyAddQuestion bodyAddQuestion);
}
