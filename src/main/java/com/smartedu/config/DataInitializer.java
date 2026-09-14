package com.smartedu.config;

import com.smartedu.model.*;
import com.smartedu.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizResultRepository quizResultRepository;
    private final AnnouncementRepository announcementRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CourseRepository courseRepository,
                           LessonRepository lessonRepository,
                           MaterialRepository materialRepository,
                           AssignmentRepository assignmentRepository,
                           SubmissionRepository submissionRepository,
                           QuizRepository quizRepository,
                           QuizQuestionRepository quizQuestionRepository,
                           QuizResultRepository quizResultRepository,
                           AnnouncementRepository announcementRepository,
                           LessonProgressRepository lessonProgressRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizResultRepository = quizResultRepository;
        this.announcementRepository = announcementRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Admin & Teachers
        User admin = new User("admin", passwordEncoder.encode("admin123"), "Alisher Qodirov", "admin@smartedu.uz", "+998 90 123 45 67", Role.ROLE_ADMIN);
        User teacher1 = new User("teacher", passwordEncoder.encode("teacher123"), "Jasur Karimov", "teacher@smartedu.uz", "+998 91 234 56 78", Role.ROLE_TEACHER);
        User teacher2 = new User("teacher2", passwordEncoder.encode("teacher123"), "Nilufar Rahimova", "rahimova@smartedu.uz", "+998 93 345 67 89", Role.ROLE_TEACHER);

        admin = userRepository.save(admin);
        teacher1 = userRepository.save(teacher1);
        teacher2 = userRepository.save(teacher2);

        // 2. Save Courses first
        Course course1 = new Course(
                "Java va Spring Boot Asoslari",
                "CS101",
                "Zamonaviy korporativ web dasturlar ishlab chiqish: Java Core, OOP, Spring Boot freymvorki, Spring Security va REST API texnologiyalari.",
                "Dasturlash",
                4,
                3,
                teacher1
        );
        course1 = courseRepository.save(course1);

        Course course2 = new Course(
                "Ma'lumotlar Bazasi va SQL",
                "CS102",
                "Relyatsion ma'lumotlar bazasi boshqaruvi, SQL so'rovlari (DML, DDL), tranzaksiyalar va normallashtirish tamoyillari.",
                "Ma'lumotlar bazasi",
                3,
                3,
                teacher2
        );
        course2 = courseRepository.save(course2);

        Course course3 = new Course(
                "Web Dasturlash Asoslari (HTML, CSS, JS)",
                "CS103",
                "Zamonaviy veb-saytlar yaratish: HTML5 semantik teglari, CSS3 fleksboks va grid, zamonaviy JavaScript (ES6+) asoslari.",
                "Web Dasturlash",
                3,
                2,
                teacher1
        );
        course3 = courseRepository.save(course3);

        // 3. 32 Official University Students from User Request
        String[][] studentsData = {
            {"bilolov_e", "BILOLOV ELYOR BAHTIYOR O‘G‘LI", "elyor@smartedu.uz"},
            {"faxriddinov_g", "FAXRIDDINOV G‘IYOSIDDIN FAXRIDDIN O‘G‘LI", "giyosiddin@smartedu.uz"},
            {"fazliddinov_u", "FAZLIDDINOV UMIDJON SHAVKAT O‘G‘LI", "umidjon.f@smartedu.uz"},
            {"ismonaliyev_sh", "ISMONALIYEV SHUKURILLO ILHOMJON O‘G‘LI", "shukurillo@smartedu.uz"},
            {"jalliyev_f", "JALLIYEV FERUZ XUSENOVICH", "feruz@smartedu.uz"},
            {"jumayev_m", "JUMAYEV MUZAFFAR FAXRITDIN O‘G‘LI", "muzaffar@smartedu.uz"},
            {"karimov_a", "KARIMOV AKROM IKRAM O‘G‘LI", "akrom@smartedu.uz"},
            {"kospayev_b", "KOSPAYEV BERDIYAR KONISOVICH", "berdiyar@smartedu.uz"},
            {"matkarimova_n", "MATKARIMOVA NOILA ASHIRBOYEVNA", "noila@smartedu.uz"},
            {"meliqoziyev_m", "MELIQO‘ZIYEV MUSLIMJON MAXSUDJON O‘G‘LI", "muslimjon@smartedu.uz"},
            {"nigmatov_t", "NIGMATOV TOXIR ISMATULLA O‘G‘LI", "toxir@smartedu.uz"},
            {"nuritdinov_a", "NURITDINOV ABDUMALIK SHAVKAT O‘G‘LI", "abdumalik@smartedu.uz"},
            {"parpiyev_s", "PARPIYEV SARDOR ADILJANOVICH", "sardor.p@smartedu.uz"},
            {"qalandarov_k", "QALANDAROV KAMRONBEK QAXRAMON O‘G‘LI", "kamronbek@smartedu.uz"},
            {"reymbayev_sh", "REYMBAYEV SHAXZOD PARAXAT ULI", "shaxzod@smartedu.uz"},
            {"roxatov_f", "ROXATOV FAZLIDDIN TIMUR O‘G‘LI", "fazliddin@smartedu.uz"},
            {"roziboyev_u", "RO‘ZIBOYEV ULMASBEK ULASH O‘G‘LI", "ulmasbek@smartedu.uz"},
            {"rustamov_s", "RUSTAMOV SAYFULLA XUDAYBERDI O‘G‘LI", "sayfulla@smartedu.uz"},
            {"shodiyev_u", "SHODIYEV UMIDJON ANVAR O‘G‘LI", "umidjon.sh@smartedu.uz"},
            {"subonova_d", "SUBONOVA DURDONAXON MA`MUR QIZI", "durdona@smartedu.uz"},
            {"tangirov_s", "TANGIROV SUNNAT RAJAB O‘G‘LI", "sunnat@smartedu.uz"},
            {"turdiboyev_a", "TURDIBOYEV ABDUAZIM ABDULAZIZ O‘G‘LI", "abduazim@smartedu.uz"},
            {"ubaydullaxojayev_a", "UBAYDULLAXO‘JAYEV ATXAMXO‘JA ZIYODULLAXO‘JA O‘G‘LI", "atxamxoja@smartedu.uz"},
            {"ubaydullayev_m", "UBAYDULLAYEV MURODILLA QUTFULLAYEVICH", "murodilla@smartedu.uz"},
            {"umarov_u", "UMAROV ULUG‘BEK HAMZA O‘G‘LI", "ulugbek@smartedu.uz"},
            {"umarova_s", "UMAROVA SEVINCH LUTFILLA QIZI", "sevinch@smartedu.uz"},
            {"vafayev_j", "VAFAYEV JALOLBEK ERKINBOY O‘G‘LI", "jalolbek@smartedu.uz"},
            {"xakimullin_r", "XAKIMULLIN RUMIL KAMILEVICH", "rumil@smartedu.uz"},
            {"xamzayev_o", "XAMZAYEV OYBEK ANVAR O‘G‘LI", "oybek@smartedu.uz"},
            {"xudoyberdiyev_r", "XUDOYBERDIYEV RAXMATULLO OBIDXUJA O‘G‘LI", "raxmatullo@smartedu.uz"},
            {"yuldasheva_u", "YULDASHEVA UMIDA ISRAIL QIZI", "umida@smartedu.uz"},
            {"ziyayev_s", "ZIYAYEV SANJAR ABDUGANIYEVICH", "sanjar@smartedu.uz"}
        };

        // Standard demo student
        User studentDemo = new User("student", passwordEncoder.encode("student123"), "Sardor Mahmudov (Demo)", "student@smartedu.uz", "+998 94 456 78 90", Role.ROLE_STUDENT);
        studentDemo.getEnrolledCourses().add(course1);
        studentDemo.getEnrolledCourses().add(course2);
        studentDemo = userRepository.save(studentDemo);

        course1.getStudents().add(studentDemo);
        course2.getStudents().add(studentDemo);

        List<User> studentList = new ArrayList<>();
        studentList.add(studentDemo);

        for (int i = 0; i < studentsData.length; i++) {
            String uName = studentsData[i][0];
            String fName = studentsData[i][1];
            String email = studentsData[i][2];
            String phone = String.format("+998 90 %03d %02d %02d", 100 + i * 7, (i * 3) % 90 + 10, (i * 5) % 90 + 10);

            User st = new User(uName, passwordEncoder.encode("student123"), fName, email, phone, Role.ROLE_STUDENT);
            st.getEnrolledCourses().add(course1);
            st.getEnrolledCourses().add(course2);
            st = userRepository.save(st);

            course1.getStudents().add(st);
            course2.getStudents().add(st);
            studentList.add(st);
        }

        course1 = courseRepository.save(course1);
        course2 = courseRepository.save(course2);

        // Lessons for Course 1
        Lesson c1l1 = new Lesson(course1, "1-mavzu: Java tiliga kirish va OOP tamoyillari", 1,
                "Java - bu obyektga yo'naltirilgan, platformaga bog'liq bo'lmagan, yuqori darajadagi dasturlash tili. Uning asosiy tamoyillari: Inkapsulyatsiya (Encapsulation), Vorislik (Inheritance), Polimorfizm (Polymorphism) va Abstraksiya (Abstraction).\n\n" +
                "Klass - bu obyekt uchun andoza (blueprint), Obyekt esa klassning aniq nusxasidir. Java dasturi JVM (Java Virtual Machine) orqali baytkod ko'rinishida bajariladi.",
                "https://www.youtube.com/embed/eIrMbAQSU34");
        lessonRepository.save(c1l1);

        Material m1 = new Material(c1l1, "1-Mavzu Taqdimoti (PPT)", "https://docs.oracle.com/javase/tutorial/", "LINK");
        Material m2 = new Material(c1l1, "Java OOP Asoslari - Rasmiy Qo'llanma", "https://spring.io/guides", "LINK");
        materialRepository.save(m1);
        materialRepository.save(m2);

        Lesson c1l2 = new Lesson(course1, "2-mavzu: Spring Boot arxitekturasi va REST API", 2,
                "Spring Boot dasturlarni oson sozlash va tezkor ishga tushirish uchun qulay freymvorkdir. Inversion of Control (IoC) va Dependency Injection (DI) uning yadrosini tashkil qiladi.\n\n" +
                "@RestController, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping annotatsiyalari orqali RESTful web-servislar quriladi.",
                "https://www.youtube.com/embed/9SGDpanrc8U");
        lessonRepository.save(c1l2);

        Lesson c1l3 = new Lesson(course1, "3-mavzu: Spring Data JPA va Ma'lumotlar Bazasi", 3,
                "Spring Data JPA ma'lumotlar bazasi bilan ishlashni soddalashtiradi. JpaRepository interfeysi CRUD amallarini avtomatik ta'minlaydi. Entity klasslari orqali jadvallar boshqariladi.",
                null);
        lessonRepository.save(c1l3);

        Lesson c1l4 = new Lesson(course1, "4-mavzu: Spring Security va Autentifikatsiya", 4,
                "Spring Security ilovalarni himoya qilish, foydalanuvchilarni autentifikatsiya va avtorizatsiya qilish uchun kuchli xavfsizlik tizimidir. BCrypt parollarni heshlash uchun ishlatiladi.",
                null);
        lessonRepository.save(c1l4);

        // Course 1 Announcement
        Announcement ann1 = new Announcement(course1, teacher1, "1-Laboratoriya ishi topshirig'i yuklandi!",
                "Assalomu alaykum talabalar! 1-Laboratoriya topshirig'i tizimga joylashtirildi. Barcha 32 nafar talaba belgilangan muddatgacha javoblarini yuborishlari shart.");
        announcementRepository.save(ann1);

        // Assignments
        Assignment assign1 = new Assignment(course1, "1-Laboratoriya: OOP tamoyillari asosida Talabalar tizimini tuzish",
                "Ushbu topshiriqda Talaba, O'qituvchi va Kurs klasslarini yaratib, ularning vorislik va inkapsulyatsiya xususiyatlarini kod orqali namoyish etishingiz kerak.",
                LocalDateTime.now().plusDays(7), 100);
        assignmentRepository.save(assign1);

        Assignment assign2 = new Assignment(course1, "2-Laboratoriya: Spring Boot CRUD API yaratish",
                "Fanlar va talabalar uchun REST API kontrollerlarini ishlab chiqish va ularni Postman orqali tekshirish.",
                LocalDateTime.now().plusDays(14), 100);
        assignmentRepository.save(assign2);

        // Quiz for Course 1
        Quiz quiz1 = new Quiz(course1, "Java & Spring Boot Oraliq Nazorat Testi",
                "Ushbu test Java sintaksisi, OOP tamoyillari va Spring Boot asoslari bo'yicha 5 ta savoldan iborat.", 15);
        quizRepository.save(quiz1);

        quizQuestionRepository.save(new QuizQuestion(quiz1,
                "Java dasturlash tilida barcha klasslarning asosiy ota klassi qaysi?",
                "Class", "Object", "Main", "Base", "B"));

        quizQuestionRepository.save(new QuizQuestion(quiz1,
                "Spring Boot'da RESTful kontroller yaratish uchun qaysi annotatsiya ishlatiladi?",
                "@Controller", "@Service", "@RestController", "@Component", "C"));

        quizQuestionRepository.save(new QuizQuestion(quiz1,
                "Qaysi tamoyil ma'lumotlarni tashqi nojo'ya ta'sirlardan yashirishni anglatadi?",
                "Polimorfizm", "Vorislik", "Abstraksiya", "Inkapsulyatsiya", "D"));

        quizQuestionRepository.save(new QuizQuestion(quiz1,
                "Spring Data JPA'da CRUD operatsiyalari uchun qaysi asosiy interfeys kengaytiriladi?",
                "JpaRepository", "SqlRepository", "DatabaseRepository", "CrudHandler", "A"));

        quizQuestionRepository.save(new QuizQuestion(quiz1,
                "Java Virtual Machine (JVM) qanday formatdagi fayllarni bajaradi?",
                ".java", ".class (baytkod)", ".exe", ".jar faqat", "B"));

        // Generate Submissions & Quiz Results for ALL 32 Students
        int[] scores = {95, 88, 92, 85, 90, 94, 87, 89, 96, 91, 84, 93, 86, 90, 88, 92, 85, 94, 89, 95, 86, 91, 87, 93, 90, 96, 88, 85, 92, 89, 94, 91, 95};
        int[] quizCorrects = {5, 4, 5, 4, 5, 5, 4, 4, 5, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 4, 5, 4, 5, 4, 5};

        for (int i = 0; i < studentList.size(); i++) {
            User st = studentList.get(i);
            int score = scores[i % scores.length];
            int correct = quizCorrects[i % quizCorrects.length];

            // 1. Assignment submission
            Submission sub = new Submission(assign1, st,
                    "1-laboratoriya ishi topshirildi. Barcha klasslar, inkapsulyatsiya va polimorfizm metodlari dasturlashtirildi.",
                    null, null);
            sub.setScore(score);
            sub.setTeacherFeedback(score >= 90 ? "A'lo darajada bajarilgan! Kod strukturasi toza va tushunarli." : "Yaxshi ishlangan, qo'shimcha izohlar qo'shish tavsiya etiladi.");
            sub.setGradedBy(teacher1);
            sub.setGradedAt(LocalDateTime.now().minusHours(i + 1));
            sub.setStatus(SubmissionStatus.GRADED);
            submissionRepository.save(sub);

            // 2. Quiz result
            QuizResult qr = new QuizResult(quiz1, st, 5, correct);
            quizResultRepository.save(qr);

            // 3. Lesson Progress
            lessonProgressRepository.save(new LessonProgress(st, c1l1, true));
            if (score >= 90) {
                lessonProgressRepository.save(new LessonProgress(st, c1l2, true));
            }
        }
    }
}
