package lisovskiy.studentslab.controller;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lisovskiy.studentslab.entity.ClassTeacher;
import lisovskiy.studentslab.entity.Director;
import lisovskiy.studentslab.entity.Faculty;
import lisovskiy.studentslab.entity.Feedback;
import lisovskiy.studentslab.entity.HeadOfDepartment;
import lisovskiy.studentslab.entity.LegalRepresentative;
import lisovskiy.studentslab.entity.Specialty;
import lisovskiy.studentslab.entity.Student;
import lisovskiy.studentslab.entity.Users;
import lisovskiy.studentslab.repository.ClassTeacherRepository;
import lisovskiy.studentslab.repository.DirectorRepository;
import lisovskiy.studentslab.repository.FacultyRepository;
import lisovskiy.studentslab.repository.FeedbackRepository;
import lisovskiy.studentslab.repository.HeadOfDepartmentRepository;
import lisovskiy.studentslab.repository.LegalRepresentativeRepository;
import lisovskiy.studentslab.repository.SpecialtyRepository;
import lisovskiy.studentslab.repository.StudentRepository;
import lisovskiy.studentslab.repository.UsersRepository;
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

@RestController
@RequestMapping("/studentslab")
public class MainController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private ClassTeacherRepository classteacherRepository;
    @Autowired
    private HeadOfDepartmentRepository headRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private LegalRepresentativeRepository legalrepresentativeRepository;

    @GetMapping("/getUsers")
    public @ResponseBody
    Iterable<Users> allUser() {
        return usersRepository.findAll();
    }

    @PostMapping("/getAutarization")
    public @ResponseBody
    String getAutorization(@RequestParam(name = "Login") String login, @RequestParam(name = "Password") String password) {
        int userId = 0;

    for (Users user : usersRepository.findAll()) {
        if (user.getLogins().equals(login) && user.getPasswords().equals(password)) {
            userId = user.getId();
            break;
        }
    }
    
        for (Director director : directorRepository.findAll()) {
            if (director.getUserId() != null && director.getUserId().getId() == userId) {
                return "DIRECTOR";
            }
        }

        for (ClassTeacher classTeacher : classteacherRepository.findAll()) {
            if (classTeacher.getUserId() != null && classTeacher.getUserId().getId() == userId) {
                return "CLASSTEACHER";
            }
        }

        for (HeadOfDepartment headOfDepartment : headRepository.findAll()) {
            if (headOfDepartment.getUserId() != null && headOfDepartment.getUserId().getId() == userId) {
                return "HEADOFDEPARTMENT";
            }
        }
        
        if (userId != 0) {
            return "YES";
        } else {
            return "NO";
        }
    }
    @PostMapping("/getCurrentPosition")
    public @ResponseBody
    String getCurrentPosition(@RequestParam(name = "UserID") Integer userId) {
        if (directorRepository.existsById(userId)) {
            return "Директор";
        } else if (classteacherRepository.existsById(userId)) {
            return "Классный Руководитель";
        } else if (headRepository.existsById(userId)) {
            return "Заведующий учебной частью";
        }
        return null; // Если должность не найдена
    }

    @PostMapping("/addUser")
    public @ResponseBody
    ResponseEntity<Integer> addUser(
            @RequestParam(name = "FirstName") String firstName,
            @RequestParam(name = "SecondName") String secondName,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfEmployment") String dateOfEmployment,
            @RequestParam(name = "PassportData") String passportData,
            @RequestParam(name = "SNILS") String snils,
            @RequestParam(name = "PhotoLink") MultipartFile photoLink,
            @RequestParam(name = "Logins") String logins,
            @RequestParam(name = "Passwords") String passwords) throws IOException {

        Users user = new Users();
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setMiddleName(middleName);

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
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\PhotoLink\\" + photoLink.getOriginalFilename();
            photoLink.transferTo(new File(filePath));
            user.setPhotoLink(filePath);
        } else {
            user.setPhotoLink(null);
        }

        user.setLogins(logins);
        user.setPasswords(passwords);


        Users savedUser = usersRepository.save(user);
        Integer userId = savedUser.getId(); 
        return ResponseEntity.ok(userId);
    }



    @PostMapping("/updateUser")
    public @ResponseBody
    ResponseEntity<Integer> updateUser(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "FirstName") String firstName,
            @RequestParam(name = "SecondName") String secondName,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfEmployment") String dateOfEmployment,
            @RequestParam(name = "PassportData") String passportData,
            @RequestParam(name = "SNILS") String snils,
            @RequestParam(name = "PhotoLink") MultipartFile photoLink, 
            @RequestParam(name = "Logins") String logins,
            @RequestParam(name = "Passwords") String passwords) throws IOException {


        Users user = usersRepository.findById(Integer.parseInt(id)).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setMiddleName(middleName);

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
            String filePath = "C:\\Users\\oneju\\OneDrive\\Рабочий стол\\PhotoLink\\" + photoLink.getOriginalFilename();
            photoLink.transferTo(new File(filePath));
            user.setPhotoLink(filePath);
        }

        user.setLogins(logins);
        user.setPasswords(passwords);

        Users updatedUser = usersRepository.save(user);
        Integer userId = updatedUser.getId();
        
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/deleteUser")
    public @ResponseBody
    boolean deleteUser(@RequestParam(name = "id") String id) {
        usersRepository.deleteById(Integer.parseInt(id));
        return true;
    }
    
    

    @GetMapping("/getDirector")
    public @ResponseBody
    List allUT() {
        List list = new ArrayList();
        for (Director d : directorRepository.findAll()) {
            list.add(d.getUserId());
        }
        return list;
    }
    
    @PostMapping("/addDirector")
    public @ResponseBody
    boolean addDirector(@RequestParam(name = "UserId") String UserId) {
        Director director = new Director();
        Users users = new Users(Integer.parseInt(UserId));
        director.setUserId(users);
        directorRepository.save(director);
        return true;
    }
    
    @PostMapping("/updateDirector")
    public @ResponseBody
    boolean updateDirector(@RequestParam(name = "DirectorId") String directorId,
            @RequestParam(name = "UserId") String userId) {

        Director director = directorRepository.findById(Integer.parseInt(directorId)).get();
        Users users = new Users(Integer.parseInt(userId));
        
        director.setUserId(users);
        
        directorRepository.save(director);
        return true;

    }
    
    @PostMapping("/deleteDirector")
    public @ResponseBody
    boolean deleteDirector(@RequestParam(name = "UserId") String userId) {
        try {
            int userIdInt = Integer.parseInt(userId);
            System.out.println("Searching for Director with User ID: " + userIdInt);

            Iterable<Director> directors = directorRepository.findAll();
            for (Director director : directors) {
                // Получаем идентификатор пользователя из объекта Users
                if (director.getUserId() != null && director.getUserId().getId() == userIdInt) {
                    directorRepository.delete(director);
                    System.out.println("Director deleted successfully.");
                    return true; // Удаление прошло успешно
                }
            }

            System.out.println("No Director found with that User ID.");
            return false; // Директор не найден
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID format.");
            return false; // Ошибка формата ID
        }
    }
    
    @GetMapping("/getClassTeacher")
    public @ResponseBody
    List allCL() {
        List list = new ArrayList();
        for (ClassTeacher cl : classteacherRepository.findAll()) {
            list.add(cl.getUserId());
        }
        return list;
    }
     
    @PostMapping("/addClassTeacher")
    public @ResponseBody
    boolean addClassTeacher(@RequestParam(name = "UserId") String UserId) {
        ClassTeacher classteacher = new ClassTeacher();
        Users users = new Users(Integer.parseInt(UserId));
        classteacher.setUserId(users);
        classteacherRepository.save(classteacher);
        return true;
    }
    
    
    @PostMapping("/updateClassTeacher")
    public @ResponseBody
    boolean updateClassTeacher(@RequestParam(name = "ClassTeacherId") String classteacherId,
            @RequestParam(name = "UserId") String userId) {

        ClassTeacher classteacher = classteacherRepository.findById(Integer.parseInt(classteacherId)).get();
        Users users = new Users(Integer.parseInt(userId));
        
        classteacher.setUserId(users);
        
        classteacherRepository.save(classteacher);
        return true;

    }
    
    @PostMapping("/deleteClassTeacher")
    public @ResponseBody
    boolean deleteClassTeacher(@RequestParam(name = "UserId") String userId) {
        try {
            int userIdInt = Integer.parseInt(userId);
            System.out.println("Searching for ClassTeacher with User ID: " + userIdInt);

            Iterable<ClassTeacher> classteachers = classteacherRepository.findAll();
            for (ClassTeacher classteacher : classteachers) {
                // Получаем идентификатор пользователя из объекта Users
                if (classteacher.getUserId() != null && classteacher.getUserId().getId() == userIdInt) {
                    classteacherRepository.delete(classteacher);
                    System.out.println("ClassTeacher deleted successfully.");
                    return true; // Удаление прошло успешно
                }
            }

            System.out.println("No ClassTeacher found with that User ID.");
            return false; // Директор не найден
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID format.");
            return false; // Ошибка формата ID
        }
    }
    
    @GetMapping("/getHeadOfDepartment")
    public @ResponseBody
    List allHOD() {
        List list = new ArrayList();
        for (HeadOfDepartment hod : headRepository.findAll()) {
            list.add(hod.getUserId());
        }
        return list;
    }
     
    @PostMapping("/addHeadOfDepartment")
    public @ResponseBody
    boolean addHeadOfDepartment(@RequestParam(name = "UserId") String UserId) {
        HeadOfDepartment headofdep = new HeadOfDepartment();
        Users users = new Users(Integer.parseInt(UserId));
        headofdep.setUserId(users);
        headRepository.save(headofdep);
        return true;
    }
    
    @PostMapping("/updateHeadOfDepartment")
    public @ResponseBody
    boolean updateHeadOfDepartment(@RequestParam(name = "HeadOfDepartmentId") String headofdepartmentId,
            @RequestParam(name = "UserId") String userId) {

        HeadOfDepartment headofdep = headRepository.findById(Integer.parseInt(headofdepartmentId)).get();
        Users users = new Users(Integer.parseInt(userId));
        
        headofdep.setUserId(users);
        
        headRepository.save(headofdep);
        return true;

    }
    
    @PostMapping("/deleteHeadOfDepartment")
    public @ResponseBody
    boolean deleteHeadOfDepartment(@RequestParam(name = "UserId") String userId) {
        try {
            int userIdInt = Integer.parseInt(userId);
            System.out.println("Searching for HeadOfDepartment with User ID: " + userIdInt);

            Iterable<HeadOfDepartment> headOfdepartments = headRepository.findAll();
            for (HeadOfDepartment headOfdepartment : headOfdepartments) {
                // Получаем идентификатор пользователя из объекта Users
                if (headOfdepartment.getUserId() != null && headOfdepartment.getUserId().getId() == userIdInt) {
                    headRepository.delete(headOfdepartment);
                    System.out.println("HeadOfDepartment deleted successfully.");
                    return true; // Удаление прошло успешно
                }
            }

            System.out.println("No HeadOfDepartment found with that User ID.");
            return false; // Директор не найден
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID format.");
            return false; // Ошибка формата ID
        }
    }
    
    @GetMapping("/getFaculty")
    public @ResponseBody
    Iterable<Faculty> allFaculty() {
        return facultyRepository.findAll();
    }
    
    @PostMapping("/addFaculty")
    public @ResponseBody
    boolean addFaculty(@RequestParam(name = "Nazvanie") String nazvanie) {

        Faculty faculty = new Faculty();
        faculty.setNazvanie(nazvanie);
        facultyRepository.save(faculty);
        return true;
    }
    
    @PostMapping("/updateFaculty")
    public @ResponseBody
    boolean updateFaculty (@RequestParam(name = "id") String id,
            @RequestParam(name = "Nazvanie") String nazvanie) {

        Faculty faculty = facultyRepository.findById(Integer.parseInt(id)).get();
        faculty.setNazvanie(nazvanie);
        facultyRepository.save(faculty);
        return true;
    }
    
    @PostMapping("/deleteFaculty")
    public @ResponseBody
    boolean deleteFaculty(@RequestParam(name = "id") String id) {
        facultyRepository.deleteById(Integer.parseInt(id));
        return true;
    }
    
    @GetMapping("/getSpecialty")
    public @ResponseBody
    Iterable<Specialty> allSpecialty() {
        return specialtyRepository.findAll();
    }
    
    @PostMapping("/addSpecialty")
    public @ResponseBody
    boolean addSpecialty(@RequestParam(name = "Nazvanie") String nazvanie,
            @RequestParam(name = "PhotoLink") String photoLink) {

        Specialty specialty = new Specialty();
        specialty.setNazvanie(nazvanie);
        specialty.setPhotoLink(photoLink);
        specialtyRepository.save(specialty);
        return true;
    }
    
    @PostMapping("/updateSpecialty")
    public @ResponseBody
    boolean updateSpecialty(@RequestParam(name = "id") String id,
            @RequestParam(name = "Nazvanie") String nazvanie,
            @RequestParam(name = "PhotoLink") String photoLink) {

        Specialty specialty = specialtyRepository.findById(Integer.parseInt(id)).get();
        specialty.setNazvanie(nazvanie);
        specialty.setPhotoLink(photoLink);
        specialtyRepository.save(specialty);
        return true;

    }
    
    @PostMapping("/deleteSpecialty")
    public @ResponseBody
    boolean deleteSpecialty(@RequestParam(name = "id") String id) {
        specialtyRepository.deleteById(Integer.parseInt(id));
        return true;
    }
    
    @GetMapping("/getStudent")
    public @ResponseBody
    List allSTUD() {
        List list = new ArrayList();
        for (Student s : studentRepository.findAll()) {
            list.add(s.getUserId());
        }
        return list;
    }
     
    @PostMapping("/addStudent")
    public @ResponseBody
    boolean addStudent(@RequestParam(name = "UserId") String userId,
            @RequestParam(name = "FacultyId") String facultyid,
            @RequestParam(name = "SpecialtyId") String specialtyid,
            @RequestParam(name = "FamStatus") String famStatus,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfAdmission") String dateOfAdmission,
            @RequestParam(name = "Course") String course) {
        Student stud = new Student();
        Users users = new Users(Integer.parseInt(userId));
        Faculty faculty = new Faculty(Integer.parseInt(facultyid));
        Specialty specialty = new Specialty(Integer.parseInt(specialtyid));
        
        stud.setUserId(users);
        stud.setFacultyid(faculty);
        stud.setSpecialtyid(specialty);
        
        stud.setFamStatus(famStatus);
        
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirthParsed = null;
        Date dateOfAdmissionParsed = null;

        try {
            dateOfBirthParsed = format.parse(dateOfBirth);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);

        }

        try {
            dateOfAdmissionParsed  = format.parse(dateOfAdmission);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        stud.setDateOfBirth(dateOfBirthParsed);
        stud.setDateOfAdmission(dateOfAdmissionParsed);
        stud.setCourse(Integer.parseInt(course));
        studentRepository.save(stud);
        return true;
    }
    
    @PostMapping("/updateStudent")
    public @ResponseBody
    boolean updateStudent(@RequestParam(name = "StudentId") String studentid,
            @RequestParam(name = "UserId") String userId,
            @RequestParam(name = "FacultyId") String facultyid,
            @RequestParam(name = "SpecialtyId") String specialtyid,
            @RequestParam(name = "FamStatus") String famStatus,
            @RequestParam(name = "DateOfBirth") String dateOfBirth,
            @RequestParam(name = "DateOfAdmission") String dateOfAdmission,
            @RequestParam(name = "Course") String course) {
        Student stud = studentRepository.findById(Integer.parseInt(studentid)).get();
        Users users = new Users(Integer.parseInt(userId));
        Faculty faculty = new Faculty(Integer.parseInt(facultyid));
        Specialty specialty = new Specialty(Integer.parseInt(specialtyid));
        
        stud.setUserId(users);
        stud.setFacultyid(faculty);
        stud.setSpecialtyid(specialty);
        
        stud.setFamStatus(famStatus);
        
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirthParsed = null;
        Date dateOfAdmissionParsed = null;

        try {
            dateOfBirthParsed = format.parse(dateOfBirth);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);

        }

        try {
            dateOfAdmissionParsed  = format.parse(dateOfAdmission);
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        stud.setDateOfBirth(dateOfBirthParsed);
        stud.setDateOfAdmission(dateOfAdmissionParsed);
        stud.setCourse(Integer.parseInt(course));
        studentRepository.save(stud);
        return true;
    }
    
    @PostMapping("/deleteStudent")
    public @ResponseBody
    boolean deleteStudent(@RequestParam(name = "StudentId") String studentid) {
        try {
            int id = Integer.parseInt(studentid);
            if (studentRepository.existsById(id)) {
                studentRepository.deleteById(id);
                return true; 
            } else {
                return false; 
            }
        } catch (NumberFormatException e) {
            return false; 
        }
    }
    
    @GetMapping("/getLegalRepresentative")
    public @ResponseBody
    List allLR() {
        List list = new ArrayList();
        for (LegalRepresentative lr : legalrepresentativeRepository.findAll()) {
            list.add(lr.getStudentid());
        }
        return list;
    }
     
    @PostMapping("/addLegalRepresentative")
    public @ResponseBody
    boolean addLegalRepresentative(@RequestParam(name = "StudentId") String studentid,
            @RequestParam(name = "SecondName") String secondName,
            @RequestParam(name = "FirstName") String firstName,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "HomeAddress") String homeAddress,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "RelationshipDegree") String relationshipDegree) {
        LegalRepresentative lr = new LegalRepresentative();
        
        Student student = new Student(Integer.parseInt(studentid));
        
        lr.setStudentid(student);
        lr.setSecondName(secondName);
        lr.setFirstName(firstName);
        lr.setMiddleName(middleName);
        lr.setHomeAddress(homeAddress);
        lr.setPhone(phone);
        lr.setRelationshipDegree(relationshipDegree);
        
        legalrepresentativeRepository.save(lr);
        return true;
    }
    
    @PostMapping("/updateLegalRepresentative")
    public @ResponseBody
    boolean updateLegalRepresentative(@RequestParam(name = "LegalRepresentativeId") String legalrepresentativeid,
            @RequestParam(name = "StudentId") String studentid,
            @RequestParam(name = "SecondName") String secondName,
            @RequestParam(name = "FirstName") String firstName,
            @RequestParam(name = "MiddleName") String middleName,
            @RequestParam(name = "HomeAddress") String homeAddress,
            @RequestParam(name = "Phone") String phone,
            @RequestParam(name = "RelationshipDegree") String relationshipDegree) {
        
        LegalRepresentative lr = legalrepresentativeRepository.findById(Integer.parseInt(legalrepresentativeid)).get();
        Student student = new Student(Integer.parseInt(studentid));
        
        lr.setStudentid(student);
        lr.setSecondName(secondName);
        lr.setFirstName(firstName);
        lr.setMiddleName(middleName);
        lr.setHomeAddress(homeAddress);
        lr.setPhone(phone);
        lr.setRelationshipDegree(relationshipDegree);
        
        legalrepresentativeRepository.save(lr);
        return true;
    }
    
    @PostMapping("/deleteLegalRepresentative")
    public @ResponseBody
    boolean deleteLegalRepresentative(@RequestParam(name = "LegalRepresentativeId") String legalrepresentativeid) {
        try {
            int id = Integer.parseInt(legalrepresentativeid);
            if (legalrepresentativeRepository.existsById(id)) {
                legalrepresentativeRepository.deleteById(id);
                return true; 
            } else {
                return false; 
            }
        } catch (NumberFormatException e) {
            return false; 
        }
    }

    @GetMapping("/getFeedback")
    public @ResponseBody
    List allF() {
        List list = new ArrayList();
        for (Feedback fdb : feedbackRepository.findAll()) {
            list.add(fdb.getStudentid());
        }
        return list;
    }
     
    @PostMapping("/addFeedback")
    public @ResponseBody
    boolean addFeedback(@RequestParam(name = "StudentId") String studentid,
            @RequestParam(name = "FeedbackText") String feedbackText) {
        Feedback fdb = new Feedback();
        Student student = new Student(Integer.parseInt(studentid));
        
        fdb.setStudentid(student);
        fdb.setFeedbackText(feedbackText);
        feedbackRepository.save(fdb);
        return true;
    }
    
    @PostMapping("/updateFeedback")
    public @ResponseBody
    boolean updateFeedback(@RequestParam(name = "FeedbackId") String feedbackid,
            @RequestParam(name = "StudentId") String studentid,
            @RequestParam(name = "FeedbackText") String feedbackText) {
        Feedback fdb = feedbackRepository.findById(Integer.parseInt(feedbackid)).get();
        Student student = new Student(Integer.parseInt(studentid));
        
        fdb.setStudentid(student);
        fdb.setFeedbackText(feedbackText);
        feedbackRepository.save(fdb);
        return true;
    }
    
    @PostMapping("/deleteFeedback")
    public @ResponseBody
    boolean deleteFeedback(@RequestParam(name = "FeedbackId") String feedbackid) {
        try {
            int id = Integer.parseInt(feedbackid);
            if (feedbackRepository.existsById(id)) {
                feedbackRepository.deleteById(id);
                return true; 
            } else {
                return false; 
            }
        } catch (NumberFormatException e) {
            return false; 
        }
    }
    
    
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






