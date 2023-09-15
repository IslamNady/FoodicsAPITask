package com.foodics.testcases;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Tests {
    RequestSpecification request;
    String token;
    @BeforeClass
    public void beforeClass(){
        request = given().baseUri("https://pay2.foodics.dev/cp_internal")
                .contentType(ContentType.JSON).log().all();
    }
    @Test(priority = 1)
    public void postLogin(){
        LoginPojo requestBody = new LoginPojo();
        requestBody.setEmail("merchant@foodics.com");
        requestBody.setPassword("123456");
        requestBody.setToken("Lyz22cfYKMetFhKQybx5HAmVimF1i0xO");

        token = given().spec(request)
                .body(requestBody)
                .when().post("/login")
                .then().log().body()
                .assertThat().statusCode(200).body("token",notNullValue()).log().all().extract().response().path("token");
    }
    @Test(priority = 2)
    public void getWhoAmI(){
        given().spec(request)
                .auth().oauth2(token)
                .when().get("/whoami")
                .then().log().body()
                .assertThat().body("user.name",equalToIgnoringCase("Test Foodics")).statusCode(200);
    }
    @Test(priority = 3)
    public void postLogin_InvalidCredentials(){
        LoginPojo requestBody = new LoginPojo();
        requestBody.setEmail("merchant@foodics.com");
        requestBody.setPassword("12345");
        requestBody.setToken("Lyz22cfYKMetFhKQybx5HAmVimF1i0xO");

        given().spec(request)
                .body(requestBody)
                .when().post("/login")
                .then().log().body()
                .assertThat().statusCode(500);
    }
}
