package group.api.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import group.api.controller.MainController;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    
    private final String botUsername = "frameshopAPI_bot";
    private final String botToken = "8218247231:AAELszaRBp5-TVtDEOpJmJSfVIylzeAzum0";
    
    @Autowired
    private MainController mainController;
    
    private ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();
    
    private static class UserState {
        String state; // "WAITING_LOGIN", "WAITING_PASSWORD", "AUTHENTICATED"
        String login;
        String userRole;
        
        UserState() {
            this.state = "START";
        }
    }
    
    @PostConstruct
    public void init() {
        System.out.println("=== Telegram Bot Initialized ===");
        System.out.println("Bot Username: " + botUsername);
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();
            
            System.out.println("Message from " + userName + " (" + chatId + "): " + messageText);
            System.out.println("Current state: " + getUserState(chatId).state);
            
            handleMessage(chatId, messageText, userName);
        }
    }
    
    private void handleMessage(Long chatId, String messageText, String userName) {
        UserState userState = getUserState(chatId);
        
        try {
            // Обработка состояний авторизации
            if ("WAITING_LOGIN".equals(userState.state)) {
                handleLoginInput(chatId, messageText, userState);
                return;
            } else if ("WAITING_PASSWORD".equals(userState.state)) {
                handlePasswordInput(chatId, messageText, userState);
                return;
            }
            
            switch (messageText.toLowerCase()) {
                case "/start":
                    sendWelcomeMessage(chatId, userName);
                    break;
                case "/auth":
                    startAuthorization(chatId, userState);
                    break;
                case "/help":
                    sendHelpMessage(chatId);
                    break;
                case "/logout":
                    logout(chatId, userState);
                    break;
                default:
                    if ("AUTHENTICATED".equals(userState.state)) {
                        sendMessage(chatId, "Вы авторизованы как: " + userState.userRole + 
                                  "\nИспользуйте /help для списка команд.");
                    } else {
                        sendMessage(chatId, "Для работы с системой необходимо авторизоваться. Используйте команду /auth");
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            sendMessage(chatId, "❌ Произошла ошибка. Попробуйте позже.");
            e.printStackTrace();
        }
    }
    
    private UserState getUserState(Long chatId) {
        return userStates.computeIfAbsent(chatId, k -> new UserState());
    }
    
    private void sendWelcomeMessage(Long chatId, String userName) {
        String welcome = "👋 Привет, " + userName + "!\n\n" +
                        "Добро пожаловать в систему мастерской вышивки!\n\n" +
                        "Для начала работы выполните авторизацию:\n" +
                        "🔐 /auth - Авторизация\n\n" +
                        "Другие команды:\n" +
                        "ℹ️  /help - Справка\n" +
                        "🚪 /logout - Выйти";
        
        sendMessage(chatId, welcome);
    }
    
    private void startAuthorization(Long chatId, UserState userState) {
        userState.state = "WAITING_LOGIN";
        userState.login = null;
        userState.userRole = null;
        sendMessage(chatId, "🔐 Введите ваш логин:");
    }
    
    private void handleLoginInput(Long chatId, String login, UserState userState) {
        userState.login = login.trim();
        userState.state = "WAITING_PASSWORD";
        sendMessage(chatId, "🔑 Теперь введите ваш пароль для пользователя '" + login + "':");
    }
    
    private void handlePasswordInput(Long chatId, String password, UserState userState) {
        if (userState.login == null) {
            sendMessage(chatId, "❌ Ошибка сессии. Начните заново: /auth");
            userState.state = "START";
            return;
        }
        
        try {
            System.out.println("Attempting authentication for login: " + userState.login);
            String authResult = mainController.getAutorization(userState.login, password.trim());
            System.out.println("Authentication result: " + authResult);
            
            String response;
            if (!"NO".equals(authResult)) {
                userState.state = "AUTHENTICATED";
                userState.userRole = getRoleName(authResult);
                response = "✅ Авторизация успешна! Вы вошли как " + userState.userRole + 
                          "\n\nТеперь вы можете использовать функции системы.\n" +
                          "Используйте /help для просмотра доступных команд.";
            } else {
                userState.state = "START";
                userState.login = null;
                response = "❌ Неверный логин или пароль. Попробуйте снова: /auth";
            }
            
            sendMessage(chatId, response);
            
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            userState.state = "START";
            userState.login = null;
            sendMessage(chatId, "❌ Ошибка при авторизации. Попробуйте позже: /auth");
            e.printStackTrace();
        }
    }
    
    private String getRoleName(String authResult) {
        switch (authResult) {
            case "DIRECTOR": return "ДИРЕКТОР";
            case "SELLER": return "ПРОДАВЕЦ";
            case "PRODUCTIONMASTER": return "МАСТЕР ПРОИЗВОДСТВА";
            case "YES": return "ПОКУПАТЕЛЬ";
            default: return "ПОЛЬЗОВАТЕЛЬ";
        }
    }
    
    private void logout(Long chatId, UserState userState) {
        if ("AUTHENTICATED".equals(userState.state)) {
            String role = userState.userRole;
            userState.state = "START";
            userState.login = null;
            userState.userRole = null;
            sendMessage(chatId, "✅ Вы успешно вышли из системы (роль: " + role + "). Для повторной авторизации используйте /auth");
        } else {
            sendMessage(chatId, "❌ Вы не авторизованы. Используйте /auth для входа.");
        }
    }
    
    private void sendHelpMessage(Long chatId) {
        UserState userState = getUserState(chatId);
        
        String help = "📋 Доступные команды:\n\n" +
                     "/start - Начало работы\n" +
                     "/auth - Авторизация в системе\n" +
                     "/help - Показать справку\n" +
                     "/logout - Выйти из системы\n\n";
        
        if ("AUTHENTICATED".equals(userState.state)) {
            help += "✅ Вы авторизованы как: " + userState.userRole + "\n\n";
            
            // Добавляем ролевые команды
            switch (userState.userRole) {
                case "МАСТЕР ПРОИЗВОДСТВА":
                    help += "🎯 Команды для мастера производства:\n" +
                           "• Просмотр заказов\n" +
                           "• Управление материалами\n" +
                           "• Изменение статусов заказов\n";
                    break;
                case "ПОКУПАТЕЛЬ":
                    help += "🎯 Команды для покупателя:\n" +
                           "• Проверка скидки\n" +
                           "• Статус заказа\n" +
                           "• Заказ рамок\n";
                    break;
            }
        } else {
            help += "🔐 Для доступа к функциям системы выполните авторизацию: /auth";
        }
        
        sendMessage(chatId, help);
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            execute(message);
            System.out.println("Message sent to chat: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message to " + chatId + ": " + e.getMessage());
        }
    }
}