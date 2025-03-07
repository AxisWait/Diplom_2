import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserPageObj {
    RequestSpecification requestSpec;
    private  final String ENDPOINT_USER_REGISTER = "/api/auth/register";
    private  final String ENDPOINT_USER_LOGIN = "api/auth/login";
    private  final String ENDPOINT_USER = "api/auth/user";
    private  final String ENDPOINT_ORDERS = "/api/orders";

    @BeforeEach
    public void setUp() {
        requestSpec = given()
                .baseUri("https://stellarburgers.nomoreparties.site/");
    }

    @Step("Send POST request to /api/auth/register")
    public Response sendPostRequestUser(String json){
        Response response = given(requestSpec)
                .log()
                .all()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(ENDPOINT_USER_REGISTER);
        return response;
    }
    @Step("Send POST request to /api/orders")
    public Response sendPostRequestOrdersWithAuthorization(String json, String accessToken){
        Response response = given(requestSpec)
                .log()
                .all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(ENDPOINT_ORDERS);
        return response;
    }
    @Step("Send POST request to /api/orders")
    public Response sendPostRequestOrders(String json){
        Response response = given(requestSpec)
                .log()
                .all()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(ENDPOINT_ORDERS);
        return response;
    }
    @Step("Send PATCH request to /api/auth/user with Authorization")
    public Response sendPatchRequestUserWithAuthorization(String json, String accessToken){
        Response response = given(requestSpec)
                .log()
                .all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .patch(ENDPOINT_USER);
        return response;
    }
    @Step("Send PATCH request to /api/auth/user with Authorization")
    public Response sendPatchRequestUserWithoutAuthorization(String json){
        Response response = given(requestSpec)
                .log()
                .all()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .patch(ENDPOINT_USER);
        return response;
    }

    @Step("Create user")
    public Response CreateUser(String email, String password, String name){
        String json = "{\n" +
                "    \"email\": \""+email+"\",\n" +
                "    \"password\": \""+password+"\",\n" +
                "    \"name\": \""+name+"\"\n" +
                "}";
        Response response = sendPostRequestUser(json);
        return response;
    }

    @Step("Checking the response body")
    public void compareResponseBodyRegistrationAndLogin(Response response, String email, String name){
        response
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }
    @Step("Checking the response body")
    public void compareResponseBodyUpdateUser(Response response, String email, String name){
        response
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }
    @Step("Compare response to something")
    public void compareResponseToText(Response response, String key, String message){
        response
                .then()
                .assertThat()
                .body(key,equalTo(message));
    }
    @Step("Checking the status code")
    public void compareStatusCode(Response response, int statusCode){
        response
                .then()
                .statusCode(statusCode);
    }


    @Step("Print response body to console")
    public void printResponseBodyToConsole(Response response){
        System.out.println(response.body().asString());
    }
    @Step("User login using the data")
    public Response LoginUser(String email, String password) {
        String loginRequestBody = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        Response loginResponse = given(requestSpec)
                .contentType(ContentType.JSON)
                .body(loginRequestBody)
                .when()
                .post(ENDPOINT_USER_LOGIN);
        return loginResponse;
    }
    @Step("User login using the JSON")
    public Response LoginUser(String json) {
        Response loginResponse = given(requestSpec)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(ENDPOINT_USER_LOGIN);
        return loginResponse;
    }
    @Step("Clearing the test data. deleting a user")
    public void deleteUser(String email, String password){

        Response loginResponse = LoginUser(email,password);

        String accessToken = loginResponse.path("accessToken"); // Получаем accessToken из ответа

        // Шаг 2: Удаление пользователя
        given(requestSpec)
                .header("Authorization", accessToken) // Передаем accessToken в заголовке
                .contentType(ContentType.JSON)
                .body("{ \"password\": \""+password+"\" }") // Если API требует подтверждения пароля
                .when()
                .delete(ENDPOINT_USER)
                .then()
                .statusCode(202)
                .body("success", equalTo(true))
                .body("message", equalTo("User successfully removed"));
        System.out.println("User deleted");
    }

    @Step("Auth for Token")
    public String authorizationForToken(String json){
        Response loginResponse = LoginUser(json);
        String accessToken = loginResponse.path("accessToken"); // Получаем accessToken из ответа
        return accessToken;
    }
    @Step("Checking the response body")
    public void compareResponseBodyCreateOrder(Response response){
        response
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }
}
