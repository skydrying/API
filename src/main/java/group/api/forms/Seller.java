package group.api.forms;

import group.api.entity.Customer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Vector;

import okhttp3.*;
import org.eclipse.persistence.exceptions.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Seller extends javax.swing.JFrame {

    public Seller() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Панель Продавца");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/group/icon.png")));
        allCustomers();
        allEmbroiderykit();
        allСonsumable();
        allOrders();
        allCustomFrameOrder();
        loadCustomersToComboBox();
        loadFrameMaterialsToComboBox();
        allSale();
        loadConsumblesToComboBox();
        loadEmbroiderykitToComboBox();
    }

    private File selectedFile;

    private void loadCustomersToComboBox() {
        try {
            jComboBox5.removeAllItems();
            jComboBox5.addItem("Выберите клиента");
            jComboBox9.removeAllItems();
            jComboBox9.addItem("Выберите клиента");
            jComboBox10.removeAllItems();
            jComboBox10.addItem("Выберите клиента");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:9090/api/getCustomers"; 

            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject customer = jsonArray.getJSONObject(i);
                String lastName = customer.getString("lastName");
                String firstName = customer.getString("firstName");
                String middleName = customer.getString("middleName");

                String displayText = lastName + " " + firstName + " " + middleName;
                jComboBox5.addItem(displayText);
                jComboBox9.addItem(displayText);
                jComboBox10.addItem(displayText);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка загрузки клиентов: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFrameMaterialsToComboBox() {
        try {
            jComboBox6.removeAllItems();
            jComboBox6.addItem("Выберите материал");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:9090/api/getFrameMaterial";

            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject frameMaterial = jsonArray.getJSONObject(i);
                String name = frameMaterial.getString("name");
                jComboBox6.addItem(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка загрузки материалов рамки: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadConsumblesToComboBox() {
        try {
            jComboBox7.removeAllItems();
            jComboBox7.addItem("Выберите материал");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:9090/api/getConsumables";

            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject frameMaterial = jsonArray.getJSONObject(i);
                String name = frameMaterial.getString("name");
                jComboBox7.addItem(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка загрузки материалов рамки: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
       private void loadEmbroiderykitToComboBox() {
        try {
            jComboBox8.removeAllItems();
            jComboBox8.addItem("Выберите вышивку");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:9090/api/getEmbroiderykit";

            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject frameMaterial = jsonArray.getJSONObject(i);
                String name = frameMaterial.getString("name");
                jComboBox8.addItem(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка загрузки материалов рамки: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   
    
    public void allEmbroiderykit() {
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Название");
        tableHeaders.add("Примечание");
        tableHeaders.add("Стоимость");
        tableHeaders.add("Количество");
        tableHeaders.add("Фотография");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getEmbroiderykit")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));
                oneRow.add(jo.getString("name"));
                oneRow.add(jo.getString("description"));
                oneRow.add(jo.getString("price"));
                oneRow.add(jo.getString("stockQuantity"));
                oneRow.add(jo.getString("image"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }

    public void allСonsumable() {
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Название");
        tableHeaders.add("Примечание");
        tableHeaders.add("Стоимость");
        tableHeaders.add("Количество");
        tableHeaders.add("Единица измерения");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getConsumables")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));
                oneRow.add(jo.getString("name"));
                oneRow.add(jo.getString("description"));
                oneRow.add(jo.getString("price"));
                oneRow.add(jo.getString("stockQuantity"));
                oneRow.add(jo.getString("unit"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable2.setModel(new DefaultTableModel(tableData, tableHeaders));
    }

    public void allCustomers() {
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Фамилия");
        tableHeaders.add("Имя");
        tableHeaders.add("Отчество");
        tableHeaders.add("Телефон");
        tableHeaders.add("Почта");
        tableHeaders.add("Скидка");
        tableHeaders.add("Сумма покупок");
        tableHeaders.add("Логин");
        tableHeaders.add("Пароль");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getCustomers")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));
                oneRow.add(jo.getString("lastName"));
                oneRow.add(jo.getString("firstName"));
                oneRow.add(jo.getString("middleName"));
                oneRow.add(jo.getString("phone"));
                oneRow.add(jo.getString("email"));
                oneRow.add(jo.getString("discount"));
                oneRow.add(jo.getString("totalPurchases"));
                oneRow.add(jo.getString("logins"));
                oneRow.add(jo.getString("passwords"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable3.setModel(new DefaultTableModel(tableData, tableHeaders));
    }

    
    public void allSale() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Дата продажи");
        tableHeaders.add("Общая сумма");
        tableHeaders.add("Скидка");
        tableHeaders.add("Итоговая сумма");
        tableHeaders.add("Клиент");
        tableHeaders.add("Продавец");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getSales")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));

                String saleDateStr = jo.getString("saleDate");
                try {
                    LocalDateTime saleDate = LocalDateTime.parse(saleDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    String formattedDate = saleDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    oneRow.add(formattedDate);
                } catch (Exception e) {
                    oneRow.add(saleDateStr);
                }

                oneRow.add(jo.getInt("totalAmount"));
                oneRow.add(jo.getInt("discountAmount"));
                oneRow.add(jo.getInt("finalAmount"));

                JSONObject customer = jo.getJSONObject("customerID");
                oneRow.add(customer.getString("lastName") + " " + customer.getString("firstName"));

                JSONObject seller = jo.getJSONObject("sellerID");
                oneRow.add(seller.getString("lastName") + " " + seller.getString("firstName"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable4.setModel(new DefaultTableModel(tableData, tableHeaders));
        jTable5.setModel(new DefaultTableModel(tableData, tableHeaders));
    }
    
    
    public void allCustomFrameOrder() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Ширина");
        tableHeaders.add("Высота");
        tableHeaders.add("Цвет");
        tableHeaders.add("Стиль");
        tableHeaders.add("Тип крепления");
        tableHeaders.add("Тип стекла");
        tableHeaders.add("Примечания");
        tableHeaders.add("Расход материала (план)");
        tableHeaders.add("Расход материала (факт)");
        tableHeaders.add("Материал рамы");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getCustomFrameOrder")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONArray ordersArray = new JSONArray(response.body().string());

                for (int i = 0; i < ordersArray.length(); i++) {
                    Vector<Object> oneRow = new Vector<>();
                    JSONObject order = ordersArray.getJSONObject(i);

                    oneRow.add(order.getInt("id"));
                    oneRow.add(order.getInt("width"));
                    oneRow.add(order.getInt("height"));
                    oneRow.add(order.getString("color"));
                    oneRow.add(order.getString("style"));
                    oneRow.add(order.getString("mountType"));
                    oneRow.add(order.getString("glassType"));

                    if (order.has("notes") && !order.isNull("notes")) {
                        oneRow.add(order.getString("notes"));
                    } else {
                        oneRow.add("");
                    }

                    if (order.has("estimatedMaterialUsage") && !order.isNull("estimatedMaterialUsage")) {
                        Object estimatedUsage = order.get("estimatedMaterialUsage");
                        if (estimatedUsage instanceof Number) {
                            oneRow.add(order.getBigDecimal("estimatedMaterialUsage"));
                        } else {
                            oneRow.add("Не указан");
                        }
                    } else {
                        oneRow.add("Не указан");
                    }

                    if (order.has("actualMaterialUsage") && !order.isNull("actualMaterialUsage")) {
                        Object actualUsage = order.get("actualMaterialUsage");
                        if (actualUsage instanceof Number) {
                            oneRow.add(order.getBigDecimal("actualMaterialUsage"));
                        } else {
                            oneRow.add("Не указан");
                        }
                    } else {
                        oneRow.add("Не указан");
                    }

                    if (order.has("frameMaterialID") && !order.isNull("frameMaterialID")) {
                        JSONObject frameMaterial = order.getJSONObject("frameMaterialID");
                        oneRow.add(frameMaterial.getString("name"));
                    } else {
                        oneRow.add("Не указан");
                    }

                    tableData.add(oneRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jTable6.setModel(new DefaultTableModel(tableData, tableHeaders));
    }

    
    public void allOrders() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Дата заказа");
        tableHeaders.add("Сумма");
        tableHeaders.add("Статус");
        tableHeaders.add("Срок выполнения");
        tableHeaders.add("Дата завершения");
        tableHeaders.add("Примечания");
        tableHeaders.add("Клиент");
        tableHeaders.add("Мастер");
        tableHeaders.add("Продавец");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getOrdersSimple")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));

                if (jo.has("orderDate") && !jo.isNull("orderDate")) {
                    oneRow.add(jo.getString("orderDate"));
                } else {
                    oneRow.add("Не указана");
                }

                if (jo.has("totalAmount") && !jo.isNull("totalAmount")) {
                    oneRow.add(jo.getInt("totalAmount"));
                } else {
                    oneRow.add(0);
                }

                if (jo.has("status") && !jo.isNull("status")) {
                    oneRow.add(jo.getString("status"));
                } else {
                    oneRow.add("Не указан");
                }

                if (jo.has("dueDate") && !jo.isNull("dueDate")) {
                    oneRow.add(jo.getString("dueDate"));
                } else {
                    oneRow.add("Не указан");
                }

                if (jo.has("completionDate") && !jo.isNull("completionDate")) {
                    oneRow.add(jo.getString("completionDate"));
                } else {
                    oneRow.add("Не завершен");
                }

                if (jo.has("notes") && !jo.isNull("notes")) {
                    oneRow.add(jo.getString("notes"));
                } else {
                    oneRow.add("");
                }

                if (jo.has("customer") && !jo.isNull("customer")) {
                    JSONObject customer = jo.getJSONObject("customer");
                    String lastName = customer.has("lastName") && !customer.isNull("lastName") ? customer.getString("lastName") : "";
                    String firstName = customer.has("firstName") && !customer.isNull("firstName") ? customer.getString("firstName") : "";
                    oneRow.add(lastName + " " + firstName);
                } else {
                    oneRow.add("Не указан");
                }

                if (jo.has("master") && !jo.isNull("master")) {
                    JSONObject master = jo.getJSONObject("master");
                    String lastName = master.has("lastName") && !master.isNull("lastName") ? master.getString("lastName") : "";
                    String firstName = master.has("firstName") && !master.isNull("firstName") ? master.getString("firstName") : "";
                    oneRow.add(lastName + " " + firstName);
                } else {
                    oneRow.add("Не назначен");
                }

                if (jo.has("seller") && !jo.isNull("seller")) {
                    JSONObject seller = jo.getJSONObject("seller");
                    String lastName = seller.has("lastName") && !seller.isNull("lastName") ? seller.getString("lastName") : "";
                    String firstName = seller.has("firstName") && !seller.isNull("firstName") ? seller.getString("firstName") : "";
                    oneRow.add(lastName + " " + firstName);
                } else {
                    oneRow.add("Не указан");
                }

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable7.setModel(new DefaultTableModel(tableData, tableHeaders));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField8 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jTextField12 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTextField19 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton21 = new javax.swing.JButton();
        jComboBox7 = new javax.swing.JComboBox<>();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel52 = new javax.swing.JLabel();
        jButton28 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jButton26 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jComboBox8 = new javax.swing.JComboBox<>();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        jButton29 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jTextField31 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jTextField34 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jButton27 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jButton36 = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton37 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jLabel4.setText("Название");

        jLabel5.setText("Примечание");

        jLabel9.setText("Стоимость");

        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });

        jLabel15.setText("Количество");

        jButton5.setText("Добавить");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setText("Изменить");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Удалить");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Обновить таблицу");

        jButton10.setText("Выход");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton16.setText("Выбрать файл");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel10.setText("Фотография");

        jLabel11.setText("Выбранная фотография");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(26, 26, 26)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton16))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addGap(106, 106, 106)))
                .addGap(107, 107, 107)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(270, 270, 270))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10)
                                            .addComponent(jButton16))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addGap(66, 66, 66)
                                .addComponent(jButton10))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 21, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Вышивка", jPanel1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);

        jButton11.setText("Добавить");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Изменить");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Удалить");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Обновить таблицу");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Выход");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jLabel13.setText("Название");

        jLabel16.setText("Примечание");

        jLabel17.setText("Единица измерения");

        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });

        jLabel18.setText("Стоимость");

        jLabel19.setText("Количество");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton15)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(26, 26, 26)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(34, 34, 34)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel18)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel19)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(93, 93, 93)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(635, 635, 635)
                        .addComponent(jButton14)))
                .addContainerGap(576, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jButton14)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 86, Short.MAX_VALUE)
                        .addComponent(jButton15))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)
                                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab(" Расходные материалы", jPanel2);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable3);

        jLabel1.setText("Фамилия");

        jLabel2.setText("Имя");

        jLabel3.setText("Отчество");

        jLabel14.setText("Телефон");

        jLabel8.setText("Почта");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel6.setText("Скидка");

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel7.setText("Сумма покупок");

        jButton2.setText("Добавить");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Изменить");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Удалить");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setText("Обновить таблицу");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton1.setText("Выход");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField19ActionPerformed(evt);
            }
        });

        jLabel25.setText("Логин");

        jLabel26.setText("Пароль");

        jTextField20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(596, 596, 596)
                        .addComponent(jButton6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel3)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                                .addComponent(jTextField3))))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addGap(106, 106, 106)))
                                .addGap(38, 38, 38)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(3, 3, 3)
                                                .addComponent(jLabel8)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                            .addComponent(jTextField4)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(51, 51, 51)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel25)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(51, 51, 51)
                                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel26)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(51, 51, 51)
                                            .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(106, 106, 106)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(576, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)))))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jButton6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Покупатели", jPanel3);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable4);

        jButton21.setText("Выход");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jLabel47.setText("Расход. материал");

        jLabel48.setText("Количество");

        jLabel20.setText("Стоимость:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        jLabel52.setText("Клиент");

        jButton28.setText("Оформить");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton21))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(208, 208, 208)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel47)
                                        .addGap(18, 18, 18)
                                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel48)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(131, 131, 131)
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(41, 41, 41)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48)
                            .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap(9, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20)
                                .addGap(11, 11, 11))
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addGap(18, 18, 18)
                .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jButton21)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Продажа расход. материалов", jPanel4);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable5MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable5);

        jButton26.setText("Выход");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        jLabel23.setText("Стоимость:");

        jLabel50.setText("Вышивка");

        jLabel51.setText("Количество");

        jLabel53.setText("Клиент");

        jButton29.setText("Оформить");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton26)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(121, 121, 121)
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(878, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel51)
                                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(20, 20, 20)))
                        .addGap(21, 21, 21))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addGap(18, 18, 18)
                .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton26)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Продажа вышивки", jPanel5);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable6MouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(jTable6);

        jLabel35.setText("Ширина");

        jLabel36.setText("Высота");

        jLabel37.setText("Цвет");

        jLabel41.setText("Стиль");

        jButton27.setText("Оформить");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton31.setText("Выход");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jLabel42.setText("Тип крепления");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Классический", "Модерн", "Винтаж", "Минимализм", "Кантри" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Подвесное", "Настольное", "Напольное" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Стандартное", "Антибликовое", "Оргстекло", "Без стекла" }));

        jLabel43.setText("Тип стекла");

        jLabel44.setText("Примечания");

        jLabel45.setText("Клиент");

        jLabel46.setText("Материал");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap(21, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton31)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addGap(70, 70, 70)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel43)
                    .addComponent(jLabel42)
                    .addComponent(jLabel44))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(703, 703, 703))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jLabel41)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel42)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44)
                            .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jButton31)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Заявка на изготовление", jPanel6);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable7MouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(jTable7);

        jButton36.setText("Выход");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jLabel49.setText("Статус");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Новый", "Выполняется", "Готов", "Забран", "Отменен" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButton37.setText("Изменить");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton36))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addGap(18, 18, 18)
                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addComponent(jButton36)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Статус", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(704, Short.MAX_VALUE)
                    .addComponent(jLabel24)
                    .addContainerGap(705, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(368, Short.MAX_VALUE)
                    .addComponent(jLabel24)
                    .addContainerGap(367, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        allCustomers();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) jTable3.getValueAt(selectedRow, 0);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
            .url("http://localhost:9090/api/deleteCustomer")
            .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + userId))
            .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Покупатель успешно удален!");
                        allCustomers();
                    } else {
                        System.out.println("Ошибка при удалении покупателя: " + response.message());
                    }
                }
            });
        } else {
            System.out.println("Пожалуйста, выберите покупателя для удаления!");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("Выберите пользователя для обновления данных");
            return;
        }

        Integer customerId = (Integer) jTable3.getValueAt(selectedRow, 0);

        String lastname = jTextField1.getText();
        String firstname = jTextField2.getText();
        String middlename = jTextField3.getText();
        String phone = jTextField4.getText();
        String email = jTextField5.getText();
        String discount = jTextField6.getText();
        String totalPurchases = jTextField7.getText();
        String logins = jTextField19.getText();
        String passwords = jTextField20.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(customerId))
                .addFormDataPart("LastName", lastname)
                .addFormDataPart("FirstName", firstname)
                .addFormDataPart("MiddleName", middlename)
                .addFormDataPart("Phone", phone)
                .addFormDataPart("Email", email)
                .addFormDataPart("Discount", discount)
                .addFormDataPart("TotalPurchases", totalPurchases)
                .addFormDataPart("Logins", logins)
                .addFormDataPart("Passwords", passwords);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
        .url("http://localhost:9090/api/updateCustomer")
        .post(requestBody)
        .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        int customerId = Integer.parseInt(responseData);

                        System.out.println("Пользователь успешно обновлен! ID: " + customerId);
                        allCustomers();
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка при парсинге ID: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("Ошибка ввода/вывода: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Ошибка при обновлении пользователя: " + response.message());
                }
            }

        });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        OkHttpClient client = new OkHttpClient();

        String lastname = jTextField1.getText();
        String firstname = jTextField2.getText();
        String middlename = jTextField3.getText();
        String phone = jTextField4.getText();
        String email = jTextField5.getText();
        String discount = jTextField6.getText();
        String totalPurchases = jTextField7.getText();
        String logins = jTextField19.getText();
        String passwords = jTextField20.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("LastName", lastname)
        .addFormDataPart("FirstName", firstname)
        .addFormDataPart("MiddleName", middlename)
        .addFormDataPart("Phone", phone)
        .addFormDataPart("Email", email)
        .addFormDataPart("Discount", discount)
        .addFormDataPart("TotalPurchases", totalPurchases)
        .addFormDataPart("Logins", logins)
        .addFormDataPart("Passwords", passwords);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
        .url("http://localhost:9090/api/addCustomer")
        .post(requestBody)
        .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("id", responseData);

                        int userId = jsonResponse.getInt("id");

                        System.out.println("Пользователь успешно добавлен! ID: " + userId);
                        allCustomers();
                    } catch (JSONException e) {
                        System.err.println("Ошибка при парсинге JSON: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("Ошибка ввода/вывода: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Ошибка при добавлении пользователя: " + response.message());
                }
            }

        });
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow != -1) {

            jTextField1.setText((String) jTable3.getValueAt(selectedRow, 1));
            jTextField2.setText((String) jTable3.getValueAt(selectedRow, 2));
            jTextField3.setText((String) jTable3.getValueAt(selectedRow, 3));
            jTextField4.setText((String) jTable3.getValueAt(selectedRow, 4));
            jTextField5.setText((String) jTable3.getValueAt(selectedRow, 5));
            jTextField6.setText((String) jTable3.getValueAt(selectedRow, 6));
            jTextField7.setText((String) jTable3.getValueAt(selectedRow, 7));
            jTextField19.setText((String) jTable3.getValueAt(selectedRow, 8));
            jTextField20.setText((String) jTable3.getValueAt(selectedRow, 9));
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            int consumableId = (int) jTable2.getValueAt(selectedRow, 0);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/deleteConsumable")
                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + consumableId))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Фурнитура успешно удалена!");
                        allСonsumable();
                    } else {
                        System.out.println("Ошибка при удалении фурнитуры: " + response.message());
                    }
                }
            });
        } else {
            System.out.println("Пожалуйста, выберите набор вышивки для удаления");
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("Выберите набор вышивки для обновления данных");
            return;
        }

        Integer consumableId = (Integer) jTable2.getValueAt(selectedRow, 0);

        String name = jTextField12.getText();
        String description = jTextField13.getText();
        String price = jTextField14.getText();
        String stockQuantity = jTextField15.getText();
        String unit = jTextField16.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(consumableId))
                .addFormDataPart("Name", name)
                .addFormDataPart("Description", description)
                .addFormDataPart("Price", price)
                .addFormDataPart("StockQuantity", stockQuantity)
                .addFormDataPart("Unit", unit);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/updateConsumable")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Фурнитура успешно обновлена! ID: " + responseData);
                    allСonsumable();
                } else {
                    System.out.println("Ошибка при обновлении фурнитуры: " + response.message());
                }
            }
        });
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        OkHttpClient client = new OkHttpClient();

        String name = jTextField12.getText();
        String description = jTextField13.getText();
        String price = jTextField14.getText();
        String stockQuantity = jTextField15.getText();
        String unit = jTextField16.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Name", name)
                .addFormDataPart("Description", description)
                .addFormDataPart("Price", price)
                .addFormDataPart("StockQuantity", stockQuantity)
                .addFormDataPart("Unit", unit);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/addConsumable")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Фурнитура успешно добавлена! Ответ сервера: " + responseData);
                    allСonsumable();
                } else {
                    System.out.println("Ошибка при добавлении фурнитуры: " + response.message());
                }
            }
        });
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {

            jTextField12.setText((String) jTable2.getValueAt(selectedRow, 1));
            jTextField13.setText((String) jTable2.getValueAt(selectedRow, 2));
            jTextField14.setText((String) jTable2.getValueAt(selectedRow, 3));
            jTextField15.setText((String) jTable2.getValueAt(selectedRow, 4));
            jTextField16.setText((String) jTable2.getValueAt(selectedRow, 5));

        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, "Выбрать файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getPath());
            jLabel12.setIcon(icon);
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            int embroideryKitId = (int) jTable1.getValueAt(selectedRow, 0);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
            .url("http://localhost:9090/api/deleteEmbroideryKit")
            .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "id=" + embroideryKitId))
            .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Набор вышивки успешно удален!");
                        allEmbroiderykit();
                    } else {
                        System.out.println("Ошибка при удалении набора вышивки: " + response.message());
                    }
                }
            });
        } else {
            System.out.println("Пожалуйста, выберите набор вышивки для удаления");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("Выберите набор вышивки для обновления данных");
            return;
        }

        Integer embroideryKitId = (Integer) jTable1.getValueAt(selectedRow, 0);

        String name = jTextField8.getText();
        String description = jTextField9.getText();
        String price = jTextField10.getText();
        String stockQuantity = jTextField11.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("id", String.valueOf(embroideryKitId))
        .addFormDataPart("Name", name)
        .addFormDataPart("Description", description)
        .addFormDataPart("Price", price)
        .addFormDataPart("StockQuantity", stockQuantity);

        if (selectedFile != null) {
            builder.addFormDataPart("Image", selectedFile.getName(),
                RequestBody.create(selectedFile, MediaType.parse("image/png")));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
        .url("http://localhost:9090/api/updateEmbroideryKit")
        .post(requestBody)
        .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Набор вышивки успешно обновлён! ID: " + responseData);
                    allEmbroiderykit();
                } else {
                    System.out.println("Ошибка при обновлении набора вышивки: " + response.message());
                }
            }
        });
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        OkHttpClient client = new OkHttpClient();

        String name = jTextField8.getText();
        String description = jTextField9.getText();
        String price = jTextField10.getText();
        String stockQuantity = jTextField11.getText();

        MultipartBody.Builder builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("Name", name)
        .addFormDataPart("Description", description)
        .addFormDataPart("Price", price)
        .addFormDataPart("StockQuantity", stockQuantity);

        if (selectedFile != null) {
            builder.addFormDataPart("Image", selectedFile.getName(),
                RequestBody.create(selectedFile, MediaType.parse("image/png")));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
        .url("http://localhost:9090/api/addEmbroideryKit")
        .post(requestBody)
        .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Набор вышивки успешно добавлен! Ответ сервера: " + responseData);
                    allEmbroiderykit();
                } else {
                    System.out.println("Ошибка при добавлении набора вышивки: " + response.message());
                }
            }
        });
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {

            jTextField8.setText((String) jTable1.getValueAt(selectedRow, 1));
            jTextField9.setText((String) jTable1.getValueAt(selectedRow, 2));
            jTextField10.setText((String) jTable1.getValueAt(selectedRow, 3));
            jTextField11.setText((String) jTable1.getValueAt(selectedRow, 4));

            String imagePath = (String) jTable1.getValueAt(selectedRow, 5);
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imagePath);
                jLabel12.setIcon(icon);
            } else {
                jLabel12.setIcon(null);
            }

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable4MouseClicked

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jTable5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable5MouseClicked

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jTable6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable6MouseClicked

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        try {
            String selectedCustomer = (String) jComboBox5.getSelectedItem();
            if (selectedCustomer == null || selectedCustomer.equals("-- Выберите клиента --")) {
                JOptionPane.showMessageDialog(null, "Выберите клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer customer = null;
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://localhost:9090/api/getCustomers";
                String jsonResponse = restTemplate.getForObject(url, String.class);
                JSONArray jsonArray = new JSONArray(jsonResponse);

                String[] nameParts = selectedCustomer.trim().split("\\s+");
                if (nameParts.length < 3) {
                    JOptionPane.showMessageDialog(null, "Неверный формат ФИО клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String lastName = nameParts[0];
                String firstName = nameParts[1];
                String middleName = nameParts[2];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject customerJson = jsonArray.getJSONObject(i);
                    String jsonLastName = customerJson.getString("lastName");
                    String jsonFirstName = customerJson.getString("firstName");
                    String jsonMiddleName = customerJson.getString("middleName");

                    if (jsonLastName.equals(lastName) && jsonFirstName.equals(firstName) && jsonMiddleName.equals(middleName)) {
                        customer = new Customer();
                        customer.setId(customerJson.getInt("id"));
                        customer.setLastName(jsonLastName);
                        customer.setFirstName(jsonFirstName);
                        customer.setMiddleName(jsonMiddleName);
                        break;
                    }
                }

                if (customer == null) {
                    JOptionPane.showMessageDialog(null, "Клиент не найден на сервере!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ошибка получения данных клиента: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedFrameMaterialName = (String) jComboBox6.getSelectedItem();
            if (selectedFrameMaterialName == null || selectedFrameMaterialName.equals("-- Выберите материал рамки --")) {
                JOptionPane.showMessageDialog(null, "Выберите материал рамки!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer frameMaterialId = null;
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://localhost:9090/api/getFrameMaterial";
                String jsonResponse = restTemplate.getForObject(url, String.class);
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject frameMaterialJson = jsonArray.getJSONObject(i);
                    String jsonName = frameMaterialJson.getString("name");

                    if (jsonName.equals(selectedFrameMaterialName)) {
                        frameMaterialId = frameMaterialJson.getInt("id");
                        break;
                    }
                }

                if (frameMaterialId == null) {
                    JOptionPane.showMessageDialog(null, "Материал рамки не найден на сервере!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ошибка получения данных материала рамки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer sellerId = 5;

            String widthStr = jTextField31.getText().trim();
            String heightStr = jTextField32.getText().trim();
            String color = jTextField33.getText().trim();
            String style = (String) jComboBox1.getSelectedItem();
            String mountType = (String) jComboBox3.getSelectedItem();
            String glassType = (String) jComboBox4.getSelectedItem();
            String notes = jTextField34.getText().trim();

            if (widthStr.isEmpty() || heightStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Заполните ширину и высоту!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String orderDateStr = dateFormat.format(new Date());
            long twoWeeksInMillis = 14L * 24 * 60 * 60 * 1000;
            String dueDateStr = dateFormat.format(new Date(System.currentTimeMillis() + twoWeeksInMillis));

            MultiValueMap<String, String> orderParams = new LinkedMultiValueMap<>();
            orderParams.add("CustomerID", String.valueOf(customer.getId()));
            orderParams.add("SellerID", String.valueOf(sellerId));
            orderParams.add("OrderDate", orderDateStr);
            orderParams.add("TotalAmount", "0");
            orderParams.add("Status", "Новый");
            orderParams.add("DueDate", dueDateStr);
            orderParams.add("CompletionDate", dueDateStr);
            if (!notes.isEmpty()) {
                orderParams.add("Notes", notes);
            }

            System.out.println("Order Params: " + orderParams);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> orderEntity = new HttpEntity<>(orderParams, headers);
            ResponseEntity<Integer> orderResponse = restTemplate.postForEntity("http://localhost:9090/api/addOrder", orderEntity, Integer.class);

            if (orderResponse.getStatusCode() != HttpStatus.OK || orderResponse.getBody() == null) {
                JOptionPane.showMessageDialog(null, "Ошибка создания заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer orderId = orderResponse.getBody();

            MultiValueMap<String, String> customParams = new LinkedMultiValueMap<>();
            customParams.add("OrderID", String.valueOf(orderId));
            customParams.add("Width", widthStr);
            customParams.add("Height", heightStr);
            customParams.add("FrameMaterialID", String.valueOf(frameMaterialId));  // Изменено: передаем ID вместо названия
            customParams.add("Color", color);
            customParams.add("Style", style);
            customParams.add("MountType", mountType);
            customParams.add("GlassType", glassType);
            if (!notes.isEmpty()) {
                customParams.add("Notes", notes);
            }
            customParams.add("EstimatedMaterialUsage", "0");
            customParams.add("ActualMaterialUsage", "0");

            System.out.println("Custom Params: " + customParams);

            HttpEntity<MultiValueMap<String, String>> customEntity = new HttpEntity<>(customParams, headers);
            ResponseEntity<Integer> customResponse = restTemplate.postForEntity("http://localhost:9090/api/addCustomFrameOrder", customEntity, Integer.class);

            if (customResponse.getStatusCode() == HttpStatus.OK && customResponse.getBody() != null) {
                JOptionPane.showMessageDialog(null, "Заказ и CustomFrameOrder успешно созданы!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка создания CustomFrameOrder!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            allCustomFrameOrder();
            allOrders();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ширина и высота должны быть числами!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jTable7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable7MouseClicked
        try {
            int row = jTable7.getSelectedRow();
            if (row >= 0) {
                String status = jTable7.getValueAt(row, 3).toString();
                jComboBox2.setSelectedItem(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при выборе статуса: " + e.getMessage());
        }
    }//GEN-LAST:event_jTable7MouseClicked

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable7.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Выберите заказ для смены статуса");
            return;
        }

        Integer orderId = (Integer) jTable7.getValueAt(selectedRow, 0);
        String newStatus = (String) jComboBox2.getSelectedItem();

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9090/api/changeOrderStatusDesktop").newBuilder();
            urlBuilder.addQueryParameter("orderId", orderId.toString());
            urlBuilder.addQueryParameter("newStatus", newStatus);

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .post(RequestBody.create("", null))
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Статус заказа успешно обновлён!");
                allOrders();
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка при обновлении статуса: " + response.message());
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка соединения: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        try {
            String selectedCustomerName = (String) jComboBox9.getSelectedItem();
            if (selectedCustomerName == null || selectedCustomerName.equals("Выберите клиента")) {
                JOptionPane.showMessageDialog(null, "Выберите клиента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RestTemplate restTemplate = new RestTemplate();
            String customersUrl = "http://localhost:9090/api/getCustomers";
            String customersJsonResponse = restTemplate.getForObject(customersUrl, String.class);
            JSONArray customersArray = new JSONArray(customersJsonResponse);

            Integer customerId = null;
            Integer discount = 0;
            for (int i = 0; i < customersArray.length(); i++) {
                JSONObject customerJson = customersArray.getJSONObject(i);
                String lastName = customerJson.getString("lastName");
                String firstName = customerJson.getString("firstName");
                String middleName = customerJson.getString("middleName");
                String fullName = lastName + " " + firstName + " " + middleName;
                if (fullName.equals(selectedCustomerName)) {
                    customerId = customerJson.getInt("id");
                    String discountStr = customerJson.optString("discount", "0");
                    discount = Integer.parseInt(discountStr);
                    break;
                }
            }
            if (customerId == null) {
                JOptionPane.showMessageDialog(null, "Клиент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int sellerId = 5;

            LocalDate saleDate = LocalDate.now();
            long saleDateLong = saleDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

            String selectedConsumable = (String) jComboBox7.getSelectedItem();
            if (selectedConsumable == null || selectedConsumable.equals("Выберите расходный материал")) {
                JOptionPane.showMessageDialog(null, "Выберите расходный материал", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String consumablesUrl = "http://localhost:9090/api/getConsumables";
            String consumablesJsonResponse = restTemplate.getForObject(consumablesUrl, String.class);
            JSONArray consumablesArray = new JSONArray(consumablesJsonResponse);

            Integer price = null;
            Integer stockQuantity = null;
            Integer consumableId = null;
            String description = null;
            String unit = null;
            for (int i = 0; i < consumablesArray.length(); i++) {
                JSONObject consumableJson = consumablesArray.getJSONObject(i);
                String name = consumableJson.getString("name");
                if (name.equals(selectedConsumable)) {
                    consumableId = consumableJson.getInt("id");
                    price = consumableJson.getInt("price");
                    stockQuantity = consumableJson.getInt("stockQuantity");
                    description = consumableJson.optString("description", "");
                    unit = consumableJson.optString("unit", "");
                    break;
                }
            }
            if (price == null || stockQuantity == null) {
                JOptionPane.showMessageDialog(null, "Расходный материал не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(jTextField17.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Количество должно быть положительным числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Введите корректное количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (stockQuantity < quantity) {
                JOptionPane.showMessageDialog(null, "Недостаточно расходного материала на складе", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int totalAmount = quantity * price;

            int discountAmount = (totalAmount * discount) / 100;
            int finalAmount = totalAmount - discountAmount;

            jLabel21.setText(String.valueOf(finalAmount));

            String addSaleUrl = "http://localhost:9090/api/addSale";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("CustomerID", String.valueOf(customerId));
            params.add("SellerID", String.valueOf(sellerId));
            params.add("SaleDate", String.valueOf(saleDateLong));
            params.add("TotalAmount", String.valueOf(totalAmount));
            params.add("DiscountAmount", String.valueOf(discount));
            params.add("FinalAmount", String.valueOf(finalAmount));

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<Integer> response = restTemplate.exchange(addSaleUrl, HttpMethod.POST, entity, Integer.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Integer saleId = response.getBody();
                JOptionPane.showMessageDialog(null, "Продажа добавлена, ID: " + saleId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                String updateUrl = "http://localhost:9090/api/updateConsumable";
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

                JSONObject updateJson = new JSONObject();
                updateJson.put("id", consumableId);
                updateJson.put("name", selectedConsumable);
                updateJson.put("price", price);
                updateJson.put("stockQuantity", stockQuantity - quantity);
                updateJson.put("description", description);
                updateJson.put("unit", unit);

                HttpEntity<String> updateEntity = new HttpEntity<>(updateJson.toString(), headers);
                ResponseEntity<Void> updateResponse = restTemplate.exchange(updateUrl, HttpMethod.PUT, updateEntity, Void.class);

                if (updateResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("StockQuantity расходного материала обновлен");
                } else {
                    JOptionPane.showMessageDialog(null, "Ошибка обновления StockQuantity: " + updateResponse.getStatusCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                allSale();
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка добавления продажи: " + response.getStatusCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        try {
            String selectedCustomerName = (String) jComboBox10.getSelectedItem();
            if (selectedCustomerName == null || selectedCustomerName.equals("Выберите клиента")) {
                JOptionPane.showMessageDialog(null, "Выберите клиента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RestTemplate restTemplate = new RestTemplate();
            String customersUrl = "http://localhost:9090/api/getCustomers";
            String customersJsonResponse = restTemplate.getForObject(customersUrl, String.class);
            JSONArray customersArray = new JSONArray(customersJsonResponse);

            Integer customerId = null;
            Integer discount = 0;
            for (int i = 0; i < customersArray.length(); i++) {
                JSONObject customerJson = customersArray.getJSONObject(i);
                String lastName = customerJson.getString("lastName");
                String firstName = customerJson.getString("firstName");
                String middleName = customerJson.getString("middleName");
                String fullName = lastName + " " + firstName + " " + middleName;
                if (fullName.equals(selectedCustomerName)) {
                    customerId = customerJson.getInt("id");
                    String discountStr = customerJson.optString("discount", "0");
                    discount = Integer.parseInt(discountStr);
                    break;
                }
            }
            if (customerId == null) {
                JOptionPane.showMessageDialog(null, "Клиент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int sellerId = 5;

            LocalDate saleDate = LocalDate.now();
            long saleDateLong = saleDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

            String selectedKit = (String) jComboBox8.getSelectedItem();
            if (selectedKit == null || selectedKit.equals("Выберите набор для вышивания")) {
                JOptionPane.showMessageDialog(null, "Выберите набор для вышивания", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kitsUrl = "http://localhost:9090/api/getEmbroiderykit";
            String kitsJsonResponse = restTemplate.getForObject(kitsUrl, String.class);
            JSONArray kitsArray = new JSONArray(kitsJsonResponse);

            Integer price = null;
            Integer stockQuantity = null;
            Integer kitId = null;
            String description = null;
            String image = null;
            for (int i = 0; i < kitsArray.length(); i++) {
                JSONObject kitJson = kitsArray.getJSONObject(i);
                String name = kitJson.getString("name");
                if (name.equals(selectedKit)) {
                    kitId = kitJson.getInt("id");
                    price = kitJson.getInt("price");
                    stockQuantity = kitJson.getInt("stockQuantity");
                    description = kitJson.optString("description", "");
                    image = kitJson.optString("image", "");
                    break;
                }
            }
            if (price == null || stockQuantity == null) {
                JOptionPane.showMessageDialog(null, "Набор для вышивания не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(jTextField18.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Количество должно быть положительным числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Введите корректное количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (stockQuantity < quantity) {
                JOptionPane.showMessageDialog(null, "Недостаточно наборов для вышивания на складе", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int totalAmount = quantity * price;

            int discountAmount = (totalAmount * discount) / 100;
            int finalAmount = totalAmount - discountAmount;

            jLabel22.setText(String.valueOf(finalAmount));

            String addSaleUrl = "http://localhost:9090/api/addSale";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("CustomerID", String.valueOf(customerId));
            params.add("SellerID", String.valueOf(sellerId));
            params.add("SaleDate", String.valueOf(saleDateLong));
            params.add("TotalAmount", String.valueOf(totalAmount));
            params.add("DiscountAmount", String.valueOf(discount));
            params.add("FinalAmount", String.valueOf(finalAmount));

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<Integer> response = restTemplate.exchange(addSaleUrl, HttpMethod.POST, entity, Integer.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Integer saleId = response.getBody();
                JOptionPane.showMessageDialog(null, "Продажа добавлена, ID: " + saleId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                String updateUrl = "http://localhost:9090/api/updateEmbroideryKitSale";
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

                JSONObject updateJson = new JSONObject();
                updateJson.put("id", kitId);
                updateJson.put("name", selectedKit);
                updateJson.put("price", price);
                updateJson.put("stockQuantity", stockQuantity - quantity);
                updateJson.put("description", description);
                updateJson.put("image", image);

                HttpEntity<String> updateEntity = new HttpEntity<>(updateJson.toString(), headers);
                ResponseEntity<Void> updateResponse = restTemplate.exchange(updateUrl, HttpMethod.PUT, updateEntity, Void.class);

                if (updateResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("StockQuantity набора для вышивания обновлен");
                } else {
                    JOptionPane.showMessageDialog(null, "Ошибка обновления StockQuantity: " + updateResponse.getStatusCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                allSale();
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка добавления продажи: " + response.getStatusCode(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jTextField19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField19ActionPerformed

    private void jTextField20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField20ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Seller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Seller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Seller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Seller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Seller().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
