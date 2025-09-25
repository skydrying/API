package group.api.telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {
    
    @Bean
    public TelegramBotsApi telegramBotsApi(MyTelegramBot telegramBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
            System.out.println("=== Telegram-бот зарегистрирован в API ===");
            return botsApi;
        } catch (TelegramApiException e) {
            System.err.println("❌ Не удалось зарегистрировать Telegram-бота: " + e.getMessage());
            return null;
        }
    }
}