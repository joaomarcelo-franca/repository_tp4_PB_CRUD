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
public class ProductCrudSeleniumTest {

    private ProductRepository repository = new ProductRepository();
    private ProductService service = new ProductService(repository);
    private static Javalin app;
    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:7000/products";

    // Caminho relativo para funcionar no GitHub Actions
    private final String testImagePath =
            new File("C:\\Users\\W-11\\Desktop\\FACULDADE\\6 PERIODO\\PB\\TP4_PB\\src\\main\\resources\\public\\uploads\\euekalecmine.png").getAbsolutePath();

    @BeforeAll
    void setupAll() {
        WebDriverManager.chromedriver().setup();
        app = Javalin.create();
        new ProductController(app, service);
        ChromeOptions options = new ChromeOptions();
        app.start(7000);

        // Detecta automaticamente se está rodando em CI
        boolean ci = System.getenv("CI") != null;

        if (ci) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));


    }

    @AfterAll
    void teardownAll() {
        if (driver != null) {
            driver.quit();
        }
        app.stop();
    }

    @BeforeEach
    void openProductsPage() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    // -----------------------------
    // TESTE 1: Criar Produto com imagem
    // -----------------------------
    @Test
    void testAddProductSuccessfully() {

        // Garantir que o botão existe
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Adicionar Novo Produto"))
        );
        addButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Selenium");
        driver.findElement(By.id("price")).sendKeys("25");
        driver.findElement(By.id("quantity")).sendKeys("10");
        driver.findElement(By.id("category")).sendKeys("Teste");

        // Upload seguro
        File img = new File(testImagePath);
        assertTrue(img.exists(), "A imagem de teste não existe no repositório!");

        driver.findElement(By.id("image")).sendKeys(img.getAbsolutePath());

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

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Adicionar Novo Produto"))
        );
        addButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("Produto Sem Imagem");
        driver.findElement(By.id("price")).sendKeys("10");
        driver.findElement(By.id("quantity")).sendKeys("5");
        driver.findElement(By.id("category")).sendKeys("Teste");

        driver.findElement(By.cssSelector("button.btn-success")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        String body = driver.getPageSource();

        assertFalse(
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

        // Se não houver produtos, cria um primeiro
        if (!table.getText().contains("Produto Selenium")) {
            System.out.println("Nenhum produto encontrado. Criando um antes de editar.");
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

        int beforeCount = table.findElements(By.cssSelector("tbody tr")).size();

        if (beforeCount == 0) {
            System.out.println("Nenhum produto para deletar. Criando um...");
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
