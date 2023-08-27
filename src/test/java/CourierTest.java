import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.CourierCreate;
import pojo.LoginAnswer;
import pojo.Login;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierTest {

    final String login = "avsteklova";
    final String password = "password";
    final String firstName = "Анна";

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Step("Создание курьера")
    public void createSuccess() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new CourierCreate(login, password, firstName))
                .post("/api/v1/courier");
        response.then().statusCode(201);
        response.then().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка повторного создания курьера")
    public void createFailDuplicate() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new CourierCreate(login, password, firstName))
                .post("/api/v1/courier");
        response.then().statusCode(409);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Ошибка создания курьера с пустым логином")
    public void createFailLoginNull() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new CourierCreate(null, password, firstName))
                .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка создания курьера без логина")
    public void createFailWithoutLogin() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\"password\": \"" + password + "\"}")
                .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка создания курьера с пустым паролем")
    public void createFailPasswordNull() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new CourierCreate(login, null, firstName))
                .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка создания курьера без пароля")
    public void createFailWithoutPassword() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\"login\": \"" + login + "\"}")
                .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешный логин")
    public void loginSuccess() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(login, password))
                .post("/api/v1/courier/login");
        response.then().statusCode(200);
        response.body().as(LoginAnswer.class);
    }

    @Test
    @DisplayName("Ошибка логина с пустым паролем")
    public void loginFailPasswordNull() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(login, null))
                .post("/api/v1/courier/login");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка логина без пароля")
    public void loginFailWithoutPassword() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\"login\":\"" + login + "\"}")
                .post("/api/v1/courier/login");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка логина с пустым логином")
    public void loginFailLoginNull() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(null, password))
                .post("/api/v1/courier/login");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка логина без логина")
    public void loginFailWithoutLogin() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\"password\":\"" + password + "\"}")
                .post("/api/v1/courier/login");
        response.then().statusCode(400);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка логина с неправильным паролем")
    public void loginFailWrongPassword() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(login, password+"error"))
                .post("/api/v1/courier/login");
        response.then().statusCode(404);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка логина с неправильным логином")
    public void loginFailWrongLogin() {
        this.createSuccess();
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(login+"error", password))
                .post("/api/v1/courier/login");
        response.then().statusCode(404);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }


    @After
    @Step("Удаление курьера после теста")
    public void clean() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(new Login(login, password))
                .post("/api/v1/courier/login");
        int status = response.statusCode();
        if (status == 200) {
            LoginAnswer loginAnswer = response.body().as(LoginAnswer.class);
            response = given()
                    .delete("/api/v1/courier/" + loginAnswer.getId(), Map.of());
            response.then().statusCode(200);
            response.then().assertThat().body("ok", equalTo(true));
        }
    }
}
