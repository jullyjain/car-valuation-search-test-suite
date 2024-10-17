package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CarValuationSearchPage {

    WebDriver driver;

    public CarValuationSearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    @FindBy(id="onetrust-accept-btn-handler")
    WebElement acceptCookiesButton;

    @FindBy(id="vehicleReg")
    WebElement carRegNum;

    @FindBy(id="Mileage")
    WebElement carMileage;

    public WebDriver getDriver() {
        return driver;
    }

    @FindBy(id="btn-go")
    WebElement searchButton;

    public WebElement getAcceptCookiesButton() {
        return acceptCookiesButton;
    }

    public WebElement getCarRegNum() {
        return carRegNum;
    }

    public WebElement getCarMileage() {
        return carMileage;
    }

    public WebElement getSearchButton() {
        return searchButton;
    }



}
