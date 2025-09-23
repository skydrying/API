package group.api.controller;

import group.api.entity.*;
import group.api.repository.EmbroideryKitRepository;
import group.api.repository.SellerRepository;
import group.api.repository.UserRepository;
import group.api.repository.CustomerRepository;
import group.api.repository.DirectorRepository;
import group.api.repository.ConsumableRepository;
import group.api.repository.SaleItemRepository;
import group.api.repository.SaleRepository;
import group.api.repository.ProductionmasterRepository;
import group.api.repository.OrderItemRepository;
import group.api.repository.FrameMaterialRepository;
import group.api.repository.FrameComponentRepository;
import group.api.repository.CustomFrameOrderRepository;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import group.api.repository.OrdersRepository;

@RestController
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
    private OrdersRepository orderRepository;
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

    @GetMapping("/getOrders")
    public @ResponseBody
    List allOrders() {
        List list = new ArrayList();
        for (Orders order : orderRepository.findAll()) {
            list.add(order);
        }
        return list;
    }

    @GetMapping("/getSales")
    public @ResponseBody
    Iterable<Sale> allSales() {
        return saleRepository.findAll();
    }

    @GetMapping("/getFrameMaterial")
    public @ResponseBody
    Iterable<FrameMaterial> allFrameMaterial() {
        return frameMaterialRepository.findAll();
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
        
        if (userId != 0) {
            return "YES";
        } else {
            return "NO";
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

//    @GetMapping("/getStudent")
//    public @ResponseBody
//    List allSTUD() {
//        List list = new ArrayList();
//        for (Student s : studentRepository.findAll()) {
//            list.add(s.getUserId());
//        }
//        return list;
//    }


    @GetMapping(path="/formindex")
    public ModelAndView home() {
        return new ModelAndView("index");
    }

    @GetMapping(path="/formsvedeniy")
    public ModelAndView svedeniy() {
        return new ModelAndView("info");
    }

    @GetMapping(path="/formspecial")
    public ModelAndView special() {
        return new ModelAndView("special");
    }

    @GetMapping(path="/formcontact")
    public ModelAndView contact() {
        return new ModelAndView("contact");
    }

    @GetMapping(path="/formotziv")
    public ModelAndView otziv() {
        return new ModelAndView("otziv");
    }

    @GetMapping(path="/formauto")
    public ModelAndView auto() {
        return new ModelAndView("auto");
    }

    @GetMapping(path="/formreg")
    public ModelAndView reg() {
        return new ModelAndView("reg");
    }

    @GetMapping(path="/formstatus")
    public ModelAndView status() {
        return new ModelAndView("status");
    }

    
}






