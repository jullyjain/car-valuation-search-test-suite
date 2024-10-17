import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobjects.CarValuationSearchPage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentityE2EAssignment {

    public static void main(String[] args) throws IOException {

        String carInputFilePath = "src/test/java/resources/car_input.txt";
        String carOutputFilePath = "src/test/java/resources/car_output.txt";
        String regexPattern = "[A-Z]{2}[0-9]{2} [A-Z]{3}|[A-Z]{2}[0-9]{2}[A-Z]{3}";
        String manufacturer = "";
        String model = "";
        int passedTests = 0;
        int failedTests = 0;

        List<String> filteredData = fetchDataBasedOnRegex(carInputFilePath, regexPattern);

        WebDriver driver = new ChromeDriver();
        CarValuationSearchPage carValuationSearchPage = new CarValuationSearchPage(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.get("https://www.webuyanycar.com");
        carValuationSearchPage.getAcceptCookiesButton().click();
        List<String> concatenatedList;
        for (String filteredDatum : filteredData) {
            try {
                carValuationSearchPage.getCarRegNum().sendKeys(filteredDatum);
                carValuationSearchPage.getCarMileage().sendKeys("32000");
                carValuationSearchPage.getSearchButton().click();

                //  Wait until page load
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                        webDriver -> Objects.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState"), "complete")
                );

                // Wait until the element is available in the DOM
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                        webDriver -> Objects.requireNonNull(((JavascriptExecutor) webDriver).executeScript("return document.querySelector('.spec-manufacturer') !== null;"))
                );

                // validate elements exists and retrieve text
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Object element = js.executeScript("return document.querySelector('.spec-manufacturer');");

                if (element == null) {
                    System.out.println("The element was not found in the DOM.");
                } else {
                    manufacturer = (String) js.executeScript("return document.querySelector('.spec-manufacturer').innerText;");
                }

                if (element == null) {
                    System.out.println("The element was not found in the DOM.");
                } else {
                    model = (String) js.executeScript("return document.querySelector('.spec-full-name.text-center').innerText;");
                    System.out.println("Card Model: " + model);
                }
                String carRegNum = filteredData.get(0);
                driver.findElement(By.id("btn-back")).click();
                carValuationSearchPage.getCarRegNum().clear();

                // Call the method to concatenate registrationNumber, manufacturer and model and list of searched items
                concatenatedList = concatenateStrings(carRegNum, manufacturer, model);
                // Read the file and store its content in a list
                List<String> fileContent = readFileContent(carOutputFilePath);
                // Validate the list items against the file content
                for (String item : concatenatedList) {
                    if (fileContent.contains(item)) {
                        passedTests++;
                    } else {
                        System.out.println("Item not found: " + item);
                    }
                }

            } catch (Exception e) {
                // Validate the displayed error message
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),\"Sorry, we couldn't find your car\")]")));
                String expectedErrorMessage = "Sorry, we couldn't find your car";
                if (errorMessage.getText().equals(expectedErrorMessage))
                    passedTests++;
                else
                    failedTests++;

            }

        }

        System.out.println("Total Passed Test Cases: " + passedTests);
        System.out.println("Total Failed Test Cases: " + failedTests);
        driver.quit();
    }

    private static List<String> fetchDataBasedOnRegex(String filePath, String regexPattern) throws IOException {
        List<String> data = new ArrayList<>();
        Pattern regex = Pattern.compile(regexPattern);

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            Matcher matcher = regex.matcher(line);

            while (matcher.find()) {
                data.add(matcher.group());

            }
        }

        return data;
    }

    public static ArrayList<String> concatenateStrings(String carRegNum, String modelName, String modelNum) {
        String concatenatedString = carRegNum + "," + modelName + "," + modelNum;
        ArrayList<String> list = new ArrayList<>();
        list.add(concatenatedString);
        return list;
    }

    public static List<String> readFileContent(String filePath) {
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

}
