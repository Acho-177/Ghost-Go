package edu.msu.hujiahui.team13.Cloud;

import static edu.msu.hujiahui.team13.Cloud.Cloud.CATALOG_PATH;
import static edu.msu.hujiahui.team13.Cloud.Cloud.CREATE_PATH;
import static edu.msu.hujiahui.team13.Cloud.Cloud.LOAD_CHESS_PATH;
import static edu.msu.hujiahui.team13.Cloud.Cloud.LOAD_PATH;
import static edu.msu.hujiahui.team13.Cloud.Cloud.SAVE_PATH;
import static edu.msu.hujiahui.team13.Cloud.Cloud.UPLOAD_CHESS_PATH;

import edu.msu.hujiahui.team13.Cloud.Models.Catalog;
import edu.msu.hujiahui.team13.Cloud.Models.LoadResult;
import edu.msu.hujiahui.team13.Cloud.Models.SaveResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Project3Service {
    @GET(CATALOG_PATH)
    Call<Catalog> getCatalog(
    );

    @GET(LOAD_CHESS_PATH)
    Call<LoadResult> loadChessBoard(
            @Query("id") String idRoomToLoad
    );

    @GET(UPLOAD_CHESS_PATH)
    Call<LoadResult> uploadChessBoard(
            @Query("user") String userId,
            @Query("id") String idRoomToUpload
    );

    @GET(LOAD_PATH)
    Call<LoadResult> loadUser(
            @Query("user") String userId,
            @Query("pw") String password
    );

    @FormUrlEncoded
    @POST(SAVE_PATH)
    Call<SaveResult> saveUser(
            @Field("xml") String xmlData
    );

    @FormUrlEncoded
    @POST(CREATE_PATH)
    Call<SaveResult> createRoom(
            @Field("xml") String xmlData
    );


}
