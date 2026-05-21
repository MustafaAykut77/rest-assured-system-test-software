package com.example.api.tests;

import com.example.api.models.Post;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Negatif Test Senaryoları – Sistemin hata durumlarına verdiği cevapları gözlemleme.
 *
 * Bu sınıf iki tür test içerir:
 *   1) API'nin DOĞRU ŞEKİLDE hata döndüğünü doğrulayan testler (PASS beklenir)
 *   2) BİLEREK YANLIŞ beklenti koyan testler (FAIL beklenir – hata çıktısını görmek için)
 *
 * FAIL beklenen testler "DELIBERATELY_FAILING" etiketi ile işaretlenmiştir.
 */
public class PostNegativeTest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";

    // =========================================================================
    // BÖLÜM 1: API'nin hata yanıtlarını doğrulayan testler (PASS beklenir)
    // =========================================================================

    // -------------------------------------------------------------------------
    // TC-NEG-001: Var olmayan endpoint'e istek → 404
    // -------------------------------------------------------------------------
    @Test
    public void requestToInvalidEndpoint_shouldReturn404() {
        given()
            .spec(requestSpec)
        .when()
            .get("/invalid-endpoint-that-does-not-exist")
        .then()
            // Var olmayan bir endpoint'e istek gönderdik → 404 bekliyoruz
            .statusCode(404)
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);

        System.out.println("✅ TC-NEG-001 PASSED: Geçersiz endpoint için 404 döndü.");
    }

    // -------------------------------------------------------------------------
    // TC-NEG-002: Var olmayan bir kaynağı silmeye çalış → 404 veya başarı
    // -------------------------------------------------------------------------
    @Test
    public void deleteNonExistentPost_shouldHandleGracefully() {
        // JSONPlaceholder fake API'dir, DELETE her zaman 200 döner.
        // Gerçek bir API'de 404 dönmesi beklenir.
        given()
            .spec(requestSpec)
        .when()
            .delete(POSTS_ENDPOINT + "/99999")
        .then()
            .statusCode(200) // JSONPlaceholder her silme işleminde 200 döner
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);

        System.out.println("✅ TC-NEG-002 PASSED: Var olmayan kaynak silme isteği başarıyla yanıtlandı (200).");
    }

    // -------------------------------------------------------------------------
    // TC-NEG-003: Boş body ile POST isteği → Sunucu yine de kabul eder (fake API)
    // -------------------------------------------------------------------------
    @Test
    public void createPost_withEmptyBody_shouldStillReturn201() {
        given()
            .spec(requestSpec)
            .body("{}")  // Boş JSON objesi
        .when()
            .post(POSTS_ENDPOINT)
        .then()
            .statusCode(201)
            .body("id", notNullValue()) // Sunucu yine id atamalı
            .time(lessThan(MAX_RESPONSE_TIME_MS), TimeUnit.MILLISECONDS);

        System.out.println("✅ TC-NEG-003 PASSED: Boş body ile POST isteği 201 döndü.");
    }

    // =========================================================================
    // BÖLÜM 2: BİLEREK HATALI beklentiler (FAIL beklenir)
    // Bu testler hata çıktısının nasıl göründüğünü gösterir.
    // =========================================================================

    // -------------------------------------------------------------------------
    // TC-NEG-004 [DELIBERATELY_FAILING]:
    // Bilerek yanlış status code beklentisi → Sunucu 200 dönerken biz 500 bekliyoruz
    // -------------------------------------------------------------------------
    @Test
    public void DELIBERATELY_FAILING_wrongStatusCodeExpectation() {
        System.out.println("⚠️  TC-NEG-004: Bu test BİLEREK FAIL olacak – " +
                "200 dönerken 500 bekliyoruz.");

        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            // ❌ HATA: Sunucu 200 döner, ama biz 500 bekliyoruz → AssertionError
            .statusCode(500);
    }

    // -------------------------------------------------------------------------
    // TC-NEG-005 [DELIBERATELY_FAILING]:
    // Bilerek yanlış response body değeri beklentisi
    // -------------------------------------------------------------------------
    @Test
    public void DELIBERATELY_FAILING_wrongBodyExpectation() {
        System.out.println("⚠️  TC-NEG-005: Bu test BİLEREK FAIL olacak – " +
                "response'daki title ile eşleşmeyen bir değer bekliyoruz.");

        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            .statusCode(200)
            // ❌ HATA: Gerçek title farklı, biz "BU BAŞLIK MEVCUT DEĞİL" bekliyoruz
            .body("title", equalTo("BU BAŞLIK MEVCUT DEĞİL – BİLEREK YANLIŞ"));
    }

    // -------------------------------------------------------------------------
    // TC-NEG-006 [DELIBERATELY_FAILING]:
    // Bilerek yanlış veri tipi beklentisi (id String değil, Integer'dır)
    // -------------------------------------------------------------------------
    @Test
    public void DELIBERATELY_FAILING_wrongDataTypeAssertion() {
        System.out.println("⚠️  TC-NEG-006: Bu test BİLEREK FAIL olacak – " +
                "id alanının String olmasını bekliyoruz ama Integer döner.");

        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            .statusCode(200)
            // ❌ HATA: id alanı Integer döner, ama biz String tipinde bekliyoruz
            .body("id", is(instanceOf(String.class)));
    }

    // -------------------------------------------------------------------------
    // TC-NEG-007 [DELIBERATELY_FAILING]:
    // Bilerek var olmayan bir JSON alanını zorunlu olarak bekleme
    // -------------------------------------------------------------------------
    @Test
    public void DELIBERATELY_FAILING_expectNonExistentField() {
        System.out.println("⚠️  TC-NEG-007: Bu test BİLEREK FAIL olacak – " +
                "response'da olmayan 'email' alanını arıyoruz.");

        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT + "/1")
        .then()
            .statusCode(200)
            // ❌ HATA: /posts endpoint'inde "email" alanı yok
            .body("email", equalTo("test@example.com"));
    }

    // -------------------------------------------------------------------------
    // TC-NEG-008 [DELIBERATELY_FAILING]:
    // Bilerek yanlış liste boyutu beklentisi
    // -------------------------------------------------------------------------
    @Test
    public void DELIBERATELY_FAILING_wrongListSizeExpectation() {
        System.out.println("⚠️  TC-NEG-008: Bu test BİLEREK FAIL olacak – " +
                "100 post dönerken 999 adet bekliyoruz.");

        given()
            .spec(requestSpec)
        .when()
            .get(POSTS_ENDPOINT)
        .then()
            .statusCode(200)
            // ❌ HATA: API 100 post döner, biz 999 bekliyoruz
            .body("size()", equalTo(999));
    }
}
