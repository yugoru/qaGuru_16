package tests;

import com.codeborne.selenide.Selenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class RestApiTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
    }

    @Test
    void addItemToCartAsNewUserTest() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_43.EnteredQuantity=2")
                .when()
                .post(baseURI + "/addproducttocart/details/43/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(2)"));
    }

    @Test
    void addItemToCartWithCookiesTest() {
        String cookie =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body("addtocart_43.EnteredQuantity=2")
                        .when()
                        .post(baseURI + "/addproducttocart/details/43/1")
                        .then()
                        .statusCode(200)
                        .log().body().extract().cookie("Nop.customer");

        given()
                .cookie(cookie)
                .when()
                .get(baseURI + "/cart")
                .then()
                .statusCode(200)
                .log().body();
        assertThat("Your Shopping Cart is empty!", is("Your Shopping Cart is empty!"));

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_43.EnteredQuantity=2")
                .cookie(cookie)
                .when()
                .post(baseURI + "/addproducttocart/details/43/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(2)"));
        open(baseURI + "/cart");
        getWebDriver().manage().addCookie(new Cookie("Nop.customer", cookie));
        Selenide.refresh();
        $(".cart-qty").shouldHave(text("(2)"));
    }

}