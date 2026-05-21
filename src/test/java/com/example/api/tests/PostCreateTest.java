package com.example.api.tests;

import com.example.api.models.Post;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * JSONPlaceholder /posts endpoint'i için POST testleri.
 *
 * Test Kapsamı:
 *   - Request body ile yeni kaynak oluşturma
 *   - Status code 201 Created kontrolü
 *   - Response body'de oluşturulan değerlerin doğrulanması
 *   - Yanıt süresi performans kontrolü
 */
public class PostCreateTest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";

    // -------------------------------------------------------------------------
    // TC-POST-001: Geçerli request body ile yeni post oluştur
    // -------------------------------------------------------------------------
    @Test
    public void createPost_withValidBody_shouldReturn201AndEchoData() {
        // Gönderilecek JSON request body (Java nesnesi olarak)
        Post newPost = new Post(5, "Yeni Test Başlığı", "Bu bir otomatik test tarafından oluşturuldu.");

        Response response = given()
            .spec(requestSpec)
            .body(newPost)                         // JSON request body
        .when()
            .post(POSTS_ENDPOINT)
        .then()
            // 1) Status code kontrolü – kaynak oluşturulduğunda 201 beklenir
            .statusCode(201)
            // 2) Response body kontrolleri – sunucu gönderdiğimiz verileri dönmeli
            .body("userId", equalTo(5))
            .body("title",  equalTo("Yeni Test Başlığı"))
            .body("body",   equalTo("Bu bir otomatik test tarafından oluşturuldu."))
            .body("id",     notNullValue())        // Sunucu id atamalı
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS)
        .extract().response();

        // Java nesnesi olarak da doğrula
        Post createdPost = response.as(Post.class);
        assertThat(createdPost.getId(),     notNullValue());
        assertThat(createdPost.getUserId(), equalTo(5));
        assertThat(createdPost.getTitle(),  equalTo("Yeni Test Başlığı"));
    }

    // -------------------------------------------------------------------------
    // TC-POST-002: Minimum geçerli alanlarla post oluştur
    // -------------------------------------------------------------------------
    @Test
    public void createPost_withMinimalFields_shouldReturn201() {
        // Sadece zorunlu alanları gönder
        String minimalBody = "{ \"userId\": 1, \"title\": \"Minimal Post\", \"body\": \"İçerik\" }";

        given()
            .spec(requestSpec)
            .body(minimalBody)
        .when()
            .post(POSTS_ENDPOINT)
        .then()
            // 1) Status code kontrolü
            .statusCode(201)
            // 2) Gönderilen değerlerin response'da varlığı
            .body("title",  equalTo("Minimal Post"))
            .body("userId", equalTo(1))
            .body("id",     notNullValue())
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);
    }

    // -------------------------------------------------------------------------
    // TC-POST-003: Özel karakterler içeren body ile post oluştur
    // -------------------------------------------------------------------------
    @Test
    public void createPost_withSpecialCharacters_shouldReturn201() {
        Post specialPost = new Post(
            10,
            "Türkçe Başlık: Ğüşıöç & <HTML>",
            "JSON özel karakterler: \"quote\", \\backslash, /slash"
        );

        given()
            .spec(requestSpec)
            .body(specialPost)
        .when()
            .post(POSTS_ENDPOINT)
        .then()
            // 1) Status code kontrolü
            .statusCode(201)
            // 2) Başlığın doğru iletildiğini kontrol et
            .body("title",  equalTo("Türkçe Başlık: Ğüşıöç & <HTML>"))
            .body("userId", equalTo(10))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);
    }
}
