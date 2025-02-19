package com.nivuskorea.procurement.factory;

import com.nivuskorea.procurement.config.SeleniumConfig;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebDriverFactory {

    private final SeleniumConfig seleniumConfig;

    public WebDriver createWebDriver() {
        return seleniumConfig.webDriver();
    }
}
