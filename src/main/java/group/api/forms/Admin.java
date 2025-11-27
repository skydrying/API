package group.api.forms;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

import group.api.forms.DateLabelFormatter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class Admin extends javax.swing.JFrame {

    public Admin() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Панель Администратора");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/group/forms/images/icon.png")));
        allUsers();
        DateForm();
    }
    public JDatePickerImpl datePickerBirth;
    public JDatePickerImpl datePickerEmployment;
    private File selectedFile;
    private String currentPosition;

    public void allUsers() {
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Номер");
        tableHeaders.add("Фамилия");
        tableHeaders.add("Имя");
        tableHeaders.add("Отчество");
        tableHeaders.add("Телефон");
        tableHeaders.add("Дата рождения");
        tableHeaders.add("Дата трудоустройства");
        tableHeaders.add("Паспортные данные");
        tableHeaders.add("Снилс");
        tableHeaders.add("Фотография");
        tableHeaders.add("Логин");
        tableHeaders.add("Пароль");
        tableHeaders.add("Должность");

        Vector<Vector<Object>> tableData = new Vector<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/getUsers")
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
                oneRow.add(jo.getString("dateOfBirth"));
                oneRow.add(jo.getString("dateOfEmployment"));
                oneRow.add(jo.getString("passportData"));
                oneRow.add(jo.getString("snils"));
                oneRow.add(jo.optString("photoLink"));
                oneRow.add(jo.getString("login"));
                oneRow.add(jo.getString("password"));

                boolean hasPosition = false;

                if (jo.getJSONArray("directorCollection") != null) {
                    JSONArray directorCollection = jo.getJSONArray("directorCollection");
                    if (directorCollection.length() != 0) {
                        oneRow.add("Директор");
                        hasPosition = true;
                    }
                }
                if (jo.getJSONArray("sellerCollection") != null) {
                    JSONArray sellerCollection = jo.getJSONArray("sellerCollection");
                    if (sellerCollection.length() != 0) {
                        oneRow.add("Продавец");
                        hasPosition = true;
                    }
                }
                if (jo.getJSONArray("productionmasterCollection") != null) {
                    JSONArray productionmasterCollection = jo.getJSONArray("productionmasterCollection");
                    if (productionmasterCollection.length() != 0) {
                        oneRow.add("Мастер");
                        hasPosition = true;
                    }
                }

                if (!hasPosition) {
                    oneRow.add("Пользователь");
                }

                tableData.add(oneRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        jTable1.setModel(new DefaultTableModel(tableData, tableHeaders));
    }

    public void DateForm() {
        UtilDateModel modelBirth = new UtilDateModel();
        modelBirth.setSelected(true);
        Properties pBirth = new Properties();
        pBirth.put("text.today", "Today");
        pBirth.put("text.month", "Month");
        pBirth.put("text.year", "Year");
        JDatePanelImpl datePanelBirth = new JDatePanelImpl(modelBirth, pBirth);
        datePickerBirth = new JDatePickerImpl(datePanelBirth, new DateLabelFormatter());
        datePickerBirth.setBounds(5,5,120,30);
        jPanel1.add(datePickerBirth);


        UtilDateModel modelEmployment = new UtilDateModel();
        modelEmployment.setSelected(true);
        Properties pEmployment = new Properties();
        pEmployment.put("text.today", "Today");
        pEmployment.put("text.month", "Month");
        pEmployment.put("text.year", "Year");
        JDatePanelImpl datePanelEmployment = new JDatePanelImpl(modelEmployment, pEmployment);
        datePickerEmployment = new JDatePickerImpl(datePanelEmployment, new DateLabelFormatter());
        datePickerEmployment.setBounds(5,5,120,30);
        jPanel2.add(datePickerEmployment);

        jPanel1.revalidate();
        jPanel1.repaint();
        jPanel2.revalidate();
        jPanel2.repaint();
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();

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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Фамилия");

        jLabel2.setText("Имя");

        jLabel3.setText("Отчество");

        jLabel4.setText("Дата рождения");

        jLabel5.setText("Дата трудоустройства");

        jLabel6.setText("Паспортные данные");

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel7.setText("Снилс");

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jLabel8.setText("Логин");

        jLabel9.setText("Пароль");

        jLabel10.setText("Фотография");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Директор", "Продавец", "Мастер" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel11.setText("Должность");

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

        jButton5.setText("Выбрать файл");
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

        jLabel13.setText("Выбранная фотография");

        jButton6.setText("Обновить таблицу");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel14.setText("Телефон");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField4)
                                    .addComponent(jTextField2)
                                    .addComponent(jTextField3))))
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(jTextField6))
                                    .addComponent(jButton5)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(92, 92, 92)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(jLabel4)
                                .addGap(36, 36, 36))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(138, 138, 138)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jLabel12)))
                        .addGap(281, 281, 281))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField9)
                            .addComponent(jTextField8))
                        .addGap(66, 66, 66)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(830, 830, 830))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(364, 364, 364)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel4))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel6)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel7)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10)
                                            .addComponent(jButton5))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       dispose();
       new Auto().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        OkHttpClient client = new OkHttpClient();

        String lastname = jTextField2.getText();
        String firstname = jTextField1.getText();
        String middlename = jTextField3.getText();
        String phone = jTextField4.getText();

        Date dateOfBirthDate = (Date) datePickerBirth.getModel().getValue();
        String dateofbirth = "";
        if (dateOfBirthDate != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateofbirth = dateFormatter.format(dateOfBirthDate);
        }

        Date dateOfEmploymentDate = (Date) datePickerEmployment.getModel().getValue();
        String dateOfEmployment = "";
        if (dateOfEmploymentDate != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateOfEmployment = dateFormatter.format(dateOfEmploymentDate);
        }

        String passportdata = jTextField6.getText();
        String snils = jTextField7.getText();
        String login = jTextField8.getText();
        String password = jTextField9.getText();
        String position = (String) jComboBox1.getSelectedItem();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("LastName", lastname)
                .addFormDataPart("FirstName", firstname)
                .addFormDataPart("MiddleName", middlename)
                .addFormDataPart("Phone", phone)
                .addFormDataPart("DateOfBirth", dateofbirth)
                .addFormDataPart("DateOfEmployment", dateOfEmployment)
                .addFormDataPart("PassportData", passportdata)
                .addFormDataPart("SNILS", snils)
                .addFormDataPart("Login", login)
                .addFormDataPart("Password", password);

        if (selectedFile != null) {
            builder.addFormDataPart("PhotoLink", selectedFile.getName(),
                    RequestBody.create(selectedFile, MediaType.parse("image/png")));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/addUser")
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
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("id", responseData);

                    int userId = jsonResponse.getInt("id");

                    switch (position) {
                        case "Директор":
                            addPositionToDatabase(userId, "addDirector");
                            break;
                        case "Продавец":
                            addPositionToDatabase(userId, "addSeller");
                            break;
                        case "Мастер":
                            addPositionToDatabase(userId, "addProductionMaster");
                            break;
                        default:
                            System.out.println("Неизвестная должность: " + position);
                    }

                    System.out.println("Пользователь успешно добавлен!");
                    allUsers();
                } else {
                    System.out.println("Ошибка при добавлении пользователя: " + response.message());
                }
            }

            private void addPositionToDatabase(int userId, String method) {

                String url = "http://localhost:9090/api/" + method;
                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("idUser", String.valueOf(userId))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            System.out.println("Должность успешно добавлена!");
                        } else {
                            System.out.println("Ошибка при добавлении должности: " + response.message());
                        }
                    }
                });
            }
        });
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         OkHttpClient client = new OkHttpClient();

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("Выберите пользователя для обновления данных");
            return;
        }

        Integer userId = (Integer) jTable1.getValueAt(selectedRow, 0);

        String lastname = jTextField2.getText();
        String firstname = jTextField1.getText();
        String middlename = jTextField3.getText();
        String phone = jTextField4.getText();

        Date dateOfBirthDate = (Date) datePickerBirth.getModel().getValue();
        String dateofbirth = "";
        if (dateOfBirthDate != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateofbirth = dateFormatter.format(dateOfBirthDate);
        }

        Date dateOfEmploymentDate = (Date) datePickerEmployment.getModel().getValue();
        String dateOfEmployment = "";
        if (dateOfEmploymentDate != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateOfEmployment = dateFormatter.format(dateOfEmploymentDate);
        }

        String passportdata = jTextField6.getText();
        String snils = jTextField7.getText();
        String login = jTextField8.getText();
        String password = jTextField9.getText();
        String newPosition = (String) jComboBox1.getSelectedItem();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(userId))
                .addFormDataPart("LastName", lastname)
                .addFormDataPart("FirstName", firstname)
                .addFormDataPart("MiddleName", middlename)
                .addFormDataPart("Phone", phone)
                .addFormDataPart("DateOfBirth", dateofbirth)
                .addFormDataPart("DateOfEmployment", dateOfEmployment)
                .addFormDataPart("PassportData", passportdata)
                .addFormDataPart("SNILS", snils)
                .addFormDataPart("Login", login)
                .addFormDataPart("Password", password);

        if (selectedFile != null) {
            builder.addFormDataPart("PhotoLink", selectedFile.getName(),
                    RequestBody.create(selectedFile, MediaType.parse("image/png")));
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/updateUser")
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
                    
                    checkUserPosition(userId);

                    System.out.println("Пользователь успешно обновлен");
                    
                    if (newPosition != null) {
                        addPositionToDatabase(userId, newPosition);
                    }
                } else {
                    System.out.println("Ошибка обновления: " + response.message());
                }
            }
        });
    }

    private void addPositionToDatabase(int userId, String position) {
        String method = "";
        switch (position) {
            case "Директор":
                method = "addDirector";
                break;
            case "Продавец":
                method = "addSeller";
                break;
            case "Мастер":
                method = "addProductionmaster";
                break;
            default:
                System.out.println("Неизвестная должность: " + position);
                return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("idUser", String.valueOf(userId))
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/" + method)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Должность успешна добавлена!");
                } else {
                    System.out.println("Ошибка добавления: " + response.message());
                }
            }
        });
    }

    private void checkUserPosition(int userId) {
        checkUserPositionDerictor(userId);
    }

    private void checkUserPositionDerictor(int userId) {
        OkHttpClient client = new OkHttpClient();

        Request requestDirector = new Request.Builder()
                .url("http://localhost:9090/api/getDirector")
                .get()
                .build();

        client.newCall(requestDirector).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JsonArray directorArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

                    for (JsonElement element : directorArray) {
                        JsonObject director = element.getAsJsonObject();
                        if (director.has("id")) {
                            int directorUserId = director.get("id").getAsInt();
                            if (directorUserId == userId) {
                                System.out.println("Пользователь с ID " + userId + " Директор.");
                                deletePosition("Director", String.valueOf(directorUserId));
                                return;
                            }
                        }
                    }
                }
                checkUserPositionSeller(userId);
            }
        });
    }

    private void checkUserPositionSeller(int userId) {
        OkHttpClient client = new OkHttpClient();

        Request requestSeller = new Request.Builder()
                .url("http://localhost:9090/api/getSeller")
                .get()
                .build();

        client.newCall(requestSeller).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JsonArray classTeacherArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

                    for (JsonElement element : classTeacherArray) {
                        JsonObject seller = element.getAsJsonObject();
                        if (seller.has("id")) {
                            int sellerUserId = seller.get("id").getAsInt();
                            if (sellerUserId == userId) {
                                System.out.println("Пользователь с ID " + userId + " Продавец");
                                deletePosition("Seller", String.valueOf(sellerUserId));
                                return;
                            }
                        }
                    }
                }
                checkUserPositionProductionmaster(userId);
            }
        });
    }

    private void checkUserPositionProductionmaster(int userId) {
        OkHttpClient client = new OkHttpClient();

        Request requestProductionMaster = new Request.Builder()
                .url("http://localhost:9090/api/getProductionmaster")
                .get()
                .build();

        client.newCall(requestProductionMaster).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JsonArray productionMasterArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

                    for (JsonElement element : productionMasterArray) {
                        JsonObject head = element.getAsJsonObject();
                        if (head.has("id")) {
                            int productionMasterUserId = head.get("id").getAsInt();
                            if (productionMasterUserId == userId) {
                                System.out.println("Пользователь с ID " + userId + " Мастер");
                                deletePosition("Productionmaster", String.valueOf(productionMasterUserId));
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    private void deletePosition(String position, String userId) {
        OkHttpClient client = new OkHttpClient();
        String method = "";

        if (position.equalsIgnoreCase("Director")) {
            method = "deleteDirector";
        } else if (position.equalsIgnoreCase("Seller")) {
            method = "deleteSeller";
        } else if (position.equalsIgnoreCase("Productionmaster")) {
            method = "deleteProductionmaster";
        } else {
            System.out.println("Неизвестная должность для удаления: " + position);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("idUser", userId)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:9090/api/" + method)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Должность " + position + " успешно для удаления User ID " + userId);
                } else {
                    System.out.println("Ошибка удаления позиции " + position + ": " + response.code() + " - " + response.body().string());
                }
            }
        });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) jTable1.getValueAt(selectedRow, 0); 

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/deleteUser")
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
                        System.out.println("Пользователь успешно удален!");
                        allUsers();
                    } else {
                        System.out.println("Ошибка при удалении пользователя: " + response.message());
                    }
                }
            });
        } else {
            System.out.println("Пожалуйста, выберите пользователя для удаления.");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, "Выбрать файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getPath());
            jLabel12.setIcon(icon);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            jTextField1.setText((String) jTable1.getValueAt(selectedRow, 1));
            jTextField2.setText((String) jTable1.getValueAt(selectedRow, 2));
            jTextField3.setText((String) jTable1.getValueAt(selectedRow, 3));
            jTextField4.setText((String) jTable1.getValueAt(selectedRow, 4));
            jTextField6.setText((String) jTable1.getValueAt(selectedRow, 7));
            jTextField7.setText((String) jTable1.getValueAt(selectedRow, 8));
            jTextField8.setText((String) jTable1.getValueAt(selectedRow, 10));
            jTextField9.setText((String) jTable1.getValueAt(selectedRow, 11));

            String dateOfBirthString = (String) jTable1.getValueAt(selectedRow, 5);
            String dateOfEmploymentString = (String) jTable1.getValueAt(selectedRow, 6);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (dateOfBirthString != null && !dateOfBirthString.isEmpty()) {
                try {
                    Date dateOfBirthDate = dateFormat.parse(dateOfBirthString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfBirthDate);
                    datePickerBirth.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerBirth.getModel().setSelected(true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (dateOfEmploymentString != null && !dateOfEmploymentString.isEmpty()) {
                try {
                    Date dateOfEmploymentDate = dateFormat.parse(dateOfEmploymentString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfEmploymentDate);
                    datePickerEmployment.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerEmployment.getModel().setSelected(true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String imagePath = (String) jTable1.getValueAt(selectedRow, 9);
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imagePath);
                jLabel12.setIcon(icon);
            } else {
                jLabel12.setIcon(null);
            }

            String position = (String) jTable1.getValueAt(selectedRow, 12);
            if (position != null && !position.isEmpty()) {
                jComboBox1.setSelectedItem(position); 
            } else {
                jComboBox1.setSelectedItem(null); 
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        allUsers();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

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
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
