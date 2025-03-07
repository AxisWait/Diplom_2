import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@DisplayName("Тесты по созданию пользователя")
public class UserCreationTests extends PageObj {

    private String email = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private String password = "password123";
    private String name = "Unique User";


    @DisplayName("Создание пользователя")
    @Test
    public void testCreateUserWithValidData(){
        Response response = CreateUser(email,password,name);
        printResponseBodyToConsole(response);
        compareStatusCode(response,200);
        compareResponseBodyRegistrationAndLogin(response,email,name);
        deleteUser(email,password);
    }

    @DisplayName("Создание уже существующего пользователя")
    @Test
    public void testCreateUserWithExistingLogin() {
        Response response = CreateUser(email,password,name);
        compareStatusCode(response,200);
        compareResponseBodyRegistrationAndLogin(response,email,name);
        Response new_response = CreateUser(email,password,name);
        compareResponseToText(new_response, "message", "User already exists");
        compareStatusCode(new_response,403);
        printResponseBodyToConsole(new_response);
        deleteUser(email,password);
    }

    @DisplayName("Создание пользователя с невалидными данными (без пароля)")
    @Test
    public void testCreateUserWithInvalidLogin() {
        String json = "{\n" +
                "    \"email\": \""+email+"\",\n" +
                "    \"password\": \"\",\n" +
                "    \"name\": \""+name+"\"\n" +
                "}";
        Response response = sendPostRequestUser(json);
        compareResponseToText(response, "message", "Email, password and name are required fields");
        compareStatusCode(response, 403);
        printResponseBodyToConsole(response);
    }
}
