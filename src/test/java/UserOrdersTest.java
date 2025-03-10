import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@DisplayName("Тесты получения заказов")
public class UserOrdersTest extends PageObj{
    private  String email = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private  String password = "password123";
    private  String name = "Unique User";

    @Test
    public void testGetUserOrdersWithAuth() {
        createUser(email,password,name);

        String loginRequestBody = "{ \"email\": \""+email+"\", \"password\": \""+password+"\" }";
        String accessToken = authorizationForToken(loginRequestBody);
        String orderRequestBody = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response response = sendPostRequestOrdersWithAuthorization(orderRequestBody,accessToken);
        compareStatusCode(response, 200);
        compareResponseBodyCreateOrder(response);
        Response new_response = sendGetRequestOrdersWithAuthorization(accessToken);
        compareStatusCode(response, 200);
        compareResponseBodyGetOrders(new_response);
        deleteUser(email,password);
    }

    @Test
    public void testGetUserOrdersWithoutAuth() {
        Response response = sendGetRequestOrders();
        compareStatusCode(response, 401);
    }
}
