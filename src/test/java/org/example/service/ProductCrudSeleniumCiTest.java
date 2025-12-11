package org.example.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import org.example.controller.ProductController;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductCrudSeleniumCiTest {

    private ProductRepository repository;
    private ProductService service;
    private FileService fileService;
    private static Javalin app;
    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:7000/products";

    private final String testImagePath = new File("src/test/resources/euekalecmine.png").getAbsolutePath();

    @BeforeAll
    void setupAll() {
        repository = new ProductRepository();
        service = new ProductService(repository);
        fileService = new FileService();

        WebDriverManager.chromedriver().setup();

        app = Javalin.create();
        new ProductController(app, service, fileService);

        ChromeOptions options = new ChromeOptions();

        // Start server
        app.start(7000);

        // Detecta CI e força headless + flags seguras
        boolean ci = System.getenv("CI") != null;

        if (ci) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");
        } else {
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    void teardownAll() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
        }
        if (app != null) {
            app.stop();
        }
    }

    @BeforeEach
    void openProductsPage() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @Test
    void testAddProductSuccessfully() {
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Adicionar Novo Produto"))
        );
        addButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Selenium CI");
        driver.findElement(By.id("price")).sendKeys("25");
        driver.findElement(By.id("quantity")).sendKeys("10");
        driver.findElement(By.id("category")).sendKeys("Teste");

        File img = new File(testImagePath);
        assertTrue(img.exists(), "A imagem de teste não existe no repositório em src/test/resources!");

        driver.findElement(By.id("image")).sendKeys(img.getAbsolutePath());

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        assertTrue(
                table.getText().contains("Produto Selenium CI"),
                "O produto deveria aparecer na tabela após ser criado."
        );
    }

    @Test
    void testAddProductWithoutImageMustFail() {
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Adicionar Novo Produto"))
        );
        addButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Sem Imagem CI");
        driver.findElement(By.id("price")).sendKeys("10");
        driver.findElement(By.id("quantity")).sendKeys("5");
        driver.findElement(By.id("category")).sendKeys("Teste");

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        String body = driver.getPageSource();

        assertTrue(
                body.contains("É obrigatório enviar uma imagem do produto") ||
                        body.toLowerCase().contains("imagem"),
                "O sistema deveria exibir erro ao tentar criar produto sem imagem."
        );
    }

    @Test
    void testEditProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        if (!table.getText().contains("Produto Selenium CI")) {
            testAddProductSuccessfully();
            driver.get(baseUrl);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
            table = driver.findElement(By.tagName("table"));
        }

        WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-warning"))
        );
        editButton.click();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name"))
        );

        nameInput.clear();
        nameInput.sendKeys("Produto Editado Selenium CI");

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        table = driver.findElement(By.tagName("table"));

        assertTrue(
                table.getText().contains("Produto Editado Selenium CI"),
                "O nome editado deveria aparecer na tabela."
        );
    }

    @Test
    void testDeleteProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        int beforeCount = table.findElements(By.cssSelector("tbody tr")).size();

        if (beforeCount == 0) {
            testAddProductSuccessfully();
            driver.get(baseUrl);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
            table = driver.findElement(By.tagName("table"));
            beforeCount = table.findElements(By.cssSelector("tbody tr")).size();
        }

        WebElement deleteButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("form button.btn-danger"))
        );

        deleteButton.click();
        wait.until(ExpectedConditions.stalenessOf(deleteButton));

        table = driver.findElement(By.tagName("table"));
        int afterCount = table.findElements(By.cssSelector("tbody tr")).size();

        assertTrue(
                afterCount < beforeCount,
                "Após deletar, a tabela deve ter menos linhas."
        );
    }
}
