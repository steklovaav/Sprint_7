import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import pojo.OrderList;

import static io.restassured.RestAssured.*;

public class OrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void orderListSuccess() {
        Response response = given()
                .get("/api/v1/orders");
        response.then().statusCode(200);
        response.body().as(OrderList.class);
    }



}
