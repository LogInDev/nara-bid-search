package com.nivuskorea.procurement.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
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

            WebDriverManager.chromedriver().setup(); // 크롬 드라이버 자동 다운로드 및 설정
            ChromeOptions options = new ChromeOptions();

            if (headless) {
                options.addArguments("--headless"); // UI 없이 실행
            }

            //gpu 가속 비활성화(일부 환경에서의 오류 방지), 샌드박스 모드 비활성화(일부 환경에서 충돌 방지), 공유 메모리 문제 해결
            options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

            WebDriver driver = new ChromeDriver(options);
            // 요소가 나타날 때까지 3초 지연
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            // 브라우저 크기 설정 - 크기별로 생성 버튼이 다르므로 고정
//            driver.manage().window().setSize(new Dimension(900, 800));
            driver.manage().window().setSize(new Dimension(1200, 800));

            System.out.println("Selenium WebDriver 설정 완료");
            return driver;
        } catch (Exception e) {
            System.err.println("WebDriver 초기화 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("WebDriver 초기화 실패", e);
        }
    }
}
