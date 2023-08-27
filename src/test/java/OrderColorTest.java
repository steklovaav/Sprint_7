import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.MetroStation;
import pojo.OrderCreate;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isA;

@RunWith(Parameterized.class)
public class OrderColorTest {

    final String firstName = "Иванов";
    final String lastName = "Иван";
    final String address = "Ивановская 1";
    private String phone = "79111111111";
    private int rentTime = 4;
    private String deliveryDate = "2033-01-01";
    private String comment = "коментарий";
    private List<String> color;
    private OrderCreate orderCreate;

    public OrderColorTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()},
                {null},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        Response response = given()
                .get("/api/v1/stations/search");
        MetroStation[] metro = response.body().as(MetroStation[].class);
        orderCreate = new OrderCreate(firstName, lastName, address, metro[0].getNumber(), phone, rentTime, deliveryDate, comment, color);
    }

    @Test
    public void createOrderWithColorParams() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(orderCreate)
                .post("/api/v1/orders");
        response.then().statusCode(201);
        response.then().assertThat().body("track", isA(int.class));
    }

}
