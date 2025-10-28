package group.api.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import group.api.controller.MainController;
import group.api.entity.Customer;
import group.api.entity.Orders;
import group.api.entity.User;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        UserState() {
            this.state = "START";
            this.selectedOrderId = null;
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

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        UserState userState = getUserState(chatId);

        try {
            if (data.startsWith("select_order_")) {
                String orderIdStr = data.substring("select_order_".length());
                Long orderId = Long.parseLong(orderIdStr);
                userState.selectedOrderId = orderId;
                sendStatusMenu(chatId, orderId);
            } else if (data.startsWith("change_status_")) {
                String[] parts = data.split("_");
                Long orderId = Long.parseLong(parts[2]);
                String newStatus = parts[3];
                if (userState.selectedOrderId != null && userState.selectedOrderId.equals(orderId)) {
                    changeOrderStatus(chatId, orderId, newStatus, userState);
                } else {
                    sendMessage(chatId, "❌ Ошибка: заказ не выбран.");
                }
            } else {
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
                            String response = getMyOrdersResponse(userState);
                            InlineKeyboardMarkup keyboard = createBackKeyboard();
                            sendMessageWithInlineKeyboard(chatId, response, keyboard);
                        } else {
                            sendMessage(chatId, "❌ Доступ запрещен. Эта функция доступна только для покупателей.");
                        }
                        break;
                    case "view_orders":
                        if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole) && "AUTHENTICATED".equals(userState.state)) {
                            System.out.println("Production master viewing orders for userId: " + userState.userId);
                            String response = getProductionMasterOrdersResponse(userState);
                            InlineKeyboardMarkup keyboard = createBackKeyboard();
                            sendMessageWithInlineKeyboard(chatId, response, keyboard);
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
                    case "exit":
                        logout(chatId, userState);
                        break;
                    case "back_to_menu":
                        sendMainMenu(chatId, userState);
                        break;
                    case "auth":
                        startAuthorization(chatId, userState);
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

    private void sendMainMenu(Long chatId, UserState userState) {
        String text = "⭐ Добро пожаловать, " + userState.fullName + "!\n\nВыберите действие:";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton personalDataButton = new InlineKeyboardButton();
        personalDataButton.setText("📋 Личные данные");
        personalDataButton.setCallbackData("personal_data");
        row.add(personalDataButton);

        if ("ПОКУПАТЕЛЬ".equals(userState.userRole)) {
            InlineKeyboardButton myOrdersButton = new InlineKeyboardButton();
            myOrdersButton.setText("📦 Мои заказы");
            myOrdersButton.setCallbackData("my_orders");
            row.add(myOrdersButton);
        } else if ("МАСТЕР ПРОИЗВОДСТВА".equals(userState.userRole)) {
            InlineKeyboardButton viewOrdersButton = new InlineKeyboardButton();
            viewOrdersButton.setText("📋 Просмотр заказов");
            viewOrdersButton.setCallbackData("view_orders");
            row.add(viewOrdersButton);
            InlineKeyboardButton changeStatusButton = new InlineKeyboardButton();
            changeStatusButton.setText("🔄 Поменять статус заказа");
            changeStatusButton.setCallbackData("change_order_status");
            row.add(changeStatusButton);
        }

        rows.add(row);
        List<InlineKeyboardButton> exitRow = new ArrayList<>();
        InlineKeyboardButton exitButton = new InlineKeyboardButton();
        exitButton.setText("🚪 Выйти");
        exitButton.setCallbackData("exit");
        exitRow.add(exitButton);
        rows.add(exitRow);
        keyboard.setKeyboard(rows);

        sendMessageWithInlineKeyboard(chatId, text, keyboard);
    }

    private void sendOrderListForStatusChange(Long chatId, UserState userState) {
        try {
            Long masterId = userState.userId;
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();

            List<Orders> masterOrders = new ArrayList<>();
            for (Orders order : allOrders) {
                if (order.getProductionMasterID().getIdUser() != null && order.getProductionMasterID().getIdUser().getId().longValue() == masterId) {
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
            mainController.changeOrderStatus(orderId.intValue(), newStatus.replace("_", " "));
            userState.selectedOrderId = null;
            sendMessage(chatId, "✅ Статус заказа №" + orderId + " изменен на: " + newStatus.replace("_", " "));
            sendMainMenu(chatId, userState);
        } catch (Exception e) {
            System.err.println("Error changing order status: " + e.getMessage());
            sendMessage(chatId, "❌ Ошибка при изменении статуса заказа.");
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

    private String getMyOrdersResponse(UserState userState) {
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
                return "📦 У вас пока нет заказов.";
            }

            StringBuilder response = new StringBuilder("📦 Ваши заказы:\n\n");
            for (Orders order : userOrders) {
                response.append(String.format("🆔 Номер заказа: %d\n" +
                                "📅 Дата заказа: %s\n" +
                                "💰 Сумма: %.2f\n" +
                                "📊 Статус: %s\n" +
                                "⏰ Срок выполнения: %s\n" +
                                "✅ Дата завершения: %s\n" +
                                "📝 Примечания: %s\n\n",
                        order.getId(),
                        order.getOrderDate() != null ? order.getOrderDate() : "Не указана",
                        order.getTotalAmount() != null ? order.getTotalAmount() : 0.0,
                        order.getStatus() != null ? order.getStatus() : "Не указан",
                        order.getDueDate() != null ? order.getDueDate() : "Не указан",
                        order.getCompletionDate() != null ? order.getCompletionDate() : "Не завершён",
                        order.getNotes() != null ? order.getNotes() : "Нет"));
            }

            return response.toString();
        } catch (Exception e) {
            System.err.println("Error in getMyOrdersResponse: " + e.getMessage());
            return "❌ Ошибка при получении заказов. Попробуйте позже.";
        }
    }

    private String getProductionMasterOrdersResponse(UserState userState) {
        try {
            Long masterId = userState.userId;
            List<Orders> allOrders = (List<Orders>) mainController.allOrders();

            List<Orders> masterOrders = new ArrayList<>();
            for (Orders order : allOrders) {
                if (order.getProductionMasterID().getIdUser() != null && order.getProductionMasterID().getIdUser().getId().longValue() == masterId) {
                    masterOrders.add(order);
                }
            }

            if (masterOrders.isEmpty()) {
                return "📋 У вас пока нет назначенных заказов.";
            }

            StringBuilder response = new StringBuilder("📋 Заказы под вашим контролем:\n\n");
            for (Orders order : masterOrders) {
                response.append(String.format("🆔 Номер заказа: %d\n" +
                                "📅 Дата заказа: %s\n" +
                                "💰 Сумма: %.2f\n" +
                                "📊 Статус: %s\n" +
                                "⏰ Срок выполнения: %s\n" +
                                "✅ Дата завершения: %s\n" +
                                "📝 Примечания: %s\n\n",
                        order.getId(),
                        order.getOrderDate() != null ? order.getOrderDate() : "Не указана",
                        order.getTotalAmount() != null ? order.getTotalAmount() : 0.0,
                        order.getStatus() != null ? order.getStatus() : "Не указан",
                        order.getDueDate() != null ? order.getDueDate() : "Не указан",
                        order.getCompletionDate() != null ? order.getCompletionDate() : "Не завершён",
                        order.getNotes() != null ? order.getNotes() : "Нет"));
            }

            return response.toString();
        } catch (Exception e) {
            System.err.println("Error in getProductionMasterOrdersResponse: " + e.getMessage());
            return "❌ Ошибка при получении заказов. Попробуйте позже.";
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
                "Для начала работы выберите действия:";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton authButton = new InlineKeyboardButton();
        authButton.setText("🔐 Авторизация");
        authButton.setCallbackData("auth");
        row.add(authButton);
        rows.add(row);
        keyboard.setKeyboard(rows);

        sendMessageWithInlineKeyboard(chatId, welcome, keyboard);
    }

    private void startAuthorization(Long chatId, UserState userState) {
        userState.state = "WAITING_LOGIN";
        userState.login = null;
        userState.userRole = null;
        userState.userId = null;
        userState.fullName = null;
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

    private void logout(Long chatId, UserState userState) {
        if ("AUTHENTICATED".equals(userState.state)) {
            String fullName = userState.fullName;
            userState.state = "START";
            userState.login = null;
            userState.userRole = null;
            userState.userId = null;
            userState.fullName = null;
            String text = "\uD83D\uDC4B До новых встреч, " + fullName + "!";
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton authButton = new InlineKeyboardButton();
            authButton.setText("🔐 Авторизоваться");
            authButton.setCallbackData("auth");
            row.add(authButton);
            rows.add(row);
            keyboard.setKeyboard(rows);

            sendMessageWithInlineKeyboard(chatId, text, keyboard);
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

            switch (userState.userRole) {
                case "МАСТЕР ПРОИЗВОДСТВА":
                    help += "🎯 Команды для мастера производства:\n" +
                            "• Просмотр заказов (кнопка после авторизации)\n" +
                            "• Управление материалами\n" +
                            "• Изменение статусов заказов\n";
                    break;
                case "ПОКУПАТЕЛЬ":
                    help += "🎯 Команды для покупателя:\n" +
                            "• Проверка скидки\n" +
                            "• Статус заказа\n" +
                            "• Заказ рамок\n" +
                            "• 📋 Личные данные (кнопка после авторизации)\n" +
                            "• 📦 Мои заказы (кнопка после авторизации)";
                    break;
            }
            help += "\n\nПосле авторизации появится кнопка для просмотра личных данных.";
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

    private void sendMessageWithInlineKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
            System.out.println("Message with inline keyboard sent to chat: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message with keyboard to " + chatId + ": " + e.getMessage());
        }
    }
}
