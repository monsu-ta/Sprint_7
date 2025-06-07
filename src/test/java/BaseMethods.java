import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BaseMethods {
    protected static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    protected static final String CREATE_COURIER = "/api/v1/courier";
    protected static final String LOGIN_COURIER = "/api/v1/courier/login";
    protected static final String DELETE_COURIER = "/api/v1/courier/";
    protected static final String CREATE_ORDER_PATH = "/api/v1/orders";
    protected static final String ORDERS_PATH = "/api/v1/orders";

    public BaseMethods() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создание курьера")
    public Response createCourier(String login, String password, String firstName) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format(
                        "{\"login\":\"%s\"," +
                                "\"password\":\"%s\"," +
                                "\"firstName\":\"%s\"}",
                        login, password, firstName))
                .post(CREATE_COURIER);
    }

    @Step("Получение id курьера через авторизацию")
    public String getCourierId(String login, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\"}",
                        login, password))
                .when()
                .post(LOGIN_COURIER)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("id").toString();
    }

    @Step("Удаление курьера по id")
    public void deleteCourier(String id) {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\":\"" + id + "\"}")
                .delete(DELETE_COURIER + id)
                .then()
                .statusCode(200);
    }

    @Step("Авторизация курьера")
    public Response loginCourier(String login, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                .when()
                .post(LOGIN_COURIER);
    }

    @Step("Проверка успешной авторизации")
    public void verifySuccessfulLogin(Response response) {
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Проверка ошибки авторизации")
    public void verifyLoginError(Response response, int expectedStatusCode, String expectedMessage) {
        response.then()
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedMessage));
    }

    @Step("Создание заказа")
    public Response createOrder(String requestBody) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER_PATH);
    }

    @Step("Проверка успешного создания заказа")
    public void verifyOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Получение списка заказов")
    public Response getOrdersList() {
        return RestAssured.given()
                .when()
                .get(ORDERS_PATH);
    }
}
