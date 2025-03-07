import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@DisplayName("Тесты логина пользователя в системе")
public class UserLoginTests extends PageObj {
    private String email = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private String password = "password123";
    private String name = "Unique User";

    @DisplayName("Логин пользователя в системе")
    @Test
    public void testLoginUserrWithValidData(){
        Response response = CreateUser(email,password,name);
        printResponseBodyToConsole(response);
        compareStatusCode(response,200);
        compareResponseBodyRegistrationAndLogin(response,email,name);
        deleteUser(email,password);
    }
    @DisplayName("Логин пользователя в системе без параметра")
    @Test
    public void testLoginUserWithInvalidPass(){
        String emptypass = "";
        Response response = CreateUser(email,password,name);
        compareStatusCode(response,200);
        compareResponseBodyRegistrationAndLogin(response,email,name);
        Response loginResponse = LoginUser(email,emptypass);
        compareStatusCode(loginResponse, 401);
        compareResponseToText(loginResponse, "message","email or password are incorrect");
        printResponseBodyToConsole(response);
        deleteUser(email,password);
    }
    @DisplayName("Логин несуществующего пользователя в системе")
    @Test
    public void testLoginNonExistentCourier(){
        String json = "{\n" +
                "    \"email\": \"enakin89\",\n" +
                "    \"password\": \"321\"\n" +
                "}";
        Response response = LoginUser(json);
        compareStatusCode(response, 401);
        compareResponseToText(response, "message","email or password are incorrect");
        printResponseBodyToConsole(response);
    }
}
