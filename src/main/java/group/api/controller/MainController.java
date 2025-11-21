package group.api.controller;

import group.api.entity.*;
import group.api.forms.ProductionMaster;
import group.api.repository.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ProductionmasterRepository productionmasterRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private FrameComponentRepository frameComponentRepository;
    @Autowired
    private FrameMaterialRepository frameMaterialRepository;
    @Autowired
    private EmbroideryKitRepository embroideryKitRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomFrameOrderRepository customFrameOrderRepository;
    @Autowired
    private ConsumableRepository consumableRepository;
    @Autowired
    private ReviewsRepository reviewsRepository;

    @GetMapping("/getUsers")
    public @ResponseBody
    Iterable<User> allUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getCustomers")
    public @ResponseBody
    Iterable<Customer> allCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/getReviews")
    public @ResponseBody
    Iterable<Reviews> allReviews() {
        return reviewsRepository.findAll();
    }

    @PostMapping("/addReviewTG")
    public Reviews addReviewTG(@RequestBody Reviews review) {
        return reviewsRepository.save(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void delReview(@PathVariable Integer id) {
        reviewsRepository.deleteById(id);
    }

    @GetMapping("/getEmbroiderykit")
    public @ResponseBody
    Iterable<EmbroideryKit> allEmbroiderykit() {
        return embroideryKitRepository.findAll();
    }

    @GetMapping("/getConsumables")
    public @ResponseBody
    Iterable<Consumable> allConsumable() {
        return consumableRepository.findAll();
    }

    @GetMapping("/getFrameMaterial")
    public @ResponseBody
    Iterable<FrameMaterial> getFrameMaterial() {
        return frameMaterialRepository.findAll();
    }

    @GetMapping("/getFrameComponent")
    public @ResponseBody
    Iterable<FrameComponent> allFrameComponent() {
        return frameComponentRepository.findAll();
    }

    @GetMapping("/getCustomFrameOrder")
    public @ResponseBody
    Iterable<CustomFrameOrder> allCustomFrameOrder() {
        return customFrameOrderRepository.findAll();
    }

    @PostMapping("/createOrder")
    public Orders createOrder(@RequestBody Orders order) {
        return ordersRepository.save(order);
    }

    @PostMapping("/createCustomFrameOrder")
    public CustomFrameOrder createCustomFrameOrder(@RequestBody CustomFrameOrder customFrameOrder) {
        return customFrameOrderRepository.save(customFrameOrder);
    }

    public Orders updateOrder(Orders order) {
        return ordersRepository.save(order);
    }

    public CustomFrameOrder updateCustomFrameOrder(CustomFrameOrder customFrameOrder) {
        return customFrameOrderRepository.save(customFrameOrder);
    }

    @GetMapping("/frameMaterials")
    public Iterable<FrameMaterial> allFrameMaterial() {
        return frameMaterialRepository.findAll();
    }

    @PostMapping("/frameMaterials")
    public FrameMaterial createFrameMaterial(@RequestBody FrameMaterial frameMaterial) {
        return frameMaterialRepository.save(frameMaterial);
    }

    @PutMapping("/frameMaterials")
    public FrameMaterial updateFrameMaterial(@RequestBody FrameMaterial frameMaterial) {
        return frameMaterialRepository.save(frameMaterial);
    }

    @DeleteMapping("/frameMaterials/{id}")
    public void deleteFrameMaterial(@PathVariable Integer id) {
        frameMaterialRepository.deleteById(id);
    }

    @GetMapping("/frameComponents")
    public Iterable<FrameComponent> allFC() {
        return frameComponentRepository.findAll();
    }

    @PostMapping("/frameComponents")
    public FrameComponent createFrameComponent(@RequestBody FrameComponent frameComponent) {
        return frameComponentRepository.save(frameComponent);
    }

    @PutMapping("/frameComponents")
    public FrameComponent updateFrameComponent(@RequestBody FrameComponent frameComponent) {
        return frameComponentRepository.save(frameComponent);
    }

    @DeleteMapping("/frameComponents/{id}")
    public void deleteFrameComponent(@PathVariable Integer id) {
        frameComponentRepository.deleteById(id);
    }

    public Productionmaster findProductionMasterByUserId(Long userId) {
        try {
            Iterable<Productionmaster> productionMasters = productionmasterRepository.findAll();
            for (Productionmaster master : productionMasters) {
                if (master.getIdUser() != null && master.getIdUser().getId().longValue() == userId) {
                    return master;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error finding production master by user ID: " + e.getMessage());
            return null;
        }
    }

    @GetMapping("/getOrders")
    public @ResponseBody
    List allOrders() {
        List list = new ArrayList();
        for (Orders order : ordersRepository.findAll()) {
            list.add(order);
        }
        return list;
    }

    @PostMapping("/changeOrderStatus")
    public void changeOrderStatus(Integer orderId, String newStatus) {
        try {
            Orders order = ordersRepository.findById(orderId).orElse(null);

            if (order != null) {
                order.setStatus(newStatus);
                ordersRepository.save(order);
                System.out.println("Статус заказа №" + orderId + " изменен на: " + newStatus);
            } else {
                System.err.println("Заказ с ID " + orderId + " не найден.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при изменении статуса заказа: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/changeOrderStatusSite")
    public String changeOrderStatus(@RequestParam("orderId") Integer orderId,
                                    @RequestParam("newStatus") String newStatus,
                                    HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null) {
            return "redirect:/api/formauto";
        }

        try {
            Orders order = ordersRepository.findById(orderId).orElse(null);

            if (order != null) {
                boolean canChangeStatus = false;

                if ("director".equals(role)) {
                    canChangeStatus = true;
                }
                else if ("customer".equals(role) && user instanceof Customer) {
                    Customer customer = (Customer) user;
                    if (order.getCustomerID() != null &&
                            order.getCustomerID().getId().equals(customer.getId()) &&
                            "Отменен".equals(newStatus)) {
                        canChangeStatus = true;
                    }
                }
                else if ("productionmaster".equals(role) && user instanceof User) {
                    User currentUser = (User) user;

                    Productionmaster currentMaster = null;
                    List<Productionmaster> allMasters = new ArrayList<>();
                    for (Productionmaster master : productionmasterRepository.findAll()) {
                        allMasters.add(master);
                    }
                    for (Productionmaster master : allMasters) {
                        if (master.getIdUser() != null && master.getIdUser().getId().equals(currentUser.getId())) {
                            currentMaster = master;
                            break;
                        }
                    }

                    if (currentMaster != null) {
                        if (order.getProductionMasterID() != null &&
                                order.getProductionMasterID().getId().equals(currentMaster.getId())) {
                            canChangeStatus = true;
                        }
                        else {
                            List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                            for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                                allCustomOrders.add(customOrder);
                            }

                            for (CustomFrameOrder customOrder : allCustomOrders) {
                                if (customOrder.getOrderID() != null &&
                                        customOrder.getOrderID().getId().equals(order.getId()) &&
                                        customOrder.getProductionMasterID() != null &&
                                        customOrder.getProductionMasterID().getId().equals(currentMaster.getId())) {
                                    canChangeStatus = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if ("seller".equals(role) && user instanceof User) {
                    User currentUser = (User) user;
                    if (order.getSellerID() != null &&
                            order.getSellerID().getId().equals(currentUser.getId())) {
                        canChangeStatus = true;
                    }
                }

                if (canChangeStatus) {
                    order.setStatus(newStatus);

                    if ("Забран".equals(newStatus)) {
                        order.setCompletionDate(new Date());
                    }

                    ordersRepository.save(order);
                    System.out.println("Статус заказа №" + orderId + " изменен на: " + newStatus);
                } else {
                    System.err.println("Доступ запрещен: пользователь " + user + " не может изменить статус заказа " + orderId);
                }
            } else {
                System.err.println("Заказ с ID " + orderId + " не найден.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при изменении статуса заказа: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/api/formspecial";
    }

    @GetMapping("/getSales")
    public @ResponseBody
    Iterable<Sale> allSales() {
        return saleRepository.findAll();
    }

    @PostMapping("/getAutarization")
    public @ResponseBody
    String getAutorization(@RequestParam(name = "Login") String login, @RequestParam(name = "Password") String password) {
        int userId = 0;
        for (User user : userRepository.findAll()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                userId = user.getId();
                break;
            }
        }

        if (userId != 0) {
            for (Director director : directorRepository.findAll()) {
                if (director.getIdUser() != null && director.getIdUser().getId() == userId) {
                    return "DIRECTOR";
                }
            }

            for (Seller seller : sellerRepository.findAll()) {
                if (seller.getIdUser() != null && seller.getIdUser().getId() == userId) {
                    return "SELLER";
                }
            }

            for (Productionmaster productionmaster : productionmasterRepository.findAll()) {
                if (productionmaster.getIdUser() != null && productionmaster.getIdUser().getId() == userId) {
                    return "PRODUCTIONMASTER";
                }
            }

            return "YES";
        } else {
            return "NO";
        }
    }

    @PostMapping("/getAutarizationTelegramBot")
    public @ResponseBody
    String getAutarizationTelegramBot(@RequestParam(name = "Login") String login, @RequestParam(name = "Password") String password) {
        for (Customer customer : customerRepository.findAll()) {
            if (customer.getLogins().equals(login) && customer.getPasswords().equals(password)) {
                return "CUSTOMER:" + customer.getId();
            }
        }

        int userId = 0;
        for (User user : userRepository.findAll()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                userId = user.getId();
                break;
            }
        }

        if (userId != 0) {
            for (Director director : directorRepository.findAll()) {
                if (director.getIdUser() != null && director.getIdUser().getId() == userId) {
                    return "DIRECTOR:" + userId;
                }
            }

            for (Seller seller : sellerRepository.findAll()) {
                if (seller.getIdUser() != null && seller.getIdUser().getId() == userId) {
                    return "SELLER:" + userId;
                }
            }

            for (Productionmaster productionmaster : productionmasterRepository.findAll()) {
                if (productionmaster.getIdUser() != null && productionmaster.getIdUser().getId() == userId) {
                    return "PRODUCTIONMASTER:" + userId;
                }
            }

            return "EMPLOYEE:" + userId;
        } else {
            return "NO";
        }
    }

    @PostMapping("/getAutarizationSite")
    public String getAutarizationSite(
            @RequestParam(name = "Login") String login,
            @RequestParam(name = "Password") String password,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        for (Customer customer : customerRepository.findAll()) {
            if (customer.getLogins().equals(login) && customer.getPasswords().equals(password)) {
                session.setAttribute("user", customer);
                session.setAttribute("role", "customer");
                redirectAttributes.addFlashAttribute("successMessage", "Авторизация успешна! Вы вошли как пользователь.");
                return "redirect:/api/formspecial";
            }
        }

        int userId = 0;
        for (User user : userRepository.findAll()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                userId = user.getId();
                session.setAttribute("user", user);
                break;
            }
        }

        if (userId != 0) {
            for (Director director : directorRepository.findAll()) {
                if (director.getIdUser() != null && director.getIdUser().getId() == userId) {
                    session.setAttribute("role", "director");
                    redirectAttributes.addFlashAttribute("successMessage", "Авторизация успешна! Вы вошли как директор");
                    return "redirect:/api/formspecial";
                }
            }

            for (Seller seller : sellerRepository.findAll()) {
                if (seller.getIdUser() != null && seller.getIdUser().getId() == userId) {
                    session.setAttribute("role", "seller");
                    redirectAttributes.addFlashAttribute("successMessage", "Авторизация успешна! Вы вошли как продавец");
                    return "redirect:/api/formspecial";
                }
            }

            for (Productionmaster productionmaster : productionmasterRepository.findAll()) {
                if (productionmaster.getIdUser() != null && productionmaster.getIdUser().getId() == userId) {
                    session.setAttribute("role", "productionmaster");
                    redirectAttributes.addFlashAttribute("successMessage", "Авторизация успешна! Вы вошли как мастер производства");
                    return "redirect:/api/formspecial";
                }
            }

            session.setAttribute("role", "employee");
            redirectAttributes.addFlashAttribute("successMessage", "Авторизация успешна! Вы вошли как сотрудник");
            return "redirect:/api/formspecial";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Неверный логин или пароль. Попробуйте снова");
            return "redirect:/api/formauto";
        }
    }

    @PostMapping("/addUser")
    public @ResponseBody
    ResponseEntity<Integer> addUser(
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfEmployment") String dateOfEmployment,
            @RequestParam(name = "PassportData") String passportData,
            @RequestParam(name = "SNILS") String snils,
            @RequestParam(name = "PhotoLink") MultipartFile photoLink,
            @RequestParam(name = "Login") String login,
            @RequestParam(name = "Password") String password) throws IOException {

        User user = new User();
        user.setLastName(lastname);
        user.setFirstName(firstname);
        user.setMiddleName(middleName);
        user.setPhone(phone);

        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirthParsed = null;
        Date dateOfEmploymentParsed = null;

        try {
            dateOfBirthParsed = format.parse(dateOfBirth);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            dateOfEmploymentParsed = format.parse(dateOfEmployment);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        user.setDateOfBirth(dateOfBirthParsed);
        user.setDateOfEmployment(dateOfEmploymentParsed);
        user.setPassportData(passportData);
        user.setSnils(snils);

        if (!photoLink.isEmpty()) {
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\photoLink\\photoLink" + photoLink.getOriginalFilename();
            photoLink.transferTo(new File(filePath));
            user.setPhotoLink(filePath);
        } else {
            user.setPhotoLink(null);
        }

        user.setLogin(login);
        user.setPassword(password);


        User savedUser = userRepository.save(user);
        Integer userId = savedUser.getId(); 
        return ResponseEntity.ok(userId);
    }


    @PostMapping("/updateUser")
    public @ResponseBody
    ResponseEntity<Integer> updateUser(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfEmployment") String dateOfEmployment,
            @RequestParam(name = "PassportData") String passportData,
            @RequestParam(name = "SNILS") String snils,
            @RequestParam(name = "PhotoLink") MultipartFile photoLink,
            @RequestParam(name = "Login") String login,
            @RequestParam(name = "Password") String password) throws IOException {


        User user = userRepository.findById(Integer.parseInt(id)).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setLastName(lastname);
        user.setFirstName(firstname);
        user.setMiddleName(middleName);
        user.setPhone(phone);

        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirthParsed = null;
        Date dateOfEmploymentParsed = null;

        try {
            dateOfBirthParsed = format.parse(dateOfBirth);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            dateOfEmploymentParsed = format.parse(dateOfEmployment);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        user.setDateOfBirth(dateOfBirthParsed);
        user.setDateOfEmployment(dateOfEmploymentParsed);
        user.setPassportData(passportData);
        user.setSnils(snils);

        if (!photoLink.isEmpty()) {
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\photoLink\\photoLink" + photoLink.getOriginalFilename();
            photoLink.transferTo(new File(filePath));
            user.setPhotoLink(filePath);
        }

        user.setLogin(login);
        user.setPassword(password);

        User updatedUser = userRepository.save(user);
        Integer userId = updatedUser.getId();
        
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/deleteUser")
    public @ResponseBody
    boolean deleteUser(@RequestParam(name = "id") String id) {
        userRepository.deleteById(Integer.parseInt(id));
        return true;
    }

    @PostMapping("/addCustomer")
    public @ResponseBody
    ResponseEntity<Integer> addCustomer(
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "Email") String email,
            @RequestParam(name = "Discount") String discount,
            @RequestParam(name = "TotalPurchases") String totalPurchases) throws IOException {

        Customer customer = new Customer();
        customer.setLastName(lastname);
        customer.setFirstName(firstname);
        customer.setMiddleName(middleName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setDiscount(discount);
        customer.setTotalPurchases(totalPurchases);

        Customer savedCustomer = customerRepository.save(customer);
        Integer customerId = savedCustomer.getId();
        return ResponseEntity.ok(customerId);
    }

    @PostMapping("/registerCustomer")
    public @ResponseBody
    ResponseEntity<Integer> registerCustomer(
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "Email") String email,
            @RequestParam(name = "Login") String login,
            @RequestParam(name = "Password") String password) {

        try {
            Customer customer = new Customer();
            customer.setLastName(lastname);
            customer.setFirstName(firstname);
            customer.setMiddleName(middleName);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setLogins(login);
            customer.setPasswords(password);
            customer.setDiscount("0");
            customer.setTotalPurchases("0");

            Customer savedCustomer = customerRepository.save(customer);
            Integer customerId = savedCustomer.getId();
            return ResponseEntity.ok(customerId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(-1);
        }
    }
    @PostMapping("/registrationCustomer")
    public String registrationCustomer(
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "Email") String email,
            @RequestParam(name = "Login") String logins,
            @RequestParam(name = "Password") String passwords,
            RedirectAttributes redirectAttributes) throws IOException {

        if (lastname == null || lastname.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Фамилия' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (firstname == null || firstname.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Имя' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (middleName == null || middleName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Отчество' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (phone == null || phone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Телефон' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Email' не может быть пустым");
            return "redirect:/api/formreg";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Некорректный формат email");
            return "redirect:/api/formreg";
        }

        if (!lastname.matches("^[А-Яа-яЁё\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Фамилия' должно содержать только кириллические буквы");
            return "redirect:/api/formreg";
        }
        if (!firstname.matches("^[А-Яа-яЁё\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Имя' должно содержать только кириллические буквы");
            return "redirect:/api/formreg";
        }
        if (!middleName.matches("^[А-Яа-яЁё\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Отчество' должно содержать только кириллические буквы");
            return "redirect:/api/formreg";
        }

        if (!phone.matches("^[0-9]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Телефон' должно содержать только цифры");
            return "redirect:/api/formreg";
        }

        if (logins == null || logins.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Логин' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (!logins.matches("^[a-zA-Z0-9]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Логин' должно содержать только английские буквы");
            return "redirect:/api/formreg";
        }

        if (passwords == null || passwords.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Пароль' не может быть пустым");
            return "redirect:/api/formreg";
        }
        if (!passwords.matches("^[a-zA-Z0-9]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Поле 'Пароль' должно содержать только английские буквы");
            return "redirect:/api/formreg";
        }

        Customer customer = new Customer();
        customer.setLastName(lastname);
        customer.setFirstName(firstname);
        customer.setMiddleName(middleName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setLogins(logins);
        customer.setPasswords(passwords);
        customer.setDiscount("1");
        customer.setTotalPurchases("0");

        Customer savedCustomer = customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Теперь вы можете войти в систему");

        return "redirect:/api/formauto";
    }


    @PostMapping("/updateCustomer")
    public @ResponseBody
    ResponseEntity<Integer> updateCustomer(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "LastName") String lastname,
            @RequestParam(name = "FirstName") String firstname,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "Email") String email,
            @RequestParam(name = "Discount") String discount,
            @RequestParam(name = "TotalPurchases") String totalPurchases) throws IOException {

        Customer customer = customerRepository.findById(Integer.parseInt(id)).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        customer.setLastName(lastname);
        customer.setFirstName(firstname);
        customer.setMiddleName(middleName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setDiscount(discount);
        customer.setTotalPurchases(totalPurchases);

        Customer updatedCustomer = customerRepository.save(customer);
        Integer customerId = updatedCustomer.getId();
        return ResponseEntity.ok(customerId);
    }

    @PostMapping("/deleteCustomer")
    public @ResponseBody
    boolean deleteCustomer(@RequestParam(name = "id") String id) {
        customerRepository.deleteById(Integer.parseInt(id));
        return true;
    }

    @GetMapping("/getDirector")
    public @ResponseBody
    List allD() {
        List list = new ArrayList();
        for (Director d : directorRepository.findAll()) {
            list.add(d.getIdUser());
        }
        return list;
    }
    
    @PostMapping("/addDirector")
    public @ResponseBody
    boolean addDirector(@RequestParam(name = "idUser") String idUser) {
        Director director = new Director();
        User user = new User(Integer.parseInt(idUser));
        director.setIdUser(user);
        directorRepository.save(director);
        return true;
    }
    
    @PostMapping("/updateDirector")
    public @ResponseBody
    boolean updateDirector(@RequestParam(name = "DirectorId") String directorId,
            @RequestParam(name = "idUser") String idUser) {
        Director director = directorRepository.findById(Integer.parseInt(directorId)).get();
        User user = new User(Integer.parseInt(idUser));
        director.setIdUser(user);
        directorRepository.save(director);
        return true;
    }
    
    @PostMapping("/deleteDirector")
    public @ResponseBody
    boolean deleteDirector(@RequestParam(name = "idUser") String idUser) {
        try {
            int userIdInt = Integer.parseInt(idUser);
            System.out.println("Поиск директора с User ID: " + userIdInt);

            Iterable<Director> directors = directorRepository.findAll();
            for (Director director : directors) {
                if (director.getIdUser() != null && director.getIdUser().getId() == userIdInt) {
                    directorRepository.delete(director);
                    System.out.println("Директор удален успешно!");
                    return true;
                }
            }

            System.out.println("Директора не существует с таким User ID");
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, неправильный ввод");
            return false;
        }
    }
    
    @GetMapping("/getSeller")
    public @ResponseBody
    List allSEL() {
        List list = new ArrayList();
        for (Seller seller : sellerRepository.findAll()) {
            list.add(seller.getIdUser());
        }
        return list;
    }

    @PostMapping("/addSeller")
    public @ResponseBody
    boolean addSeller(@RequestParam(name = "idUser") String idUser) {
        Seller seller = new Seller();
        User user = new User(Integer.parseInt(idUser));
        seller.setIdUser(user);
        sellerRepository.save(seller);
        return true;
    }

    @PostMapping("/updateSeller")
    public @ResponseBody
    boolean updateSeller(@RequestParam(name = "SellerId") String sellerId,
            @RequestParam(name = "idUser") String idUser) {
        Seller seller = sellerRepository.findById(Integer.parseInt(sellerId)).get();
        User user = new User(Integer.parseInt(idUser));
        seller.setIdUser(user);
        sellerRepository.save(seller);
        return true;
    }
    
    @PostMapping("/deleteSeller")
    public @ResponseBody
    boolean deleteSeller(@RequestParam(name = "idUser") String idUser) {
        try {
            int userIdInt = Integer.parseInt(idUser);
            System.out.println("Поиск продавца с таким User ID: " + userIdInt);
            Iterable<Seller> sellers = sellerRepository.findAll();
            for (Seller seller : sellers) {
                if (seller.getIdUser() != null && seller.getIdUser().getId() == userIdInt) {
                    sellerRepository.delete(seller);
                    System.out.println("Продавец успешно удален");
                    return true;
                }
            }
            System.out.println("Не существует продавца с таким User ID");
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, неверный ввод");
            return false;
        }
    }

    @GetMapping("/getProductionmaster")
    public @ResponseBody
    List allPM() {
        List list = new ArrayList();
        for (Productionmaster pm : productionmasterRepository.findAll()) {
            list.add(pm.getIdUser());
        }
        return list;
    }

    @GetMapping("/getProductionmaster2")
    public @ResponseBody
    List<Productionmaster> allPM2() {
        List<Productionmaster> list = new ArrayList<>();
        for (Productionmaster pm : productionmasterRepository.findAll()) {
            list.add(pm);
        }
        return list;
    }
     
    @PostMapping("/addProductionmaster")
    public @ResponseBody
    boolean addProductionmaster(@RequestParam(name = "idUser") String idUser) {
        Productionmaster pm = new Productionmaster();
        User user = new User(Integer.parseInt(idUser));
        pm.setIdUser(user);
        productionmasterRepository.save(pm);
        return true;
    }
    
    @PostMapping("/updateProductionmaster")
    public @ResponseBody
    boolean updateProductionmaster(@RequestParam(name = "ProductionmasterId") String productionmasterId,
            @RequestParam(name = "idUser") String idUser) {
        Productionmaster pm = productionmasterRepository.findById(Integer.parseInt(productionmasterId)).get();
        User user = new User(Integer.parseInt(idUser));
        pm.setIdUser(user);
        productionmasterRepository.save(pm);
        return true;

    }
    
    @PostMapping("/deleteProductionmaster")
    public @ResponseBody
    boolean deleteProductionmaster(@RequestParam(name = "idUser") String idUser) {
        try {
            int userIdInt = Integer.parseInt(idUser);
            System.out.println("Поиск мастера с таким User ID: " + userIdInt);

            Iterable<Productionmaster> pms = productionmasterRepository.findAll();
            for (Productionmaster pm : pms) {
                if (pm.getIdUser() != null && pm.getIdUser().getId() == userIdInt) {
                    productionmasterRepository.delete(pm);
                    System.out.println("Мастер успешно удален");
                    return true;
                }
            }

            System.out.println("Не существует продавца с таким User ID");
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, неверный ввод");
            return false;
        }
    }

    @PostMapping("/addEmbroideryKit")
    public @ResponseBody
    ResponseEntity<Integer> addEmbroideryKit(
            @RequestParam(name = "Name") String name,
            @RequestParam(name = "Description") String description,
            @RequestParam(name = "Price") String price,
            @RequestParam(name = "StockQuantity") String stockQuantity,
            @RequestParam(name = "Image") MultipartFile image) throws IOException {

        EmbroideryKit embroideryKit = new EmbroideryKit();

        embroideryKit.setName(name);
        embroideryKit.setDescription(description);
        embroideryKit.setPrice(price);
        embroideryKit.setStockQuantity(stockQuantity);

        if (!image.isEmpty()) {
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\images\\image" + image.getOriginalFilename();
            image.transferTo(new File(filePath));
            embroideryKit.setImage(filePath);
        } else {
            embroideryKit.setImage(null);
        }

        EmbroideryKit savedEmbroideryKit = embroideryKitRepository.save(embroideryKit);
        Integer embroideryKitId = savedEmbroideryKit.getId();
        return ResponseEntity.ok(embroideryKitId);
    }

    @PostMapping("/updateEmbroideryKit")
    public @ResponseBody
    ResponseEntity<Integer> updateEmbroideryKit(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "Name") String name,
            @RequestParam(name = "Description") String description,
            @RequestParam(name = "Price") String price,
            @RequestParam(name = "StockQuantity") String stockQuantity,
            @RequestParam(name = "Image") MultipartFile image) throws IOException {

        EmbroideryKit embroideryKit = embroideryKitRepository.findById(Integer.parseInt(id)).orElse(null);
        if (embroideryKit == null) {
            return ResponseEntity.notFound().build();
        }

        embroideryKit.setName(name);
        embroideryKit.setDescription(description);
        embroideryKit.setPrice(price);
        embroideryKit.setStockQuantity(stockQuantity);

        if (!image.isEmpty()) {
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\images\\image" + image.getOriginalFilename();
            image.transferTo(new File(filePath));
            embroideryKit.setImage(filePath);
        }

        EmbroideryKit updatedEmbroideryKit = embroideryKitRepository.save(embroideryKit);
        Integer embroideryKitId = updatedEmbroideryKit.getId();
        return ResponseEntity.ok(embroideryKitId);
    }

    @PostMapping("/deleteEmbroideryKit")
    public @ResponseBody
    boolean deleteEmbroideryKit(@RequestParam(name = "id") String id) {
        embroideryKitRepository.deleteById(Integer.parseInt(id));
        return true;
    }

    @PostMapping("/addConsumable")
    public @ResponseBody
    ResponseEntity<Integer> addСonsumable(
            @RequestParam(name = "Name") String name,
            @RequestParam(name = "Description") String description,
            @RequestParam(name = "Price") String price,
            @RequestParam(name = "StockQuantity") String stockQuantity,
            @RequestParam(name = "Unit") String unit) throws IOException {

        Consumable consumable = new Consumable();

        consumable.setName(name);
        consumable.setDescription(description);
        consumable.setPrice(price);
        consumable.setStockQuantity(stockQuantity);
        consumable.setUnit(unit);


        Consumable savedConsumable = consumableRepository.save(consumable);
        Integer consumableId = savedConsumable.getId();
        return ResponseEntity.ok(consumableId);
    }

    @PostMapping("/updateConsumable")
    public @ResponseBody
    ResponseEntity<Integer> updateСonsumable(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "Name") String name,
            @RequestParam(name = "Description") String description,
            @RequestParam(name = "Price") String price,
            @RequestParam(name = "StockQuantity") String stockQuantity,
            @RequestParam(name = "Unit") String unit) throws IOException {

        Consumable consumable = consumableRepository.findById(Integer.parseInt(id)).orElse(null);
        if (consumable == null) {
            return ResponseEntity.notFound().build();
        }

        consumable.setName(name);
        consumable.setDescription(description);
        consumable.setPrice(price);
        consumable.setStockQuantity(stockQuantity);
        consumable.setUnit(unit);

        Consumable updatedConsumable = consumableRepository.save(consumable);
        Integer consumableId = updatedConsumable.getId();
        return ResponseEntity.ok(consumableId);
    }

    @PostMapping("/deleteConsumable")
    public @ResponseBody
    boolean deleteСonsumable(@RequestParam(name = "id") String id) {
        consumableRepository.deleteById(Integer.parseInt(id));
        return true;
    }


    @GetMapping("/formindex")
    public String formindex(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        return "index";
    }

    @GetMapping("/formsvedeniy")
    public String formsvedeniy(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        return "info";
    }

    @GetMapping("/formspecial")
    public String formspecial(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null) {
            return "redirect:/api/formauto";
        }

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        List<Orders> allOrders = new ArrayList<>();
        for (Orders order : ordersRepository.findAll()) {
            allOrders.add(order);
        }

        
        List<CustomFrameOrder> allCustomFrameOrders = new ArrayList<>();
        for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
            allCustomFrameOrders.add(customOrder);
        }

        
        List<Productionmaster> allProductionMasters = new ArrayList<>();
        for (Productionmaster master : productionmasterRepository.findAll()) {
            allProductionMasters.add(master);
        }

        List<Orders> orders = new ArrayList<>();
        List<Orders> freeOrders = new ArrayList<>();

        if ("customer".equals(role) && user instanceof Customer) {
            Customer customer = (Customer) user;
            model.addAttribute("customerId", customer.getId());
            String fullName = customer.getFirstName() + " " +
                    (customer.getMiddleName() != null ? customer.getMiddleName() + " " : "") +
                    customer.getLastName();
            model.addAttribute("customerName", fullName);
            model.addAttribute("discount", customer.getDiscount());
            model.addAttribute("totalPurchases", customer.getTotalPurchases());
            model.addAttribute("email", customer.getEmail());
            model.addAttribute("phone", customer.getPhone());

            for (Orders order : allOrders) {
                if (order.getCustomerID() != null && order.getCustomerID().getId().equals(customer.getId())) {
                    orders.add(order);
                }
            }

            boolean hasCompletedOrder = orders.stream()
                    .anyMatch(order -> "Забран".equals(order.getStatus()));
            model.addAttribute("hasCompletedOrder", hasCompletedOrder);

            boolean hasReview = false;
            Reviews userReview = null;
            List<Reviews> allReviews = toList(reviewsRepository.findAll());
            for (Reviews review : allReviews) {
                if (review.getIdCustomer() != null && review.getIdCustomer().getId().equals(customer.getId())) {
                    hasReview = true;
                    userReview = review;
                    break;
                }
            }
            model.addAttribute("hasReview", hasReview);
            model.addAttribute("userReview", userReview);

        } else if (user instanceof User) {
            User userObj = (User) user;
            model.addAttribute("customerId", userObj.getId());
            model.addAttribute("dateOfBirth", userObj.getDateOfBirth());
            model.addAttribute("dateOfEmployment", userObj.getDateOfEmployment());
            model.addAttribute("passportData", userObj.getPassportData());
            model.addAttribute("snils", userObj.getSnils());
            model.addAttribute("photoLink", userObj.getPhotoLink());
            model.addAttribute("phone", userObj.getPhone());
            String fullName = userObj.getFirstName() + " " +
                    (userObj.getMiddleName() != null ? userObj.getMiddleName() + " " : "") +
                    userObj.getLastName();
            model.addAttribute("customerName", fullName);

            if ("director".equals(role)) {
                orders = allOrders;

                List<Reviews> allReviews = toList(reviewsRepository.findAll());
                model.addAttribute("allReviews", allReviews);
                model.addAttribute("hasReviews", !allReviews.isEmpty());
            }
            else if ("productionmaster".equals(role)) {

                Productionmaster currentMaster = null;
                for (Productionmaster master : allProductionMasters) {
                    if (master.getIdUser() != null && master.getIdUser().getId().equals(userObj.getId())) {
                        currentMaster = master;
                        break;
                    }
                }

                if (currentMaster != null) {
                    System.out.println("Found production master with ID: " + currentMaster.getId() + " for user ID: " + userObj.getId());

                    // Собираем текущие заказы мастера
                    for (Orders order : allOrders) {
                        if (order.getProductionMasterID() != null &&
                                order.getProductionMasterID().getId().equals(currentMaster.getId())) {
                            orders.add(order);
                            System.out.println("Found order in orders table: " + order.getId());
                        }
                    }

                    for (CustomFrameOrder customOrder : allCustomFrameOrders) {
                        if (customOrder.getProductionMasterID() != null &&
                                customOrder.getProductionMasterID().getId().equals(currentMaster.getId())) {

                            for (Orders order : allOrders) {
                                if (customOrder.getOrderID() != null &&
                                        customOrder.getOrderID().getId().equals(order.getId()) &&
                                        !orders.contains(order)) {
                                    orders.add(order);
                                    System.out.println("Found order in custom_frame_order: " + order.getId());
                                    break;
                                }
                            }
                        }
                    }

                    // Собираем свободные заказы (без ProductionMasterID)
                    for (Orders order : allOrders) {
                        if (order.getProductionMasterID() == null &&
                                !"Забран".equals(order.getStatus()) &&
                                !"Отменен".equals(order.getStatus())) {
                            freeOrders.add(order);
                        }
                    }

                    // Также добавляем custom_frame_orders без ProductionMasterID
                    for (CustomFrameOrder customOrder : allCustomFrameOrders) {
                        if (customOrder.getProductionMasterID() == null) {
                            // Находим соответствующий заказ
                            for (Orders order : allOrders) {
                                if (customOrder.getOrderID() != null &&
                                        customOrder.getOrderID().getId().equals(order.getId()) &&
                                        !freeOrders.contains(order) &&
                                        !"Забран".equals(order.getStatus()) &&
                                        !"Отменен".equals(order.getStatus())) {
                                    freeOrders.add(order);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    System.out.println("No production master found for user ID: " + userObj.getId());
                }

                System.out.println("Total orders found for master: " + orders.size());
                System.out.println("Total free orders found: " + freeOrders.size());
            }

            else if ("seller".equals(role)) {
                for (Orders order : allOrders) {
                    if (order.getSellerID() != null && order.getSellerID().getId().equals(userObj.getId())) {
                        orders.add(order);
                    }
                }
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("freeOrders", freeOrders);

        List<Orders> currentOrders = new ArrayList<>();
        List<Orders> historyOrders = new ArrayList<>();

        for (Orders order : orders) {
            String status = order.getStatus();
            if ("Забран".equals(status) || "Отменен".equals(status)) {
                historyOrders.add(order);
            } else {
                currentOrders.add(order);
            }
        }

        model.addAttribute("currentOrders", currentOrders);
        model.addAttribute("historyOrders", historyOrders);

        String lastName = "";
        String initials = "";
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            lastName = customer.getLastName();
            initials = customer.getFirstName().substring(0, 1) + "." +
                    (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
        } else if (user instanceof User) {
            User userObj = (User) user;
            lastName = userObj.getLastName();
            initials = userObj.getFirstName().substring(0, 1) + "." +
                    (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
        }
        model.addAttribute("userDisplayName", lastName + " " + initials);

        return "special";
    }

    @PostMapping("/takeOrder")
    public String takeOrder(@RequestParam("orderId") Integer orderId,
                            @RequestParam("totalAmount") Integer totalAmount,
                            @RequestParam("estimatedMaterialUsage") BigDecimal estimatedMaterialUsage,
                            HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (!"productionmaster".equals(role) || !(user instanceof User)) {
            return "redirect:/api/formauto";
        }

        try {
            Orders order = ordersRepository.findById(orderId).orElse(null);
            if (order != null) {
                User userObj = (User) user;

                // Находим production master для этого пользователя
                Productionmaster currentMaster = null;
                List<Productionmaster> allMasters = new ArrayList<>();
                for (Productionmaster master : productionmasterRepository.findAll()) {
                    allMasters.add(master);
                }
                for (Productionmaster master : allMasters) {
                    if (master.getIdUser() != null && master.getIdUser().getId().equals(userObj.getId())) {
                        currentMaster = master;
                        break;
                    }
                }

                if (currentMaster != null) {
                    // Устанавливаем стоимость и назначаем мастера в orders
                    order.setTotalAmount(totalAmount);
                    order.setProductionMasterID(currentMaster);
                    ordersRepository.save(order);

                    // Также назначаем в custom_frame_order если есть
                    List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                    for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                        allCustomOrders.add(customOrder);
                    }

                    for (CustomFrameOrder customOrder : allCustomOrders) {
                        if (customOrder.getOrderID() != null && customOrder.getOrderID().getId().equals(orderId)) {
                            customOrder.setProductionMasterID(currentMaster);
                            customOrder.setEstimatedMaterialUsage(estimatedMaterialUsage);
                            customFrameOrderRepository.save(customOrder);
                            break;
                        }
                    }

                    System.out.println("Заказ №" + orderId + " взят мастером " + currentMaster.getId() +
                            ", стоимость: " + totalAmount +
                            ", расчетный расход материалов: " + estimatedMaterialUsage);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при взятии заказа: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/api/formspecial";
    }


    @PostMapping("/refuseOrder")
    public String refuseOrder(@RequestParam("orderId") Integer orderId, HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (!"productionmaster".equals(role) || !(user instanceof User)) {
            return "redirect:/api/formauto";
        }

        try {
            Orders order = ordersRepository.findById(orderId).orElse(null);

            if (order != null) {
                User currentUser = (User) user;

                // Находим мастера
                Productionmaster currentMaster = null;
                List<Productionmaster> allMasters = new ArrayList<>();
                for (Productionmaster master : productionmasterRepository.findAll()) {
                    allMasters.add(master);
                }
                for (Productionmaster master : allMasters) {
                    if (master.getIdUser() != null && master.getIdUser().getId().equals(currentUser.getId())) {
                        currentMaster = master;
                        break;
                    }
                }

                if (currentMaster != null) {
                    // Проверяем, принадлежит ли заказ этому мастеру
                    boolean isOrderAssignedToMaster = false;

                    // Проверяем в orders
                    if (order.getProductionMasterID() != null &&
                            order.getProductionMasterID().getId().equals(currentMaster.getId())) {
                        isOrderAssignedToMaster = true;
                    }

                    // Проверяем в custom_frame_order
                    if (!isOrderAssignedToMaster) {
                        List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                        for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                            allCustomOrders.add(customOrder);
                        }

                        for (CustomFrameOrder customOrder : allCustomOrders) {
                            if (customOrder.getOrderID() != null &&
                                    customOrder.getOrderID().getId().equals(order.getId()) &&
                                    customOrder.getProductionMasterID() != null &&
                                    customOrder.getProductionMasterID().getId().equals(currentMaster.getId())) {
                                isOrderAssignedToMaster = true;
                                break;
                            }
                        }
                    }

                    if (isOrderAssignedToMaster) {
                        // Очищаем ProductionMasterID, стоимость и ставим статус "Новый"
                        order.setProductionMasterID(null);
                        order.setTotalAmount(null); // Очищаем стоимость
                        order.setStatus("Новый");

                        // Очищаем ProductionMasterID и EstimatedMaterialUsage в custom_frame_order
                        List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                        for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                            allCustomOrders.add(customOrder);
                        }

                        for (CustomFrameOrder customOrder : allCustomOrders) {
                            if (customOrder.getOrderID() != null &&
                                    customOrder.getOrderID().getId().equals(order.getId())) {
                                customOrder.setProductionMasterID(null);
                                customOrder.setEstimatedMaterialUsage(null); // Очищаем расчетный расход
                                customOrder.setActualMaterialUsage(null); // Очищаем фактический расход
                                customFrameOrderRepository.save(customOrder);
                                break;
                            }
                        }

                        ordersRepository.save(order);
                        System.out.println("Мастер отказался от заказа №" + orderId + ", статус изменен на 'Новый', данные очищены");
                    } else {
                        System.err.println("Мастер не может отказаться от заказа №" + orderId + " - заказ не назначен на него");
                    }
                }
            } else {
                System.err.println("Заказ с ID " + orderId + " не найден.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при отказе от заказа: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/api/formspecial";
    }

    @GetMapping("/takeOrderForm")
    public String showTakeOrderForm(@RequestParam("orderId") Integer orderId, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (!"productionmaster".equals(role) || !(user instanceof User)) {
            return "redirect:/api/formauto";
        }

        Orders order = ordersRepository.findById(orderId).orElse(null);
        if (order != null) {
            model.addAttribute("order", order);

            // Ищем custom_frame_order для этого заказа
            CustomFrameOrder customOrder = null;
            List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
            for (CustomFrameOrder co : customFrameOrderRepository.findAll()) {
                allCustomOrders.add(co);
            }
            for (CustomFrameOrder co : allCustomOrders) {
                if (co.getOrderID() != null && co.getOrderID().getId().equals(orderId)) {
                    customOrder = co;
                    break;
                }
            }
            model.addAttribute("customOrder", customOrder);
        }

        return "take-order-form";
    }

    @GetMapping("/completeOrderForm")
    public String showCompleteOrderForm(@RequestParam("orderId") Integer orderId, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (!"productionmaster".equals(role) || !(user instanceof User)) {
            return "redirect:/api/formauto";
        }

        Orders order = ordersRepository.findById(orderId).orElse(null);
        if (order != null) {
            model.addAttribute("order", order);

            // Ищем custom_frame_order для этого заказа
            CustomFrameOrder customOrder = null;
            List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
            for (CustomFrameOrder co : customFrameOrderRepository.findAll()) {
                allCustomOrders.add(co);
            }
            for (CustomFrameOrder co : allCustomOrders) {
                if (co.getOrderID() != null && co.getOrderID().getId().equals(orderId)) {
                    customOrder = co;
                    break;
                }
            }
            model.addAttribute("customOrder", customOrder);
        }

        return "complete-order-form";
    }

    @PostMapping("/completeOrder")
    public String completeOrder(@RequestParam("orderId") Integer orderId,
                                @RequestParam("actualMaterialUsage") BigDecimal actualMaterialUsage,
                                HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (!"productionmaster".equals(role) || !(user instanceof User)) {
            return "redirect:/api/formauto";
        }

        try {
            Orders order = ordersRepository.findById(orderId).orElse(null);

            if (order != null) {
                User currentUser = (User) user;

                // Находим мастера
                Productionmaster currentMaster = null;
                List<Productionmaster> allMasters = new ArrayList<>();
                for (Productionmaster master : productionmasterRepository.findAll()) {
                    allMasters.add(master);
                }
                for (Productionmaster master : allMasters) {
                    if (master.getIdUser() != null && master.getIdUser().getId().equals(currentUser.getId())) {
                        currentMaster = master;
                        break;
                    }
                }

                if (currentMaster != null) {
                    // Проверяем, принадлежит ли заказ этому мастеру
                    boolean isOrderAssignedToMaster = false;

                    // Проверяем в orders
                    if (order.getProductionMasterID() != null &&
                            order.getProductionMasterID().getId().equals(currentMaster.getId())) {
                        isOrderAssignedToMaster = true;
                    }

                    // Проверяем в custom_frame_order
                    if (!isOrderAssignedToMaster) {
                        List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                        for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                            allCustomOrders.add(customOrder);
                        }

                        for (CustomFrameOrder customOrder : allCustomOrders) {
                            if (customOrder.getOrderID() != null &&
                                    customOrder.getOrderID().getId().equals(order.getId()) &&
                                    customOrder.getProductionMasterID() != null &&
                                    customOrder.getProductionMasterID().getId().equals(currentMaster.getId())) {
                                isOrderAssignedToMaster = true;
                                break;
                            }
                        }
                    }

                    if (isOrderAssignedToMaster) {
                        // Обновляем ActualMaterialUsage в custom_frame_order
                        List<CustomFrameOrder> allCustomOrders = new ArrayList<>();
                        for (CustomFrameOrder customOrder : customFrameOrderRepository.findAll()) {
                            allCustomOrders.add(customOrder);
                        }

                        for (CustomFrameOrder customOrder : allCustomOrders) {
                            if (customOrder.getOrderID() != null &&
                                    customOrder.getOrderID().getId().equals(order.getId())) {
                                customOrder.setActualMaterialUsage(actualMaterialUsage);
                                customFrameOrderRepository.save(customOrder);
                                break;
                            }
                        }

                        // Меняем статус заказа на "Готов"
                        order.setStatus("Готов");
                        ordersRepository.save(order);

                        System.out.println("Заказ №" + orderId + " завершен, фактический расход материалов: " + actualMaterialUsage);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при завершении заказа: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/api/formspecial";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/api/formindex";
    }

    @GetMapping("/formassortiment")
    public String formassortiment(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        List<Consumable> consumables = convertIterableToList(consumableRepository.findAll());
        List<EmbroideryKit> embroideryKits = convertIterableToList(embroideryKitRepository.findAll());

        System.out.println("Loaded consumables: " + consumables.size());
        System.out.println("Loaded embroidery kits: " + embroideryKits.size());

        model.addAttribute("consumables", consumables);
        model.addAttribute("embroideryKits", embroideryKits);

        return "assortiment";
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @GetMapping("/formcontact")
    public String formcontact(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        return "contact";
    }

    @GetMapping("/formyslygs")
    public String formyslygs(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);


        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        return "yslygs";
    }

    @GetMapping("/formotziv")
    public String formotziv(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        List<Reviews> reviews = toList(reviewsRepository.findAll());
        model.addAttribute("reviews", reviews);

        return "otziv";
    }

    @PostMapping("/delete-review/{id}")
    public String deleteReview(@PathVariable("id") Integer id, HttpSession session) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null || !"director".equals(role)) {
            return "redirect:/api/formauto";
        }

        reviewsRepository.deleteById(id);

        return "redirect:/api/formotziv";
    }

    @GetMapping("/formreview")
    public String formReview(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null || !(user instanceof Customer)) {
            return "redirect:/api/formauto";
        }

        Customer customer = (Customer) user;

        boolean hasReview = false;
        List<Reviews> allReviews = toList(reviewsRepository.findAll());
        for (Reviews review : allReviews) {
            if (review.getIdCustomer() != null && review.getIdCustomer().getId().equals(customer.getId())) {
                hasReview = true;
                break;
            }
        }

        if (hasReview) {
            return "redirect:/api/formspecial";
        }

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customerUser = (Customer) user;
                lastName = customerUser.getLastName();
                initials = customerUser.getFirstName().substring(0, 1) + "." +
                        (customerUser.getMiddleName() != null ? customerUser.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        return "review";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam String reviewText,
                            @RequestParam int estimation,
                            HttpSession session,
                            Model model) {

        Object user = session.getAttribute("user");

        if (user == null || !(user instanceof Customer)) {
            return "redirect:/api/formauto";
        }

        Customer customer = (Customer) user;


        boolean hasReview = false;
        List<Reviews> allReviews = toList(reviewsRepository.findAll());
        for (Reviews review : allReviews) {
            if (review.getIdCustomer() != null && review.getIdCustomer().getId().equals(customer.getId())) {
                hasReview = true;
                break;
            }
        }

        if (hasReview) {
            model.addAttribute("errorMessage", "Вы уже оставили отзыв");
            return "redirect:/api/formspecial";
        }

        try {
            Reviews review = new Reviews();
            review.setName(reviewText);
            review.setEstimation(estimation);
            review.setDatereview(new Date());
            review.setIdCustomer(customer);

            reviewsRepository.save(review);

            model.addAttribute("successMessage", "Спасибо за ваш отзыв!");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при сохранении отзыва: " + e.getMessage());
        }

        return "redirect:/api/formspecial";
    }

    @GetMapping("/sales-report")
    public String salesReport(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        
        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        
        if (user == null || !"director".equals(role)) {
            return "redirect:/api/formauto";
        }

        List<Orders> allOrders = toList(ordersRepository.findAll());
        List<Sale> allSales = toList(saleRepository.findAll()); 

        double totalRevenue = 0;
        int totalOrders = allOrders.size();
        int completedOrders = 0;
        int cancelledOrders = 0;
        int newOrders = 0;
        int inProgressOrders = 0;
        int readyOrders = 0;

        List<String> months = new ArrayList<>();
        List<Double> monthlyRevenues = new ArrayList<>();
        List<Integer> monthlyOrderCounts = new ArrayList<>();

        
        for (Sale sale : allSales) {
            if (sale.getFinalAmount() != null) {
                totalRevenue += sale.getFinalAmount();
            } else if (sale.getTotalAmount() != null) {
                
                totalRevenue += sale.getTotalAmount();
            }

            
            if (sale.getSaleDate() != null) {
                String monthKey = new SimpleDateFormat("yyyy-MM").format(sale.getSaleDate());

                boolean monthExists = false;
                for (int i = 0; i < months.size(); i++) {
                    if (months.get(i).equals(monthKey)) {
                        double currentRevenue = monthlyRevenues.get(i);
                        int currentCount = monthlyOrderCounts.get(i);

                        
                        double saleAmount = sale.getFinalAmount() != null ? sale.getFinalAmount() :
                                (sale.getTotalAmount() != null ? sale.getTotalAmount() : 0.0);

                        monthlyRevenues.set(i, currentRevenue + saleAmount);
                        monthlyOrderCounts.set(i, currentCount + 1);
                        monthExists = true;
                        break;
                    }
                }

                if (!monthExists) {
                    months.add(monthKey);
                    double saleAmount = sale.getFinalAmount() != null ? sale.getFinalAmount() :
                            (sale.getTotalAmount() != null ? sale.getTotalAmount() : 0.0);
                    monthlyRevenues.add(saleAmount);
                    monthlyOrderCounts.add(1);
                }
            }
        }

        
        for (Orders order : allOrders) {
            String status = order.getStatus();
            if ("Забран".equals(status)) {
                completedOrders++;
            } else if ("Отменен".equals(status)) {
                cancelledOrders++;
            } else if ("Новый".equals(status)) {
                newOrders++;
            } else if ("Выполняется".equals(status)) {
                inProgressOrders++;
            } else if ("Готов".equals(status)) {
                readyOrders++;
            }
        }

        
        for (int i = 0; i < months.size() - 1; i++) {
            for (int j = i + 1; j < months.size(); j++) {
                if (months.get(i).compareTo(months.get(j)) > 0) {
                    String tempMonth = months.get(i);
                    months.set(i, months.get(j));
                    months.set(j, tempMonth);

                    Double tempRevenue = monthlyRevenues.get(i);
                    monthlyRevenues.set(i, monthlyRevenues.get(j));
                    monthlyRevenues.set(j, tempRevenue);

                    Integer tempCount = monthlyOrderCounts.get(i);
                    monthlyOrderCounts.set(i, monthlyOrderCounts.get(j));
                    monthlyOrderCounts.set(j, tempCount);
                }
            }
        }

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("newOrders", newOrders);
        model.addAttribute("inProgressOrders", inProgressOrders);
        model.addAttribute("readyOrders", readyOrders);
        model.addAttribute("months", months);
        model.addAttribute("monthlyRevenues", monthlyRevenues);
        model.addAttribute("monthlyOrderCounts", monthlyOrderCounts);
        model.addAttribute("conversionRate", totalOrders > 0 ? (completedOrders * 100.0 / totalOrders) : 0);

        return "sales-report";
    }

    @GetMapping("/orders-report")
    public String ordersReport(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        
        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        
        if (user == null || !"director".equals(role)) {
            return "redirect:/api/formauto";
        }

        List<Orders> allOrders = toList(ordersRepository.findAll());

        List<String> statusNames = new ArrayList<>();
        List<Integer> statusCounts = new ArrayList<>();

        List<String> masterNames = new ArrayList<>();
        List<Integer> masterOrderCounts = new ArrayList<>();

        for (Orders order : allOrders) {
            String status = order.getStatus() != null ? order.getStatus() : "Неизвестно";

            boolean statusExists = false;
            for (int i = 0; i < statusNames.size(); i++) {
                if (statusNames.get(i).equals(status)) {
                    statusCounts.set(i, statusCounts.get(i) + 1);
                    statusExists = true;
                    break;
                }
            }

            if (!statusExists) {
                statusNames.add(status);
                statusCounts.add(1);
            }

            if (order.getProductionMasterID() != null) {
                User master = order.getProductionMasterID().getIdUser();
                String masterName = master.getLastName() + " " + master.getFirstName().charAt(0) + ".";

                boolean masterExists = false;
                for (int i = 0; i < masterNames.size(); i++) {
                    if (masterNames.get(i).equals(masterName)) {
                        masterOrderCounts.set(i, masterOrderCounts.get(i) + 1);
                        masterExists = true;
                        break;
                    }
                }

                if (!masterExists) {
                    masterNames.add(masterName);
                    masterOrderCounts.add(1);
                }
            }
        }

        
        for (int i = 0; i < statusCounts.size() - 1; i++) {
            for (int j = i + 1; j < statusCounts.size(); j++) {
                if (statusCounts.get(i) < statusCounts.get(j)) {
                    String tempStatus = statusNames.get(i);
                    statusNames.set(i, statusNames.get(j));
                    statusNames.set(j, tempStatus);

                    Integer tempCount = statusCounts.get(i);
                    statusCounts.set(i, statusCounts.get(j));
                    statusCounts.set(j, tempCount);
                }
            }
        }

        
        for (int i = 0; i < masterOrderCounts.size() - 1; i++) {
            for (int j = i + 1; j < masterOrderCounts.size(); j++) {
                if (masterOrderCounts.get(i) < masterOrderCounts.get(j)) {
                    String tempMaster = masterNames.get(i);
                    masterNames.set(i, masterNames.get(j));
                    masterNames.set(j, tempMaster);

                    Integer tempCount = masterOrderCounts.get(i);
                    masterOrderCounts.set(i, masterOrderCounts.get(j));
                    masterOrderCounts.set(j, tempCount);
                }
            }
        }

        model.addAttribute("allOrders", allOrders);
        model.addAttribute("statusNames", statusNames);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("masterNames", masterNames);
        model.addAttribute("masterOrderCounts", masterOrderCounts);

        return "orders-report";
    }


    @PostMapping("/deleteReview")
    public String deleteReview(HttpSession session, Model model) {
        Object user = session.getAttribute("user");

        if (user == null || !(user instanceof Customer)) {
            return "redirect:/api/formauto";
        }

        Customer customer = (Customer) user;

        try {
            List<Reviews> allReviews = toList(reviewsRepository.findAll());
            for (Reviews review : allReviews) {
                if (review.getIdCustomer() != null && review.getIdCustomer().getId().equals(customer.getId())) {
                    reviewsRepository.delete(review);
                    model.addAttribute("successMessage", "Ваш отзыв удален");
                    break;
                }
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при удалении отзыва: " + e.getMessage());
        }

        return "redirect:/api/formspecial";
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }


    @GetMapping("/formorderspeople")
    public String formorderspeople(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null) {
            return "redirect:/api/formauto";
        }

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        if (user != null) {
            String lastName = "";
            String initials = "";
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                lastName = customer.getLastName();
                initials = customer.getFirstName().substring(0, 1) + "." +
                        (customer.getMiddleName() != null ? customer.getMiddleName().substring(0, 1) + "." : "");
            } else if (user instanceof User) {
                User userObj = (User) user;
                lastName = userObj.getLastName();
                initials = userObj.getFirstName().substring(0, 1) + "." +
                        (userObj.getMiddleName() != null ? userObj.getMiddleName().substring(0, 1) + "." : "");
            }
            model.addAttribute("userDisplayName", lastName + " " + initials);
        }

        List<FrameMaterial> frameMaterials = new ArrayList<>();
        for (FrameMaterial material : frameMaterialRepository.findAll()) {
            frameMaterials.add(material);
        }
        model.addAttribute("frameMaterials", frameMaterials);

        return "orderspeople";
    }

    @PostMapping("/createFrameOrder")
    public String createFrameOrder(
            @RequestParam Integer width,
            @RequestParam Integer height,
            @RequestParam Integer frameMaterialId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String mountType,
            @RequestParam(required = false) String glassType,
            @RequestParam(required = false) String notes,
            HttpSession session,
            Model model) {

        Object user = session.getAttribute("user");

        if (user == null || !(user instanceof Customer)) {
            model.addAttribute("errorMessage", "Для оформления заказа необходимо авторизоваться как покупатель");
            return "redirect:/api/formauto";
        }

        Customer customer = (Customer) user;

        try {
            Orders order = new Orders();
            order.setCustomerID(customer);
            order.setOrderDate(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            order.setDueDate(calendar.getTime());

            order.setStatus("Новый");

            User seller = new User();
            seller.setId(2);
            order.setSellerID(seller);

            FrameMaterial material = frameMaterialRepository.findById(frameMaterialId).orElse(null);
            double basePrice = material != null ? material.getPricePerMeter() : 1000;
            double perimeter = (width + height) * 2 / 1000.0;
            double estimatedAmount = perimeter * basePrice * 1.2;

            order.setTotalAmount((int) estimatedAmount);
            order.setNotes("Заказ рамки: " + (notes != null ? notes : "Индивидуальный заказ"));

            Orders savedOrder = ordersRepository.save(order);

            CustomFrameOrder customFrameOrder = new CustomFrameOrder();
            customFrameOrder.setOrderID(savedOrder);
            customFrameOrder.setWidth(width);
            customFrameOrder.setHeight(height);

            FrameMaterial frameMaterial = new FrameMaterial();
            frameMaterial.setId(frameMaterialId);

            customFrameOrder.setFrameMaterialID(frameMaterial);

            customFrameOrder.setColor(color);
            customFrameOrder.setStyle(style);
            customFrameOrder.setMountType(mountType);
            customFrameOrder.setGlassType(glassType);
            customFrameOrder.setNotes(notes);


            customFrameOrderRepository.save(customFrameOrder);

            model.addAttribute("successMessage", "Заказ успешно оформлен! Номер вашего заказа: " + savedOrder.getId());

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при оформлении заказа: " + e.getMessage());
            e.printStackTrace(); 
        }

        return "redirect:/api/formorderspeople";
    }

    @PostMapping("/cancelOrder")
    public String cancelOrder(@RequestParam Integer orderId, HttpSession session, Model model) {
        Object user = session.getAttribute("user");

        if (user == null) {
            return "redirect:/api/formauto";
        }

        try {
            Optional<Orders> orderOptional = ordersRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                Orders order = orderOptional.get();

                if (user instanceof Customer) {
                    Customer customer = (Customer) user;
                    if (order.getCustomerID() != null && order.getCustomerID().getId().equals(customer.getId())) {
                        order.setStatus("Отменен");
                        ordersRepository.save(order);
                        model.addAttribute("successMessage", "Заказ #" + orderId + " успешно отменен");
                    }
                } else if (user instanceof User) {
                    User userObj = (User) user;
                    if (order.getSellerID() != null && order.getSellerID().getId().equals(userObj.getId())) {
                        order.setStatus("Отменен");
                        ordersRepository.save(order);
                        model.addAttribute("successMessage", "Заказ #" + orderId + " успешно отменен");
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при отмене заказа: " + e.getMessage());
        }

        return "redirect:/api/formspecial";
    }

    @GetMapping(path="/formauto")
    public ModelAndView auto() {
        return new ModelAndView("auto");
    }

    @GetMapping(path="/formreg")
    public ModelAndView reg() {
        return new ModelAndView("reg");
    }
}






