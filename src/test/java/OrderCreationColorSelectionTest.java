import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Scooter")
@Feature("Создание заказа")
@RunWith(Parameterized.class)
public class OrderCreationColorSelectionTest extends BaseMethods {

    @Parameterized.Parameter
    public String testName;

    @Parameterized.Parameter(1)
    public String[] colors;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Можно указать цвет BLACK", new String[]{"BLACK"}},
                {"Можно указать цвет GREY", new String[]{"GREY"}},
                {"Можно указать оба цвета", new String[]{"BLACK", "GREY"}},
                {"Можно не указывать цвет", new String[]{}}
        });
    }

    @Test
    @Story("Создание заказа с разными цветами")
    @Description("Проверка создания заказа с разными вариантами цветов")
    public void testCreateOrderWithDifferentColors() {
        Response response = sendCreateOrderRequest(colors);
        verifySuccessfulOrderCreation(response);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest(String[] colors) {
        return given()
                .contentType(ContentType.JSON)
                .body(createOrderRequest(colors))
                .when()
                .post(CREATE_ORDER_PATH);
    }

    @Step("Проверка успешного создания заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Подготовка тела запроса для создания заказа")
    private String createOrderRequest(String[] colors) {
        String orderData = "{ " +
                "\"firstName\": \"Naruto\"," +
                "\"lastName\": \"Uzumaki\"," +
                "\"address\": \"Konoha, 142 apt.\"," +
                "\"metroStation\": 4," +
                "\"phone\": \"+7 800 355 35 35\"," +
                "\"rentTime\": 5," +
                "\"deliveryDate\": \"2020-06-06\"," +
                "\"comment\": \"Saske, come back to Konoha\"";

        if (colors != null && colors.length > 0) {
            orderData += ", \"color\": [\"" + String.join("\", \"", colors) + "\"]";
        }

        return orderData + " }";
    }
}
