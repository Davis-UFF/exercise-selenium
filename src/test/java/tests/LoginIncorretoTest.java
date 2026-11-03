package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginIncorretoTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "https://automationexercise.com";

    @BeforeEach
    void iniciarNavegador() {
        // Abre o Chrome antes de cada teste.
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    static Stream<Arguments> credenciaisInvalidas() {
        return Stream.of(
                Arguments.of("L01 - e-mail não cadastrado e senha curta", "a@invalid.test", "x"),
                Arguments.of("L02 - e-mail e senha incorretos", "usuario@invalid.test", "SenhaErrada123"),
                Arguments.of("L03 - parte local com 63 caracteres", "a".repeat(63) + "@invalid.test", "1234567"),
                Arguments.of("L04 - parte local com 64 caracteres", "b".repeat(64) + "@invalid.test", "12345678"),
                Arguments.of("L05 - parte local com 65 caracteres", "c".repeat(65) + "@invalid.test", "P".repeat(64))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("credenciaisInvalidas")
    void deveExibirErroComCredenciaisInvalidas(String caso, String email, String senha) {
        abrirPaginaDeLogin();
        preencher("input[data-qa='login-email']", email);
        preencher("input[data-qa='login-password']", senha);
        driver.findElement(By.cssSelector("button[data-qa='login-button']")).click();

        String mensagem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Your email or password is incorrect!']"))).getText();
        assertEquals("Your email or password is incorrect!", mensagem);
    }

    @Test
    void deveImpedirEnvioDeEmailComFormatoInvalido() {
        abrirPaginaDeLogin();
        preencher("input[data-qa='login-email']", "email-sem-arroba");
        preencher("input[data-qa='login-password']", "senha-qualquer");
        driver.findElement(By.cssSelector("button[data-qa='login-button']")).click();

        WebElement campoEmail = driver.findElement(By.cssSelector("input[data-qa='login-email']"));
        Boolean formatoInvalido = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validity.typeMismatch;", campoEmail);
        assertTrue(formatoInvalido, "O campo de e-mail deve rejeitar um formato inválido.");
        assertFalse(driver.findElements(By.xpath(
                "//p[normalize-space()='Your email or password is incorrect!']")).size() > 0);
    }

    private void abrirPaginaDeLogin() {
        driver.get(URL);
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".features_items"))).isDisplayed(), "A página inicial deve aparecer.");

        driver.findElement(By.cssSelector("a[href='/login']")).click();
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(normalize-space(.), 'Login to your account')]"))).isDisplayed());
    }

    private void preencher(String seletor, String valor) {
        WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(seletor)));
        campo.clear();
        campo.sendKeys(valor);
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }
}
