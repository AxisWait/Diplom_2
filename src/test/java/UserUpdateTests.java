import io.restassured.response.Response;
import org.junit.jupiter.api.*;

@DisplayName("Тесты изменения данных пользователя")
public class UserUpdateTests extends PageObj {
    private  String email = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private  String password = "password123";
    private  String name = "Unique User";
    private  String newEmail = "unique_user_1@example.com";
    @BeforeEach
    public  void createUser(){
        CreateUser(email,password,name);
    }
    @Test
    public void testSuccessfulUserUpdate() {
        Response loginResponse = LoginUser(email,password);
        String accessToken = loginResponse.path("accessToken"); // Получаем accessToken из ответа
        String updateRequestBody = "{ \"email\": \""+newEmail+"\", \"name\": \"New Name\" }";
        Response response = sendPatchRequestUserWithAuthorization(updateRequestBody,accessToken);
        String resp = response.asString();
        boolean isTrue = resp.contains("User with such email already exists");
        if(isTrue){
            deleteUser(newEmail,password);;
            response = sendPatchRequestUserWithAuthorization(updateRequestBody,accessToken);
            compareStatusCode(response, 200);
            deleteUser(newEmail,password);
        }
        else {
            compareStatusCode(response,200);
            compareResponseBodyUpdateUser(response,newEmail,"New Name");
            deleteUser(newEmail,password);
        }

    }
    @Test
    public void testUpdateWithoutAuthorization() {
        String updateRequestBody = "{ \"email\": \""+newEmail+"\", \"name\": \"New Name\" }";
        Response response = sendPatchRequestUserWithoutAuthorization(updateRequestBody);
        compareStatusCode(response,401);
        deleteUser(email,password);
    }

}
