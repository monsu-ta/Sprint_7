import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Epic("API Scooter")
@Feature("Авторизация курьерра")
public class CourierLoginTest extends BaseMethods {

    private String existingLogin = "naruto";
    private String existingPassword = "1234";

    @Before
    @Step("Установка базового URL")
    public void setUp() {
        baseURI = BASE_URL;
    }

    @Test
    @Story("Успешная авторизация")
    @Description("Проверка успешной авторизации курьера с валидными данными")
    public void testSuccessfulCourierLogin() {
        Response response = sendLoginRequest(existingLogin, existingPassword);
        verifySuccessfulLoginResponse(response);
        String courierId = extractCourierId(response);
        assertNotNull("ID курьера не должен быть null", courierId);
        System.out.println("Успешная авторизация. ID курьера: " + courierId);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации без логина")
    public void testLoginWithoutLoginField() {
        Response response = sendLoginRequestWithoutLogin(existingPassword);
        verifyLoginWithoutRequiredFieldResponse(response);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации без пароля")
    public void testLoginWithoutPasswordField() {
        Response response = sendLoginRequestWithoutPassword(existingLogin);
        verifyLoginWithoutRequiredFieldResponse(response);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации с несуществующим логином")
    public void testLoginWithInvalidLogin() {
        Response response = sendLoginRequest("notnaruto", existingPassword);
        verifyInvalidCredentialsResponse(response);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации с некорректным паролем")
    public void testLoginWithInvalidPassword() {
        Response response = sendLoginRequest(existingLogin, "5555");
        verifyInvalidCredentialsResponse(response);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации с пустым логином")
    public void testLoginWithEmptyLogin() {
        Response response = sendLoginRequest("", existingPassword);
        verifyLoginWithoutRequiredFieldResponse(response);
    }

    @Test
    @Story("Неуспешная авторизация")
    @Description("Проверка авторизации с пустым паролем")
    public void testLoginWithEmptyPassword() {
        Response response = sendLoginRequest(existingLogin, "");
        verifyLoginWithoutRequiredFieldResponse(response);
    }

    @Step("Отправка запроса на авторизацию")
    private Response sendLoginRequest(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                .when()
                .post(LOGIN_COURIER);
    }

    @Step("Отправка запроса на авторизацию без логина")
    private Response sendLoginRequestWithoutLogin(String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"password\":\"%s\"}", password))
                .when()
                .post(LOGIN_COURIER);
    }

    @Step("Отправка запроса на авторизацию без пароля")
    private Response sendLoginRequestWithoutPassword(String login) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"login\":\"%s\"}", login))
                .when()
                .post(LOGIN_COURIER);
    }

    @Step("Проверка успешного ответа на авторизацию")
    private void verifySuccessfulLoginResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Проверка ответа при отсутствии обязательного поля")
    private void verifyLoginWithoutRequiredFieldResponse(Response response) {
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Проверка ответа при неверных учетных данных")
    private void verifyInvalidCredentialsResponse(Response response) {
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Извлечение ID курьера из ответа")
    private String extractCourierId(Response response) {
        return response.jsonPath().getString("id");
    }
}
