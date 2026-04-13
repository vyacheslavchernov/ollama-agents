package ru.vych.tools.web;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * Клиент для взаимодействия с браузером Playwright
 */
public class PlaywrightClient {

    private static final PlaywrightClient INSTANCE = new PlaywrightClient();

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;


    private PlaywrightClient() {
        init();
    }

    public static PlaywrightClient getInstance() {
        return INSTANCE;
    }

    private void init() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
        );

        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setUserAgent(UserAgentProvider.getRandomUserAgent())
                        .setViewportSize(1280, 800)
                        .setLocale("en-US")
        );

        applyStealth(context);
    }

    private void applyStealth(BrowserContext context) {
        context.addInitScript(
                "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });" +
                        "window.chrome = { runtime: {} };" +
                        "Object.defineProperty(navigator, 'languages', { get: () => ['en-US','en'] });" +
                        "Object.defineProperty(navigator, 'plugins', { get: () => [1,2,3,4,5] });"
        );
    }

    /**
     * Выполнить GET запрос
     *
     * @param url URL запроса
     * @param headers заголовки запроса
     * @return ответ на запрос
     */
    public Document get(String url, Map<String, String> headers) {
        APIRequestContext request = context.request();

        RequestOptions options = RequestOptions.create();

        if (headers != null) {
            headers.forEach(options::setHeader);
        }

        APIResponse response = request.get(url, options);

        return Jsoup.parse(response.text(), url);
    }

    /**
     * Выполнить POST запрос
     *
     * @param url URL запроса
     * @param data тело запроса
     * @param headers заголовки запроса
     * @return ответ на запрос
     */
    public Document post(String url, FormData data, Map<String, String> headers) {
        APIRequestContext request = context.request();

        RequestOptions options = RequestOptions.create();

        if (data != null) {
            options.setForm(data);
        }

        if (headers != null) {
            headers.forEach(options::setHeader);
        }

        APIResponse response = request.post(url, options);

        return Jsoup.parse(response.text(), url);
    }
}
