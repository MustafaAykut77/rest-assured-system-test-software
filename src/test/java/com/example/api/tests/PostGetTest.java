package com.example.api.tests;

import com.example.api.models.Post;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * JSONPlaceholder /posts endpoint'i için GET testleri.
 *
 * Test Kapsamı:
 *   - Status code kontrolü
 *   - Response body değer kontrolleri
 *   - Yanıt süresi performans kontrolü
 */
public class PostGetTest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";

    // -------------------------------------------------------------------------
    // TC-GET-001: Tüm postları listele
    // -------------------------------------------------------------------------
    @Test
    public void getAllPosts_shouldReturn200AndNonEmptyList() {
        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT)
        .then()
            // 1) Status code kontrolü
            .statusCode(200)
            // 2) Response body kontrolü – 100 post bekleniyor
            .body("size()", equalTo(100))
            .body("[0].userId", notNullValue())
            .body("[0].id",     equalTo(1))
            .body("[0].title",  not(emptyOrNullString()))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);
    }

    // -------------------------------------------------------------------------
    // TC-GET-002: Belirli bir postu getir (id=1)
    // -------------------------------------------------------------------------
    @Test
    public void getPostById_shouldReturnCorrectPost() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            // 1) Status code kontrolü
            .statusCode(200)
            // 2) Response body değer kontrolleri
            .body("id",     equalTo(1))
            .body("userId", equalTo(1))
            .body("title",  equalTo("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
            .body("body",   not(emptyOrNullString()))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS)
        .extract().response();

        // Jackson ile deserialize edip Java nesnesinde de doğrula
        Post post = response.as(Post.class);
        assertThat(post.getId(),     equalTo(1));
        assertThat(post.getUserId(), equalTo(1));
        assertThat(post.getTitle(),  not(emptyOrNullString()));
    }

    // -------------------------------------------------------------------------
    // TC-GET-003: Var olmayan bir postu sorgula → 404
    // -------------------------------------------------------------------------
    @Test
    public void getPostById_nonExistent_shouldReturn404() {
        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/9999")
        .then()
            // 1) Status code kontrolü
            .statusCode(404)
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);
    }

    // -------------------------------------------------------------------------
    // TC-GET-004: Query param ile filtrele (userId=1)
    // -------------------------------------------------------------------------
    @Test
    public void getPostsByUserId_shouldReturnOnlyUserPosts() {
        int targetUserId = 1;

        List<Integer> userIds = given()
            .spec(requestSpec)
            .queryParam("userId", targetUserId)
        .when()
            .get(POSTS_ENDPOINT)
        .then()
            // 1) Status code kontrolü
            .statusCode(200)
            // 2) Response body kontrolleri
            .body("size()", equalTo(10))           // userId=1 için 10 post
            .body("userId", everyItem(equalTo(targetUserId)))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS)
        .extract().path("userId");

        // Tüm dönen postların istenen kullanıcıya ait olduğunu doğrula
        assertThat(userIds, everyItem(equalTo(targetUserId)));
    }

    // -------------------------------------------------------------------------
    // TC-GET-005: Performans – hızlı endpoint testi
    // -------------------------------------------------------------------------
    @Test
    public void getPostById_shouldRespondFast() {
        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            .statusCode(200)
            // 3) 1 saniyenin altında yanıt vermeli (daha sıkı eşik)
            .time(lessThan(FAST_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);
    }
}
