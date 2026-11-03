package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterUserTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean contaCriada;
    private static final String URL = "https://automationexercise.com";

    @BeforeEach
    void iniciarNavegador() {
        // Abre o Chrome antes do teste.
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void deveRegistrarUsuarioEExcluirConta() {
        String nome = "Selenium QA";
        String email = "aluno." + UUID.randomUUID().toString().replace("-", "") + "@example.invalid";

        driver.get(URL);
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".features_items"))).isDisplayed(), "A página inicial deve aparecer.");

        driver.findElement(By.cssSelector("a[href='/login']")).click();
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(normalize-space(.), 'New User Signup!')]"))).isDisplayed());

        preencher("input[data-qa='signup-name']", nome);
        preencher("input[data-qa='signup-email']", email);
        driver.findElement(By.cssSelector("button[data-qa='signup-button']")).click();

        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//h2[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'ENTER ACCOUNT INFORMATION')]")))
                .isDisplayed());

        driver.findElement(By.id("id_gender1")).click();
        preencher("#password", "SenhaDeTeste#2026");
        new Select(driver.findElement(By.id("days"))).selectByValue("1");
        new Select(driver.findElement(By.id("months"))).selectByValue("1");
        new Select(driver.findElement(By.id("years"))).selectByValue("1990");
        marcar("#newsletter");
        marcar("#optin");
        preencher("#first_name", "Selenium");
        preencher("#last_name", "QA");
        preencher("#company", "Projeto de testes");
        preencher("#address1", "Rua dos Testes, 123");
        preencher("#address2", "Apto 45");
        new Select(driver.findElement(By.id("country"))).selectByVisibleText("Canada");
        preencher("#state", "Ontario");
        preencher("#city", "Toronto");
        preencher("#zipcode", "M5V 2T6");
        preencher("#mobile_number", "+14165550123");
        driver.findElement(By.cssSelector("button[data-qa='create-account']")).click();
        contaCriada = true;

        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//h2[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'ACCOUNT CREATED!')]")))
                .isDisplayed());
        driver.findElement(By.cssSelector("a[data-qa='continue-button']")).click();
        assertEquals(nome, wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(., 'Logged in as')]/b"))).getText().trim());

        excluirConta();
        contaCriada = false;
    }

    private void preencher(String seletor, String valor) {
        WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(seletor)));
        campo.clear();
        campo.sendKeys(valor);
    }

    private void marcar(String seletor) {
        WebElement checkbox = driver.findElement(By.cssSelector(seletor));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    private void excluirConta() {
        driver.findElement(By.cssSelector("a[href='/delete_account']")).click();
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//h2[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'ACCOUNT DELETED!')]")))
                .isDisplayed());
        driver.findElement(By.cssSelector("a[data-qa='continue-button']")).click();
    }

    @AfterEach
    void fecharNavegador() {
        try {
            if (contaCriada && driver != null
                    && driver.findElements(By.cssSelector("a[href='/delete_account']"))
                    .stream().anyMatch(WebElement::isDisplayed)) {
                excluirConta();
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
