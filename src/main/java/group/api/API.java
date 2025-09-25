package group.api;

import group.api.telegram.MyTelegramBot;
import group.api.forms.Forms;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@ComponentScan(basePackages = {"group.api.controller", "group.api.telegram", "group.api.repository"})
public class API implements ApplicationRunner {

    @Autowired
    private MyTelegramBot telegramBot;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(API.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startSwingForms();
        startTelegramBot();
    }

    private void startSwingForms() {
        try {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Forms.main(new String[0]);
                    System.out.println("✅ Формы Swing запущены успешно!");
                } catch (Exception e) {
                    System.err.println("❌ Ошибка запуска форм Swing: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("❌ Ошибка запуска форм Swing: " + e.getMessage());
        }
    }

    private void startTelegramBot() {
        try {
            if (telegramBot != null) {
                System.out.println("✅ Telegram-бот успешно включен!");
                System.out.println("🤖 Имя пользователя бота: " + telegramBot.getBotUsername());
                System.out.println("🔗 Бот готов принимать сообщения...");
            } else {
                System.err.println("❌ Telegram-бот равен null - проверьте конфигурацию Spring");
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка доступа к Telegram-боту: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


