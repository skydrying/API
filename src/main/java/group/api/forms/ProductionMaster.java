package group.api.forms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.api.entity.Productionmaster;
import group.api.entity.User;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.eclipse.persistence.exceptions.JSONException;
import org.springframework.web.client.RestTemplate;

public class ProductionMaster extends javax.swing.JFrame {


    public ProductionMaster() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Панель Мастера");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/group/forms/images/icon.png")));
        allFrameMaterial();
        allFrameComponent();
        loadProductionMastersToComboBox();
        allOrders();
    }

    
    private void loadProductionMastersToComboBox() {
        try {
            jComboBox1.removeAllItems();
            jComboBox1.addItem("Выберите мастера");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:9090/api/getProductionmaster";

            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productionMaster = jsonArray.getJSONObject(i);

                String lastName = productionMaster.getString("lastName");
                String firstName = productionMaster.getString("firstName");
                String middleName = productionMaster.getString("middleName");
                String fullName = lastName + " " + firstName + " " + middleName;

                JSONArray productionMasterCollection = productionMaster.getJSONArray("productionmasterCollection");
                if (productionMasterCollection.length() > 0) {
                    JSONObject masterInfo = productionMasterCollection.getJSONObject(0);
                    int masterId = masterInfo.getInt("id");

                    jComboBox1.addItem(masterId + " - " + fullName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка загрузки мастеров: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void allCustomFrameOrder() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер рамки");
        tableHeaders.add("Номер заказа");
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
        tableHeaders.add("Мастер");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        String selectedMaster = (String) jComboBox1.getSelectedItem();
        if (selectedMaster == null || selectedMaster.equals("Выберите мастера")) {
            JOptionPane.showMessageDialog(this, "Выберите мастера!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] parts = selectedMaster.split(" - ");
        int selectedMasterId = Integer.parseInt(parts[0]);
        String selectedMasterName = parts[1]; // ФИО мастера

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getCustomFrameOrder")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONArray ordersArray = new JSONArray(response.body().string());

                for (int i = 0; i < ordersArray.length(); i++) {
                    Vector<Object> oneRow = new Vector<>();
                    JSONObject order = ordersArray.getJSONObject(i);

                    boolean isMasterMatch = false;
                    String orderMasterName = "Не назначен";
                    int orderId = 0;

                    if (order.has("orderID") && !order.isNull("orderID")) {
                        JSONObject orderInfo = order.getJSONObject("orderID");

                        orderId = orderInfo.getInt("id");

                        if (!orderInfo.isNull("productionMasterID")) {
                            JSONObject productionMaster = orderInfo.getJSONObject("productionMasterID");
                            int orderMasterId = productionMaster.getInt("id");

                            if (orderMasterId == selectedMasterId) {
                                isMasterMatch = true;
                                orderMasterName = selectedMasterName;
                            }
                        }
                    }

                    if (!isMasterMatch) {
                        continue;
                    }

                    oneRow.add(order.getInt("id"));

                    oneRow.add(orderId);

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
                            oneRow.add(" ");
                        }
                    } else {
                        oneRow.add(" ");
                    }

                    if (order.has("actualMaterialUsage") && !order.isNull("actualMaterialUsage")) {
                        Object actualUsage = order.get("actualMaterialUsage");
                        if (actualUsage instanceof Number) {
                            oneRow.add(order.getBigDecimal("actualMaterialUsage"));
                        } else {
                            oneRow.add(" ");
                        }
                    } else {
                        oneRow.add(" ");
                    }

                    if (order.has("frameMaterialID") && !order.isNull("frameMaterialID")) {
                        JSONObject frameMaterial = order.getJSONObject("frameMaterialID");
                        oneRow.add(frameMaterial.getString("name"));
                    } else {
                        oneRow.add("Не указан");
                    }

                    oneRow.add(orderMasterName);

                    tableData.add(oneRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка обработки данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
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

        jTable4.setModel(new DefaultTableModel(tableData, tableHeaders));
    }
    
    public void allFrameMaterial() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("ID");
        tableHeaders.add("Название");
        tableHeaders.add("Описание");
        tableHeaders.add("Стоимость за метр");
        tableHeaders.add("Количество на складе");
        tableHeaders.add("Цвет");
        tableHeaders.add("Ширина");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getFrameMaterial")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));
                oneRow.add(jo.getString("name"));
                oneRow.add(jo.getString("description"));
                oneRow.add(jo.getBigDecimal("pricePerMeter"));
                oneRow.add(jo.getInt("stockQuantity"));
                oneRow.add(jo.getString("color"));
                oneRow.add(jo.getInt("width"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable3.setModel(new DefaultTableModel(tableData, tableHeaders));
    }
    
    public void allFrameComponent() {
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("ID");
        tableHeaders.add("Название");
        tableHeaders.add("Описание");
        tableHeaders.add("Стоимость");
        tableHeaders.add("Количество на складе");
        tableHeaders.add("Тип");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getFrameComponent")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                Vector<Object> oneRow = new Vector<>();
                JSONObject jo = ja.getJSONObject(i);

                oneRow.add(jo.getInt("id"));
                oneRow.add(jo.getString("name"));
                oneRow.add(jo.getString("description"));
                oneRow.add(jo.getBigDecimal("price"));
                oneRow.add(jo.getInt("stockQuantity"));
                oneRow.add(jo.getString("type"));

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTable2.setModel(new DefaultTableModel(tableData, tableHeaders));
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
        jPanel2 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField12 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton36 = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton37 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jTextField6 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton15.setText("Выход");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel12.setText("Мастер");

        jButton5.setText("Сформировать");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

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
        jScrollPane5.setViewportView(jTable1);

        jLabel4.setText("Расход. материалов (план)");

        jLabel5.setText("Расход. материалов (факт)");

        jButton6.setText("Изменить");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton15)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton5))
                        .addGap(94, 94, 94)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)))
                .addContainerGap(794, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jButton6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addComponent(jButton15)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Просмотр своих заказов", jPanel2);

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
        jScrollPane7.setViewportView(jTable4);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addComponent(jButton36)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Статус", jPanel7);

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
        jScrollPane1.setViewportView(jTable2);

        jLabel1.setText("Название");

        jLabel2.setText("Описание");

        jLabel3.setText("Стоимость");

        jLabel14.setText("Тип");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel7.setText("Количество");

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

        jButton1.setText("Выход");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3))
                        .addGap(79, 79, 79)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField4)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(136, 136, 136)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
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
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Транзакции фурнитуры", jPanel3);

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
        jScrollPane4.setViewportView(jTable3);

        jLabel20.setText("Название");

        jLabel21.setText("Описание");

        jLabel22.setText("Стоимость");

        jLabel23.setText("Количество");

        jLabel24.setText("Цвет");

        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });

        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });

        jLabel25.setText("Ширина");

        jButton17.setText("Добавить");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Изменить");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Удалить");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton21.setText("Выход");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton21)
                        .addGap(255, 255, 255))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField8)
                                    .addComponent(jTextField7)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(81, 81, 81)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(jTextField10)
                    .addComponent(jTextField11))
                .addGap(153, 153, 153)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(305, 305, 305))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22))
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addComponent(jButton21)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Транзакции материалы", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            jTextField1.setText((String) jTable2.getValueAt(selectedRow, 1));
            jTextField2.setText((String) jTable2.getValueAt(selectedRow, 2));
            
            Object priceValue = jTable2.getValueAt(selectedRow, 3);
            if (priceValue != null) {
                jTextField3.setText(String.valueOf(priceValue));
            } else {
                jTextField3.setText("");
            }

            jTextField4.setText((String) jTable2.getValueAt(selectedRow, 5));

            Object quantityValue = jTable2.getValueAt(selectedRow, 4);
            if (quantityValue != null) {
                jTextField5.setText(String.valueOf(quantityValue));
            } else {
                jTextField5.setText("");
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            String name = jTextField1.getText().trim();
            String description = jTextField2.getText().trim();
            String price = jTextField3.getText().trim();
            String type = jTextField4.getText().trim();
            String stockQuantity = jTextField5.getText().trim();

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || type.isEmpty() || stockQuantity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(price);
                Integer.parseInt(stockQuantity);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Стоимость и количество должны быть числами!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("Name", name)
                    .addFormDataPart("Description", description)
                    .addFormDataPart("Price", price)
                    .addFormDataPart("Type", type)
                    .addFormDataPart("StockQuantity", stockQuantity)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/addFrameComponent")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Integer componentId = Integer.parseInt(responseBody);
                    JOptionPane.showMessageDialog(this, "Фурнитура успешно добавлена! ID: " + componentId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    jTextField1.setText("");
                    jTextField2.setText("");
                    jTextField3.setText("");
                    jTextField4.setText("");
                    jTextField5.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении фурнитуры: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameComponent();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            int selectedRow = jTable2.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите фурнитуру для редактирования!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = String.valueOf(jTable2.getValueAt(selectedRow, 0));
            String name = jTextField1.getText().trim();
            String description = jTextField2.getText().trim();
            String price = jTextField3.getText().trim();
            String type = jTextField4.getText().trim();
            String stockQuantity = jTextField5.getText().trim();

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || type.isEmpty() || stockQuantity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(price);
                Integer.parseInt(stockQuantity);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Стоимость и количество должны быть числами!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("Name", name)
                    .addFormDataPart("Description", description)
                    .addFormDataPart("Price", price)
                    .addFormDataPart("Type", type)
                    .addFormDataPart("StockQuantity", stockQuantity)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/updateFrameComponent")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Integer componentId = Integer.parseInt(responseBody);
                    JOptionPane.showMessageDialog(this, "Фурнитура успешно обновлена! ID: " + componentId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    jTextField1.setText("");
                    jTextField2.setText("");
                    jTextField3.setText("");
                    jTextField4.setText("");
                    jTextField5.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении фурнитуры: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameComponent();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            int selectedRow = jTable2.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите фурнитуру для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = String.valueOf(jTable2.getValueAt(selectedRow, 0));
            String name = (String) jTable2.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Вы уверены, что хотите удалить фурнитуру: " + name + "?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/deleteFrameComponent")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    boolean isDeleted = Boolean.parseBoolean(responseBody);

                    if (isDeleted) {
                        JOptionPane.showMessageDialog(this, "Фурнитура успешно удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);

                        jTextField1.setText("");
                        jTextField2.setText("");
                        jTextField3.setText("");
                        jTextField4.setText("");
                        jTextField5.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ошибка при удалении фурнитуры", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении фурнитуры: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameComponent();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }     
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow != -1) {
            jTextField6.setText((String) jTable3.getValueAt(selectedRow, 1));
            jTextField7.setText((String) jTable3.getValueAt(selectedRow, 2)); 

            Object priceValue = jTable3.getValueAt(selectedRow, 3);
            if (priceValue != null) {
                jTextField8.setText(String.valueOf(priceValue));
            } else {
                jTextField8.setText("");
            }

            Object quantityValue = jTable3.getValueAt(selectedRow, 4);
            if (quantityValue != null) {
                jTextField9.setText(String.valueOf(quantityValue));
            } else {
                jTextField9.setText("");
            }

            jTextField10.setText((String) jTable3.getValueAt(selectedRow, 5));

            Object widthValue = jTable3.getValueAt(selectedRow, 6);
            if (widthValue != null) {
                jTextField11.setText(String.valueOf(widthValue));
            } else {
                jTextField11.setText("");
            }
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            String name = jTextField6.getText().trim();
            String description = jTextField7.getText().trim();
            String pricePerMeter = jTextField8.getText().trim();
            String stockQuantity = jTextField9.getText().trim();
            String color = jTextField10.getText().trim();
            String width = jTextField11.getText().trim();

            if (name.isEmpty() || description.isEmpty() || pricePerMeter.isEmpty() || stockQuantity.isEmpty() || color.isEmpty() || width.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(pricePerMeter);
                Integer.parseInt(stockQuantity);
                Integer.parseInt(width);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Стоимость, количество и ширина должны быть числами!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("Name", name)
                    .addFormDataPart("Description", description)
                    .addFormDataPart("PricePerMeter", pricePerMeter)
                    .addFormDataPart("StockQuantity", stockQuantity)
                    .addFormDataPart("Color", color)
                    .addFormDataPart("Width", width)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/addFrameMaterial")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Integer materialId = Integer.parseInt(responseBody);
                    JOptionPane.showMessageDialog(this, "Материал рамы успешно добавлен! ID: " + materialId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    jTextField6.setText("");
                    jTextField7.setText("");
                    jTextField8.setText("");
                    jTextField9.setText("");
                    jTextField10.setText("");
                    jTextField11.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении материала рамы: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameMaterial();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        try {
            int selectedRow = jTable3.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите материал рамы для редактирования!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = String.valueOf(jTable3.getValueAt(selectedRow, 0));
            String name = jTextField6.getText().trim();
            String description = jTextField7.getText().trim();
            String pricePerMeter = jTextField8.getText().trim();
            String stockQuantity = jTextField9.getText().trim();
            String color = jTextField10.getText().trim();
            String width = jTextField11.getText().trim();

            if (name.isEmpty() || description.isEmpty() || pricePerMeter.isEmpty() || stockQuantity.isEmpty() || color.isEmpty() || width.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(pricePerMeter);
                Integer.parseInt(stockQuantity);
                Integer.parseInt(width);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Стоимость, количество и ширина должны быть числами!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("Name", name)
                    .addFormDataPart("Description", description)
                    .addFormDataPart("PricePerMeter", pricePerMeter)
                    .addFormDataPart("StockQuantity", stockQuantity)
                    .addFormDataPart("Color", color)
                    .addFormDataPart("Width", width)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/updateFrameMaterial")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Integer materialId = Integer.parseInt(responseBody);
                    JOptionPane.showMessageDialog(this, "Материал рамы успешно обновлен! ID: " + materialId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    jTextField6.setText("");
                    jTextField7.setText("");
                    jTextField8.setText("");
                    jTextField9.setText("");
                    jTextField10.setText("");
                    jTextField11.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении материала рамы: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameMaterial();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        try {
            int selectedRow = jTable3.getSelectedRow(); 
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите материал рамы для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = String.valueOf(jTable3.getValueAt(selectedRow, 0)); 
            String name = (String) jTable3.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Вы уверены, что хотите удалить материал рамы: " + name + "?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/deleteFrameMaterial") 
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    boolean isDeleted = Boolean.parseBoolean(responseBody);

                    if (isDeleted) {
                        JOptionPane.showMessageDialog(this, "Материал рамы успешно удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);

                        jTextField6.setText("");
                        jTextField7.setText("");
                        jTextField8.setText("");
                        jTextField9.setText("");
                        jTextField10.setText("");
                        jTextField11.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ошибка при удалении материала рамы", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении материала рамы: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allFrameMaterial();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        allCustomFrameOrder();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            Object estimatedUsage = jTable1.getValueAt(selectedRow, 8);
            if (estimatedUsage != null) {
                jTextField12.setText(String.valueOf(estimatedUsage));
            } else {
                jTextField12.setText("");
            }

            Object actualUsage = jTable1.getValueAt(selectedRow, 9);
            if (actualUsage != null) {
                jTextField13.setText(String.valueOf(actualUsage));
            } else {
                jTextField13.setText("");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        try {
            int row = jTable4.getSelectedRow();
            if (row >= 0) {
                String status = jTable4.getValueAt(row, 3).toString();
                jComboBox2.setSelectedItem(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при выборе статуса: " + e.getMessage());
        }
    }//GEN-LAST:event_jTable4MouseClicked

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable4.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Выберите заказ для смены статуса");
            return;
        }

        Integer orderId = (Integer) jTable4.getValueAt(selectedRow, 0);
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

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите заказ для обновления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = String.valueOf(jTable1.getValueAt(selectedRow, 0));
            String estimatedMaterialUsage = jTextField12.getText().trim();
            String actualMaterialUsage = jTextField13.getText().trim();

            if (estimatedMaterialUsage.isEmpty() && actualMaterialUsage.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните хотя бы одно поле расхода материала!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (!estimatedMaterialUsage.isEmpty()) {
                    new BigDecimal(estimatedMaterialUsage);
                }
                if (!actualMaterialUsage.isEmpty()) {
                    new BigDecimal(actualMaterialUsage);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Расход материала должен быть числом!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id);

            if (!estimatedMaterialUsage.isEmpty()) {
                requestBodyBuilder.addFormDataPart("EstimatedMaterialUsage", estimatedMaterialUsage);
            }

            if (!actualMaterialUsage.isEmpty()) {
                requestBodyBuilder.addFormDataPart("ActualMaterialUsage", actualMaterialUsage);
            }

            RequestBody requestBody = requestBodyBuilder.build();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/updateCustomFrameOrderMaterialUsage")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Integer orderId = Integer.parseInt(responseBody);
                    JOptionPane.showMessageDialog(this, "Расход материала успешно обновлен! ID заказа: " + orderId, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    jTextField12.setText("");
                    jTextField13.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении расхода материала: " + response.code(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
            allCustomFrameOrder();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка соединения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(ProductionMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductionMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductionMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductionMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProductionMaster().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
