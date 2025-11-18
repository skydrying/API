package group.api.telegram;

import group.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import group.api.controller.MainController;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    private final String botUsername = "frameshopAPI_bot";
    private final String botToken = "8218247231:AAELszaRBp5-TVtDEOpJmJSfVIylzeAzum0";

    @Autowired
    private MainController mainController;

    private ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();

    private static class UserState {
        String state;
        String login;
        String userRole;
        Long userId;
        String fullName;
        Long selectedOrderId;
        Integer lastMessageId;

        // Новые поля для навигации по заказам
        List<Orders> currentOrders;
        int currentOrderIndex;

        // Новые поля для навигации по ассортименту
        List<?> currentAssortment;
        int currentAssortmentIndex;
        String currentAssortmentType;

        // Новые поля для заказа рамки
        CustomFrameOrder currentFrameOrder;
        String currentFrameOrderStep;

        // Новые поля для свободных заказов
        List<Orders> freeOrders;
        int currentFreeOrderIndex;
        boolean viewingFreeOrders;

        Double currentMaterialEstimate;
        Double currentMaterialActual;

        // Новые поля для управления материалами рамок
        List<FrameMaterial> currentFrameMaterials;
        int currentFrameMaterialIndex;
        FrameMaterial selectedFrameMaterial;
        String frameMaterialAction; // "ADD", "EDIT", "DELETE"
        String waitingForField; // Поле, которое ожидаем от пользователя

        List<FrameComponent> currentFrameComponents;
        int currentFrameComponentIndex;
        FrameComponent selectedFrameComponent;
        String frameComponentAction; // "ADD", "EDIT", "DELETE"
        String waitingForFieldComponent;

        Customer registrationCustomer;
        String registrationStep;

        UserState() {
            this.state = "START";
            this.selectedOrderId = null;
            this.lastMessageId = null;
            this.currentOrders = new ArrayList<>();
            this.currentOrderIndex = 0;
            this.currentAssortment = new ArrayList<>();
            this.currentAssortmentIndex = 0;
            this.currentAssortmentType = "";
            this.currentFrameOrder = new CustomFrameOrder();
            this.currentFrameOrderStep = "";
            this.freeOrders = new ArrayList<>();
            this.currentFreeOrderIndex = 0;
            this.viewingFreeOrders = false;
            this.currentMaterialEstimate = null;
            this.currentMaterialActual = null;

            // Инициализация новых полей для управления материалами рамок
            this.currentFrameMaterials = new ArrayList<>();
            this.currentFrameMaterialIndex = 0;
            this.selectedFrameMaterial = null;
            this.frameMaterialAction = "";
            this.waitingForField = "";

            this.currentFrameComponents = new ArrayList<>();
            this.currentFrameComponentIndex = 0;
            this.selectedFrameComponent = null;
            this.frameComponentAction = "";
            this.waitingForFieldComponent = "";

            this.registrationCustomer = new Customer();
            this.registrationStep = "";
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
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(Long chatId, String messageText, String userName) {
        UserState userState = getUserState(chatId);

        try {
            if ("WAITING_LOGIN".equals(userState.state)) {
                handleLoginInput(chatId, messageText, userState);
                return;
            } else if ("WAITING_PASSWORD".equals(userState.state)) {
                handlePasswordInput(chatId, messageText, userState);
                return;
            } else if (userState.state.startsWith("FRAME_ORDER_")) {
                handleFrameOrderInput(chatId, messageText, userState);
                return;
            } else if ("WAITING_ORDER_COST".equals(userState.state)) {
                handleOrderCostInput(chatId, messageText, userState);
                return;
            } else if ("WAITING_MATERIAL_ESTIMATE".equals(userState.state)) {
                handleMaterialEstimateInput(chatId, messageText, userState);
                return;
            } else if ("WAITING_ACTUAL_MATERIAL".equals(userState.state)) {
                handleActualMaterialInput(chatId, messageText, userState);
                return;
            }
            // НОВАЯ ПРОВЕРКА ДЛЯ ОБРАБОТКИ ВВОДА ДАННЫХ МАТЕРИАЛОВ
            else if (userState.waitingForField != null && !userState.waitingForField.isEmpty()) {
                handleFrameMaterialFieldInput(chatId, messageText, userState);
                return;
            }
            else if (userState.waitingForFieldComponent != null && !userState.waitingForFieldComponent.isEmpty()) {
                handleFrameComponentFieldInput(chatId, messageText, userState);
                return;
            }
            else if (userState.state.startsWith("REGISTRATION_")) {
                handleRegistrationInput(chatId, messageText, userState);
                return;
            }

            switch (messageText.toLowerCase()) {
                case "/start":
                    sendWelcomeMessage(chatId, userName);
                    break;
                case "/auth":
                    startAuthorization(chatId, userState);
                    break;
                case "/register":  // ДОБАВЬТЕ ЭТОТ CASE
                    startRegistration(chatId, userState);
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
                        sendMessage(chatId, "Для работы с системой необходимо авторизоваться. Используйте команду /auth или /register для регистрации.");
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            sendMessage(chatId, "❌ Произошла ошибка. Попробуйте позже.");
            e.printStackTrace();
        }
    }

    private void startRegistration(Long chatId, UserState userState) {
        userState.state = "REGISTRATION_FIRST_NAME"; // ИЗМЕНЕНО
        userState.registrationCustomer = new Customer();
        userState.registrationStep = "FIRST_NAME";

        String text = "👋 Добро пожаловать в регистрацию!\n\n" +
                "Давайте создадим ваш аккаунт покупателя.\n\n" +
                "📝 Шаг 1: Введите ваше имя:";

        sendMessage(chatId, text);
    }

    private void handleRegistrationInput(Long chatId, String messageText, UserState userState) {
        try {
            switch (userState.state) {
                case "REGISTRATION_FIRST_NAME":
                    userState.registrationCustomer.setFirstName(messageText.trim());
                    userState.state = "REGISTRATION_LAST_NAME";
                    userState.registrationStep = "LAST_NAME";
                    sendMessage(chatId, "✅ Имя сохранено!\n\n" +
                            "📝 Шаг 2: Введите вашу фамилию:");
                    break;

                case "REGISTRATION_LAST_NAME":
                    userState.registrationCustomer.setLastName(messageText.trim());
                    userState.state = "REGISTRATION_MIDDLE_NAME";
                    userState.registrationStep = "MIDDLE_NAME";
                    sendMessage(chatId, "✅ Фамилия сохранена!\n\n" +
                            "📝 Шаг 3: Введите ваше отчество:\n\n" +
                            "Если отчества нет, напишите \"нет\"");
                    break;

                case "REGISTRATION_MIDDLE_NAME":
                    String middleName = messageText.trim();
                    if (middleName.equalsIgnoreCase("нет") || middleName.isEmpty()) {
                        userState.registrationCustomer.setMiddleName("");
                    } else {
                        userState.registrationCustomer.setMiddleName(middleName);
                    }
                    userState.state = "REGISTRATION_PHONE";
                    userState.registrationStep = "PHONE";
                    sendMessage(chatId, "✅ Отчество сохранено!\n\n" +
                            "📝 Шаг 4: Введите ваш номер телефона:\n\n" +
                            "Пример: +79123456789 или 89123456789");
                    break;

                case "REGISTRATION_PHONE":
                    String phone = messageText.trim();
                    // Простая валидация телефона
                    if (phone.matches("^[+]?[0-9]{10,15}$")) {
                        userState.registrationCustomer.setPhone(phone);
                        userState.state = "REGISTRATION_EMAIL";
                        userState.registrationStep = "EMAIL";
                        sendMessage(chatId, "✅ Телефон сохранен!\n\n" +
                                "📝 Шаг 5: Введите ваш email:");
                    } else {
                        sendMessage(chatId, "❌ Неверный формат телефона. Пожалуйста, введите номер в формате:\n" +
                                "+79123456789 или 89123456789");
                    }
                    break;

                case "REGISTRATION_EMAIL":
                    String email = messageText.trim();
                    // Простая валидация email
                    if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        userState.registrationCustomer.setEmail(email);
                        userState.state = "REGISTRATION_LOGIN";
                        userState.registrationStep = "LOGIN";
                        sendMessage(chatId, "✅ Email сохранен!\n\n" +
                                "📝 Шаг 6: Придумайте логин для входа:");
                    } else {
                        sendMessage(chatId, "❌ Неверный формат email. Пожалуйста, введите корректный email:");
                    }
                    break;

                case "REGISTRATION_LOGIN":
                    String login = messageText.trim();
                    // Проверяем, не занят ли логин
                    if (isLoginAvailable(login)) {
                        userState.registrationCustomer.setLogins(login);
                        userState.state = "REGISTRATION_PASSWORD";
                        userState.registrationStep = "PASSWORD";
                        sendMessage(chatId, "✅ Логин сохранен!\n\n" +
                                "📝 Шаг 7: Придумайте пароль:\n\n" +
                                "Пароль должен содержать не менее 6 символов.");
                    } else {
                        sendMessage(chatId, "❌ Этот логин уже занят. Пожалуйста, выберите другой логин:");
                    }
                    break;

                case "REGISTRATION_PASSWORD":
                    String password = messageText.trim();
                    if (password.length() >= 6) {
                        userState.registrationCustomer.setPasswords(password);
                        userState.state = "REGISTRATION_CONFIRM";
                        userState.registrationStep = "CONFIRM";
                        showRegistrationConfirmation(chatId, userState);
                    } else {
                        sendMessage(chatId, "❌ Пароль слишком короткий. Пожалуйста, придумайте пароль длиной не менее 6 символов:");
                    }
                    break;

                default:
                    sendMessage(chatId, "❌ Ошибка в процессе регистрации. Начните заново: /register");
                    userState.state = "START";
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling registration input: " + e.getMessage());
            sendMessage(chatId, "❌ Произошла ошибка при регистрации. Попробуйте позже: /register");
            userState.state = "START";
        }
    }

    // НОВЫЙ МЕТОД ДЛЯ ПРОВЕРКИ ДОСТУПНОСТИ ЛОГИНА
    private boolean isLoginAvailable(String login) {
        try {
            // Проверяем среди пользователей
            Iterable<User> users = mainController.allUsers();
            for (User user : users) {
                if (user.getLogin() != null && user.getLogin().equals(login)) {
                    return false;
                }
            }

            // Проверяем среди покупателей
            Iterable<Customer> customers = mainController.allCustomers();
            for (Customer customer : customers) {
                if (customer.getLogins() != null && customer.getLogins().equals(login)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error checking login availability: " + e.getMessage());
            return false;
        }
    }

    // НОВЫЙ МЕТОД ДЛЯ ПОКАЗА ПОДТВЕРЖДЕНИЯ РЕГИСТРАЦИИ
    private void showRegistrationConfirmation(Long chatId, UserState userState) {
        Customer customer = userState.registrationCustomer;

        StringBuilder confirmationText = new StringBuilder();
        confirmationText.append("✅ Проверьте введенные данные:\n\n");
        confirmationText.append("👤 Имя: ").append(customer.getFirstName()).append("\n");
        confirmationText.append("👤 Фамилия: ").append(customer.getLastName()).append("\n");
        if (customer.getMiddleName() != null && !customer.getMiddleName().isEmpty()) {
            confirmationText.append("👤 Отчество: ").append(customer.getMiddleName()).append("\n");
        }
        confirmationText.append("📞 Телефон: ").append(customer.getPhone()).append("\n");
        confirmationText.append("📧 Email: ").append(customer.getEmail()).append("\n");
        confirmationText.append("🔑 Логин: ").append(customer.getLogins()).append("\n");
        confirmationText.append("\nВсё верно?");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка подтверждения
        List<InlineKeyboardButton> confirmRow = new ArrayList<>();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("✅ Да, всё верно");
        confirmButton.setCallbackData("confirm_registration");
        confirmRow.add(confirmButton);

        // Кнопка отмены
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Нет, исправить");
        cancelButton.setCallbackData("cancel_registration");
        cancelRow.add(cancelButton);

        rows.add(confirmRow);
        rows.add(cancelRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, confirmationText.toString(), keyboard);
    }

    // НОВЫЙ МЕТОД ДЛЯ ЗАВЕРШЕНИЯ РЕГИСТРАЦИИ
    private void completeRegistration(Long chatId, UserState userState) {
        try {
            Customer customer = userState.registrationCustomer;

            // Проверяем, что все обязательные поля заполнены
            if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty() ||
                    customer.getLastName() == null || customer.getLastName().trim().isEmpty() ||
                    customer.getPhone() == null || customer.getPhone().trim().isEmpty() ||
                    customer.getEmail() == null || customer.getEmail().trim().isEmpty() ||
                    customer.getLogins() == null || customer.getLogins().trim().isEmpty() ||
                    customer.getPasswords() == null || customer.getPasswords().trim().isEmpty()) {

                throw new Exception("Не все обязательные поля заполнены");
            }

            // Сохраняем покупателя через новый метод registerCustomer
            ResponseEntity<Integer> response = mainController.registerCustomer(
                    customer.getLastName().trim(),
                    customer.getFirstName().trim(),
                    customer.getMiddleName() != null ? customer.getMiddleName().trim() : "", // отчество
                    customer.getPhone().trim(),
                    customer.getEmail().trim(),
                    customer.getLogins().trim(),
                    customer.getPasswords().trim()
            );

            Integer customerId = null;
            if (response != null && response.getBody() != null) {
                customerId = response.getBody();
            }

            if (customerId != null && customerId > 0) {
                // Авторизуем пользователя
                userState.state = "AUTHENTICATED";
                userState.userRole = "ПОКУПАТЕЛЬ";
                userState.userId = customerId.longValue();

                // Формируем полное имя с учетом отчества
                String fullName = customer.getLastName() + " " + customer.getFirstName();
                if (customer.getMiddleName() != null && !customer.getMiddleName().isEmpty()) {
                    fullName += " " + customer.getMiddleName();
                }
                userState.fullName = fullName.trim();

                // Очищаем данные регистрации
                userState.registrationCustomer = new Customer();
                userState.registrationStep = "";

                String welcomeText = "🎉 Регистрация успешно завершена!\n\n" +
                        "Добро пожаловать, " + userState.fullName + "!\n\n" +
                        "✅ Ваш аккаунт покупателя создан.\n" +
                        "🔑 Логин: " + customer.getLogins() + "\n" +
                        "💰 Начальная скидка: 0%\n\n" +
                        "Теперь вы можете пользоваться всеми функциями системы!";

                sendMessage(chatId, welcomeText);
                sendMainMenu(chatId, userState);

            } else {
                throw new Exception("Не удалось сохранить покупателя (ID: " + customerId + ")");
            }

        } catch (Exception e) {
            System.err.println("Error completing registration: " + e.getMessage());
            e.printStackTrace();
            sendMessage(chatId, "❌ Ошибка при сохранении данных: " + e.getMessage() + "\n\nПопробуйте позже: /register");
            userState.state = "START";
            userState.registrationCustomer = new Customer();
            userState.registrationStep = "";
        }
    }

    // НОВЫЙ МЕТОД ДЛЯ ОТМЕНЫ РЕГИСТРАЦИИ
    private void cancelRegistration(Long chatId, UserState userState) {
        userState.state = "START";
        userState.registrationCustomer = new Customer();
        userState.registrationStep = "";

        sendMessage(chatId, "❌ Регистрация отменена.\n\n" +
                "Если хотите попробовать снова, используйте команду /register");
    }

    // Метод для обработки ввода данных материалов
    private void handleFrameMaterialFieldInput(Long chatId, String messageText, UserState userState) {
        try {
            String field = userState.waitingForField;

            if ("ADD".equals(userState.frameMaterialAction)) {
                // Обработка добавления нового материала
                switch (field) {
                    case "NAME":
                        userState.selectedFrameMaterial.setName(messageText.trim());
                        userState.waitingForField = "DESCRIPTION";
                        sendMessage(chatId, "✅ Название сохранено.\n\nВведите описание материала:");
                        break;
                    case "DESCRIPTION":
                        userState.selectedFrameMaterial.setDescription(messageText.trim());
                        userState.waitingForField = "PRICE_PER_METER";
                        sendMessage(chatId, "✅ Описание сохранено.\n\nВведите цену за метр (число):");
                        break;
                    case "PRICE_PER_METER":
                        try {
                            // Используем Integer вместо BigDecimal
                            Integer price = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setPricePerMeter(price);
                            userState.waitingForField = "STOCK_QUANTITY"; // ДОБАВИТЬ ЭТУ СТРОКУ
                            sendMessage(chatId, "✅ Цена сохранена.\n\nВведите количество на складе (число, в метрах):"); // ДОБАВИТЬ ЭТУ СТРОКУ
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное целое число для цены:");
                        }
                        break;
                    case "STOCK_QUANTITY":
                        try {
                            Integer quantity = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setStockQuantity(quantity);
                            userState.waitingForField = "COLOR";
                            sendMessage(chatId, "✅ Количество сохранено.\n\nВведите цвет материала:");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества:");
                        }
                        break;
                    case "COLOR":
                        userState.selectedFrameMaterial.setColor(messageText.trim());
                        userState.waitingForField = "WIDTH";
                        sendMessage(chatId, "✅ Цвет сохранен.\n\nВведите ширину материала (число, в мм):");
                        break;
                    case "WIDTH":
                        try {
                            Integer width = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setWidth(width);

                            // Сохраняем материал в базу данных
                            mainController.createFrameMaterial(userState.selectedFrameMaterial);

                            sendMessage(chatId, "✅ Материал успешно добавлен!\n\nНазвание: " + userState.selectedFrameMaterial.getName() +
                                    "\nОписание: " + userState.selectedFrameMaterial.getDescription() +
                                    "\nЦена за метр: " + userState.selectedFrameMaterial.getPricePerMeter() + " руб." +
                                    "\nКоличество: " + userState.selectedFrameMaterial.getStockQuantity() + " м." +
                                    "\nЦвет: " + userState.selectedFrameMaterial.getColor() +
                                    "\nШирина: " + userState.selectedFrameMaterial.getWidth() + " мм");

                            // Сбрасываем состояние
                            userState.frameMaterialAction = "";
                            userState.waitingForField = "";
                            userState.selectedFrameMaterial = null;

                            showFrameMaterialsManagementMenu(chatId, userState);

                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для ширины:");
                        } catch (Exception e) {
                            System.err.println("Error creating frame material: " + e.getMessage());
                            sendMessage(chatId, "❌ Ошибка при сохранении материала. Попробуйте позже.");
                            userState.frameMaterialAction = "";
                            userState.waitingForField = "";
                            userState.selectedFrameMaterial = null;
                            showFrameMaterialsManagementMenu(chatId, userState);
                        }
                        break;
                }
            } else if ("EDIT".equals(userState.frameMaterialAction)) {
                // Обработка редактирования существующего материала
                switch (field) {
                    case "NAME":
                        userState.selectedFrameMaterial.setName(messageText.trim());
                        break;
                    case "DESCRIPTION":
                        userState.selectedFrameMaterial.setDescription(messageText.trim());
                        break;
                    case "PRICE_PER_METER":
                        try {
                            // ИСПРАВЛЕНО: используем Integer вместо BigDecimal
                            Integer price = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setPricePerMeter(price);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное целое число для цены:");
                            return;
                        }
                        break;
                    case "STOCK_QUANTITY":
                        try {
                            Integer quantity = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setStockQuantity(quantity);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества:");
                            return;
                        }
                        break;
                    case "COLOR":
                        userState.selectedFrameMaterial.setColor(messageText.trim());
                        break;
                    case "WIDTH":
                        try {
                            Integer width = Integer.parseInt(messageText.trim());
                            userState.selectedFrameMaterial.setWidth(width);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для ширины:");
                            return;
                        }
                        break;
                }

                // Сохраняем изменения
                try {
                    mainController.updateFrameMaterial(userState.selectedFrameMaterial);
                    sendMessage(chatId, "✅ Изменения успешно сохранены!");

                    // Сбрасываем состояние
                    userState.frameMaterialAction = "";
                    userState.waitingForField = "";
                    userState.selectedFrameMaterial = null;

                    showFrameMaterialsManagementMenu(chatId, userState);

                } catch (Exception e) {
                    System.err.println("Error updating frame material: " + e.getMessage());
                    sendMessage(chatId, "❌ Ошибка при сохранении изменений. Попробуйте позже.");
                    userState.frameMaterialAction = "";
                    userState.waitingForField = "";
                    userState.selectedFrameMaterial = null;
                    showFrameMaterialsManagementMenu(chatId, userState);
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling frame material field input: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обработке данных. Попробуйте позже.");
            userState.frameMaterialAction = "";
            userState.waitingForField = "";
            userState.selectedFrameMaterial = null;
            showFrameMaterialsManagementMenu(chatId, userState);
        }
    }

    private void deleteFrameMaterial(Long chatId, UserState userState) {
        try {
            if (userState.selectedFrameMaterial == null) {
                sendMessage(chatId, "❌ Материал не выбран.");
                return;
            }

            String materialName = userState.selectedFrameMaterial.getName();
            mainController.deleteFrameMaterial(userState.selectedFrameMaterial.getId());

            sendMessage(chatId, "✅ Материал \"" + materialName + "\" успешно удален!");

            // Сбрасываем состояние
            userState.frameMaterialAction = "";
            userState.waitingForField = "";
            userState.selectedFrameMaterial = null;

            showFrameMaterialsManagementMenu(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error deleting frame material: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при удалении материала. Попробуйте позже.");
            userState.frameMaterialAction = "";
            userState.waitingForField = "";
            userState.selectedFrameMaterial = null;
            showFrameMaterialsManagementMenu(chatId, userState);
        }
    }

    private void handleFreeOrderNavigation(Long chatId, String action, UserState userState) {
        if (action.equals("no_action_free_order")) {
            return;
        }

        if (action.equals("prev_free_order")) {
            if (userState.currentFreeOrderIndex > 0) {
                userState.currentFreeOrderIndex--;
                editCurrentFreeOrder(chatId, userState);
            }
        } else if (action.equals("next_free_order")) {
            if (userState.currentFreeOrderIndex < userState.freeOrders.size() - 1) {
                userState.currentFreeOrderIndex++;
                editCurrentFreeOrder(chatId, userState);
            }
        }
    }

    private void editCurrentFreeOrder(Long chatId, UserState userState) {
        if (userState.freeOrders == null || userState.freeOrders.isEmpty()) {
            return;
        }

        Orders currentOrder = userState.freeOrders.get(userState.currentFreeOrderIndex);
        String orderText = formatFreeOrderDetails(currentOrder, userState.currentFreeOrderIndex + 1, userState.freeOrders.size());
        InlineKeyboardMarkup keyboard = createFreeOrderNavigationKeyboard(userState, currentOrder);

        if (userState.lastMessageId != null) {
            editMessageWithInlineKeyboard(chatId, userState.lastMessageId, orderText, keyboard);
        } else {
            sendMessageWithInlineKeyboard(chatId, orderText, keyboard);
        }
    }


    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        UserState userState = getUserState(chatId);

        try {
            if (data.equals("prev_order") || data.equals("next_order") ||
                    data.equals("change_current_order_status") || data.equals("no_action")) {

                handleOrderNavigation(chatId, data, userState);

            }
            else if (data.equals("prev_assortment") || data.equals("next_assortment") ||
                    data.equals("no_action_assortment")) {

                handleAssortmentNavigation(chatId, data, userState);

            }
            else if (data.equals("prev_free_order") || data.equals("next_free_order") ||
                    data.equals("no_action_free_order")) {

                handleFreeOrderNavigation(chatId, data, userState);

            }
            else if (data.startsWith("view_order_")) {
                String orderIdStr = data.substring("view_order_".length());
                Long orderId = Long.parseLong(orderIdStr);
                showOrderDetails(chatId, orderId, userState);
            }
            // НОВЫЕ ОБРАБОТЧИКИ ДЛЯ УПРАВЛЕНИЯ МАТЕРИАЛАМИ РАМОК
            else if (data.equals("prev_frame_material") || data.equals("next_frame_material") ||
                    data.equals("no_action_frame_material") || data.equals("select_frame_material")) {
                handleFrameMaterialNavigation(chatId, data, userState);
            }
            else if (data.startsWith("frame_material_field_")) {
                handleFrameMaterialFieldSelection(chatId, data, userState);
            }
            else if (data.startsWith("frame_material_action_")) {
                handleFrameMaterialAction(chatId, data, userState);
            }
            else if (data.startsWith("select_material_")) {
                String materialIdStr = data.substring("select_material_".length());
                Integer materialId = Integer.parseInt(materialIdStr);
                FrameMaterial frameMaterial = new FrameMaterial();
                frameMaterial.setId(materialId);
                userState.currentFrameOrder.setFrameMaterialID(frameMaterial);
                handleFrameOrderStep(chatId, userState, "WIDTH");
            }
            // НОВЫЕ ОБРАБОТЧИКИ ДЛЯ УПРАВЛЕНИЯ КОМПОНЕНТАМИ РАМОК
            else if (data.equals("prev_frame_component") || data.equals("next_frame_component") ||
                    data.equals("no_action_frame_component") || data.equals("select_frame_component")) {
                handleFrameComponentNavigation(chatId, data, userState);
            }
            else if (data.startsWith("frame_component_field_")) {
                handleFrameComponentFieldSelection(chatId, data, userState);
            }
            else if (data.startsWith("frame_component_action_")) {
                handleFrameComponentAction(chatId, data, userState);
            }
            else if ("frame_components_management".equals(data)) {
                if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                    showFrameComponentsManagementMenu(chatId, userState);
                } else {
                    sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для мастеров производства.");
                }
            }
            else if ("confirm_delete_frame_component".equals(data)) {
                if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                    deleteFrameComponent(chatId, userState);
                } else {
                    sendMessage(chatId, "❌ Доступ запрещен.");
                }
            }
            else if (data.startsWith("take_free_order_")) {
                String orderIdStr = data.substring("take_free_order_".length());
                Long orderId = Long.parseLong(orderIdStr);
                takeFreeOrder(chatId, orderId, userState);
            }
            // ДОБАВЛЕННЫЕ ОБРАБОТЧИКИ ДЛЯ СМЕНЫ СТАТУСА
            else if (data.startsWith("select_order_")) {
                // Обработка выбора заказа для смены статуса
                String orderIdStr = data.substring("select_order_".length());
                Long orderId = Long.parseLong(orderIdStr);
                sendStatusMenu(chatId, orderId);
            }
            else if (data.startsWith("change_status_")) {
                // Обработка смены статуса заказа
                String[] parts = data.substring("change_status_".length()).split("_");
                if (parts.length >= 2) {
                    Long orderId = Long.parseLong(parts[0]);
                    String newStatus = parts[1];
                    // Собираем полное название статуса, если оно состоит из нескольких частей
                    for (int i = 2; i < parts.length; i++) {
                        newStatus += "_" + parts[i];
                    }
                    changeOrderStatus(chatId, orderId, newStatus, userState);
                }
            }

            else if ("register".equals(data)) {
                startRegistration(chatId, userState);
            }
            else if ("confirm_registration".equals(data)) {
                completeRegistration(chatId, userState);
            }
            else if ("cancel_registration".equals(data)) {
                cancelRegistration(chatId, userState);
            }
            else if ("help".equals(data)) {
                sendHelpMessage(chatId);
            }
            else {
                switch (data) {
                    case "personal_data":
                        if ("AUTHENTICATED".equals(userState.state) && userState.userId != null) {
                            String response = getPersonalDataResponse(userState);
                            InlineKeyboardMarkup keyboard = createBackKeyboard();
                            sendMessageWithInlineKeyboard(chatId, response, keyboard);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Убедитесь, что вы авторизованы.");
                        }
                        break;
                    case "my_orders":
                        if ("ПОКУПАТЕЛЬ".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            showMyOrdersWithNavigation(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для покупателей.");
                        }
                        break;
                    case "assortment":
                        if ("ПОКУПАТЕЛЬ".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            showAssortmentCategories(chatId);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для покупателей.");
                        }
                        break;
                    case "order_frame":
                        if ("ПОКУПАТЕЛЬ".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            startFrameOrder(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для покупателей.");
                        }
                        break;
                    case "view_orders":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            System.out.println("Production master viewing orders for userId: " + userState.userId);
                            showProductionMasterOrdersWithNavigation(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для мастеров производства.");
                        }
                        break;
                    case "free_orders":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            showFreeOrdersWithNavigation(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для мастеров производства.");
                        }
                        break;
                    case "change_order_status":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            sendOrderListForStatusChange(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для мастеров производства.");
                        }
                        break;
                    // НОВЫЙ CASE ДЛЯ УПРАВЛЕНИЯ РАМКАМИ
                    case "frame_materials_management":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            showFrameMaterialsManagementMenu(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для мастеров производства.");
                        }
                        break;
                    case "confirm_delete_frame_material":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            deleteFrameMaterial(chatId, userState);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен.");
                        }
                        break;
                    case "exit":
                        logout(chatId, userState);
                        break;
                    case "back_to_menu":
                        sendMainMenu(chatId, userState);
                        break;
                    case "auth":
                        startAuthorization(chatId, userState);
                        break;
                    case "confirm_frame_order":
                        if (userState == null || !userState.state.equals("FRAME_ORDER_CONFIRM") || !"ПОКУПАТЕЛЬ".equals(userState.userRole)) {
                            sendMessage(chatId, "❌ Ошибка: заказ не найден или доступ запрещен.");
                            sendMainMenu(chatId, getUserState(chatId));
                        } else {
                            confirmFrameOrder(chatId, userState);
                        }
                        break;
                    case "cancel_frame_order":
                        cancelFrameOrder(chatId, userState);
                        break;
                    // Обработка категорий ассортимента
                    case "embroidery_kit":
                        showEmbroideryKitsWithNavigation(chatId, userState);
                        break;
                    case "consumable":
                        showConsumablesWithNavigation(chatId, userState);
                        break;
                    case "frame_component":
                        showFrameComponentsWithNavigation(chatId, userState);
                        break;
                    case "frame_material":
                        showFrameMaterialsWithNavigation(chatId, userState);
                        break;
                    default:
                        sendMessage(chatId, "❓ Неизвестная команда.");
                        break;
                }
            }

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            answer.setText("Обработано!");
            execute(answer);

        } catch (Exception e) {
            System.err.println("Error handling callback: " + e.getMessage());
            try {
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(callbackQuery.getId());
                answer.setText("Ошибка при обработке.");
                answer.setShowAlert(true);
                execute(answer);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Метод для показа меню управления компонентами рамок
    private void showFrameComponentsManagementMenu(Long chatId, UserState userState) {
        String text = "🖼️ Управление фурнитурами рамок\n\nВыберите действие:";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Добавить"
        List<InlineKeyboardButton> addRow = new ArrayList<>();
        InlineKeyboardButton addButton = new InlineKeyboardButton();
        addButton.setText("➕ Добавить");
        addButton.setCallbackData("frame_component_action_ADD");
        addRow.add(addButton);

        // Кнопка "Изменить"
        List<InlineKeyboardButton> editRow = new ArrayList<>();
        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("✏️ Изменить");
        editButton.setCallbackData("frame_component_action_EDIT");
        editRow.add(editButton);

        // Кнопка "Удалить"
        List<InlineKeyboardButton> deleteRow = new ArrayList<>();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("🗑️ Удалить");
        deleteButton.setCallbackData("frame_component_action_DELETE");
        deleteRow.add(deleteButton);

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад");
        backButton.setCallbackData("back_to_menu");
        backRow.add(backButton);

        rows.add(addRow);
        rows.add(editRow);
        rows.add(deleteRow);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Метод для обработки действий с компонентами рамок
    private void handleFrameComponentAction(Long chatId, String action, UserState userState) {
        String actionType = action.substring("frame_component_action_".length());

        try {
            switch (actionType) {
                case "ADD":
                    userState.frameComponentAction = "ADD";
                    userState.selectedFrameComponent = new FrameComponent();
                    startAddFrameComponent(chatId, userState);
                    break;
                case "EDIT":
                    userState.frameComponentAction = "EDIT";
                    showFrameComponentsForManagement(chatId, userState);
                    break;
                case "DELETE":
                    userState.frameComponentAction = "DELETE";
                    showFrameComponentsForManagement(chatId, userState);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling frame component action: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обработке действия. Попробуйте позже.");
        }
    }

    // Метод для начала добавления компонента
    private void startAddFrameComponent(Long chatId, UserState userState) {
        userState.waitingForFieldComponent = "NAME";
        sendMessage(chatId, "➕ Добавление нового компонента рамки\n\nВведите название компонента:");
    }

    // Метод для показа списка компонентов для управления
    private void showFrameComponentsForManagement(Long chatId, UserState userState) {
        try {
            Iterable<FrameComponent> frameComponents = mainController.allFC();
            List<FrameComponent> componentsList = new ArrayList<>();

            for (FrameComponent component : frameComponents) {
                componentsList.add(component);
            }

            if (componentsList.isEmpty()) {
                sendMessage(chatId, "❌ Нет компонентов для управления.");
                showFrameComponentsManagementMenu(chatId, userState);
                return;
            }

            userState.currentFrameComponents = componentsList;
            userState.currentFrameComponentIndex = 0;

            showCurrentFrameComponentForManagement(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing frame components for management: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке компонентов.");
        }
    }

    // Метод для показа текущего компонента при управлении
    private void showCurrentFrameComponentForManagement(Long chatId, UserState userState) {
        if (userState.currentFrameComponents == null || userState.currentFrameComponents.isEmpty()) {
            sendMessage(chatId, "❌ Нет компонентов для отображения.");
            return;
        }

        FrameComponent currentComponent = userState.currentFrameComponents.get(userState.currentFrameComponentIndex);
        String componentText = formatFrameComponentDetails(currentComponent,
                userState.currentFrameComponentIndex + 1,
                userState.currentFrameComponents.size());

        InlineKeyboardMarkup keyboard = createFrameComponentManagementKeyboard(userState, currentComponent);
        sendMessageWithInlineKeyboard(chatId, componentText, keyboard);
    }

    // Метод для форматирования деталей компонента
    private String formatFrameComponentDetails(FrameComponent component, int currentNumber, int totalComponents) {
        StringBuilder sb = new StringBuilder();
        sb.append("🖼️ Фурнитура ").append(currentNumber).append(" из ").append(totalComponents).append("\n\n");
        sb.append("📝 Название: ").append(component.getName() != null ? component.getName() : "Не указано").append("\n");
        sb.append("📋 Описание: ").append(component.getDescription() != null ? component.getDescription() : "Не указано").append("\n");
        sb.append("💰 Цена: ").append(component.getPrice() != null ? component.getPrice() : "0").append(" руб.\n");
        sb.append("📦 Количество на складе: ").append(component.getStockQuantity() != null ? component.getStockQuantity() : "0").append(" шт.\n");
        sb.append("📋 Тип: ").append(component.getType() != null ? component.getType() : "Не указан").append("\n");

        return sb.toString();
    }

    // Метод для создания клавиатуры управления компонентами
    private InlineKeyboardMarkup createFrameComponentManagementKeyboard(UserState userState, FrameComponent currentComponent) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Строка с навигацией
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        // Кнопка "Предыдущий"
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅️ Предыдущий");
        prevButton.setCallbackData("prev_frame_component");
        if (userState.currentFrameComponentIndex > 0) {
            navRow.add(prevButton);
        } else {
            InlineKeyboardButton disabledPrev = new InlineKeyboardButton();
            disabledPrev.setText("⏹️ Предыдущий");
            disabledPrev.setCallbackData("no_action_frame_component");
            navRow.add(disabledPrev);
        }

        // Кнопка "Выбрать этот" для EDIT и DELETE
        if ("EDIT".equals(userState.frameComponentAction) || "DELETE".equals(userState.frameComponentAction)) {
            InlineKeyboardButton selectButton = new InlineKeyboardButton();
            selectButton.setText("✅ Выбрать этот");
            selectButton.setCallbackData("select_frame_component");
            navRow.add(selectButton);
        }

        // Кнопка "Следующий"
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий ➡️");
        nextButton.setCallbackData("next_frame_component");
        if (userState.currentFrameComponentIndex < userState.currentFrameComponents.size() - 1) {
            navRow.add(nextButton);
        } else {
            InlineKeyboardButton disabledNext = new InlineKeyboardButton();
            disabledNext.setText("⏹️ Следующий");
            disabledNext.setCallbackData("no_action_frame_component");
            navRow.add(disabledNext);
        }

        rows.add(navRow);

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад");
        backButton.setCallbackData("frame_components_management");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    // Метод для обработки навигации по компонентам
    private void handleFrameComponentNavigation(Long chatId, String action, UserState userState) {
        if (action.equals("no_action_frame_component")) {
            return;
        }

        if (action.equals("prev_frame_component")) {
            if (userState.currentFrameComponentIndex > 0) {
                userState.currentFrameComponentIndex--;
                editCurrentFrameComponentForManagement(chatId, userState);
            }
        } else if (action.equals("next_frame_component")) {
            if (userState.currentFrameComponentIndex < userState.currentFrameComponents.size() - 1) {
                userState.currentFrameComponentIndex++;
                editCurrentFrameComponentForManagement(chatId, userState);
            }
        } else if (action.equals("select_frame_component")) {
            userState.selectedFrameComponent = userState.currentFrameComponents.get(userState.currentFrameComponentIndex);

            if ("EDIT".equals(userState.frameComponentAction)) {
                showEditFrameComponentMenu(chatId, userState);
            } else if ("DELETE".equals(userState.frameComponentAction)) {
                showDeleteComponentConfirmation(chatId, userState);
            }
        }
    }

    private void editCurrentFrameComponentForManagement(Long chatId, UserState userState) {
        if (userState.currentFrameComponents == null || userState.currentFrameComponents.isEmpty()) {
            return;
        }

        FrameComponent currentComponent = userState.currentFrameComponents.get(userState.currentFrameComponentIndex);
        String componentText = formatFrameComponentDetails(currentComponent,
                userState.currentFrameComponentIndex + 1,
                userState.currentFrameComponents.size());

        InlineKeyboardMarkup keyboard = createFrameComponentManagementKeyboard(userState, currentComponent);

        if (userState.lastMessageId != null) {
            editMessageWithInlineKeyboard(chatId, userState.lastMessageId, componentText, keyboard);
        } else {
            sendMessageWithInlineKeyboard(chatId, componentText, keyboard);
        }
    }

    // Метод для показа меню редактирования компонента
    private void showEditFrameComponentMenu(Long chatId, UserState userState) {
        if (userState.selectedFrameComponent == null) {
            sendMessage(chatId, "❌ Ошибка: фурнитура не выбрана");
            showFrameComponentsManagementMenu(chatId, userState);
            return;
        }

        String componentName = userState.selectedFrameComponent.getName() != null ?
                userState.selectedFrameComponent.getName() : "Без названия";
        String text = "✏️ Редактирование компонента: " + userState.selectedFrameComponent.getName() + "\n\nЧто вы хотите изменить?";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопки для выбора поля для изменения
        String[] fields = {"NAME", "DESCRIPTION", "PRICE", "STOCK_QUANTITY", "TYPE"};
        String[] fieldNames = {"📝 Название", "📋 Описание", "💰 Цена", "📦 Количество", "📋 Тип"};

        for (int i = 0; i < fields.length; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton fieldButton = new InlineKeyboardButton();
            fieldButton.setText(fieldNames[i]);
            fieldButton.setCallbackData("frame_component_field_" + fields[i]);
            row.add(fieldButton);
            rows.add(row);
        }

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад к списку");
        backButton.setCallbackData("frame_component_action_EDIT");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Метод для обработки выбора поля для изменения компонента
    private void handleFrameComponentFieldSelection(Long chatId, String data, UserState userState) {
        String field = data.substring("frame_component_field_".length());
        userState.waitingForFieldComponent = field;

        String prompt = "";
        switch (field) {
            case "NAME":
                prompt = "Введите новое название компонента:";
                break;
            case "DESCRIPTION":
                prompt = "Введите новое описание компонента:";
                break;
            case "PRICE":
                prompt = "Введите новую цену (число):";
                break;
            case "STOCK_QUANTITY":
                prompt = "Введите новое количество на складе (число):";
                break;
            case "TYPE":
                prompt = "Введите новый тип компонента:";
                break;
        }

        sendMessage(chatId, prompt);
    }

    // Метод для показа подтверждения удаления компонента
    private void showDeleteComponentConfirmation(Long chatId, UserState userState) {
        String text = "🗑️ Вы уверены, что хотите удалить фурнитуру:\n\"" + userState.selectedFrameComponent.getName() + "\"?\n\nЭта операция необратима!";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка подтверждения удаления
        List<InlineKeyboardButton> confirmRow = new ArrayList<>();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("✅ Да, удалить");
        confirmButton.setCallbackData("confirm_delete_frame_component");
        confirmRow.add(confirmButton);

        // Кнопка отмены
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отмена");
        cancelButton.setCallbackData("frame_component_action_DELETE");
        cancelRow.add(cancelButton);

        rows.add(confirmRow);
        rows.add(cancelRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Метод для удаления компонента
    private void deleteFrameComponent(Long chatId, UserState userState) {
        try {
            if (userState.selectedFrameComponent == null) {
                sendMessage(chatId, "❌ Фурнитура не выбрана");
                return;
            }

            String componentName = userState.selectedFrameComponent.getName();
            mainController.deleteFrameComponent(userState.selectedFrameComponent.getId());

            sendMessage(chatId, "✅ Фурнитура \"" + componentName + "\" успешно удалена!");

            // Сбрасываем состояние
            userState.frameComponentAction = "";
            userState.waitingForFieldComponent = "";
            userState.selectedFrameComponent = null;

            showFrameComponentsManagementMenu(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error deleting frame component: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при удалении компонента. Попробуйте позже.");
            userState.frameComponentAction = "";
            userState.waitingForFieldComponent = "";
            userState.selectedFrameComponent = null;
            showFrameComponentsManagementMenu(chatId, userState);
        }
    }

    // Метод для обработки ввода данных компонентов
    private void handleFrameComponentFieldInput(Long chatId, String messageText, UserState userState) {
        try {
            String field = userState.waitingForFieldComponent;

            if ("ADD".equals(userState.frameComponentAction)) {
                // Обработка добавления нового компонента
                switch (field) {
                    case "NAME":
                        userState.selectedFrameComponent.setName(messageText.trim());
                        userState.waitingForFieldComponent = "DESCRIPTION";
                        sendMessage(chatId, "✅ Название сохранено.\n\nВведите описание компонента:");
                        break;
                    case "DESCRIPTION":
                        userState.selectedFrameComponent.setDescription(messageText.trim());
                        userState.waitingForFieldComponent = "PRICE";
                        sendMessage(chatId, "✅ Описание сохранено.\n\nВведите цену (число):");
                        break;
                    case "PRICE":
                        try {
                            Integer price = Integer.parseInt(messageText.trim());
                            userState.selectedFrameComponent.setPrice(price);
                            userState.waitingForFieldComponent = "STOCK_QUANTITY";
                            sendMessage(chatId, "✅ Цена сохранена.\n\nВведите количество на складе (число):");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное целое число для цены:");
                        }
                        break;
                    case "STOCK_QUANTITY":
                        try {
                            Integer quantity = Integer.parseInt(messageText.trim());
                            userState.selectedFrameComponent.setStockQuantity(quantity);
                            userState.waitingForFieldComponent = "TYPE";
                            sendMessage(chatId, "✅ Количество сохранено.\n\nВведите тип компонента:");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества:");
                        }
                        break;
                    case "TYPE":
                        userState.selectedFrameComponent.setType(messageText.trim());

                        // Сохраняем компонент в базу данных
                        mainController.createFrameComponent(userState.selectedFrameComponent);

                        sendMessage(chatId, "✅ Фурнитура успешно добавлена!\n\nНазвание: " + userState.selectedFrameComponent.getName() +
                                "\nОписание: " + userState.selectedFrameComponent.getDescription() +
                                "\nЦена: " + userState.selectedFrameComponent.getPrice() + " руб." +
                                "\nКоличество: " + userState.selectedFrameComponent.getStockQuantity() + " шт." +
                                "\nТип: " + userState.selectedFrameComponent.getType());

                        // Сбрасываем состояние
                        userState.frameComponentAction = "";
                        userState.waitingForFieldComponent = "";
                        userState.selectedFrameComponent = null;

                        showFrameComponentsManagementMenu(chatId, userState);

                        break;
                }
            } else if ("EDIT".equals(userState.frameComponentAction)) {
                // Обработка редактирования существующего компонента
                switch (field) {
                    case "NAME":
                        userState.selectedFrameComponent.setName(messageText.trim());
                        break;
                    case "DESCRIPTION":
                        userState.selectedFrameComponent.setDescription(messageText.trim());
                        break;
                    case "PRICE":
                        try {
                            Integer price = Integer.parseInt(messageText.trim());
                            userState.selectedFrameComponent.setPrice(price);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное целое число для цены:");
                            return;
                        }
                        break;
                    case "STOCK_QUANTITY":
                        try {
                            Integer quantity = Integer.parseInt(messageText.trim());
                            userState.selectedFrameComponent.setStockQuantity(quantity);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества:");
                            return;
                        }
                        break;
                    case "TYPE":
                        userState.selectedFrameComponent.setType(messageText.trim());
                        break;
                }

                // Сохраняем изменения
                try {
                    mainController.updateFrameComponent(userState.selectedFrameComponent);
                    sendMessage(chatId, "✅ Изменения успешно сохранены!");

                    // Сбрасываем состояние
                    userState.frameComponentAction = "";
                    userState.waitingForFieldComponent = "";
                    userState.selectedFrameComponent = null;

                    showFrameComponentsManagementMenu(chatId, userState);

                } catch (Exception e) {
                    System.err.println("Error updating frame component: " + e.getMessage());
                    sendMessage(chatId, "❌ Ошибка при сохранении изменений. Попробуйте позже.");
                    userState.frameComponentAction = "";
                    userState.waitingForFieldComponent = "";
                    userState.selectedFrameComponent = null;
                    showFrameComponentsManagementMenu(chatId, userState);
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling frame component field input: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обработке данных. Попробуйте позже.");
            userState.frameComponentAction = "";
            userState.waitingForFieldComponent = "";
            userState.selectedFrameComponent = null;
            showFrameComponentsManagementMenu(chatId, userState);
        }
    }

    // Метод для показа меню управления материалами рамок
    private void showFrameMaterialsManagementMenu(Long chatId, UserState userState) {
        String text = "🖼️ Управление материалами для рамок\n\nВыберите действие:";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Добавить"
        List<InlineKeyboardButton> addRow = new ArrayList<>();
        InlineKeyboardButton addButton = new InlineKeyboardButton();
        addButton.setText("➕ Добавить");
        addButton.setCallbackData("frame_material_action_ADD");
        addRow.add(addButton);

        // Кнопка "Изменить"
        List<InlineKeyboardButton> editRow = new ArrayList<>();
        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("✏️ Изменить");
        editButton.setCallbackData("frame_material_action_EDIT");
        editRow.add(editButton);

        // Кнопка "Удалить"
        List<InlineKeyboardButton> deleteRow = new ArrayList<>();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("🗑️ Удалить");
        deleteButton.setCallbackData("frame_material_action_DELETE");
        deleteRow.add(deleteButton);

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад");
        backButton.setCallbackData("back_to_menu");
        backRow.add(backButton);

        rows.add(addRow);
        rows.add(editRow);
        rows.add(deleteRow);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Метод для обработки действий с материалами рамок
    private void handleFrameMaterialAction(Long chatId, String action, UserState userState) {
        String actionType = action.substring("frame_material_action_".length());

        try {
            switch (actionType) {
                case "ADD":
                    userState.frameMaterialAction = "ADD";
                    userState.selectedFrameMaterial = new FrameMaterial();
                    startAddFrameMaterial(chatId, userState);
                    break;
                case "EDIT":
                    userState.frameMaterialAction = "EDIT";
                    showFrameMaterialsForManagement(chatId, userState);
                    break;
                case "DELETE":
                    userState.frameMaterialAction = "DELETE";
                    showFrameMaterialsForManagement(chatId, userState);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling frame material action: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обработке действия. Попробуйте позже.");
        }
    }

    // Метод для начала добавления материала
    private void startAddFrameMaterial(Long chatId, UserState userState) {
        userState.waitingForField = "NAME";
        sendMessage(chatId, "➕ Добавление нового материала для рамки\n\nВведите название материала:");
    }

    // Метод для показа списка материалов для управления
    private void showFrameMaterialsForManagement(Long chatId, UserState userState) {
        try {
            Iterable<FrameMaterial> frameMaterials = mainController.allFrameMaterial();
            List<FrameMaterial> materialsList = new ArrayList<>();

            for (FrameMaterial material : frameMaterials) {
                materialsList.add(material);
            }

            if (materialsList.isEmpty()) {
                sendMessage(chatId, "❌ Нет материалов для управления.");
                showFrameMaterialsManagementMenu(chatId, userState);
                return;
            }

            userState.currentFrameMaterials = materialsList;
            userState.currentFrameMaterialIndex = 0;

            showCurrentFrameMaterialForManagement(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing frame materials for management: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке материалов.");
        }
    }

    // Метод для показа текущего материала при управлении
    private void showCurrentFrameMaterialForManagement(Long chatId, UserState userState) {
        if (userState.currentFrameMaterials == null || userState.currentFrameMaterials.isEmpty()) {
            sendMessage(chatId, "❌ Нет материалов для отображения.");
            return;
        }

        FrameMaterial currentMaterial = userState.currentFrameMaterials.get(userState.currentFrameMaterialIndex);
        String materialText = formatFrameMaterialDetails(currentMaterial,
                userState.currentFrameMaterialIndex + 1,
                userState.currentFrameMaterials.size());

        InlineKeyboardMarkup keyboard = createFrameMaterialManagementKeyboard(userState, currentMaterial);
        sendMessageWithInlineKeyboard(chatId, materialText, keyboard);
    }

    // Метод для форматирования деталей материала
    private String formatFrameMaterialDetails(FrameMaterial material, int currentNumber, int totalMaterials) {
        StringBuilder sb = new StringBuilder();
        sb.append("🖼️ Материал ").append(currentNumber).append(" из ").append(totalMaterials).append("\n\n");
        sb.append("📝 Название: ").append(material.getName() != null ? material.getName() : "Не указано").append("\n");
        sb.append("📋 Описание: ").append(material.getDescription() != null ? material.getDescription() : "Не указано").append("\n");
        sb.append("💰 Цена за метр: ").append(material.getPricePerMeter() != null ? material.getPricePerMeter() : "0").append(" руб.\n");
        sb.append("📦 Количество на складе: ").append(material.getStockQuantity() != null ? material.getStockQuantity() : "0").append(" м.\n");
        sb.append("🎨 Цвет: ").append(material.getColor() != null ? material.getColor() : "Не указан").append("\n");
        sb.append("📏 Ширина: ").append(material.getWidth() != null ? material.getWidth() : "0").append(" мм\n");

        return sb.toString();
    }

    // Метод для создания клавиатуры управления материалами
    private InlineKeyboardMarkup createFrameMaterialManagementKeyboard(UserState userState, FrameMaterial currentMaterial) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Строка с навигацией
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        // Кнопка "Предыдущий"
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅️ Предыдущий");
        prevButton.setCallbackData("prev_frame_material");
        if (userState.currentFrameMaterialIndex > 0) {
            navRow.add(prevButton);
        } else {
            InlineKeyboardButton disabledPrev = new InlineKeyboardButton();
            disabledPrev.setText("⏹️ Предыдущий");
            disabledPrev.setCallbackData("no_action_frame_material");
            navRow.add(disabledPrev);
        }

        // Кнопка "Выбрать этот" для EDIT и DELETE
        if ("EDIT".equals(userState.frameMaterialAction) || "DELETE".equals(userState.frameMaterialAction)) {
            InlineKeyboardButton selectButton = new InlineKeyboardButton();
            selectButton.setText("✅ Выбрать этот");
            selectButton.setCallbackData("select_frame_material");
            navRow.add(selectButton);
        }

        // Кнопка "Следующий"
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий ➡️");
        nextButton.setCallbackData("next_frame_material");
        if (userState.currentFrameMaterialIndex < userState.currentFrameMaterials.size() - 1) {
            navRow.add(nextButton);
        } else {
            InlineKeyboardButton disabledNext = new InlineKeyboardButton();
            disabledNext.setText("⏹️ Следующий");
            disabledNext.setCallbackData("no_action_frame_material");
            navRow.add(disabledNext);
        }

        rows.add(navRow);

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад");
        backButton.setCallbackData("frame_materials_management");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    // Метод для обработки навигации по материалам
    private void handleFrameMaterialNavigation(Long chatId, String action, UserState userState) {
        if (action.equals("no_action_frame_material")) {
            return;
        }

        if (action.equals("prev_frame_material")) {
            if (userState.currentFrameMaterialIndex > 0) {
                userState.currentFrameMaterialIndex--;
                editCurrentFrameMaterialForManagement(chatId, userState);
            }
        } else if (action.equals("next_frame_material")) {
            if (userState.currentFrameMaterialIndex < userState.currentFrameMaterials.size() - 1) {
                userState.currentFrameMaterialIndex++;
                editCurrentFrameMaterialForManagement(chatId, userState);
            }
        } else if (action.equals("select_frame_material")) {
            userState.selectedFrameMaterial = userState.currentFrameMaterials.get(userState.currentFrameMaterialIndex);

            if ("EDIT".equals(userState.frameMaterialAction)) {
                showEditFrameMaterialMenu(chatId, userState);
            } else if ("DELETE".equals(userState.frameMaterialAction)) {
                showDeleteConfirmation(chatId, userState);
            }
        }
    }

    private void editCurrentFrameMaterialForManagement(Long chatId, UserState userState) {
        if (userState.currentFrameMaterials == null || userState.currentFrameMaterials.isEmpty()) {
            return;
        }

        FrameMaterial currentMaterial = userState.currentFrameMaterials.get(userState.currentFrameMaterialIndex);
        String materialText = formatFrameMaterialDetails(currentMaterial,
                userState.currentFrameMaterialIndex + 1,
                userState.currentFrameMaterials.size());

        InlineKeyboardMarkup keyboard = createFrameMaterialManagementKeyboard(userState, currentMaterial);

        if (userState.lastMessageId != null) {
            editMessageWithInlineKeyboard(chatId, userState.lastMessageId, materialText, keyboard);
        } else {
            sendMessageWithInlineKeyboard(chatId, materialText, keyboard);
        }
    }

    // Метод для показа меню редактирования материала
    private void showEditFrameMaterialMenu(Long chatId, UserState userState) {
        if (userState.selectedFrameMaterial == null) {
            sendMessage(chatId, "❌ Ошибка: материал не выбран.");
            showFrameMaterialsManagementMenu(chatId, userState);
            return;
        }

        String materialName = userState.selectedFrameMaterial.getName() != null ?
                userState.selectedFrameMaterial.getName() : "Без названия";
        String text = "✏️ Редактирование материала: " + userState.selectedFrameMaterial.getName() + "\n\nЧто вы хотите изменить?";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопки для выбора поля для изменения
        String[] fields = {"NAME", "DESCRIPTION", "PRICE_PER_METER", "STOCK_QUANTITY", "COLOR", "WIDTH"};
        String[] fieldNames = {"📝 Название", "📋 Описание", "💰 Цена за метр", "📦 Количество", "🎨 Цвет", "📏 Ширина"};

        for (int i = 0; i < fields.length; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton fieldButton = new InlineKeyboardButton();
            fieldButton.setText(fieldNames[i]);
            fieldButton.setCallbackData("frame_material_field_" + fields[i]);
            row.add(fieldButton);
            rows.add(row);
        }

        // Кнопка "Назад"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Назад к списку");
        backButton.setCallbackData("frame_material_action_EDIT");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Метод для обработки выбора поля для изменения
    private void handleFrameMaterialFieldSelection(Long chatId, String data, UserState userState) {
        String field = data.substring("frame_material_field_".length());
        userState.waitingForField = field;

        String prompt = "";
        switch (field) {
            case "NAME":
                prompt = "Введите новое название материала:";
                break;
            case "DESCRIPTION":
                prompt = "Введите новое описание материала:";
                break;
            case "PRICE_PER_METER":
                prompt = "Введите новую цену за метр (число):";
                break;
            case "STOCK_QUANTITY":
                prompt = "Введите новое количество на складе (число, в метрах):";
                break;
            case "COLOR":
                prompt = "Введите новый цвет материала:";
                break;
            case "WIDTH":
                prompt = "Введите новую ширину материала (число, в мм):";
                break;
        }

        sendMessage(chatId, prompt);
    }

    // Метод для показа подтверждения удаления
    private void showDeleteConfirmation(Long chatId, UserState userState) {
        String text = "🗑️ Вы уверены, что хотите удалить материал:\n\"" + userState.selectedFrameMaterial.getName() + "\"?\n\nЭта операция необратима!";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка подтверждения удаления
        List<InlineKeyboardButton> confirmRow = new ArrayList<>();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("✅ Да, удалить");
        confirmButton.setCallbackData("confirm_delete_frame_material");
        confirmRow.add(confirmButton);

        // Кнопка отмены
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отмена");
        cancelButton.setCallbackData("frame_material_action_DELETE");
        cancelRow.add(cancelButton);

        rows.add(confirmRow);
        rows.add(cancelRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Новый метод для показа свободных заказов с навигацией
    private void showFreeOrdersWithNavigation(Long chatId, UserState userState) {
        try {
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();
            List<Orders> freeOrders = new ArrayList<>();

            // Ищем заказы без ProductionMasterID
            for (Orders order : allOrders) {
                if (order.getProductionMasterID() == null || order.getProductionMasterID().getIdUser() == null) {
                    freeOrders.add(order);
                }
            }

            if (freeOrders.isEmpty()) {
                sendMessage(chatId, "📋 На данный момент свободных заказов нет.");
                return;
            }

            userState.freeOrders = freeOrders;
            userState.currentFreeOrderIndex = 0;
            userState.viewingFreeOrders = true;
            showCurrentFreeOrder(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error in showFreeOrdersWithNavigation: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при получении свободных заказов. Попробуйте позже.");
        }
    }

    // Новый метод для показа текущего свободного заказа
    private void showCurrentFreeOrder(Long chatId, UserState userState) {
        if (userState.freeOrders == null || userState.freeOrders.isEmpty()) {
            sendMessage(chatId, "❌ Нет свободных заказов для отображения.");
            return;
        }

        Orders currentOrder = userState.freeOrders.get(userState.currentFreeOrderIndex);
        String orderText = formatFreeOrderDetails(currentOrder, userState.currentFreeOrderIndex + 1, userState.freeOrders.size());
        InlineKeyboardMarkup keyboard = createFreeOrderNavigationKeyboard(userState, currentOrder);
        sendMessageWithInlineKeyboard(chatId, orderText, keyboard);
    }

    // Новый метод для форматирования деталей свободного заказа
    private String formatFreeOrderDetails(Orders order, int currentNumber, int totalOrders) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("🆓 Свободный заказ ").append(currentNumber).append(" из ").append(totalOrders).append("\n\n");
        sb.append("🆔 Номер заказа: ").append(order.getId()).append("\n");
        sb.append("📅 Дата заказа: ").append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "Не указана").append("\n");

        // Получаем информацию о покупателе
        if (order.getCustomerID() != null) {
            Customer customer = order.getCustomerID();
            String customerName = (customer.getLastName() != null ? customer.getLastName() : "") + " " +
                    (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
                    (customer.getMiddleName() != null ? customer.getMiddleName() : "");
            customerName = customerName.trim();
            sb.append("👤 Покупатель: ").append(customerName).append("\n");
        }

        sb.append("💰 Сумма: ").append(order.getTotalAmount() != null ? order.getTotalAmount() : "Не указана").append(" руб.\n");
        sb.append("📊 Статус: ").append(order.getStatus() != null ? order.getStatus() : "Не указан").append("\n");
        sb.append("⏰ Срок выполнения: ").append(order.getDueDate() != null ? dateFormat.format(order.getDueDate()) : "Не указан").append("\n");

        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            sb.append("📝 Примечания: ").append(order.getNotes()).append("\n");
        }

        // Проверяем, есть ли custom_frame_order для этого заказа
        try {
            Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
            for (CustomFrameOrder customOrder : customFrameOrders) {
                if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == order.getId().longValue()) {
                    sb.append("\n🖼️ Информация о рамке:\n");
                    sb.append("• Ширина: ").append(customOrder.getWidth()).append(" мм\n");
                    sb.append("• Высота: ").append(customOrder.getHeight()).append(" мм\n");
                    if (customOrder.getColor() != null) {
                        sb.append("• Цвет: ").append(customOrder.getColor()).append("\n");
                    }
                    if (customOrder.getStyle() != null) {
                        sb.append("• Стиль: ").append(customOrder.getStyle()).append("\n");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking custom frame orders: " + e.getMessage());
        }

        return sb.toString();
    }

    // Новый метод для создания клавиатуры навигации по свободным заказам
    private InlineKeyboardMarkup createFreeOrderNavigationKeyboard(UserState userState, Orders currentOrder) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Строка с навигацией
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        // Кнопка "Предыдущий"
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅️ Предыдущий");
        prevButton.setCallbackData("prev_free_order");
        if (userState.currentFreeOrderIndex > 0) {
            navRow.add(prevButton);
        } else {
            InlineKeyboardButton disabledPrev = new InlineKeyboardButton();
            disabledPrev.setText("⏹️ Предыдущий");
            disabledPrev.setCallbackData("no_action_free_order");
            navRow.add(disabledPrev);
        }

        // Кнопка "Взять заказ"
        InlineKeyboardButton takeOrderButton = new InlineKeyboardButton();
        takeOrderButton.setText("✅ Взять заказ");
        takeOrderButton.setCallbackData("take_free_order_" + currentOrder.getId());
        navRow.add(takeOrderButton);

        // Кнопка "Следующий"
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий ➡️");
        nextButton.setCallbackData("next_free_order");
        if (userState.currentFreeOrderIndex < userState.freeOrders.size() - 1) {
            navRow.add(nextButton);
        } else {
            InlineKeyboardButton disabledNext = new InlineKeyboardButton();
            disabledNext.setText("⏹️ Следующий");
            disabledNext.setCallbackData("no_action_free_order");
            navRow.add(disabledNext);
        }

        rows.add(navRow);

        // Строка с дополнительными действиями
        List<InlineKeyboardButton> actionRow = new ArrayList<>();

        // Кнопка возврата в меню
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 В меню");
        backButton.setCallbackData("back_to_menu");
        actionRow.add(backButton);

        rows.add(actionRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    // Новый метод для взятия свободного заказа
    private void takeFreeOrder(Long chatId, Long orderId, UserState userState) {
        try {
            // Находим заказ
            Orders orderToTake = null;
            for (Orders order : userState.freeOrders) {
                if (order.getId().longValue() == orderId.longValue()) {
                    orderToTake = order;
                    break;
                }
            }

            if (orderToTake == null) {
                sendMessage(chatId, "❌ Заказ не найден.");
                return;
            }

            Productionmaster productionMaster = mainController.findProductionMasterByUserId(userState.userId);
            if (productionMaster == null) {
                sendMessage(chatId, "❌ Ошибка: не найден мастер производства для вашего пользователя.");
                return;
            }

            userState.selectedOrderId = orderId;

            // Сохраняем найденного мастера производства
            userState.currentFrameOrder.setProductionMasterID(productionMaster);

            // Переходим в состояние ожидания ввода примерного количества материала
            userState.state = "WAITING_MATERIAL_ESTIMATE";
            sendMessage(chatId, "📏 Введите примерное количество материала для заказа №" + orderId + " (в метрах):\n\n" +
                    "Введите число, например: 2.5");

        } catch (Exception e) {
            System.err.println("Error in takeFreeOrder: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при взятии заказа. Попробуйте позже.");
        }
    }

    private void handleMaterialEstimateInput(Long chatId, String messageText, UserState userState) {
        try {
            double materialEstimate;
            try {
                materialEstimate = Double.parseDouble(messageText.trim());
                if (materialEstimate <= 0) {
                    sendMessage(chatId, "❌ Количество материала должно быть положительным числом. Попробуйте снова:");
                    return;
                }
            } catch (NumberFormatException e) {
                sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества материала:");
                return;
            }

            userState.currentMaterialEstimate = materialEstimate;

            // Теперь запрашиваем стоимость заказа
            userState.state = "WAITING_ORDER_COST";
            sendMessage(chatId, "💵 Теперь введите стоимость заказа №" + userState.selectedOrderId + " (в рублях):\n\n" +
                    "Введите число, например: 1500");

        } catch (Exception e) {
            System.err.println("Error handling material estimate input: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обработке данных. Попробуйте позже.");
            userState.state = "AUTHENTICATED";
            sendMainMenu(chatId, userState);
        }
    }

    private void updateCustomFrameOrderWithEstimate(Long orderId, Productionmaster productionMaster, Double materialEstimate) {
        try {
            Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
            for (CustomFrameOrder customOrder : customFrameOrders) {
                if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == orderId.longValue()) {
                    // Устанавливаем мастера производства и примерное количество материала
                    customOrder.setProductionMasterID(productionMaster);
                    if (materialEstimate != null) {
                        customOrder.setEstimatedMaterialUsage(BigDecimal.valueOf(materialEstimate));
                    }
                    mainController.updateCustomFrameOrder(customOrder);
                    System.out.println("Updated custom frame order with ID: " + customOrder.getId() +
                            ", estimated material: " + materialEstimate + " m");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating custom frame order with estimate: " + e.getMessage());
        }
    }

    private void handleOrderCostInput(Long chatId, String messageText, UserState userState) {
        try {
            int cost;
            try {
                cost = Integer.parseInt(messageText.trim());
                if (cost <= 0) {
                    sendMessage(chatId, "❌ Стоимость должна быть положительным числом. Попробуйте снова:");
                    return;
                }
            } catch (NumberFormatException e) {
                sendMessage(chatId, "❌ Пожалуйста, введите корректное число для стоимости:");
                return;
            }

            Orders orderToUpdate = null;
            for (Orders order : userState.freeOrders) {
                if (order.getId().longValue() == userState.selectedOrderId.longValue()) {
                    orderToUpdate = order;
                    break;
                }
            }

            if (orderToUpdate == null) {
                sendMessage(chatId, "❌ Заказ не найден.");
                userState.state = "AUTHENTICATED";
                sendMainMenu(chatId, userState);
                return;
            }

            Productionmaster productionMaster = mainController.findProductionMasterByUserId(userState.userId);
            if (productionMaster == null) {
                sendMessage(chatId, "❌ Ошибка: не найден мастер производства для вашего пользователя.");
                userState.state = "AUTHENTICATED";
                sendMainMenu(chatId, userState);
                return;
            }

            // Обновляем заказ
            orderToUpdate.setProductionMasterID(productionMaster);
            orderToUpdate.setTotalAmount(cost);

            // Сохраняем изменения в заказе
            mainController.updateOrder(orderToUpdate);

            // Обновляем custom_frame_order - добавляем примерное количество материала
            updateCustomFrameOrderWithEstimate(userState.selectedOrderId, productionMaster, userState.currentMaterialEstimate);

            sendMessage(chatId, "✅ Заказ №" + userState.selectedOrderId + " успешно взят!\n" +
                    "💰 Установленная стоимость: " + cost + " руб.\n" +
                    "📏 Примерное количество материала: " + userState.currentMaterialEstimate + " м.\n\n" +
                    "Теперь этот заказ отображается в ваших заказах.");

            // Сбрасываем состояние
            userState.state = "AUTHENTICATED";
            userState.selectedOrderId = null;
            userState.viewingFreeOrders = false;
            userState.freeOrders.clear();
            userState.currentFreeOrderIndex = 0;
            userState.currentMaterialEstimate = null;

            sendMainMenu(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error handling order cost input: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при обновлении заказа. Попробуйте позже.");
            userState.state = "AUTHENTICATED";
            sendMainMenu(chatId, userState);
        }
    }

    private void updateCustomFrameOrderProductionMaster(Long orderId, Productionmaster productionMaster) {
        try {
            Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
            for (CustomFrameOrder customOrder : customFrameOrders) {
                if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == orderId.longValue()) {
                    customOrder.setProductionMasterID(productionMaster);
                    mainController.updateCustomFrameOrder(customOrder);
                    System.out.println("Updated custom frame order with ID: " + customOrder.getId());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating custom frame order: " + e.getMessage());
        }
    }

    private void startFrameOrder(Long chatId, UserState userState) {
        // Проверяем, что пользователь авторизован как покупатель
        if (!"AUTHENTICATED".equals(userState.state) || !"ПОКУПАТЕЛЬ".equals(userState.userRole)) {
            sendMessage(chatId, "❌ Для заказа рамки необходимо авторизоваться как покупатель.");
            sendMainMenu(chatId, userState);
            return;
        }

        userState.currentFrameOrder = new CustomFrameOrder();
        userState.state = "FRAME_ORDER_MATERIAL";
        userState.currentFrameOrderStep = "MATERIAL";

        // Показываем доступные материалы для выбора
        showFrameMaterialsForSelection(chatId);
    }

    private void showFrameMaterialsForSelection(Long chatId) {
        try {
            Iterable<FrameMaterial> frameMaterials = mainController.allFrameMaterial();
            List<FrameMaterial> availableMaterials = new ArrayList<>();

            for (FrameMaterial material : frameMaterials) {
                if (material.getStockQuantity() != null && material.getStockQuantity() > 0) {
                    availableMaterials.add(material);
                }
            }

            if (availableMaterials.isEmpty()) {
                sendMessage(chatId, "❌ К сожалению, материалы для каркасов временно отсутствуют.");
                sendMainMenu(chatId, getUserState(chatId));
                return;
            }

            String text = "🖼️ Заказ рамки по предпочтениям\n\n" +
                    "Шаг 1: Выберите материал для рамки:\n\n";

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (FrameMaterial material : availableMaterials) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton materialButton = new InlineKeyboardButton();
                String buttonText = material.getName() != null ? material.getName() : "Материал " + material.getId();
                if (material.getColor() != null) {
                    buttonText += " (" + material.getColor() + ")";
                }
                materialButton.setText(buttonText);
                materialButton.setCallbackData("select_material_" + material.getId());
                row.add(materialButton);
                rows.add(row);
            }

            // Кнопка отмены
            List<InlineKeyboardButton> cancelRow = new ArrayList<>();
            InlineKeyboardButton cancelButton = new InlineKeyboardButton();
            cancelButton.setText("❌ Отменить заказ");
            cancelButton.setCallbackData("cancel_frame_order");
            cancelRow.add(cancelButton);
            rows.add(cancelRow);

            keyboard.setKeyboard(rows);
            sendMessageWithInlineKeyboard(chatId, text, keyboard);

        } catch (Exception e) {
            System.err.println("Error showing frame materials for selection: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке материалов. Попробуйте позже.");
            sendMainMenu(chatId, getUserState(chatId));
        }
    }

    private void handleFrameOrderStep(Long chatId, UserState userState, String nextStep) {
        userState.currentFrameOrderStep = nextStep;

        switch (nextStep) {
            case "WIDTH":
                sendMessage(chatId, "📏 Шаг 2: Укажите ширину рамки (в мм):\n\n" +
                        "Введите число, например: 300");
                userState.state = "FRAME_ORDER_WIDTH";
                break;
            case "HEIGHT":
                sendMessage(chatId, "📐 Шаг 3: Укажите высоту рамки (в мм):\n\n" +
                        "Введите число, например: 400");
                userState.state = "FRAME_ORDER_HEIGHT";
                break;
            case "COLOR":
                sendMessage(chatId, "🎨 Шаг 4: Укажите цвет рамки:\n\n" +
                        "Опишите желаемый цвет, например: \"натуральное дерево\", \"черный матовый\", \"золото\"");
                userState.state = "FRAME_ORDER_COLOR";
                break;
            case "STYLE":
                sendMessage(chatId, "🎭 Шаг 5: Укажите стиль рамки:\n\n" +
                        "Опишите стиль, например: \"классический\", \"модерн\", \"винтаж\", \"минимализм\"");
                userState.state = "FRAME_ORDER_STYLE";
                break;
            case "MOUNT_TYPE":
                sendMessage(chatId, "📋 Шаг 6: Выберите тип крепления:\n\n" +
                        "Введите тип крепления, например: \"подвесное\", \"настольное\", \"напольное\"");
                userState.state = "FRAME_ORDER_MOUNT_TYPE";
                break;
            case "GLASS_TYPE":
                sendMessage(chatId, "🔍 Шаг 7: Укажите тип стекла:\n\n" +
                        "Введите тип стекла, например: \"стандартное\", \"антибликовое\", \"без стекла\", \"оргстекло\"");
                userState.state = "FRAME_ORDER_GLASS_TYPE";
                break;
            case "NOTES":
                sendMessage(chatId, "📝 Шаг 8: Дополнительные примечания:\n\n" +
                        "Если есть особые пожелания, опишите их. Или напишите \"нет\", если примечаний нет.");
                userState.state = "FRAME_ORDER_NOTES";
                break;
            case "CONFIRM":
                userState.state = "FRAME_ORDER_CONFIRM";
                showFrameOrderConfirmation(chatId, userState);
                break;
        }
    }

    private void handleFrameOrderInput(Long chatId, String messageText, UserState userState) {
        // Проверяем, что пользователь все еще авторизован
        if (!"AUTHENTICATED".equals(userState.state) && !userState.state.startsWith("FRAME_ORDER_")) {
            sendMessage(chatId, "❌ Сессия истекла. Пожалуйста, начните заказ заново.");
            sendMainMenu(chatId, userState);
            return;
        }

        try {
            switch (userState.state) {
                case "FRAME_ORDER_WIDTH":
                    try {
                        int width = Integer.parseInt(messageText.trim());
                        if (width <= 0) {
                            sendMessage(chatId, "❌ Ширина должна быть положительным числом. Попробуйте снова:");
                            return;
                        }
                        userState.currentFrameOrder.setWidth(width);
                        handleFrameOrderStep(chatId, userState, "HEIGHT");
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "❌ Пожалуйста, введите корректное число для ширины:");
                    }
                    break;

                case "FRAME_ORDER_HEIGHT":
                    try {
                        int height = Integer.parseInt(messageText.trim());
                        if (height <= 0) {
                            sendMessage(chatId, "❌ Высота должна быть положительным числом. Попробуйте снова:");
                            return;
                        }
                        userState.currentFrameOrder.setHeight(height);
                        handleFrameOrderStep(chatId, userState, "COLOR");
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "❌ Пожалуйста, введите корректное число для высоты:");
                    }
                    break;

                case "FRAME_ORDER_COLOR":
                    userState.currentFrameOrder.setColor(messageText.trim());
                    handleFrameOrderStep(chatId, userState, "STYLE");
                    break;

                case "FRAME_ORDER_STYLE":
                    userState.currentFrameOrder.setStyle(messageText.trim());
                    handleFrameOrderStep(chatId, userState, "MOUNT_TYPE");
                    break;

                case "FRAME_ORDER_MOUNT_TYPE":
                    userState.currentFrameOrder.setMountType(messageText.trim());
                    handleFrameOrderStep(chatId, userState, "GLASS_TYPE");
                    break;

                case "FRAME_ORDER_GLASS_TYPE":
                    userState.currentFrameOrder.setGlassType(messageText.trim());
                    handleFrameOrderStep(chatId, userState, "NOTES");
                    break;

                case "FRAME_ORDER_NOTES":
                    if (!messageText.trim().equalsIgnoreCase("нет")) {
                        userState.currentFrameOrder.setNotes(messageText.trim());
                    }
                    handleFrameOrderStep(chatId, userState, "CONFIRM");
                    break;

                default:
                    sendMessage(chatId, "❌ Неизвестное состояние заказа. Начните заново.");
                    sendMainMenu(chatId, userState);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling frame order input: " + e.getMessage());
            sendMessage(chatId, "❌ Произошла ошибка. Попробуйте начать заказ заново.");

            // Сбрасываем состояние заказа
            userState.currentFrameOrder = new CustomFrameOrder();
            userState.currentFrameOrderStep = "";
            userState.state = "AUTHENTICATED";

            sendMainMenu(chatId, userState);
        }
    }

    private void showFrameOrderConfirmation(Long chatId, UserState userState) {
        try {
            // Получаем информацию о выбранном материале
            FrameMaterial selectedMaterial = null;
            Iterable<FrameMaterial> frameMaterials = mainController.allFrameMaterial();
            for (FrameMaterial material : frameMaterials) {
                if (material.getId().equals(userState.currentFrameOrder.getFrameMaterialID().getId())) {
                    selectedMaterial = material;
                    break;
                }
            }

            StringBuilder confirmationText = new StringBuilder();
            confirmationText.append("✅ Подтверждение заказа рамки\n\n");
            confirmationText.append("📋 Детали заказа:\n");

            if (selectedMaterial != null) {
                confirmationText.append("• Материал: ").append(selectedMaterial.getName()).append("\n");
                if (selectedMaterial.getColor() != null) {
                    confirmationText.append("• Цвет материала: ").append(selectedMaterial.getColor()).append("\n");
                }
            }

            confirmationText.append("• Ширина: ").append(userState.currentFrameOrder.getWidth()).append(" мм\n");
            confirmationText.append("• Высота: ").append(userState.currentFrameOrder.getHeight()).append(" мм\n");

            if (userState.currentFrameOrder.getColor() != null) {
                confirmationText.append("• Цвет рамки: ").append(userState.currentFrameOrder.getColor()).append("\n");
            }
            if (userState.currentFrameOrder.getStyle() != null) {
                confirmationText.append("• Стиль: ").append(userState.currentFrameOrder.getStyle()).append("\n");
            }
            if (userState.currentFrameOrder.getMountType() != null) {
                confirmationText.append("• Тип крепления: ").append(userState.currentFrameOrder.getMountType()).append("\n");
            }
            if (userState.currentFrameOrder.getGlassType() != null) {
                confirmationText.append("• Тип стекла: ").append(userState.currentFrameOrder.getGlassType()).append("\n");
            }
            if (userState.currentFrameOrder.getNotes() != null && !userState.currentFrameOrder.getNotes().isEmpty()) {
                confirmationText.append("• Примечания: ").append(userState.currentFrameOrder.getNotes()).append("\n");
            }

            confirmationText.append("\nДля подтверждения заказа нажмите кнопку ниже.");

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> confirmRow = new ArrayList<>();
            InlineKeyboardButton confirmButton = new InlineKeyboardButton();
            confirmButton.setText("✅ Подтвердить заказ");
            confirmButton.setCallbackData("confirm_frame_order");
            confirmRow.add(confirmButton);

            List<InlineKeyboardButton> cancelRow = new ArrayList<>();
            InlineKeyboardButton cancelButton = new InlineKeyboardButton();
            cancelButton.setText("❌ Отменить");
            cancelButton.setCallbackData("cancel_frame_order");
            cancelRow.add(cancelButton);

            rows.add(confirmRow);
            rows.add(cancelRow);

            keyboard.setKeyboard(rows);
            sendMessageWithInlineKeyboard(chatId, confirmationText.toString(), keyboard);

        } catch (Exception e) {
            System.err.println("Error showing frame order confirmation: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при формировании заказа. Попробуйте позже.");
            sendMainMenu(chatId, userState);
        }
    }

    private void confirmFrameOrder(Long chatId, UserState userState) {
        try {
            if (userState == null || !"ПОКУПАТЕЛЬ".equals(userState.userRole)) {
                sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для покупателей.");
                sendMainMenu(chatId, userState);
                return;
            }

            Orders newOrder = new Orders();

            Customer customer = findCustomerById(userState.userId);
            if (customer == null) {
                sendMessage(chatId, "❌ Ошибка: не найден покупатель.");
                sendMainMenu(chatId, userState);
                return;
            }
            newOrder.setCustomerID(customer);

            User defaultSeller = new User();
            defaultSeller.setId(1);
            newOrder.setSellerID(defaultSeller);

            newOrder.setOrderDate(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            newOrder.setDueDate(calendar.getTime());

            newOrder.setStatus("Новый");

            if (userState.currentFrameOrder.getNotes() != null) {
                newOrder.setNotes("Заказ рамки: " + userState.currentFrameOrder.getNotes());
            } else {
                newOrder.setNotes("Заказ рамки по индивидуальным предпочтениям");
            }

            Orders savedOrder = mainController.createOrder(newOrder);

            userState.currentFrameOrder.setOrderID(savedOrder);

            mainController.createCustomFrameOrder(userState.currentFrameOrder);

            // ОТПРАВЛЯЕМ УВЕДОМЛЕНИЯ МАСТЕРАМ
            notifyMastersAboutNewOrder(savedOrder);

            sendMessage(chatId, "🎉 Заказ рамки успешно создан!\n\n" +
                    "✅ Ваш заказ принят в обработку.\n" +
                    "📅 Ориентировочная дата готовности: " +
                    new SimpleDateFormat("dd.MM.yyyy").format(newOrder.getDueDate()) + "\n\n" +
                    "Спасибо за заказ! Вы можете отслеживать статус в разделе \"Мои заказы\".");

            // Сбрасываем состояние заказа и возвращаем в главное меню
            userState.currentFrameOrder = new CustomFrameOrder();
            userState.currentFrameOrderStep = "";
            userState.state = "AUTHENTICATED";

            sendMainMenu(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error confirming frame order: " + e.getMessage());
            e.printStackTrace();
            sendMessage(chatId, "❌ Ошибка при создании заказа. Попробуйте позже.");

            // В случае ошибки также сбрасываем состояние
            userState.currentFrameOrder = new CustomFrameOrder();
            userState.currentFrameOrderStep = "";
            userState.state = "AUTHENTICATED";

            sendMainMenu(chatId, userState);
        }
    }

    private void cancelFrameOrder(Long chatId, UserState userState) {
        userState.currentFrameOrder = new CustomFrameOrder();
        userState.state = "AUTHENTICATED";
        userState.currentFrameOrderStep = "";
        sendMessage(chatId, "❌ Заказ рамки отменен.");
        sendMainMenu(chatId, userState);
    }

    private Customer findCustomerById(Long customerId) {
        try {
            Iterable<Customer> customers = mainController.allCustomers();
            for (Customer customer : customers) {
                if (customer.getId().longValue() == customerId) {
                    return customer;
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding customer by ID: " + e.getMessage());
        }
        return null;
    }

    private void notifyMastersAboutNewOrder(Orders newOrder) {
        try {
            // Получаем всех мастеров производства
            Iterable<Productionmaster> productionMasters = mainController.allPM2();

            for (Productionmaster master : productionMasters) {
                if (master.getIdUser() != null && master.getIdUser().getId() != null) {
                    Long masterUserId = master.getIdUser().getId().longValue();

                    // Ищем chatId мастера по его userId
                    Long masterChatId = findChatIdByUserId(masterUserId);

                    if (masterChatId != null) {
                        sendOrderNotification(masterChatId, newOrder);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error notifying masters about new order: " + e.getMessage());
        }
    }

    private Long findChatIdByUserId(Long userId) {
        // Ищем chatId по userId в userStates
        for (Map.Entry<Long, UserState> entry : userStates.entrySet()) {
            if (entry.getValue().userId != null && entry.getValue().userId.longValue() == userId.longValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void sendOrderNotification(Long chatId, Orders order) {
        try {
            String notificationText = "🔔 *НОВЫЙ ЗАКАЗ!*\n\n" +
                    "🆔 Номер заказа: " + order.getId() + "\n" +
                    "📅 Дата заказа: " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(order.getOrderDate()) + "\n" +
                    "💰 Сумма: " + (order.getTotalAmount() != null ? order.getTotalAmount() : "Не указана") + " руб.\n" +
                    "📊 Статус: " + (order.getStatus() != null ? order.getStatus() : "Новый") + "\n";

            // Добавляем информацию о покупателе
            if (order.getCustomerID() != null) {
                Customer customer = order.getCustomerID();
                String customerName = (customer.getLastName() != null ? customer.getLastName() : "") + " " +
                        (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
                        (customer.getMiddleName() != null ? customer.getMiddleName() : "");
                customerName = customerName.trim();
                if (!customerName.isEmpty()) {
                    notificationText += "👤 Покупатель: " + customerName + "\n";
                }
            }

            // Проверяем, есть ли custom_frame_order для этого заказа
            try {
                Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
                for (CustomFrameOrder customOrder : customFrameOrders) {
                    if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == order.getId().longValue()) {
                        notificationText += "\n🖼️ *Информация о рамке:*\n";
                        notificationText += "• Ширина: " + customOrder.getWidth() + " мм\n";
                        notificationText += "• Высота: " + customOrder.getHeight() + " мм\n";
                        if (customOrder.getColor() != null) {
                            notificationText += "• Цвет: " + customOrder.getColor() + "\n";
                        }
                        if (customOrder.getStyle() != null) {
                            notificationText += "• Стиль: " + customOrder.getStyle() + "\n";
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting custom frame order details: " + e.getMessage());
            }

            // Добавляем кнопки для быстрых действий
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            // Кнопка "Посмотреть заказ"
            List<InlineKeyboardButton> viewRow = new ArrayList<>();
            InlineKeyboardButton viewButton = new InlineKeyboardButton();
            viewButton.setText("📋 Посмотреть заказ");
            viewButton.setCallbackData("view_order_" + order.getId());
            viewRow.add(viewButton);

            // Кнопка "Взять заказ" (если заказ свободный)
            List<InlineKeyboardButton> takeRow = new ArrayList<>();
            InlineKeyboardButton takeButton = new InlineKeyboardButton();
            takeButton.setText("✅ Взять заказ");
            takeButton.setCallbackData("take_free_order_" + order.getId());
            takeRow.add(takeButton);

            rows.add(viewRow);
            rows.add(takeRow);

            keyboard.setKeyboard(rows);

            sendMessageWithInlineKeyboard(chatId, notificationText, keyboard);

        } catch (Exception e) {
            System.err.println("Error sending order notification: " + e.getMessage());
        }
    }

    private void showOrderDetails(Long chatId, Long orderId, UserState userState) {
        try {
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();
            Orders targetOrder = null;

            for (Orders order : allOrders) {
                if (order.getId().longValue() == orderId.longValue()) {
                    targetOrder = order;
                    break;
                }
            }

            if (targetOrder == null) {
                sendMessage(chatId, "❌ Заказ не найден.");
                return;
            }

            String orderDetails = formatOrderDetailsForMaster(targetOrder);
            InlineKeyboardMarkup keyboard = createOrderDetailsKeyboard(targetOrder);

            sendMessageWithInlineKeyboard(chatId, orderDetails, keyboard);

        } catch (Exception e) {
            System.err.println("Error showing order details: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке деталей заказа.");
        }
    }

    private void checkAndNotifyAboutFreeOrders(Long chatId) {
        try {
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();
            List<Orders> freeOrders = new ArrayList<>();

            // Ищем свободные заказы
            for (Orders order : allOrders) {
                if (order.getProductionMasterID() == null || order.getProductionMasterID().getIdUser() == null) {
                    freeOrders.add(order);
                }
            }

            if (!freeOrders.isEmpty()) {
                String notification = "📋 У вас " + freeOrders.size() + " свободных заказов!\n" +
                        "Используйте раздел \"🆓 Свободные заказы\" для просмотра.";
                sendMessage(chatId, notification);
            }

        } catch (Exception e) {
            System.err.println("Error checking free orders: " + e.getMessage());
        }
    }

    private String formatOrderDetailsForMaster(Orders order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("📋 *Детали заказа №").append(order.getId()).append("*\n\n");
        sb.append("📅 Дата заказа: ").append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "Не указана").append("\n");
        sb.append("💰 Сумма: ").append(order.getTotalAmount() != null ? order.getTotalAmount() : "Не указана").append(" руб.\n");
        sb.append("📊 Статус: ").append(order.getStatus() != null ? order.getStatus() : "Не указан").append("\n");
        sb.append("⏰ Срок выполнения: ").append(order.getDueDate() != null ? dateFormat.format(order.getDueDate()) : "Не указан").append("\n");

        // Информация о покупателе
        if (order.getCustomerID() != null) {
            Customer customer = order.getCustomerID();
            String customerName = (customer.getLastName() != null ? customer.getLastName() : "") + " " +
                    (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
                    (customer.getMiddleName() != null ? customer.getMiddleName() : "");
            customerName = customerName.trim();
            if (!customerName.isEmpty()) {
                sb.append("👤 Покупатель: ").append(customerName).append("\n");
            }
            if (customer.getPhone() != null) {
                sb.append("📞 Телефон: ").append(customer.getPhone()).append("\n");
            }
            if (customer.getEmail() != null) {
                sb.append("📧 Email: ").append(customer.getEmail()).append("\n");
            }
        }

        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            sb.append("📝 Примечания: ").append(order.getNotes()).append("\n");
        }

        // Информация о рамке
        try {
            Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
            for (CustomFrameOrder customOrder : customFrameOrders) {
                if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == order.getId().longValue()) {
                    sb.append("\n🖼️ *Детали рамки:*\n");
                    sb.append("• Ширина: ").append(customOrder.getWidth()).append(" мм\n");
                    sb.append("• Высота: ").append(customOrder.getHeight()).append(" мм\n");
                    if (customOrder.getColor() != null) {
                        sb.append("• Цвет: ").append(customOrder.getColor()).append("\n");
                    }
                    if (customOrder.getStyle() != null) {
                        sb.append("• Стиль: ").append(customOrder.getStyle()).append("\n");
                    }
                    if (customOrder.getMountType() != null) {
                        sb.append("• Тип крепления: ").append(customOrder.getMountType()).append("\n");
                    }
                    if (customOrder.getGlassType() != null) {
                        sb.append("• Тип стекла: ").append(customOrder.getGlassType()).append("\n");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting custom frame details: " + e.getMessage());
        }

        return sb.toString();
    }

    private InlineKeyboardMarkup createOrderDetailsKeyboard(Orders order) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Если заказ свободный, добавляем кнопку "Взять заказ"
        if (order.getProductionMasterID() == null) {
            List<InlineKeyboardButton> takeRow = new ArrayList<>();
            InlineKeyboardButton takeButton = new InlineKeyboardButton();
            takeButton.setText("✅ Взять заказ");
            takeButton.setCallbackData("take_free_order_" + order.getId());
            takeRow.add(takeButton);
            rows.add(takeRow);
        }

        // Кнопка "Свободные заказы"
        List<InlineKeyboardButton> freeOrdersRow = new ArrayList<>();
        InlineKeyboardButton freeOrdersButton = new InlineKeyboardButton();
        freeOrdersButton.setText("🆓 Свободные заказы");
        freeOrdersButton.setCallbackData("free_orders");
        freeOrdersRow.add(freeOrdersButton);
        rows.add(freeOrdersRow);

        // Кнопка "Назад в меню"
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 В меню");
        backButton.setCallbackData("back_to_menu");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    // Обновленный метод sendMainMenu для добавления кнопки "Свободные заказы"
    private void sendMainMenu(Long chatId, UserState userState) {
        String text = "⭐ Добро пожаловать, " + userState.fullName + "!\n\nВыберите действие:";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton personalDataButton = new InlineKeyboardButton();
        personalDataButton.setText("📋 Личные данные");
        personalDataButton.setCallbackData("personal_data");
        row1.add(personalDataButton);

        if ("ПОКУПАТЕЛЬ".equals(userState.userRole)) {
            InlineKeyboardButton myOrdersButton = new InlineKeyboardButton();
            myOrdersButton.setText("📦 Мои заказы");
            myOrdersButton.setCallbackData("my_orders");
            row1.add(myOrdersButton);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton assortmentButton = new InlineKeyboardButton();
            assortmentButton.setText("🛍️ Ассортимент");
            assortmentButton.setCallbackData("assortment");
            row2.add(assortmentButton);

            InlineKeyboardButton orderFrameButton = new InlineKeyboardButton();
            orderFrameButton.setText("🖼️ Заказать рамку");
            orderFrameButton.setCallbackData("order_frame");
            row2.add(orderFrameButton);

            rows.add(row1);
            rows.add(row2);

        } else if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole)) {
            InlineKeyboardButton viewOrdersButton = new InlineKeyboardButton();
            viewOrdersButton.setText("📋 Мои заказы");
            viewOrdersButton.setCallbackData("view_orders");
            row1.add(viewOrdersButton);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton freeOrdersButton = new InlineKeyboardButton();
            freeOrdersButton.setText("🆓 Свободные заказы");
            freeOrdersButton.setCallbackData("free_orders");
            row2.add(freeOrdersButton);

            InlineKeyboardButton changeStatusButton = new InlineKeyboardButton();
            changeStatusButton.setText("🔄 Поменять статус заказа");
            changeStatusButton.setCallbackData("change_order_status");
            row2.add(changeStatusButton);

            // НОВАЯ КНОПКА ДЛЯ УПРАВЛЕНИЯ КОМПОНЕНТАМИ
            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton frameComponentsButton = new InlineKeyboardButton();
            frameComponentsButton.setText("🖼️ Управление фурнитурами");
            frameComponentsButton.setCallbackData("frame_components_management");
            row3.add(frameComponentsButton);

            // Существующая кнопка для материалов
            InlineKeyboardButton frameMaterialsButton = new InlineKeyboardButton();
            frameMaterialsButton.setText("📦 Управление материалами");
            frameMaterialsButton.setCallbackData("frame_materials_management");
            row3.add(frameMaterialsButton);

            rows.add(row1);
            rows.add(row2);
            rows.add(row3);
        } else {
            rows.add(row1);
        }

        List<InlineKeyboardButton> exitRow = new ArrayList<>();
        InlineKeyboardButton exitButton = new InlineKeyboardButton();
        exitButton.setText("🚪 Выйти");
        exitButton.setCallbackData("exit");
        exitRow.add(exitButton);
        rows.add(exitRow);

        keyboard.setKeyboard(rows);

        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    // Обновленный метод startAuthorization для сброса всех состояний
    private void startAuthorization(Long chatId, UserState userState) {
        userState.state = "WAITING_LOGIN";
        userState.login = null;
        userState.userRole = null;
        userState.userId = null;
        userState.fullName = null;
        userState.currentOrders.clear();
        userState.currentOrderIndex = 0;
        userState.currentAssortment.clear();
        userState.currentAssortmentIndex = 0;
        userState.currentAssortmentType = "";
        userState.currentFrameOrder = new CustomFrameOrder();
        userState.currentFrameOrderStep = "";
        userState.freeOrders.clear();
        userState.currentFreeOrderIndex = 0;
        userState.viewingFreeOrders = false;

        // ДОБАВИТЬ СБРОС ПОЛЕЙ ДЛЯ УПРАВЛЕНИЯ МАТЕРИАЛАМИ
        userState.currentFrameMaterials.clear();
        userState.currentFrameMaterialIndex = 0;
        userState.selectedFrameMaterial = null;
        userState.frameMaterialAction = "";
        userState.waitingForField = "";

        // СБРОС ПОЛЕЙ ДЛЯ УПРАВЛЕНИЯ КОМПОНЕНТАМИ
        userState.currentFrameComponents.clear();
        userState.currentFrameComponentIndex = 0;
        userState.selectedFrameComponent = null;
        userState.frameComponentAction = "";
        userState.waitingForFieldComponent = "";

        // СБРОС РЕГИСТРАЦИОННЫХ ДАННЫХ
        userState.registrationCustomer = new Customer();
        userState.registrationStep = "";

        sendMessage(chatId, "🔐 Введите ваш логин:");
    }

    // Обновленный метод logout для сброса всех состояний
    private void logout(Long chatId, UserState userState) {
        if ("AUTHENTICATED".equals(userState.state)) {
            String fullName = userState.fullName;

            // Сброс всех состояний
            userState.state = "START";
            userState.login = null;
            userState.userRole = null;
            userState.userId = null;
            userState.fullName = null;
            userState.lastMessageId = null;
            userState.currentOrders.clear();
            userState.currentOrderIndex = 0;
            userState.currentAssortment.clear();
            userState.currentAssortmentIndex = 0;
            userState.currentAssortmentType = "";
            userState.currentFrameOrder = new CustomFrameOrder();
            userState.currentFrameOrderStep = "";
            userState.freeOrders.clear();
            userState.currentFreeOrderIndex = 0;
            userState.viewingFreeOrders = false;
            userState.currentMaterialEstimate = null;
            userState.currentMaterialActual = null;

            // Сброс полей для управления материалами
            userState.currentFrameMaterials.clear();
            userState.currentFrameMaterialIndex = 0;
            userState.selectedFrameMaterial = null;
            userState.frameMaterialAction = "";
            userState.waitingForField = "";

            // Сброс полей для управления компонентами
            userState.currentFrameComponents.clear();
            userState.currentFrameComponentIndex = 0;
            userState.selectedFrameComponent = null;
            userState.frameComponentAction = "";
            userState.waitingForFieldComponent = "";

            // Сброс регистрационных данных
            userState.registrationCustomer = new Customer();
            userState.registrationStep = "";

            String text = "👋 До новых встреч, " + fullName + "!";
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            // Строка с авторизацией и регистрацией
            List<InlineKeyboardButton> authRow = new ArrayList<>();
            InlineKeyboardButton authButton = new InlineKeyboardButton();
            authButton.setText("🔐 Авторизоваться");
            authButton.setCallbackData("auth");
            authRow.add(authButton);

            InlineKeyboardButton registerButton = new InlineKeyboardButton();
            registerButton.setText("📝 Регистрация");
            registerButton.setCallbackData("register");
            authRow.add(registerButton);

            rows.add(authRow);
            keyboard.setKeyboard(rows);

            sendMessageWithInlineKeyboard(chatId, text, keyboard);
        } else {
            sendMessage(chatId, "❌ Вы не авторизованы. Используйте /auth для входа.");
        }
    }

    // Обновленный метод sendHelpMessage для добавления информации о новой функции
    private void sendHelpMessage(Long chatId) {
        UserState userState = getUserState(chatId);

        String help = "📋 Доступные команды:\n\n" +
                "/start - Начало работы\n" +
                "/auth - Авторизация в системе\n" +
                "/register - Регистрация нового пользователя\n" +
                "/help - Показать справку\n" +
                "/logout - Выйти из системы\n\n";

        if ("AUTHENTICATED".equals(userState.state)) {
            help += "✅ Вы авторизованы как: " + userState.userRole + "\n\n";

            switch (userState.userRole) {
                case "МАСТЕР ПРОИЗВОДСТВА":
                    help += "🎯 Команды для мастера производства:\n" +
                            "• Просмотр заказов (кнопка после авторизации)\n" +
                            "• Свободные заказы (кнопка после авторизации)\n" +
                            "• Управление материалами рамок (кнопка после авторизации)\n" +
                            "• Изменение статусов заказов\n";
                    break;
                case "ПОКУПАТЕЛЬ":
                    help += "🎯 Команды для покупателя:\n" +
                            "• Проверка скидки\n" +
                            "• Статус заказа\n" +
                            "• Заказ рамок\n" +
                            "• 📋 Личные данные (кнопка после авторизации)\n" +
                            "• 📦 Мои заказы (кнопка после авторизации)\n" +
                            "• 🛍️ Ассортимент (кнопка после авторизации)\n" +
                            "• 🖼️ Заказать рамку (кнопка после авторизации)";
                    break;
            }
            help += "\n\nПосле авторизации появится кнопка для просмотра личных данных.";
        } else {
            help += "🔐 Для доступа к функциям системы выполните авторизацию: /auth\n" +
                    "📝 Или зарегистрируйтесь как новый покупатель: /register";
        }

        sendMessage(chatId, help);
    }

    // Остальные существующие методы без изменений
    private void handleAssortmentNavigation(Long chatId, String action, UserState userState) {
        if (action.equals("no_action_assortment")) {
            return;
        }

        if (action.equals("prev_assortment")) {
            if (userState.currentAssortmentIndex > 0) {
                userState.currentAssortmentIndex--;
                editCurrentAssortmentItem(chatId, userState);
            }
        } else if (action.equals("next_assortment")) {
            if (userState.currentAssortmentIndex < userState.currentAssortment.size() - 1) {
                userState.currentAssortmentIndex++;
                editCurrentAssortmentItem(chatId, userState);
            }
        }
    }

    private void editCurrentAssortmentItem(Long chatId, UserState userState) {
        if (userState.currentAssortment == null || userState.currentAssortment.isEmpty()) {
            return;
        }

        Object currentItem = userState.currentAssortment.get(userState.currentAssortmentIndex);
        String itemText = formatAssortmentItemDetails(currentItem, userState.currentAssortmentIndex + 1,
                userState.currentAssortment.size(), userState.currentAssortmentType);
        InlineKeyboardMarkup keyboard = createAssortmentNavigationKeyboard(userState);

        if (userState.lastMessageId != null) {
            editMessageWithInlineKeyboard(chatId, userState.lastMessageId, itemText, keyboard);
        } else {
            sendMessageWithInlineKeyboard(chatId, itemText, keyboard);
        }
    }

    private void showAssortmentCategories(Long chatId) {
        String text = "🛍️ Что хотите посмотреть?";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Создаем кнопки для категорий
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton embroideryButton = new InlineKeyboardButton();
        embroideryButton.setText("🎨 Вышивка");
        embroideryButton.setCallbackData("embroidery_kit");
        row1.add(embroideryButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton consumableButton = new InlineKeyboardButton();
        consumableButton.setText("🧵 Материалы вышивки");
        consumableButton.setCallbackData("consumable");
        row2.add(consumableButton);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton frameComponentButton = new InlineKeyboardButton();
        frameComponentButton.setText("🖼️ Каркасы");
        frameComponentButton.setCallbackData("frame_component");
        row3.add(frameComponentButton);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton frameMaterialButton = new InlineKeyboardButton();
        frameMaterialButton.setText("📦 Материал каркаса");
        frameMaterialButton.setCallbackData("frame_material");
        row4.add(frameMaterialButton);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Вернуться в меню");
        backButton.setCallbackData("back_to_menu");
        backRow.add(backButton);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    private void showEmbroideryKitsWithNavigation(Long chatId, UserState userState) {
        try {
            Iterable<EmbroideryKit> embroideryKits = mainController.allEmbroiderykit();
            List<EmbroideryKit> availableKits = new ArrayList<>();

            for (EmbroideryKit kit : embroideryKits) {
                if (kit.getStockQuantity() != null && !kit.getStockQuantity().isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(kit.getStockQuantity());
                        if (quantity > 0) {
                            availableKits.add(kit);
                        }
                    } catch (NumberFormatException e) {
                        availableKits.add(kit);
                    }
                } else {
                    availableKits.add(kit);
                }
            }

            if (availableKits.isEmpty()) {
                sendMessage(chatId, "❌ Наборы для вышивки временно отсутствуют на складе.");
                return;
            }

            userState.currentAssortment = availableKits;
            userState.currentAssortmentIndex = 0;
            userState.currentAssortmentType = "embroidery_kit";

            showCurrentAssortmentItem(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing embroidery kits: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке наборов для вышивки.");
        }
    }

    private void showConsumablesWithNavigation(Long chatId, UserState userState) {
        try {
            Iterable<Consumable> consumables = mainController.allConsumable();
            List<Consumable> availableConsumables = new ArrayList<>();

            for (Consumable consumable : consumables) {
                if (consumable.getStockQuantity() != null && !consumable.getStockQuantity().isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(consumable.getStockQuantity());
                        if (quantity > 0) {
                            availableConsumables.add(consumable);
                        }
                    } catch (NumberFormatException e) {
                        availableConsumables.add(consumable);
                    }
                } else {
                    availableConsumables.add(consumable);
                }
            }

            if (availableConsumables.isEmpty()) {
                sendMessage(chatId, "❌ Материалы для вышивки временно отсутствуют на складе.");
                return;
            }

            userState.currentAssortment = availableConsumables;
            userState.currentAssortmentIndex = 0;
            userState.currentAssortmentType = "consumable";

            showCurrentAssortmentItem(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing consumables: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке материалов для вышивки.");
        }
    }

    private void showFrameComponentsWithNavigation(Long chatId, UserState userState) {
        try {
            Iterable<FrameComponent> frameComponents = mainController.allFrameComponent();
            List<FrameComponent> availableComponents = new ArrayList<>();

            for (FrameComponent component : frameComponents) {
                if (component.getStockQuantity() != null && component.getStockQuantity() > 0) {
                    availableComponents.add(component);
                } else {
                    availableComponents.add(component);
                }
            }

            if (availableComponents.isEmpty()) {
                sendMessage(chatId, "❌ Каркасы временно отсутствуют на складе.");
                return;
            }

            userState.currentAssortment = availableComponents;
            userState.currentAssortmentIndex = 0;
            userState.currentAssortmentType = "frame_component";

            showCurrentAssortmentItem(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing frame components: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке каркасов.");
        }
    }

    private void showFrameMaterialsWithNavigation(Long chatId, UserState userState) {
        try {
            Iterable<FrameMaterial> frameMaterials = mainController.allFrameMaterial();
            List<FrameMaterial> availableMaterials = new ArrayList<>();

            for (FrameMaterial material : frameMaterials) {
                if (material.getStockQuantity() != null && material.getStockQuantity() > 0) {
                    availableMaterials.add(material);
                } else {
                    availableMaterials.add(material);
                }
            }

            if (availableMaterials.isEmpty()) {
                sendMessage(chatId, "❌ Материалы для каркасов временно отсутствуют на складе.");
                return;
            }

            userState.currentAssortment = availableMaterials;
            userState.currentAssortmentIndex = 0;
            userState.currentAssortmentType = "frame_material";

            showCurrentAssortmentItem(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error showing frame materials: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при загрузке материалов для каркасов.");
        }
    }

    private void showCurrentAssortmentItem(Long chatId, UserState userState) {
        if (userState.currentAssortment == null || userState.currentAssortment.isEmpty()) {
            sendMessage(chatId, "❌ Нет товаров для отображения.");
            return;
        }

        Object currentItem = userState.currentAssortment.get(userState.currentAssortmentIndex);
        String itemText = formatAssortmentItemDetails(currentItem, userState.currentAssortmentIndex + 1, userState.currentAssortment.size(), userState.currentAssortmentType);

        // Создаем клавиатуру с навигацией
        InlineKeyboardMarkup keyboard = createAssortmentNavigationKeyboard(userState);

        sendMessageWithInlineKeyboard(chatId, itemText, keyboard);
    }

    private String formatAssortmentItemDetails(Object item, int currentNumber, int totalItems, String type) {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case "embroidery_kit":
                EmbroideryKit kit = (EmbroideryKit) item;
                sb.append("🎨 Набор для вышивки ").append(currentNumber).append(" из ").append(totalItems).append("\n\n");
                sb.append("").append(kit.getName() != null ? kit.getName() : "Без названия").append("\n");
                if (kit.getStockQuantity() != null && !kit.getStockQuantity().isEmpty()) {
                    sb.append("📦 В наличии: ").append(kit.getStockQuantity()).append(" шт.\n");
                }
                sb.append("💰 Цена: ").append(kit.getPrice() != null ? kit.getPrice() : "0").append(" руб.\n");
                if (kit.getDescription() != null && !kit.getDescription().isEmpty()) {
                    sb.append("📝 Описание: ").append(kit.getDescription()).append("\n");
                }
                break;
            case "consumable":
                Consumable consumable = (Consumable) item;
                sb.append("🧵 Материал для вышивки ").append(currentNumber).append(" из ").append(totalItems).append("\n\n");
                sb.append("").append(consumable.getName() != null ? consumable.getName() : "Без названия").append("\n");
                if (consumable.getStockQuantity() != null && !consumable.getStockQuantity().isEmpty()) {
                    sb.append("📦 В наличии: ").append(consumable.getStockQuantity()).append(" ").append(consumable.getUnit() != null ? consumable.getUnit() : "шт.").append("\n");
                }
                sb.append("💰 Цена: ").append(consumable.getPrice() != null ? consumable.getPrice() : "0").append(" руб.\n");
                if (consumable.getDescription() != null && !consumable.getDescription().isEmpty()) {
                    sb.append("📝 Описание: ").append(consumable.getDescription()).append("\n");
                }
                break;

            case "frame_component":
                FrameComponent component = (FrameComponent) item;
                sb.append("🖼️ Каркас ").append(currentNumber).append(" из ").append(totalItems).append("\n\n");
                sb.append("").append(component.getName() != null ? component.getName() : "Без названия").append("\n");
                if (component.getStockQuantity() != null) {
                    sb.append("📦 В наличии: ").append(component.getStockQuantity()).append(" шт.\n");
                }
                sb.append("💰 Цена: ").append(component.getPrice() != null ? component.getPrice() : 0).append(" руб.\n");
                if (component.getType() != null && !component.getType().isEmpty()) {
                    sb.append("📋 Тип: ").append(component.getType()).append("\n");
                }
                if (component.getDescription() != null && !component.getDescription().isEmpty()) {
                    sb.append("📝 Описание: ").append(component.getDescription()).append("\n");
                }
                break;

            case "frame_material":
                FrameMaterial material = (FrameMaterial) item;
                sb.append("📦 Материал для каркаса ").append(currentNumber).append(" из ").append(totalItems).append("\n\n");
                sb.append("").append(material.getName() != null ? material.getName() : "Без названия").append("\n");
                if (material.getStockQuantity() != null) {
                    sb.append("📦 В наличии: ").append(material.getStockQuantity()).append(" м.\n");
                }
                sb.append("💰 Цена за метр: ").append(material.getPricePerMeter() != null ? material.getPricePerMeter() : 0).append(" руб.\n");
                if (material.getColor() != null && !material.getColor().isEmpty()) {
                    sb.append("🎨 Цвет: ").append(material.getColor()).append("\n");
                }
                if (material.getWidth() != null) {
                    sb.append("📏 Ширина: ").append(material.getWidth()).append(" мм\n");
                }
                if (material.getDescription() != null && !material.getDescription().isEmpty()) {
                    sb.append("📝 Описание: ").append(material.getDescription()).append("\n");
                }
                break;
        }

        return sb.toString();
    }

    private InlineKeyboardMarkup createAssortmentNavigationKeyboard(UserState userState) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Строка с навигацией
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        // Кнопка "Предыдущий"
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅️ Предыдущий");
        prevButton.setCallbackData("prev_assortment");
        if (userState.currentAssortmentIndex > 0) {
            navRow.add(prevButton);
        } else {
            InlineKeyboardButton disabledPrev = new InlineKeyboardButton();
            disabledPrev.setText("⏹️ Предыдущий");
            disabledPrev.setCallbackData("no_action_assortment");
            navRow.add(disabledPrev);
        }

        // Кнопка "Следующий"
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий ➡️");
        nextButton.setCallbackData("next_assortment");
        if (userState.currentAssortmentIndex < userState.currentAssortment.size() - 1) {
            navRow.add(nextButton);
        } else {
            InlineKeyboardButton disabledNext = new InlineKeyboardButton();
            disabledNext.setText("⏹️ Следующий");
            disabledNext.setCallbackData("no_action_assortment");
            navRow.add(disabledNext);
        }

        rows.add(navRow);

        // Строка с дополнительными действиями
        List<InlineKeyboardButton> actionRow = new ArrayList<>();

        // Кнопка возврата к категориям
        InlineKeyboardButton categoriesButton = new InlineKeyboardButton();
        categoriesButton.setText("🔙 К категориям");
        categoriesButton.setCallbackData("assortment");
        actionRow.add(categoriesButton);

        // Кнопка возврата в меню
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 В меню");
        backButton.setCallbackData("back_to_menu");
        actionRow.add(backButton);

        rows.add(actionRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void handleOrderNavigation(Long chatId, String action, UserState userState) {
        if (action.equals("no_action")) {
            return;
        }

        if (action.equals("prev_order")) {
            if (userState.currentOrderIndex > 0) {
                userState.currentOrderIndex--;
                editCurrentOrder(chatId, userState);
            }
        } else if (action.equals("next_order")) {
            if (userState.currentOrderIndex < userState.currentOrders.size() - 1) {
                userState.currentOrderIndex++;
                editCurrentOrder(chatId, userState);
            }
        } else if (action.equals("change_current_order_status")) {
            if (!userState.currentOrders.isEmpty()) {
                Orders currentOrder = userState.currentOrders.get(userState.currentOrderIndex);
                userState.selectedOrderId = currentOrder.getId().longValue();
                sendStatusMenu(chatId, currentOrder.getId().longValue());
            }
        }
    }

    private void editCurrentOrder(Long chatId, UserState userState) {
        if (userState.currentOrders == null || userState.currentOrders.isEmpty()) {
            return;
        }

        Orders currentOrder = userState.currentOrders.get(userState.currentOrderIndex);
        String orderText = formatOrderDetails(currentOrder, userState.currentOrderIndex + 1, userState.currentOrders.size());
        InlineKeyboardMarkup keyboard = createOrderNavigationKeyboard(userState);

        if (userState.lastMessageId != null) {
            editMessageWithInlineKeyboard(chatId, userState.lastMessageId, orderText, keyboard);
        } else {
            // Если lastMessageId не сохранен, отправляем новое сообщение
            sendMessageWithInlineKeyboard(chatId, orderText, keyboard);
        }
    }

    private void showMyOrdersWithNavigation(Long chatId, UserState userState) {
        try {
            Long customerId = userState.userId;
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();

            List<Orders> userOrders = new ArrayList<>();
            for (Orders order : allOrders) {
                if (order.getCustomerID() != null && order.getCustomerID().getId().longValue() == customerId) {
                    userOrders.add(order);
                }
            }

            if (userOrders.isEmpty()) {
                sendMessage(chatId, "📦 У вас пока нет заказов.");
                return;
            }

            userState.currentOrders = userOrders;
            userState.currentOrderIndex = 0;
            showCurrentOrder(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error in showMyOrdersWithNavigation: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при получении заказов. Попробуйте позже.");
        }
    }

    private void showProductionMasterOrdersWithNavigation(Long chatId, UserState userState) {
        try {
            Long masterId = userState.userId;
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();

            List<Orders> masterOrders = new ArrayList<>();
            for (Orders order : allOrders) {
                // Добавлена проверка на null для order.getProductionMasterID() и getIdUser()
                if (order.getProductionMasterID() != null &&
                        order.getProductionMasterID().getIdUser() != null &&
                        order.getProductionMasterID().getIdUser().getId().longValue() == masterId) {
                    masterOrders.add(order);
                }
            }

            if (masterOrders.isEmpty()) {
                sendMessage(chatId, "📋 У вас пока нет назначенных заказов.");
                return;
            }

            userState.currentOrders = masterOrders;
            userState.currentOrderIndex = 0;
            showCurrentOrder(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error in showProductionMasterOrdersWithNavigation: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при получении заказов. Попробуйте позже.");
        }
    }

    private void showCurrentOrder(Long chatId, UserState userState) {
        if (userState.currentOrders == null || userState.currentOrders.isEmpty()) {
            sendMessage(chatId, "❌ Нет заказов для отображения.");
            return;
        }

        Orders currentOrder = userState.currentOrders.get(userState.currentOrderIndex);
        String orderText = formatOrderDetails(currentOrder, userState.currentOrderIndex + 1, userState.currentOrders.size());
        InlineKeyboardMarkup keyboard = createOrderNavigationKeyboard(userState);
        sendMessageWithInlineKeyboard(chatId, orderText, keyboard);
    }

    private String formatOrderDetails(Orders order, int currentNumber, int totalOrders) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("📦 Заказ ").append(currentNumber).append(" из ").append(totalOrders).append("\n\n");
        sb.append("🆔 Номер заказа: ").append(order.getId()).append("\n");
        sb.append("📅 Дата заказа: ").append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "Не указана").append("\n");
        sb.append("💰 Сумма: ").append(order.getTotalAmount() != null ? order.getTotalAmount() : 0).append(" руб.\n");
        sb.append("📊 Статус: ").append(order.getStatus() != null ? order.getStatus() : "Не указан").append("\n");
        sb.append("⏰ Срок выполнения: ").append(order.getDueDate() != null ? dateFormat.format(order.getDueDate()) : "Не указан").append("\n");

        if (order.getCompletionDate() != null) {
            sb.append("✅ Дата завершения: ").append(dateFormat.format(order.getCompletionDate())).append("\n");
        } else {
            sb.append("✅ Дата завершения: Не завершён\n");
        }

        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            sb.append("📝 Примечания: ").append(order.getNotes()).append("\n");
        }

        return sb.toString();
    }

    private InlineKeyboardMarkup createOrderNavigationKeyboard(UserState userState) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> navRow = new ArrayList<>();

        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅️ Предыдущий");
        prevButton.setCallbackData("prev_order");
        if (userState.currentOrderIndex > 0) {
            navRow.add(prevButton);
        } else {
            InlineKeyboardButton disabledPrev = new InlineKeyboardButton();
            disabledPrev.setText("⏹️ Предыдущий");
            disabledPrev.setCallbackData("no_action");
            navRow.add(disabledPrev);
        }

        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий ➡️");
        nextButton.setCallbackData("next_order");
        if (userState.currentOrderIndex < userState.currentOrders.size() - 1) {
            navRow.add(nextButton);
        } else {
            InlineKeyboardButton disabledNext = new InlineKeyboardButton();
            disabledNext.setText("⏹️ Следующий");
            disabledNext.setCallbackData("no_action");
            navRow.add(disabledNext);
        }

        rows.add(navRow);

        List<InlineKeyboardButton> actionRow = new ArrayList<>();

        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole)) {
            InlineKeyboardButton changeStatusButton = new InlineKeyboardButton();
            changeStatusButton.setText("🔄 Изменить статус");
            changeStatusButton.setCallbackData("change_current_order_status");
            actionRow.add(changeStatusButton);
        }

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Вернуться в меню");
        backButton.setCallbackData("back_to_menu");
        actionRow.add(backButton);

        if (!actionRow.isEmpty()) {
            rows.add(actionRow);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private InlineKeyboardMarkup createBackKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Вернуться");
        backButton.setCallbackData("back_to_menu");
        row.add(backButton);
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void sendOrderListForStatusChange(Long chatId, UserState userState) {
        try {
            Long masterId = userState.userId;
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();

            List<Orders> masterOrders = new ArrayList<>();
            for (Orders order : allOrders) {
                // Добавляем проверку на null для ProductionMasterID и getIdUser()
                if (order.getProductionMasterID() != null &&
                        order.getProductionMasterID().getIdUser() != null &&
                        order.getProductionMasterID().getIdUser().getId().longValue() == masterId) {
                    masterOrders.add(order);
                }
            }

            if (masterOrders.isEmpty()) {
                sendMessage(chatId, "📋 У вас пока нет назначенных заказов.");
                return;
            }

            String text = "📋 Выберите номер заказа для изменения статуса:\n\n";
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (Orders order : masterOrders) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton orderButton = new InlineKeyboardButton();
                orderButton.setText("Заказ №" + order.getId());
                orderButton.setCallbackData("select_order_" + order.getId());
                row.add(orderButton);
                rows.add(row);
            }

            List<InlineKeyboardButton> backRow = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("🔙 Вернуться");
            backButton.setCallbackData("back_to_menu");
            backRow.add(backButton);
            rows.add(backRow);

            keyboard.setKeyboard(rows);
            sendMessageWithInlineKeyboard(chatId, text, keyboard);
        } catch (Exception e) {
            System.err.println("Error in sendOrderListForStatusChange: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при получении списка заказов.");
        }
    }

    private void sendStatusMenu(Long chatId, Long orderId) {
        String text = "🔄 Выберите новый статус для заказа №" + orderId + ":";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<String> statuses = Arrays.asList("Новый", "Выполняется", "Готов", "Отменен", "Забран");

        for (String status : statuses) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton statusButton = new InlineKeyboardButton();
            statusButton.setText(status);
            statusButton.setCallbackData("change_status_" + orderId + "_" + status.replace(" ", "_"));
            row.add(statusButton);
            rows.add(row);
        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Вернуться");
        backButton.setCallbackData("back_to_menu");
        backRow.add(backButton);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    private void changeOrderStatus(Long chatId, Long orderId, String newStatus, UserState userState) {
        try {
            // Заменяем подчеркивания обратно на пробелы
            String statusWithSpaces = newStatus.replace("_", " ");

            // Если статус меняется на "Готов", запрашиваем фактическое количество материала
            if ("Готов".equals(statusWithSpaces)) {
                userState.selectedOrderId = orderId;
                userState.state = "WAITING_ACTUAL_MATERIAL";
                sendMessage(chatId, "📏 Для завершения заказа №" + orderId + " введите фактически израсходованное количество материала (в метрах):\n\n" +
                        "Введите число, например: 2.3");
            } else {
                // Для других статусов просто меняем статус
                mainController.changeOrderStatus(orderId.intValue(), statusWithSpaces);
                userState.selectedOrderId = null;
                sendMessage(chatId, "✅ Статус заказа №" + orderId + " изменен на: " + statusWithSpaces);
                sendMainMenu(chatId, userState);
            }
        } catch (Exception e) {
            System.err.println("Error changing order status: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при изменении статуса заказа.");
        }
    }

    private void updateCustomFrameOrderWithActual(Long orderId, Double actualMaterial) {
        try {
            Iterable<CustomFrameOrder> customFrameOrders = mainController.allCustomFrameOrder();
            for (CustomFrameOrder customOrder : customFrameOrders) {
                if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().longValue() == orderId.longValue()) {
                    // Устанавливаем фактическое количество материала как BigDecimal
                    if (actualMaterial != null) {
                        customOrder.setActualMaterialUsage(BigDecimal.valueOf(actualMaterial));
                    }
                    mainController.updateCustomFrameOrder(customOrder);
                    System.out.println("Updated custom frame order with ID: " + customOrder.getId() +
                            ", actual material: " + actualMaterial + " m");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating custom frame order with actual material: " + e.getMessage());
        }
    }

    private void handleActualMaterialInput(Long chatId, String messageText, UserState userState) {
        try {
            double actualMaterial;
            try {
                actualMaterial = Double.parseDouble(messageText.trim());
                if (actualMaterial <= 0) {
                    sendMessage(chatId, "❌ Количество материала должно быть положительным числом. Попробуйте снова:");
                    return;
                }
            } catch (NumberFormatException e) {
                sendMessage(chatId, "❌ Пожалуйста, введите корректное число для количества материала:");
                return;
            }

            // Сохраняем фактическое количество материала и меняем статус
            updateCustomFrameOrderWithActual(userState.selectedOrderId, actualMaterial);
            mainController.changeOrderStatus(userState.selectedOrderId.intValue(), "Готов");

            sendMessage(chatId, "✅ Заказ №" + userState.selectedOrderId + " завершен!\n" +
                    "📏 Фактически израсходовано материала: " + actualMaterial + " м.\n" +
                    "📊 Статус изменен на: Готов");

            // Сбрасываем состояние
            userState.selectedOrderId = null;
            userState.state = "AUTHENTICATED";
            userState.currentMaterialActual = null;

            sendMainMenu(chatId, userState);

        } catch (Exception e) {
            System.err.println("Error handling actual material input: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при завершении заказа. Попробуйте позже.");
            userState.state = "AUTHENTICATED";
            sendMainMenu(chatId, userState);
        }
    }

    private String getPersonalDataResponse(UserState userState) {
        try {
            Long userId = userState.userId;
            String response = "";

            if ("ПОКУПАТЕЛЬ".equals(userState.userRole)) {
                Iterable<Customer> customers = mainController.allCustomers();
                boolean found = false;
                for (Customer customer : customers) {
                    if (customer.getId().longValue() == userId) {
                        int discount = 0;
                        try {
                            discount = Integer.parseInt(customer.getDiscount());
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing discount: " + e.getMessage());
                            discount = 0;
                        }
                        double totalPurchases = 0.0;
                        try {
                            totalPurchases = Double.parseDouble(customer.getTotalPurchases());
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing totalPurchases: " + e.getMessage());
                            totalPurchases = 0.0;
                        }
                        String fullName = (customer.getLastName() != null ? customer.getLastName() : "") + " " +
                                (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
                                (customer.getMiddleName() != null ? customer.getMiddleName() : "");
                        fullName = fullName.trim();

                        response = String.format("📋 Личные данные (Покупатель):\n" +
                                        "👤 ФИО: %s\n" +
                                        "📞 Телефон: %s\n" +
                                        "📧 Email: %s\n" +
                                        "💰 Скидка: %d%%\n" +
                                        "💸 Общая сумма покупок: %.2f",
                                fullName,
                                customer.getPhone() != null ? customer.getPhone() : "Не указан",
                                customer.getEmail() != null ? customer.getEmail() : "Не указан",
                                discount,
                                totalPurchases);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    response = "❌ Данные покупателя не найдены для ID: " + userId;
                }
            } else {
                Iterable<User> users = mainController.allUsers();
                boolean found = false;
                for (User user : users) {
                    if (user.getId().longValue() == userId) {
                        String position = getPositionByRole(userState.userRole);
                        String fullName = (user.getLastName() != null ? user.getLastName() : "") + " " +
                                (user.getFirstName() != null ? user.getFirstName() : "") + " " +
                                (user.getMiddleName() != null ? user.getMiddleName() : "");
                        fullName = fullName.trim();

                        response = String.format("📋 Личные данные (Сотрудник):\n" +
                                        "👤 ФИО: %s\n" +
                                        "📞 Телефон: %s\n" +
                                        "🎂 Дата рождения: %s\n" +
                                        "📅 Дата трудоустройства: %s\n" +
                                        "🆔 Паспортные данные: %s\n" +
                                        "🔢 СНИЛС: %s\n" +
                                        "📷 Ссылка на фото: %s\n" +
                                        "👔 Должность: %s",
                                fullName,
                                user.getPhone() != null ? user.getPhone() : "Не указан",
                                user.getDateOfBirth() != null ? user.getDateOfBirth() : "Не указана",
                                user.getDateOfEmployment() != null ? user.getDateOfEmployment() : "Не указана",
                                user.getPassportData() != null ? user.getPassportData() : "Не указаны",
                                user.getSnils() != null ? user.getSnils() : "Не указан",
                                user.getPhotoLink() != null ? user.getPhotoLink() : "Не указана",
                                position);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    response = "❌ Данные сотрудника не найдены для ID: " + userId;
                }
            }

            return response;
        } catch (Exception e) {
            System.err.println("Error in getPersonalDataResponse: " + e.getMessage());
            return "❌ Ошибка при получении личных данных. Попробуйте позже.";
        }
    }

    private String getPositionByRole(String userRole) {
        switch (userRole) {
            case "ДИРЕКТОР": return "Директор";
            case "ПРОДАВЕЦ": return "Продавец";
            case "МАСТЕР ПРОИЗВОДСТВА": return "Мастер производства";
            case "СОТРУДНИК": return "Сотрудник";
            default: return "Неизвестная роль";
        }
    }

    private UserState getUserState(Long chatId) {
        return userStates.computeIfAbsent(chatId, k -> new UserState());
    }

    private void sendWelcomeMessage(Long chatId, String userName) {
        String welcome = "👋 Привет, " + userName + "!\n\n" +
                "Добро пожаловать в систему мастерской вышивки!\n\n" +
                "Для начала работы выберите действие:";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Строка с авторизацией и регистрацией
        List<InlineKeyboardButton> authRow = new ArrayList<>();
        InlineKeyboardButton authButton = new InlineKeyboardButton();
        authButton.setText("🔐 Авторизация");
        authButton.setCallbackData("auth");
        authRow.add(authButton);

        InlineKeyboardButton registerButton = new InlineKeyboardButton();
        registerButton.setText("📝 Регистрация");
        registerButton.setCallbackData("register");
        authRow.add(registerButton);

        // Строка с помощью
        List<InlineKeyboardButton> helpRow = new ArrayList<>();
        InlineKeyboardButton helpButton = new InlineKeyboardButton();
        helpButton.setText("❓ Помощь");
        helpButton.setCallbackData("help");
        helpRow.add(helpButton);

        rows.add(authRow);
        rows.add(helpRow);
        keyboard.setKeyboard(rows);

        sendMessageWithInlineKeyboard(chatId, welcome, keyboard);
    }

    private void handleLoginInput(Long chatId, String login, UserState userState) {
        userState.login = login.trim();
        userState.state = "WAITING_PASSWORD";
        sendMessage(chatId, "🔑 Теперь введите ваш пароль для пользователя '" + login + "':");
    }

    private void editMessageWithInlineKeyboard(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setReplyMarkup(keyboard);

        try {
            execute(editMessage);
            System.out.println("Message edited in chat: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("Failed to edit message in " + chatId + ": " + e.getMessage());
        }
    }

    private void handlePasswordInput(Long chatId, String password, UserState userState) {
        if (userState.login == null) {
            sendMessage(chatId, "❌ Ошибка сессии. Начните заново: /auth");
            userState.state = "START";
            return;
        } else if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole)) {
            // Проверяем наличие свободных заказов при авторизации
            checkAndNotifyAboutFreeOrders(chatId);
        }

        try {
            System.out.println("Attempting authentication for login: " + userState.login);
            String authResult = mainController.getAutorization(userState.login, password.trim());
            System.out.println("Authentication result: " + authResult);

            String response;
            if (!"NO".equals(authResult)) {
                String role = authResult;
                Long id = null;
                String fullName = "";

                Iterable<User> users = mainController.allUsers();
                for (User user : users) {
                    if (user.getLogin() != null && user.getLogin().equals(userState.login)) {
                        id = user.getId().longValue();
                        fullName = (user.getLastName() != null ? user.getLastName() : "") + " " +
                                (user.getFirstName() != null ? user.getFirstName() : "") + " " +
                                (user.getMiddleName() != null ? user.getMiddleName() : "");
                        fullName = fullName.trim();
                        break;
                    }
                }

                if (id == null) {
                    throw new Exception("User not found for login: " + userState.login);
                }

                userState.state = "AUTHENTICATED";
                userState.userRole = getRoleName(role);
                userState.userId = id;
                userState.fullName = fullName;

                response = "✅ Авторизация успешна! Добро пожаловать " + fullName +
                        "\n\nТеперь вы можете использовать функции системы.";

                sendMainMenu(chatId, userState);

            } else {
                Iterable<Customer> customers = mainController.allCustomers();
                Customer foundCustomer = null;
                for (Customer customer : customers) {
                    if (customer.getLogins() != null && customer.getLogins().equals(userState.login) &&
                            customer.getPasswords() != null && customer.getPasswords().equals(password.trim())) {
                        foundCustomer = customer;
                        break;
                    }
                }
                if (foundCustomer != null) {
                    userState.state = "AUTHENTICATED";
                    userState.userRole = "ПОКУПАТЕЛЬ";
                    userState.userId = foundCustomer.getId().longValue();
                    String fullName = (foundCustomer.getLastName() != null ? foundCustomer.getLastName() : "") + " " +
                            (foundCustomer.getFirstName() != null ? foundCustomer.getFirstName() : "") + " " +
                            (foundCustomer.getMiddleName() != null ? foundCustomer.getMiddleName() : "");
                    fullName = fullName.trim();
                    userState.fullName = fullName;

                    response = "✅ Авторизация успешна! Добро пожаловать " + fullName +
                            "\n\nТеперь вы можете использовать функции системы.";

                    sendMainMenu(chatId, userState);
                } else {
                    userState.state = "START";
                    userState.login = null;
                    userState.userId = null;
                    userState.fullName = null;
                    response = "❌ Неверный логин или пароль. Попробуйте снова: /auth";
                    sendMessage(chatId, response);
                }
            }

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            userState.state = "START";
            userState.login = null;
            userState.userId = null;
            userState.fullName = null;
            sendMessage(chatId, "❌ Ошибка при авторизации. Попробуйте позже: /auth");
            e.printStackTrace();
        }
    }

    private String getRoleName(String role) {
        switch (role) {
            case "CUSTOMER":
                return "ПОКУПАТЕЛЬ";
            case "DIRECTOR":
                return "ДИРЕКТОР";
            case "SELLER":
                return "ПРОДАВЕЦ";
            case "PRODUCTIONMASTER":
                return "МАСТЕР ПРОИЗВОДСТВА";
            case "EMPLOYEE":
                return "СОТРУДНИК";
            default:
                return "ПОЛЬЗОВАТЕЛЬ";
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        try {
            execute(message);
            System.out.println("Message sent to chat: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message to " + chatId + ": " + e.getMessage());
        }
    }

    private void sendMessageWithInlineKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            Message sentMessage = execute(message);
            // Сохраняем ID отправленного сообщения
            UserState userState = getUserState(chatId);
            userState.lastMessageId = sentMessage.getMessageId();
            System.out.println("Message with inline keyboard sent to chat: " + chatId + ", messageId: " + sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message with keyboard to " + chatId + ": " + e.getMessage());
        }
    }
}