package group.api.controller;

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

import group.api.entity.Director;
import group.api.entity.Productionmaster;
import group.api.entity.Seller;
import group.api.entity.User;
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
    Iterable<User> allUser() {
        return userRepository.findAll();
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
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\photoLink" + photoLink.getOriginalFilename();
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
            @RequestParam(name = "ID") String id,
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
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\ПРОЕКТ\\photoLink" + photoLink.getOriginalFilename();
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
    boolean addDirector(@RequestParam(name = "IdUser") String IdUser) {
        Director director = new Director();
        User user = new User(Integer.parseInt(IdUser));
        director.setIdUser(user);
        directorRepository.save(director);
        return true;
    }
    
    @PostMapping("/updateDirector")
    public @ResponseBody
    boolean updateDirector(@RequestParam(name = "DirectorId") String directorId,
            @RequestParam(name = "IdUser") String IdUser) {
        Director director = directorRepository.findById(Integer.parseInt(directorId)).get();
        User user = new User(Integer.parseInt(IdUser));
        director.setIdUser(user);
        directorRepository.save(director);
        return true;
    }
    
    @PostMapping("/deleteDirector")
    public @ResponseBody
    boolean deleteDirector(@RequestParam(name = "IdUser") String IdUser) {
        try {
            int userIdInt = Integer.parseInt(IdUser);
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
    boolean addSeller(@RequestParam(name = "IdUser") String IdUser) {
        Seller seller = new Seller();
        User user = new User(Integer.parseInt(IdUser));
        seller.setIdUser(user);
        sellerRepository.save(seller);
        return true;
    }

    @PostMapping("/updateSeller")
    public @ResponseBody
    boolean updateSeller(@RequestParam(name = "SellerId") String sellerId,
            @RequestParam(name = "IdUser") String IdUser) {
        Seller seller = sellerRepository.findById(Integer.parseInt(sellerId)).get();
        User user = new User(Integer.parseInt(IdUser));
        seller.setIdUser(user);
        sellerRepository.save(seller);
        return true;
    }
    
    @PostMapping("/deleteSeller")
    public @ResponseBody
    boolean deleteSeller(@RequestParam(name = "IdUser") String IdUser) {
        try {
            int userIdInt = Integer.parseInt(IdUser);
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
    boolean addProductionmaster(@RequestParam(name = "IdUser") String IdUser) {
        Productionmaster pm = new Productionmaster();
        User user = new User(Integer.parseInt(IdUser));
        pm.setIdUser(user);
        productionmasterRepository.save(pm);
        return true;
    }
    
    @PostMapping("/updateProductionmaster")
    public @ResponseBody
    boolean updateProductionmaster(@RequestParam(name = "ProductionmasterId") String productionmasterId,
            @RequestParam(name = "IdUser") String IdUser) {
        Productionmaster pm = productionmasterRepository.findById(Integer.parseInt(productionmasterId)).get();
        User user = new User(Integer.parseInt(IdUser));
        pm.setIdUser(user);
        productionmasterRepository.save(pm);
        return true;

    }
    
    @PostMapping("/deleteProductionmaster")
    public @ResponseBody
    boolean deleteProductionmaster(@RequestParam(name = "IdUser") String IdUser) {
        try {
            int userIdInt = Integer.parseInt(IdUser);
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
//
//    @GetMapping("/getFaculty")
//    public @ResponseBody
//    Iterable<Faculty> allFaculty() {
//        return facultyRepository.findAll();
//    }
//
//    @PostMapping("/addFaculty")
//    public @ResponseBody
//    boolean addFaculty(@RequestParam(name = "Nazvanie") String nazvanie) {
//
//        Faculty faculty = new Faculty();
//        faculty.setNazvanie(nazvanie);
//        facultyRepository.save(faculty);
//        return true;
//    }
//
//    @PostMapping("/updateFaculty")
//    public @ResponseBody
//    boolean updateFaculty (@RequestParam(name = "id") String id,
//            @RequestParam(name = "Nazvanie") String nazvanie) {
//
//        Faculty faculty = facultyRepository.findById(Integer.parseInt(id)).get();
//        faculty.setNazvanie(nazvanie);
//        facultyRepository.save(faculty);
//        return true;
//    }
//
//    @PostMapping("/deleteFaculty")
//    public @ResponseBody
//    boolean deleteFaculty(@RequestParam(name = "id") String id) {
//        facultyRepository.deleteById(Integer.parseInt(id));
//        return true;
//    }
//
//    @GetMapping("/getSpecialty")
//    public @ResponseBody
//    Iterable<Specialty> allSpecialty() {
//        return specialtyRepository.findAll();
//    }
//
//    @PostMapping("/addSpecialty")
//    public @ResponseBody
//    boolean addSpecialty(@RequestParam(name = "Nazvanie") String nazvanie,
//            @RequestParam(name = "PhotoLink") String photoLink) {
//
//        Specialty specialty = new Specialty();
//        specialty.setNazvanie(nazvanie);
//        specialty.setPhotoLink(photoLink);
//        specialtyRepository.save(specialty);
//        return true;
//    }
//
//    @PostMapping("/updateSpecialty")
//    public @ResponseBody
//    boolean updateSpecialty(@RequestParam(name = "id") String id,
//            @RequestParam(name = "Nazvanie") String nazvanie,
//            @RequestParam(name = "PhotoLink") String photoLink) {
//
//        Specialty specialty = specialtyRepository.findById(Integer.parseInt(id)).get();
//        specialty.setNazvanie(nazvanie);
//        specialty.setPhotoLink(photoLink);
//        specialtyRepository.save(specialty);
//        return true;
//
//    }
//
//    @PostMapping("/deleteSpecialty")
//    public @ResponseBody
//    boolean deleteSpecialty(@RequestParam(name = "id") String id) {
//        specialtyRepository.deleteById(Integer.parseInt(id));
//        return true;
//    }
//
//    @GetMapping("/getStudent")
//    public @ResponseBody
//    List allSTUD() {
//        List list = new ArrayList();
//        for (Student s : studentRepository.findAll()) {
//            list.add(s.getUserId());
//        }
//        return list;
//    }
//
//    @PostMapping("/addStudent")
//    public @ResponseBody
//    boolean addStudent(@RequestParam(name = "UserId") String userId,
//            @RequestParam(name = "FacultyId") String facultyid,
//            @RequestParam(name = "SpecialtyId") String specialtyid,
//            @RequestParam(name = "FamStatus") String famStatus,
//            @RequestParam(name = "DateOfBirth") String dateOfBirth,
//            @RequestParam(name = "DateOfAdmission") String dateOfAdmission,
//            @RequestParam(name = "Course") String course) {
//        Student stud = new Student();
//        Users users = new Users(Integer.parseInt(userId));
//        Faculty faculty = new Faculty(Integer.parseInt(facultyid));
//        Specialty specialty = new Specialty(Integer.parseInt(specialtyid));
//
//        stud.setUserId(users);
//        stud.setFacultyid(faculty);
//        stud.setSpecialtyid(specialty);
//
//        stud.setFamStatus(famStatus);
//
//        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
//        Date dateOfBirthParsed = null;
//        Date dateOfAdmissionParsed = null;
//
//        try {
//            dateOfBirthParsed = format.parse(dateOfBirth);
//        } catch (ParseException ex) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//
//        }
//
//        try {
//            dateOfAdmissionParsed  = format.parse(dateOfAdmission);
//        } catch (ParseException ex) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        stud.setDateOfBirth(dateOfBirthParsed);
//        stud.setDateOfAdmission(dateOfAdmissionParsed);
//        stud.setCourse(Integer.parseInt(course));
//        studentRepository.save(stud);
//        return true;
//    }
//
//    @PostMapping("/updateStudent")
//    public @ResponseBody
//    boolean updateStudent(@RequestParam(name = "StudentId") String studentid,
//            @RequestParam(name = "UserId") String userId,
//            @RequestParam(name = "FacultyId") String facultyid,
//            @RequestParam(name = "SpecialtyId") String specialtyid,
//            @RequestParam(name = "FamStatus") String famStatus,
//            @RequestParam(name = "DateOfBirth") String dateOfBirth,
//            @RequestParam(name = "DateOfAdmission") String dateOfAdmission,
//            @RequestParam(name = "Course") String course) {
//        Student stud = studentRepository.findById(Integer.parseInt(studentid)).get();
//        Users users = new Users(Integer.parseInt(userId));
//        Faculty faculty = new Faculty(Integer.parseInt(facultyid));
//        Specialty specialty = new Specialty(Integer.parseInt(specialtyid));
//
//        stud.setUserId(users);
//        stud.setFacultyid(faculty);
//        stud.setSpecialtyid(specialty);
//
//        stud.setFamStatus(famStatus);
//
//        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
//        Date dateOfBirthParsed = null;
//        Date dateOfAdmissionParsed = null;
//
//        try {
//            dateOfBirthParsed = format.parse(dateOfBirth);
//        } catch (ParseException ex) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//
//        }
//
//        try {
//            dateOfAdmissionParsed  = format.parse(dateOfAdmission);
//        } catch (ParseException ex) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        stud.setDateOfBirth(dateOfBirthParsed);
//        stud.setDateOfAdmission(dateOfAdmissionParsed);
//        stud.setCourse(Integer.parseInt(course));
//        studentRepository.save(stud);
//        return true;
//    }
//
//    @PostMapping("/deleteStudent")
//    public @ResponseBody
//    boolean deleteStudent(@RequestParam(name = "StudentId") String studentid) {
//        try {
//            int id = Integer.parseInt(studentid);
//            if (studentRepository.existsById(id)) {
//                studentRepository.deleteById(id);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @GetMapping("/getLegalRepresentative")
//    public @ResponseBody
//    List allLR() {
//        List list = new ArrayList();
//        for (LegalRepresentative lr : legalrepresentativeRepository.findAll()) {
//            list.add(lr.getStudentid());
//        }
//        return list;
//    }
//
//    @PostMapping("/addLegalRepresentative")
//    public @ResponseBody
//    boolean addLegalRepresentative(@RequestParam(name = "StudentId") String studentid,
//            @RequestParam(name = "SecondName") String secondName,
//            @RequestParam(name = "FirstName") String firstName,
//            @RequestParam(name = "MiddleName") String middleName,
//            @RequestParam(name = "HomeAddress") String homeAddress,
//            @RequestParam(name = "Phone") String phone,
//            @RequestParam(name = "RelationshipDegree") String relationshipDegree) {
//        LegalRepresentative lr = new LegalRepresentative();
//
//        Student student = new Student(Integer.parseInt(studentid));
//
//        lr.setStudentid(student);
//        lr.setSecondName(secondName);
//        lr.setFirstName(firstName);
//        lr.setMiddleName(middleName);
//        lr.setHomeAddress(homeAddress);
//        lr.setPhone(phone);
//        lr.setRelationshipDegree(relationshipDegree);
//
//        legalrepresentativeRepository.save(lr);
//        return true;
//    }
//
//    @PostMapping("/updateLegalRepresentative")
//    public @ResponseBody
//    boolean updateLegalRepresentative(@RequestParam(name = "LegalRepresentativeId") String legalrepresentativeid,
//            @RequestParam(name = "StudentId") String studentid,
//            @RequestParam(name = "SecondName") String secondName,
//            @RequestParam(name = "FirstName") String firstName,
//            @RequestParam(name = "MiddleName") String middleName,
//            @RequestParam(name = "HomeAddress") String homeAddress,
//            @RequestParam(name = "Phone") String phone,
//            @RequestParam(name = "RelationshipDegree") String relationshipDegree) {
//
//        LegalRepresentative lr = legalrepresentativeRepository.findById(Integer.parseInt(legalrepresentativeid)).get();
//        Student student = new Student(Integer.parseInt(studentid));
//
//        lr.setStudentid(student);
//        lr.setSecondName(secondName);
//        lr.setFirstName(firstName);
//        lr.setMiddleName(middleName);
//        lr.setHomeAddress(homeAddress);
//        lr.setPhone(phone);
//        lr.setRelationshipDegree(relationshipDegree);
//
//        legalrepresentativeRepository.save(lr);
//        return true;
//    }
//
//    @PostMapping("/deleteLegalRepresentative")
//    public @ResponseBody
//    boolean deleteLegalRepresentative(@RequestParam(name = "LegalRepresentativeId") String legalrepresentativeid) {
//        try {
//            int id = Integer.parseInt(legalrepresentativeid);
//            if (legalrepresentativeRepository.existsById(id)) {
//                legalrepresentativeRepository.deleteById(id);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @GetMapping("/getFeedback")
//    public @ResponseBody
//    List allF() {
//        List list = new ArrayList();
//        for (Feedback fdb : feedbackRepository.findAll()) {
//            list.add(fdb.getStudentid());
//        }
//        return list;
//    }
//
//    @PostMapping("/addFeedback")
//    public @ResponseBody
//    boolean addFeedback(@RequestParam(name = "StudentId") String studentid,
//            @RequestParam(name = "FeedbackText") String feedbackText) {
//        Feedback fdb = new Feedback();
//        Student student = new Student(Integer.parseInt(studentid));
//
//        fdb.setStudentid(student);
//        fdb.setFeedbackText(feedbackText);
//        feedbackRepository.save(fdb);
//        return true;
//    }
//
//    @PostMapping("/updateFeedback")
//    public @ResponseBody
//    boolean updateFeedback(@RequestParam(name = "FeedbackId") String feedbackid,
//            @RequestParam(name = "StudentId") String studentid,
//            @RequestParam(name = "FeedbackText") String feedbackText) {
//        Feedback fdb = feedbackRepository.findById(Integer.parseInt(feedbackid)).get();
//        Student student = new Student(Integer.parseInt(studentid));
//
//        fdb.setStudentid(student);
//        fdb.setFeedbackText(feedbackText);
//        feedbackRepository.save(fdb);
//        return true;
//    }
//
//    @PostMapping("/deleteFeedback")
//    public @ResponseBody
//    boolean deleteFeedback(@RequestParam(name = "FeedbackId") String feedbackid) {
//        try {
//            int id = Integer.parseInt(feedbackid);
//            if (feedbackRepository.existsById(id)) {
//                feedbackRepository.deleteById(id);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//
//    @GetMapping(path="/formindex")
//    public ModelAndView home() {
//        return new ModelAndView("index");
//    }
//
//    @GetMapping(path="/formsvedeniy")
//    public ModelAndView svedeniy() {
//        return new ModelAndView("info");
//    }
//
//    @GetMapping(path="/formspecial")
//    public ModelAndView special() {
//        return new ModelAndView("special");
//    }
//
//    @GetMapping(path="/formcontact")
//    public ModelAndView contact() {
//        return new ModelAndView("contact");
//    }
//
//    @GetMapping(path="/formotziv")
//    public ModelAndView otziv() {
//        return new ModelAndView("otziv");
//    }
//
//    @GetMapping(path="/formauto")
//    public ModelAndView auto() {
//        return new ModelAndView("auto");
//    }
//
//    @GetMapping(path="/formreg")
//    public ModelAndView reg() {
//        return new ModelAndView("reg");
//    }
//
//    @GetMapping(path="/formstatus")
//    public ModelAndView status() {
//        return new ModelAndView("status");
//    }
//    
    
}






