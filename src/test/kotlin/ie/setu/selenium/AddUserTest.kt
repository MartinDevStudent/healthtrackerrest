package ie.setu.selenium

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig
import io.github.bonigarcia.wdm.WebDriverManager
import io.github.bonigarcia.wdm.config.DriverManagerType
import io.javalin.Javalin
import io.javalin.testtools.HttpClient
import io.javalin.testtools.JavalinTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * AddUserTest is a test suite for testing the user addition functionality in the application.
 * It uses Selenium WebDriver for browser automation and Javalin as the web server for testing.
 *
 * This class sets up the necessary components for the tests, such as the web driver and
 * the Javalin server instance, and defines test cases to verify the user addition process.
 */
class AddUserTest {
    private lateinit var app: Javalin
    private lateinit var driver: WebDriver
    private var wait: WebDriverWait? = null

    /**
     * Sets up the test environment before each test.
     *
     * This includes starting the Javalin server, establishing a database connection,
     * configuring the web driver system properties, and preparing the WebDriverManager.
     */
    @BeforeEach
    fun setup() {
        app = JavalinConfig().getJavalinService() // Initialize the server before each test

        // Connect to the database in ElephantSQL
        DbConfig().getDbConnection()

        System.setProperty("webdriver.http.factory", "jdk-http-client")
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver")
        val manager = WebDriverManager.getInstance(DriverManagerType.CHROME)
        manager.clearResolutionCache()
        manager.clearDriverCache()
        manager.setup()

        driver =
            ChromeDriver(
                ChromeOptions().apply {
                    // addArguments("--headless")
                    addArguments("--disable-gpu")
                },
            )

        wait = WebDriverWait(driver, Duration.ofSeconds(30))
    }

    /**
     * Shuts down the WebDriver and the Javalin server.
     * This function is called after each test to ensure that all resources are properly released.
     */
    @AfterEach
    fun teardown() {
        driver.quit() // Close the WebDriver after each test
        app.stop() // Stop the Javalin server after each test
    }

    fun login(client: HttpClient) {
        driver.get("${client.origin}/login")
        driver.findElement(By.name("email")).click()
        driver.findElement(By.name("email")).sendKeys("user@mail.com")
        driver.findElement(By.name("password")).click()
        driver.findElement(By.name("password")).sendKeys("password")
        driver.findElement(By.cssSelector("button[title='Login']")).click()

        wait!!.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal")))

        driver.findElement(By.name("close-modal")).click()
    }

    /**
     * Test case for adding a user.
     */
    @Test
    fun addUser() {
        JavalinTest.test(app) { _, client ->
            // Login to site
            login(client)

            // Navigate to the home page and assert that the title is displayed.
            driver.get("${client.origin}/")
            assertThat(driver.pageSource).contains("<title>HealthTracker</title>")

            // Navigate to the user overview page and verify that it was successful
            driver.findElement(By.linkText("More Details...")).click()
            assertThat(driver.pageSource).contains("<template id=\"user-overview\">")

            // Click on the Add button to expand the add user form
            driver.findElement(By.cssSelector("button[title='Add']")).click()

            // Enter details into the name and email fields
            driver.findElement(By.name("name")).click()
            driver.findElement(By.name("name")).sendKeys("Lisa J Simpson")
            driver.findElement(By.name("email")).click()
            driver.findElement(By.name("email")).sendKeys("lisaj@simpson.com")
            driver.findElement(By.name("password")).click()
            driver.findElement(By.name("password")).sendKeys("password")

            // Click on the Add button to add the new user to the database
            driver.findElement(By.cssSelector("button[title='AddUser']")).click()

            // Verify we are still on the user overview page
            assertThat(driver.pageSource).contains("<template id=\"user-overview\">")

            // Wait for the presence of list-group-item(s) by checking if at least one exists
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.name("list-group")))
            wait!!.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.name("list-group-item"), 0))

            // Click on the new user and verify we are brought to the user profile page
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Lisa J Simpson (lisaj@simpson.com)")))
            driver.findElement(By.linkText("Lisa J Simpson (lisaj@simpson.com)")).click()
            assertThat(driver.pageSource).contains("<template id=\"user-profile\">")

            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[title='Delete']")))
            driver.findElement(By.cssSelector("button[title='Delete']")).click()
            assertThat(
                driver.switchTo().alert().text,
            ).isEqualTo("Do you really want to delete?")
            driver.switchTo().alert().accept()
        }
    }
}
