import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Scooter")
@Feature("Получение списка заказов")
public class ListOrderTest extends BaseMethods {

    @Before
    @Step("Установка URL")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @Story("Успешное получение списка заказов")
    @Description("Проверка получения списка заказов")
    public void testGetOrdersList() {
        Response response = sendGetOrdersRequest();
        verifySuccessfulOrdersListResponse(response);
    }

    @Test
    @Story("Неуспешное получение списка заказов")
    @Description("Проверка получения списка заказов с несуществующим ID курьера")
    public void testGetOrdersWithNonExistentCourierId() {
        Response response = sendGetOrdersRequestWithNonExistentCourierId();
        verifyFailedOrdersListResponse(response);
    }

    @Step("Отправка запроса на получение списка заказов")
    private Response sendGetOrdersRequest() {
        return given()
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Отправка запроса с несуществующим ID курьера")
    private Response sendGetOrdersRequestWithNonExistentCourierId() {
        int nonExistentCourierId = 999999;
        return given()
                .queryParam("courierId", nonExistentCourierId)
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Проверка успешного ответа со списком заказов")
    private void verifySuccessfulOrdersListResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("orders.id", everyItem(notNullValue()))
                .body("orders.track", everyItem(notNullValue()))
                .body("pageInfo", notNullValue())
                .body("pageInfo.total", greaterThan(0))
                .body("availableStations", not(empty()))
                .body("orders[0]", hasKey("id"))
                .body("orders[0]", hasKey("firstName"))
                .body("orders[0]", hasKey("lastName"))
                .body("orders[0]", hasKey("address"))
                .body("orders[0]", hasKey("metroStation"))
                .body("orders[0]", hasKey("phone"))
                .body("orders[0]", hasKey("rentTime"))
                .body("orders[0]", hasKey("deliveryDate"))
                .body("orders[0]", hasKey("track"))
                .body("orders[0]", hasKey("comment"))
                .body("orders[0]", hasKey("createdAt"))
                .body("orders[0]", hasKey("status"));
    }

    @Step("Проверка ответа с несуществующим ID курьера")
    private void verifyFailedOrdersListResponse(Response response) {
        response.then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором 999999 не найден"));
    }
}
