import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

@Epic("API Scooter")
@Feature("Создание курьера")
public class CourierApiTest {

    private BaseMethods courierApiClient;
    private String testLogin;
    private String testPassword;
    private String firstName;
    private String courierId;

    @Before
    @Step("Подготовка тестовых данных")
    public void setUp() {
        courierApiClient = new BaseMethods();
        testLogin = "naruto" + System.currentTimeMillis();
        testPassword = "1234";
        firstName = "naruto";
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        if (courierId != null) {
            courierApiClient.deleteCourier(courierId);
        }
    }

    @Test
    @Story("Успешное создание курьера")
    @Description("Проверка создания курьера с валидными данными")
    public void testCreateCourier() {
        Response response = courierApiClient.createCourier(testLogin, testPassword, firstName);
        verifySuccessfulCreationResponse(response);
        courierId = courierApiClient.getCourierId(testLogin, testPassword);
        assertNotNull("ID курьера не должен быть null", courierId);
    }

    @Test
    @Story("Неуспешное создание курьера")
    @Description("Проверка создания двух одинаковых курьеров")
    public void testCreateTwoCourier() {
        Response firstResponse = courierApiClient.createCourier(testLogin, testPassword, firstName);
        verifySuccessfulCreationResponse(firstResponse);

        Response secondResponse = courierApiClient.createCourier(testLogin, testPassword, firstName);
        verifyDuplicateCreationResponse(secondResponse);
    }

    @Test
    @Story("Неуспешное создание курьера")
    @Description("Проверка создания курьера без логина")
    public void testNotCreateCourierWithoutLogin() {
        Response response = sendCreateCourierRequestWithoutLogin(testPassword, firstName);
        verifyFailedCreationResponse(response);
    }

    @Test
    @Story("Неуспешное создание курьера")
    @Description("Проверка создания курьера без пароля")
    public void testNotCreateCourierWithoutPassword() {
        Response response = sendCreateCourierRequestWithoutPassword(testLogin, firstName);
        verifyFailedCreationResponse(response);
    }

    @Step("Отправка запроса на создание курьера без логина")
    private Response sendCreateCourierRequestWithoutLogin(String password, String firstName) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"password\":\"%s\",\"firstName\":\"%s\"}", password, firstName))
                .when()
                .post(BaseMethods.CREATE_COURIER);
    }

    @Step("Отправка запроса на создание курьера без пароля")
    private Response sendCreateCourierRequestWithoutPassword(String login, String firstName) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"login\":\"%s\",\"firstName\":\"%s\"}", login, firstName))
                .when()
                .post(BaseMethods.CREATE_COURIER);
    }

    @Step("Проверка успешного ответа на создание курьера")
    private void verifySuccessfulCreationResponse(Response response) {
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Проверка ответа при дублировании курьера")
    private void verifyDuplicateCreationResponse(Response response) {
        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется."));
    }

    @Step("Проверка ответа при неуспешном создании курьера")
    private void verifyFailedCreationResponse(Response response) {
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}


