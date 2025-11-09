package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {
    public static void main(String[] args) throws InterruptedException {
        // Setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--start-maximized");
        WebDriver driver = new ChromeDriver(options);

        // Open MakeMyTrip
        driver.get("https://www.makemytrip.com/");
        Thread.sleep(4000);

        // Close login popup if it appears
        try {
            WebElement popup = driver.findElement(By.cssSelector(".loginModal.displayBlock"));
            if (popup.isDisplayed()) {
                driver.findElement(By.cssSelector("body")).click();
            }
        } catch (Exception e) {
            // No popup
        }

        // Click on Flights
        driver.findElement(By.xpath("//span[text()='Flights']")).click();
        Thread.sleep(2000);

        // Click From city
        WebElement fromCity = driver.findElement(By.id("fromCity"));
        fromCity.click();
        WebElement fromInput = driver.findElement(By.xpath("//input[@placeholder='From']"));
        fromInput.sendKeys("Delhi");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//p[contains(text(),'Delhi, India')]")).click();

        // Click To city
        WebElement toCity = driver.findElement(By.id("toCity"));
        toCity.click();
        WebElement toInput = driver.findElement(By.xpath("//input[@placeholder='To']"));
        toInput.sendKeys("Chennai");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//p[contains(text(),'Chennai, India')]")).click();

        // Select departure date - next month 17th
        LocalDate nextMonthDate = LocalDate.now().plusMonths(1).withDayOfMonth(17);
        String monthYear = nextMonthDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String day = String.valueOf(nextMonthDate.getDayOfMonth());

        while (true) {
            WebElement currentMonth = driver.findElement(By.cssSelector(".DayPicker-Caption div"));
            if (currentMonth.getText().equalsIgnoreCase(monthYear)) {
                break;
            } else {
                driver.findElement(By.cssSelector(".DayPicker-NavButton--next")).click();
                Thread.sleep(1000);
            }
        }
        driver.findElement(By.xpath("//div[contains(@aria-label,'" + day + "')]")).click();

        // Click Search
        driver.findElement(By.xpath("//a[contains(text(),'Search')]")).click();
        Thread.sleep(8000);

        // Get flight details
        List<WebElement> flights = driver.findElements(By.cssSelector("div.makeFlex.simpleow"));
        System.out.println("Total flights found: " + flights.size());

        if (!flights.isEmpty()) {
            for (int i = 0; i < Math.min(5, flights.size()); i++) {
                WebElement flight = flights.get(i);
                try {
                    String airline = flight.findElement(By.cssSelector("span.airlineName")).getText();
                    String price = flight.findElement(By.cssSelector("p.blackText.fontSize18.blackFont")).getText();
                    System.out.println((i + 1) + ". " + airline + " - " + price);
                } catch (Exception e) {
                    // Skip if elements not found
                }
            }
        } else {
            System.out.println("No flights found.");
        }

        driver.quit();
    }
}
