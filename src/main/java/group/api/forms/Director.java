package group.api.forms;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.awt.Toolkit;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.persistence.exceptions.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.chart.renderer.category.BarRenderer;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JDialog;
import java.time.temporal.ChronoUnit;
import org.jfree.chart.axis.NumberAxis;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.jfree.data.general.DefaultPieDataset;

public class Director extends javax.swing.JFrame {

    private String selectedChartType = "Столбчатая";
    
    public Director() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Панель Директора");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/group/forms/images/icon.png")));
        DateForm();
    }

    public JDatePickerImpl datePickerNew;
    public JDatePickerImpl datePickerEnd;

    public void DateForm() {
        UtilDateModel modelNew = new UtilDateModel();
        modelNew.setSelected(true);
        Properties pNew = new Properties();
        pNew.put("text.today", "Today");
        pNew.put("text.month", "Month");
        pNew.put("text.year", "Year");
        JDatePanelImpl datePanelNew = new JDatePanelImpl(modelNew, pNew);
        datePickerNew = new JDatePickerImpl(datePanelNew, new DateLabelFormatter());
        datePickerNew.setBounds(5,5,120,30);
        jPanel1.add(datePickerNew);


        UtilDateModel modelEnd = new UtilDateModel();
        modelEnd.setSelected(true);
        Properties pEnd = new Properties();
        pEnd.put("text.today", "Today");
        pEnd.put("text.month", "Month");
        pEnd.put("text.year", "Year");
        JDatePanelImpl datePanelEmployment = new JDatePanelImpl(modelEnd, pEnd);
        datePickerEnd = new JDatePickerImpl(datePanelEmployment, new DateLabelFormatter());
        datePickerEnd.setBounds(5,5,120,30);
        jPanel2.add(datePickerEnd);

        jPanel1.revalidate();
        jPanel1.repaint();
        jPanel2.revalidate();
        jPanel2.repaint();
    }
    
    private String selectChartType() {
        String[] options = {"Столбчатая", "Круговая", "Линейная"};
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Выберите тип диаграммы:",
                "Тип диаграммы",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                selectedChartType
        );

        if (selected != null) {
            selectedChartType = selected;
        }
        return selected;
    }


    private JFreeChart createPieChartFromCategoryDataset(DefaultCategoryDataset dataset, String title, String seriesName) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        for (int i = 0; i < dataset.getRowCount(); i++) {
            Comparable rowKey = dataset.getRowKey(i);
            for (int j = 0; j < dataset.getColumnCount(); j++) {
                Comparable columnKey = dataset.getColumnKey(j);
                Number value = dataset.getValue(rowKey, columnKey);
                if (value != null && value.doubleValue() > 0) {
                    String key = columnKey.toString() + " (" + rowKey.toString() + ")";
                    pieDataset.setValue(key, value.doubleValue());
                }
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                pieDataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(true);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setNoDataMessage("Нет данных для отображения");
        plot.setCircular(true);
        plot.setLabelGap(0.02);

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(Color.GRAY);

        return chart;
    }

    private JFreeChart createLineChartFromCategoryDataset(DefaultCategoryDataset dataset, String title, String categoryAxisLabel, String valueAxisLabel) {
        JFreeChart chart = ChartFactory.createLineChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));

        return chart;
    }

    private void displayChart(JFreeChart chart, String dialogTitle) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setMouseZoomable(true);

        JDialog chartDialog = new JDialog(this, dialogTitle, true);
        chartDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        chartDialog.getContentPane().add(chartPanel);
        chartDialog.pack();
        chartDialog.setLocationRelativeTo(this);
        chartDialog.setVisible(true);
    }

    private void createOrdersChart(DefaultCategoryDataset dataset, String title) {
        JFreeChart chart;

        switch (selectedChartType) {
            case "Круговая":
                chart = createPieChartFromCategoryDataset(dataset, title, "Заказы");
                break;
            case "Линейная":
                chart = createLineChartFromCategoryDataset(dataset, title, "День месяца", "Количество заказов");
                break;
            default:
                chart = ChartFactory.createBarChart(
                        title,
                        "День месяца",
                        "Количество заказов",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );
                customizeChart(chart);
        }

        displayChart(chart, "Диаграмма заказов - " + selectedChartType);
    }


    private void createSalesChart(DefaultCategoryDataset dataset, String title) {
        JFreeChart chart;

        switch (selectedChartType) {
            case "Круговая":
                chart = createPieChartFromCategoryDataset(dataset, title, "Продажи");
                break;
            case "Линейная":
                chart = createLineChartFromCategoryDataset(dataset, title, "День месяца", "Сумма продаж (руб.)");
                break;
            default:
                chart = ChartFactory.createBarChart(
                        title,
                        "День месяца",
                        "Сумма продаж (руб.)",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );
                customizeSalesChart(chart);
        }

        displayChart(chart, "Диаграмма продаж - " + selectedChartType);
    }

    private void customizeChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(65, 105, 225));
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, Color.DARK_GRAY);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void customizeSalesChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(34, 139, 34));
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, Color.DARK_GRAY);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("ru", "RU")));

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
    }

  
    private String getRussianMonthName(int month) {
        String[] months = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };
        return months[month - 1];
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Выход");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("Сформировать");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("Просмотр отчета о заказах за текущий месяц");

        jLabel2.setText("Просмотр отчета о заказах за указанный временной период");

        jLabel3.setText("Просмотр отчета о продажах за текущий месяц");

        jLabel4.setText("Просмотр отчета о продажах за указанный временной период");

        jButton3.setText("Сформировать");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Сформировать");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Сформировать");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 124, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );

        jLabel5.setText("Начальная дата");

        jLabel6.setText("Конечная дата");

        jLabel7.setText("Просмотр остатков материалов");

        jButton6.setText("Сформировать");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Дашборд");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Дашборд");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton10.setText("Дашборд");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Дашборд");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jButton3)
                            .addComponent(jButton2)
                            .addComponent(jButton7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jButton5)
                                    .addComponent(jButton4))
                                .addGap(46, 46, 46)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton10)
                                .addGap(294, 294, 294)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel6)))
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jButton6)
                            .addComponent(jButton11))
                        .addGap(557, 557, 557))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jButton10))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8)
                            .addComponent(jButton11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addGap(36, 36, 36)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
        new Auto().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Клиент ФИО");
        tableHeaders.add("Продавец ФИО");
        tableHeaders.add("Дата заказа");
        tableHeaders.add("Сумма");
        tableHeaders.add("Статус");
        tableHeaders.add("Дата сдачи");
        tableHeaders.add("Дата выполнения");
        tableHeaders.add("Примечания");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getOrders")
                .build();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                String orderDateStr = jo.getString("orderDate");
                LocalDate orderDate;
                try {
                    orderDate = OffsetDateTime.parse(orderDateStr).toLocalDate();
                } catch (Exception e) {
                    orderDate = LocalDate.parse(orderDateStr);
                }

                if (orderDate.getYear() == currentYear && orderDate.getMonthValue() == currentMonth) {
                    Vector<Object> oneRow = new Vector<>();

                    oneRow.add(jo.getInt("id"));
                    JSONObject customer = jo.getJSONObject("customerID");
                    String customerFIO = customer.getString("lastName") + " " + customer.getString("firstName") + " " + customer.getString("middleName");
                    oneRow.add(customerFIO);
                    JSONObject seller = jo.getJSONObject("sellerID");
                    String sellerFIO = seller.getString("lastName") + " " + seller.getString("firstName") + " " + seller.getString("middleName");
                    oneRow.add(sellerFIO);

                    oneRow.add(orderDate.format(formatter));

                    oneRow.add(jo.optString("totalAmount"));
                    oneRow.add(jo.optString("status"));

                    String dueDateStr = jo.optString("dueDate", "");
                    if (!dueDateStr.isEmpty()) {
                        try {
                            LocalDate dueDate = LocalDate.parse(dueDateStr);
                            oneRow.add(dueDate.format(formatter));
                        } catch (Exception e) {
                            oneRow.add(dueDateStr);
                        }
                    } else {
                        oneRow.add("");
                    }

                    String completionDateStr = jo.optString("completionDate", "");
                    if (!completionDateStr.isEmpty()) {
                        try {
                            LocalDate completionDate = LocalDate.parse(completionDateStr);
                            oneRow.add(completionDate.format(formatter));
                        } catch (Exception e) {
                            oneRow.add(completionDateStr);
                        }
                    } else {
                        oneRow.add("");
                    }

                    oneRow.add(jo.optString("notes", ""));

                    tableData.add(oneRow);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Date selectedStartDate = (Date) datePickerNew.getModel().getValue();
        Date selectedEndDate = (Date) datePickerEnd.getModel().getValue();

        if (selectedStartDate == null || selectedEndDate == null) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, выберите обе даты.");
            return;
        }

        LocalDate startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(null, "Дата начала не может быть позже даты окончания");
            return;
        }

        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Клиент ФИО");
        tableHeaders.add("Продавец ФИО");
        tableHeaders.add("Дата заказа");
        tableHeaders.add("Сумма");
        tableHeaders.add("Статус");
        tableHeaders.add("Дата сдачи");
        tableHeaders.add("Дата выполнения");
        tableHeaders.add("Примечания");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getOrders")
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }
            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                String orderDateStr = jo.getString("orderDate");
                LocalDate orderDate;
                try {
                    orderDate = OffsetDateTime.parse(orderDateStr).toLocalDate();
                } catch (Exception e) {
                    orderDate = LocalDate.parse(orderDateStr);
                }

                if ((orderDate.isEqual(startDate) || orderDate.isAfter(startDate))
                        && (orderDate.isEqual(endDate) || orderDate.isBefore(endDate))) {

                    Vector<Object> oneRow = new Vector<>();

                    oneRow.add(jo.getInt("id"));

                    JSONObject customer = jo.getJSONObject("customerID");
                    String customerFIO = customer.getString("lastName") + " "
                            + customer.getString("firstName") + " "
                            + customer.optString("middleName", "");
                    oneRow.add(customerFIO.trim());

                    JSONObject seller = jo.getJSONObject("sellerID");
                    String sellerFIO = seller.getString("lastName") + " "
                            + seller.getString("firstName") + " "
                            + seller.optString("middleName", "");
                    oneRow.add(sellerFIO.trim());

                    oneRow.add(orderDate.format(formatter));

                    oneRow.add(jo.optString("totalAmount"));
                    oneRow.add(jo.optString("status"));

                    String dueDateStr = jo.optString("dueDate", "");
                    if (!dueDateStr.isEmpty()) {
                        try {
                            LocalDate dueDate = LocalDate.parse(dueDateStr);
                            oneRow.add(dueDate.format(formatter));
                        } catch (Exception e) {
                            oneRow.add(dueDateStr); 
                        }
                    } else {
                        oneRow.add("");
                    }

                    String completionDateStr = jo.optString("completionDate", "");
                    if (!completionDateStr.isEmpty()) {
                        try {
                            LocalDate completionDate = LocalDate.parse(completionDateStr);
                            oneRow.add(completionDate.format(formatter));
                        } catch (Exception e) {
                            oneRow.add(completionDateStr); 
                        }
                    } else {
                        oneRow.add("");
                    }

                    oneRow.add(jo.optString("notes", ""));

                    tableData.add(oneRow);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Клиент ФИО");
        tableHeaders.add("Продавец ФИО");
        tableHeaders.add("Дата продажи");
        tableHeaders.add("Сумма");
        tableHeaders.add("Сумма скидки");
        tableHeaders.add("Итоговая сумма");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getSales")
                .build();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }
            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                String saleDateStr = jo.getString("saleDate");
                LocalDate saleDate;
                try {
                    saleDate = OffsetDateTime.parse(saleDateStr).toLocalDate();
                } catch (Exception e) {
                    saleDate = LocalDate.parse(saleDateStr);
                }

                if (saleDate.getYear() == currentYear && saleDate.getMonthValue() == currentMonth) {
                    Vector<Object> oneRow = new Vector<>();

                    oneRow.add(jo.getInt("id"));

                    JSONObject customer = jo.getJSONObject("customerID");
                    String customerFIO = customer.getString("lastName") + " "
                            + customer.getString("firstName") + " "
                            + customer.optString("middleName", "");
                    oneRow.add(customerFIO.trim());

                    JSONObject seller = jo.getJSONObject("sellerID");
                    String sellerFIO = seller.getString("lastName") + " "
                            + seller.getString("firstName") + " "
                            + seller.optString("middleName", "");
                    oneRow.add(sellerFIO.trim());

                    oneRow.add(saleDate.format(formatter));

                    oneRow.add(jo.optString("totalAmount"));
                    oneRow.add(jo.optString("discountAmount"));
                    oneRow.add(jo.optString("finalAmount"));

                    tableData.add(oneRow);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Date selectedStartDate = (Date) datePickerNew.getModel().getValue();
        Date selectedEndDate = (Date) datePickerEnd.getModel().getValue();

        if (selectedStartDate == null || selectedEndDate == null) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, выберите обе даты.");
            return;
        }

        LocalDate startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(null, "Дата начала не может быть позже даты окончания");
            return;
        }

        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Клиент ФИО");
        tableHeaders.add("Продавец ФИО");
        tableHeaders.add("Дата продажи");
        tableHeaders.add("Сумма");
        tableHeaders.add("Сумма скидки");
        tableHeaders.add("Итоговая сумма");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getSales")
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }
            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                String saleDateStr = jo.getString("saleDate");
                LocalDate saleDate;
                try {
                    saleDate = OffsetDateTime.parse(saleDateStr).toLocalDate();
                } catch (Exception e) {
                    saleDate = LocalDate.parse(saleDateStr);
                }

                if ((saleDate.isEqual(startDate) || saleDate.isAfter(startDate))
                        && (saleDate.isEqual(endDate) || saleDate.isBefore(endDate))) {

                    Vector<Object> oneRow = new Vector<>();

                    oneRow.add(jo.getInt("id"));

                    JSONObject customer = jo.getJSONObject("customerID");
                    String customerFIO = customer.getString("lastName") + " "
                            + customer.getString("firstName") + " "
                            + customer.optString("middleName", "");
                    oneRow.add(customerFIO.trim());

                    JSONObject seller = jo.getJSONObject("sellerID");
                    String sellerFIO = seller.getString("lastName") + " "
                            + seller.getString("firstName") + " "
                            + seller.optString("middleName", "");
                    oneRow.add(sellerFIO.trim());

                    oneRow.add(saleDate.format(formatter));

                    oneRow.add(jo.optString("totalAmount"));
                    oneRow.add(jo.optString("discountAmount"));
                    oneRow.add(jo.optString("finalAmount"));

                    tableData.add(oneRow);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер материала");
        tableHeaders.add("Название");
        tableHeaders.add("Описание");
        tableHeaders.add("Цена за метр");
        tableHeaders.add("Количество на складе");
        tableHeaders.add("Цвет");
        tableHeaders.add("Ширина");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getFrameMaterial")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }
            String responseBody = response.body().string();
            JSONArray materialsArray = new JSONArray(responseBody);

            for (int i = 0; i < materialsArray.length(); i++) {
                JSONObject material = materialsArray.getJSONObject(i);

                Vector<Object> row = new Vector<>();
                row.add(material.getInt("id"));
                row.add(material.optString("name", ""));
                row.add(material.optString("description", ""));
                row.add(material.optInt("pricePerMeter", 0));
                row.add(material.optInt("stockQuantity", 0));
                row.add(material.optString("color", ""));
                row.add(material.optInt("width", 0));

                tableData.add(row);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String chartType = selectChartType();
        if (chartType == null) {
            return;
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getOrders")
                .build();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int[] ordersPerDay = new int[32];

        try (Response response = okHttpClient.newCall(request).execute()) {
            JSONArray ja = new JSONArray(response.body().string());

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String orderDateStr = jo.getString("orderDate");
                LocalDate orderDate;
                try {
                    orderDate = OffsetDateTime.parse(orderDateStr).toLocalDate();
                } catch (Exception e) {
                    orderDate = LocalDate.parse(orderDateStr);
                }

                if (orderDate.getYear() == currentYear && orderDate.getMonthValue() == currentMonth) {
                    int dayOfMonth = orderDate.getDayOfMonth();
                    ordersPerDay[dayOfMonth]++;
                }
            }

            for (int day = 1; day <= 31; day++) {
                if (ordersPerDay[day] > 0) {
                    dataset.addValue(ordersPerDay[day], "Заказы", String.valueOf(day));
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных для диаграммы");
            return;
        }

        if (dataset.getRowCount() > 0) {
            createOrdersChart(dataset, "Количество заказов по дням за "
                    + getRussianMonthName(currentMonth) + ", " + currentYear + " год");
        } else {
            JOptionPane.showMessageDialog(this, "Нет данных для построения диаграммы");
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String chartType = selectChartType();
        if (chartType == null) {
            return; 
        }
        Date selectedStartDate = (Date) datePickerNew.getModel().getValue();
        Date selectedEndDate = (Date) datePickerEnd.getModel().getValue();

        if (selectedStartDate == null || selectedEndDate == null) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите обе даты.");
            return;
        }

        LocalDate startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Дата начала не может быть позже даты окончания");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getOrders")
                .build();

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        int[] ordersPerDay = new int[(int) daysBetween];

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }

            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String orderDateStr = jo.getString("orderDate");
                LocalDate orderDate;
                try {
                    orderDate = OffsetDateTime.parse(orderDateStr).toLocalDate();
                } catch (Exception e) {
                    orderDate = LocalDate.parse(orderDateStr);
                }

                if ((orderDate.isEqual(startDate) || orderDate.isAfter(startDate))
                        && (orderDate.isEqual(endDate) || orderDate.isBefore(endDate))) {

                    long dayIndex = java.time.temporal.ChronoUnit.DAYS.between(startDate, orderDate);
                    if (dayIndex >= 0 && dayIndex < daysBetween) {
                        ordersPerDay[(int) dayIndex]++;
                    }
                }
            }

            for (int i = 0; i < daysBetween; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                String dayLabel = currentDate.getDayOfMonth() + "." + currentDate.getMonthValue();
                dataset.addValue(ordersPerDay[i], "Заказы", dayLabel);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных для диаграммы");
            return;
        }

        if (dataset.getRowCount() > 0) {
            String startDateStr = startDate.getDayOfMonth() + " "
                    + getRussianMonthName(startDate.getMonthValue()) + " "
                    + startDate.getYear();
            String endDateStr = endDate.getDayOfMonth() + " "
                    + getRussianMonthName(endDate.getMonthValue()) + " "
                    + endDate.getYear();

            createOrdersChart(dataset, "Количество заказов за период с "
                    + startDateStr + " по " + endDateStr);
        } else {
            JOptionPane.showMessageDialog(this, "Нет данных для построения диаграммы за выбранный период");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String chartType = selectChartType();
        if (chartType == null) {
            return;
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getSales")
                .build();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        double[] salesPerDay = new double[32];

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }

            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String saleDateStr = jo.getString("saleDate");
                LocalDate saleDate;
                try {
                    saleDate = OffsetDateTime.parse(saleDateStr).toLocalDate();
                } catch (Exception e) {
                    saleDate = LocalDate.parse(saleDateStr);
                }

                if (saleDate.getYear() == currentYear && saleDate.getMonthValue() == currentMonth) {
                    int dayOfMonth = saleDate.getDayOfMonth();
                    double finalAmount = jo.optDouble("finalAmount", 0);
                    salesPerDay[dayOfMonth] += finalAmount;
                }
            }

            for (int day = 1; day <= 31; day++) {
                if (salesPerDay[day] > 0) {
                    dataset.addValue(salesPerDay[day], "Продажи", String.valueOf(day));
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных для диаграммы");
            return;
        }

        if (dataset.getRowCount() > 0) {
            createSalesChart(dataset, "Сумма продаж по дням за "
                    + getRussianMonthName(currentMonth) + ", " + currentYear + " год");
        } else {
            JOptionPane.showMessageDialog(this, "Нет данных для построения диаграммы продаж");
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String chartType = selectChartType();
        if (chartType == null) {
            return; 
        }
        Date selectedStartDate = (Date) datePickerNew.getModel().getValue();
        Date selectedEndDate = (Date) datePickerEnd.getModel().getValue();

        if (selectedStartDate == null || selectedEndDate == null) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите обе даты.");
            return;
        }

        LocalDate startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Дата начала не может быть позже даты окончания");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getSales")
                .build();

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        double[] salesPerDay = new double[(int) daysBetween];

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Ошибка запроса: " + response.code());
                return;
            }

            String responseBody = response.body().string();
            JSONArray ja = new JSONArray(responseBody);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String saleDateStr = jo.getString("saleDate");
                LocalDate saleDate;
                try {
                    saleDate = OffsetDateTime.parse(saleDateStr).toLocalDate();
                } catch (Exception e) {
                    saleDate = LocalDate.parse(saleDateStr);
                }

                if ((saleDate.isEqual(startDate) || saleDate.isAfter(startDate))
                        && (saleDate.isEqual(endDate) || saleDate.isBefore(endDate))) {

                    long dayIndex = java.time.temporal.ChronoUnit.DAYS.between(startDate, saleDate);
                    if (dayIndex >= 0 && dayIndex < daysBetween) {
                        double finalAmount = jo.optDouble("finalAmount", 0);
                        salesPerDay[(int) dayIndex] += finalAmount;
                    }
                }
            }

            for (int i = 0; i < daysBetween; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                String dayLabel = currentDate.getDayOfMonth() + "." + currentDate.getMonthValue();
                dataset.addValue(salesPerDay[i], "Продажи", dayLabel);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных для диаграммы");
            return;
        }

        if (dataset.getRowCount() > 0) {
            String startDateStr = startDate.getDayOfMonth() + " "
                    + getRussianMonthName(startDate.getMonthValue()) + " "
                    + startDate.getYear();
            String endDateStr = endDate.getDayOfMonth() + " "
                    + getRussianMonthName(endDate.getMonthValue()) + " "
                    + endDate.getYear();

            createSalesChart(dataset, "Сумма продаж за период с "
                    + startDateStr + " по " + endDateStr);
        } else {
            JOptionPane.showMessageDialog(this, "Нет данных для построения диаграммы продаж за выбранный период");
        }
    }//GEN-LAST:event_jButton11ActionPerformed

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
            java.util.logging.Logger.getLogger(Director.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Director.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Director.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Director.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Director().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}