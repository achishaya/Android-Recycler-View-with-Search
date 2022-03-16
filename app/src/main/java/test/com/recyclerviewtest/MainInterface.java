package test.com.recyclerviewtest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainInterface {
    @GET("api/users")
    Call<String> STRING_CALL(
            @Query("Page") int page
//            @Query("limit") int limit
    );
}
