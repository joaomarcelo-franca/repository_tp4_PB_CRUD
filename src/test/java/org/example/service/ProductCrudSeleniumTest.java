package org.example.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductCrudSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:7000/products";

    // Caminho relativo (GitHub Actions consegue achar)
    private final String testImagePath =
            new File("src/test/resources/testimg.png").getAbsolutePath();

    @BeforeAll
    void setupAll() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // HEADLESS ativado no GitHub Actions
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    @AfterAll
    void teardownAll() {
        if (driver != null) driver.quit();
    }

    @BeforeEach
    void openProductsPage() {
        driver.get(baseUrl);
    }

    // -----------------------------
    // TESTE 1: Criar Produto
    // -----------------------------
    @Test
    void testAddProductSuccessfully() {
        driver.findElement(By.linkText("Adicionar Novo Produto")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Selenium");
        driver.findElement(By.id("price")).sendKeys("25");
        driver.findElement(By.id("quantity")).sendKeys("10");
        driver.findElement(By.id("category")).sendKeys("Teste");

        // imagem local do projeto
        driver.findElement(By.id("image")).sendKeys(testImagePath);

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        assertTrue(
                table.getText().contains("Produto Selenium"),
                "O produto deveria aparecer na tabela após ser criado."
        );
    }

    // -----------------------------
    // TESTE 2: Criar sem imagem – deve falhar
    // -----------------------------
    @Test
    void testAddProductWithoutImageMustFail() {
        driver.findElement(By.linkText("Adicionar Novo Produto")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Sem Imagem");
        driver.findElement(By.id("price")).sendKeys("10");
        driver.findElement(By.id("quantity")).sendKeys("5");
        driver.findElement(By.id("category")).sendKeys("Teste");

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        String body = driver.getPageSource();

        assertTrue(
                body.contains("É obrigatório enviar uma imagem do produto"),
                "O sistema deveria exibir erro ao tentar criar produto sem imagem."
        );
    }

    // -----------------------------
    // TESTE 3: Editar Produto
    // -----------------------------
    @Test
    void testEditProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        assertTrue(table.getText().length() > 10, "A tabela deve conter produtos antes de editar.");

        WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-warning"))
        );
        editButton.click();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys("Produto Editado Selenium");

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        table = driver.findElement(By.tagName("table"));

        assertTrue(
                table.getText().contains("Produto Editado Selenium"),
                "O nome editado deveria aparecer na tabela."
        );
    }

    // -----------------------------
    // TESTE 4: Deletar Produto
    // -----------------------------
    @Test
    void testDeleteProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = driver.findElement(By.tagName("table"));

        int beforeCount = table.findElements(By.cssSelector("tr")).size();

        WebElement deleteButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("form button.btn-danger"))
        );

        deleteButton.click();
        wait.until(ExpectedConditions.stalenessOf(deleteButton));

        table = driver.findElement(By.tagName("table"));
        int afterCount = table.findElements(By.cssSelector("tr")).size();

        assertTrue(
                afterCount < beforeCount,
                "Após deletar, a tabela deve ter menos linhas."
        );
    }
}
