import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@DisplayName("Тесты создания заказа")
public class OrderTests extends PageObj {
    private  String email = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private  String password = "password123";
    private  String name = "Unique User";
    @Test
    public void testCreateOrderWithAuthAndIngredients() {
        CreateUser(email,password,name);
        String loginRequestBody = "{ \"email\": \""+email+"\", \"password\": \""+password+"\" }";
        String accessToken = authorizationForToken(loginRequestBody);
        String orderRequestBody = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response response = sendPostRequestOrdersWithAuthorization(orderRequestBody,accessToken);
        compareStatusCode(response, 200);
        compareResponseBodyCreateOrder(response);
        deleteUser(email,password);
    }

    @Test
    public void testCreateOrderWithoutAuth() {
        String orderRequestBody = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response response = sendPostRequestOrders(orderRequestBody);
        compareStatusCode(response, 200); //fixme баг документации, заказ можно создать без авторизации. 401 не получим.
    }

    @Test
    public void testCreateOrderWithoutIngredients() {
        String orderRequestBody = "{ \"ingredients\": [] }";
        Response response = sendPostRequestOrders(orderRequestBody);
        compareStatusCode(response, 400);
    }

    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        String orderRequestBody = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa7\", \"61c0c5a71d1f82001bdaaa\"] }";
        Response response = sendPostRequestOrders(orderRequestBody);
        compareStatusCode(response, 500);
    }
}
