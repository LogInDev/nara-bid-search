package com.nivuskorea.procurement.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class SeleniumConfig {

    @Value("${selenium.headless}")
    private boolean headless;

    @Bean
    public WebDriver webDriver(){
        try {
            System.out.println("Selenium WebDriver 설정 시작... (Headless: " + headless + ")");

            WebDriverManager.chromedriver().setup(); // 크롬 드라이버 자동 설정
            ChromeOptions options = new ChromeOptions();

            if (headless) {
//                options.addArguments("--headless"); // UI 없이 실행
            }

            options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            System.out.println("Selenium WebDriver 설정 완료");
            return driver;
        } catch (Exception e) {
            System.err.println("WebDriver 초기화 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("WebDriver 초기화 실패", e);
        }
    }
}
