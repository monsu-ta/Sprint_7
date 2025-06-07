import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Scooter")
@Feature("Создание заказа")
public class OrderCreationApiTest extends BaseMethods {

    @Test
    @Story("Успешное создание заказа")
    @Description("Проверка создания заказа и получения трека")
    public void testOrderCreation() {
        Response response = sendCreateOrderRequest();
        verifySuccessfulOrderCreation(response);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ " +
                        "\"firstName\": \"Naruto\"," +
                        "\"lastName\": \"Uzumaki\"," +
                        "\"address\": \"Konoha, 142 apt.\"," +
                        "\"metroStation\": 4," +
                        "\"phone\": \"+7 800 355 35 35\"," +
                        "\"rentTime\": 5," +
                        "\"deliveryDate\": \"2020-06-06\"," +
                        "\"comment\": \"Saske, come back to Konoha\"" +
                        "}")
                .when()
                .post(CREATE_ORDER_PATH);
    }

    @Step("Проверка успешного создания заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", not(emptyString()));
    }
}
