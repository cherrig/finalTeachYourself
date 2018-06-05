package project.teachyourself.model;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.teachyourself.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Http Client using Retrofit library
 */
public class Client {

    public static String BASE_URL = BuildConfig.API_URL;
    private static Client INSTANCE = null;
    private Retrofit retrofit = null;

    /**
     * User Api
     */
    public UserApiService userApiService;

    /**
     * Private constructor for singleton
     */
    private Client() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userApiService = retrofit.create(UserApiService.class);
    }

    /**
     * Client singleton
     * @return client instance
     */
    public static Client getInstance() {
        if (INSTANCE == null)
            synchronized (Client.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Client();
                }
            }
        return INSTANCE;
    }

    /**
     * Convert string param into RequestBody
     * Prevent literal quotes to be added by retrofit
     * @param param string param
     * @return RequestBody
     */
    public static RequestBody getBodyParam(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

}
