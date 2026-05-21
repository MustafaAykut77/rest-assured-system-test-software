# 🧪 REST Assured – API Otomatik Regresyon Test Projesi

[![Java](https://img.shields.io/badge/Java-11%2B-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.4.0-green?logo=testing-library&logoColor=white)](https://rest-assured.io/)
[![JUnit](https://img.shields.io/badge/JUnit-4.13.2-25A162?logo=junit5&logoColor=white)](https://junit.org/junit4/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**JSONPlaceholder** REST API üzerinde otomatik regresyon testleri gerçekleştiren, **Java / Maven / JUnit / REST Assured** tabanlı bir test otomasyon projesidir.

---

## 📋 İçindekiler

- [Hakkında](#-hakkında)
- [Teknoloji Yığını](#-teknoloji-yığını)
- [Proje Yapısı](#-proje-yapısı)
- [Ön Gereksinimler](#-ön-gereksinimler)
- [Kurulum ve Çalıştırma](#-kurulum-ve-çalıştırma)
- [Test Senaryoları](#-test-senaryoları)
- [Mimari Tasarım Kararları](#-mimari-tasarım-kararları)
- [Test Sonuç Raporları](#-test-sonuç-raporları)

---

## 📖 Hakkında

Bu proje, REST API testlerinin **REST Assured** kütüphanesi kullanılarak otomatize edilmesini göstermektedir. Hedef API olarak ücretsiz ve herkese açık olan [JSONPlaceholder](https://jsonplaceholder.typicode.com) kullanılmaktadır.

**Proje kapsamında doğrulanan başlıca kontroller:**

- ✅ HTTP **Status Code** doğrulama
- ✅ **Response Body** değer kontrolleri (Hamcrest Matchers)
- ✅ **Yanıt süresi** (response time) performans eşik kontrolleri
- ✅ **JSON → Java POJO** dönüşüm ve nesne bazlı assertion
- ✅ **Query parameter** filtresi ile veri doğrulama

---

## 🛠️ Teknoloji Yığını

| Teknoloji | Sürüm | Kullanım Amacı |
|---|---|---|
| **Java** | 11+ | Programlama dili |
| **Maven** | 3.8+ | Bağımlılık yönetimi ve build otomasyon |
| **JUnit 4** | 4.13.2 | Test framework |
| **REST Assured** | 5.4.0 | HTTP tabanlı API test kütüphanesi |
| **Hamcrest** | 2.2 | Fluent assertion kütüphanesi |
| **Jackson** | 2.16.1 | JSON serileştirme / deserileştirme |

---

## 📁 Proje Yapısı

```
rest-assured-project/
├── .gitignore
├── pom.xml                                    # Maven yapılandırması & bağımlılıklar
├── README.md
└── src/
    └── test/
        └── java/
            └── com/example/api/
                ├── models/
                │   └── Post.java              # POJO – /posts endpoint veri modeli
                └── tests/
                    ├── BaseTest.java           # Ortak konfigürasyon (base URL, headers, logging)
                    ├── PostGetTest.java         # GET endpoint test senaryoları (5 test)
                    └── PostCreateTest.java      # POST endpoint test senaryoları (3 test)
```

---

## ⚙️ Ön Gereksinimler

| Gereksinim | Minimum Sürüm |
|---|---|
| Java (JDK) | 11 |
| Apache Maven | 3.8 |
| İnternet bağlantısı | — *(JSONPlaceholder API erişimi için)* |

Java ve Maven sürümlerini kontrol etmek için:

```bash
java -version
mvn -version
```

---

## 🚀 Kurulum ve Çalıştırma

### 1. Projeyi klonlayın

```bash
git clone https://github.com/MustafaAykut77/rest-assured-system-test-software.git
cd rest-assured-system-test-software
```

### 2. Bağımlılıkları yükleyin

```bash
mvn clean install -DskipTests
```

### 3. Tüm testleri çalıştırın

```bash
mvn test
```

### 4. Belirli bir test sınıfını çalıştırın

```bash
# Sadece GET testleri
mvn test -Dtest=PostGetTest

# Sadece POST testleri
mvn test -Dtest=PostCreateTest
```

### 5. Tek bir test metodu çalıştırın

```bash
mvn test -Dtest=PostGetTest#getPostById_shouldReturnCorrectPost
```

---

## 🎯 Test Senaryoları

### GET Testleri — `PostGetTest.java`

| Test ID | Metot Adı | Açıklama | Doğrulama Kriterleri |
|---|---|---|---|
| TC-GET-001 | `getAllPosts_shouldReturn200AndNonEmptyList` | Tüm postları listele | Status 200 · Liste boyutu 100 · İlk kaydın alanları dolu |
| TC-GET-002 | `getPostById_shouldReturnCorrectPost` | Tek post getir (`id=1`) | Status 200 · Doğru `userId`, `title` · POJO dönüşüm doğrulama |
| TC-GET-003 | `getPostById_nonExistent_shouldReturn404` | Var olmayan post sorgula | Status 404 |
| TC-GET-004 | `getPostsByUserId_shouldReturnOnlyUserPosts` | Query param filtresi (`userId=1`) | Status 200 · 10 kayıt · Tüm `userId` = 1 |
| TC-GET-005 | `getPostById_shouldRespondFast` | Performans eşik testi | Yanıt süresi < 1000ms |

### POST Testleri — `PostCreateTest.java`

| Test ID | Metot Adı | Açıklama | Doğrulama Kriterleri |
|---|---|---|---|
| TC-POST-001 | `createPost_withValidBody_shouldReturn201AndEchoData` | POJO ile post oluştur | Status 201 · Body echo · `id` atanmış · POJO doğrulama |
| TC-POST-002 | `createPost_withMinimalFields_shouldReturn201` | Minimum alanlarla oluştur | Status 201 · Gönderilen değerler döner |
| TC-POST-003 | `createPost_withSpecialCharacters_shouldReturn201` | Özel karakterli body | Status 201 · Türkçe karakterler & HTML encoding korunur |

> 💡 Her test senaryosunda **3 temel kontrol** uygulanır: `statusCode`, `body assertion`, `response time`.

---

## 🏗️ Mimari Tasarım Kararları

### Neden `BaseTest` Sınıfı?
Tekrarlayan yapılandırma (`baseURI`, `ContentType`, `logging filter`) merkezi bir sınıfta toplanarak **DRY (Don't Repeat Yourself)** prensibi sağlanmıştır. Tüm test sınıfları bu sınıftan kalıtım alır.

### Neden POJO Model (`Post.java`)?
API yanıtları ham JSON yerine Java nesnelerine dönüştürülerek **type-safe** test doğrulamaları yapılır. `@JsonIgnoreProperties(ignoreUnknown = true)` ile API değişikliklerine karşı dayanıklılık sağlanır.

### BDD Tarzı Syntax
REST Assured'ın `given() → when() → then()` yapısı, testlerin **okunabilirliğini** artırır:

```java
given()                           // Ön koşullar
    .spec(requestSpec)
    .body(requestBody)
.when()                           // Eylem
    .post("/posts")
.then()                           // Doğrulamalar
    .statusCode(201)
    .body("id", notNullValue())
    .time(lessThan(3000L), TimeUnit.MILLISECONDS);
```

---

## 📊 Test Sonuç Raporları

Testler çalıştırıldıktan sonra Maven Surefire raporları otomatik olarak oluşturulur:

```
target/surefire-reports/
├── TEST-com.example.api.tests.PostGetTest.xml
├── TEST-com.example.api.tests.PostCreateTest.xml
├── com.example.api.tests.PostGetTest.txt
└── com.example.api.tests.PostCreateTest.txt
```

---

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
