# «SmartEdu» Zamonaviy Ta'lim Platformasi

**SmartEdu** — oliy ta'lim muassasalari uchun maxsus ishlab chiqilgan, **Java 17** va **Spring Boot 3** texnologiyalariga asoslangan elektron ta'lim boshqaruv platformasi (LMS).

---

## 🎯 Universitet Texnik Topshirig'i Bajarilishi

| № | Texnik Topshiriq Talabi | Loyihadagi Amalga Oshirilishi |
|---|---|---|
| 1 | **Java va Spring Boot asosida web-ilova ishlab chiqish** | Spring Boot 3.2, Spring Security 6, Spring Data JPA, Thymeleaf arxitekturasi asosida to'liq ishlaydigan tizim ishlab chiqildi. |
| 2 | **Foydalanuvchi sifatida Talaba, O'qituvchi, Administrator rollarini yaratish** | `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT` rollari va har bir rol uchun alohida boshqaruv panellari yaratildi. |
| 3 | **Fanlar va o'quv materiallarini qo'shish** | Fanlar katalogi, mavzular (darslar), PDF, DOCX, PPTX, ZIP fayllar yuklash/yuklab olish va YouTube video darsliklar integratsiyasi qilindi. |
| 4 | **Foydalanuvchi autentifikatsiyasini amalga oshirish** | Spring Security orqali xavfsiz Login, Ro'yxatdan o'tish (Register), BCrypt bilan shifrlangan parollar va rolli ruxsat tizimi. |
| 5 | **Yakunda hisobot shakllantirish** | **PDF formatida** talabaning rasmiy akademik reyting daftarchasi (Transcript) va **Excel (.xlsx) formatida** fan bo'yicha baholar jurnali eksporti. |

---

## 🚀 Qo'shimcha Ilg'or Imkoniyatlar (Bonuslar)

1. **Topshiriqlar va Laboratoriyalar (Assignments):**
   - O'qituvchi topshiriq qo'yadi va muddat (deadline) belgilaydi;
   - Talaba o'z yechimini matn yoki fayl ko'rinishida yuklaydi;
   - O'qituvchi baholaydi (0-100 ball) va izoh (feedback) qoldiradi.
2. **Onlayn Test (Quiz) Tizimi:**
   - O'qituvchi 4 variantli test savollarini tuzadi;
   - Talaba vaqt chegarasi bilan test yechadi va natijani (ball, to'g'ri javoblar foizi) bir zumda ko'radi.
3. **Davomat va O'zlashtirish Monitoringi:**
   - Talabalar har bir darsni "O'zlashtirildi" deb belgilashi va fan bo'yicha progress foizini (% progress bar) kuzatishi mumkin.
4. **Analitika va Grafiklar (Chart.js):**
   - Tizim statistikasi: talabalar soni, fanlar qamrovi bo'yicha dinamik diagrammalar.
5. **E'lonlar Doskasi:**
   - O'qituvchi fan talabalariga tezkor e'lon va yangiliklarni yetkazishi mumkin.

---

## 🔑 Dastlabki Test Akkauntlar

Dasturni sinab ko'rish va o'qituvchiga ko'rsatish uchun tizim quyidagi tayyor akkauntlar bilan ta'minlangan:

| Rol | Login | Parol | F.I.Sh |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Alisher Qodirov |
| **O'qituvchi** | `teacher` | `teacher123` | Jasur Karimov |
| **O'qituvchi 2** | `teacher2` | `teacher123` | Nilufar Rahimova |
| **Talaba 1** | `student` | `student123` | Sardor Mahmudov |
| **Talaba 2** | `student2` | `student123` | Madina Usmonova |
| **Talaba 3** | `student3` | `student123` | Bobur Rustamov |

> *Eslatma: Kirish sahifasida tezkor to'ldirish (1-klikda to'ldirish) tugmalari ham mavjud.*

---

## 💻 Loyihani Ishga Tushirish

### 1-usul: Tayyor Windows skripti orqali (Tavsiya etiladi)
Loyiha papkasidagi **`run.bat`** faylini ikki marta bosing. Loyiha avtomatik tarzda ishga tushadi.

### 2-usul: Buyruqlar qatori orqali
```bash
.\mvnw.cmd spring-boot:run
```

Loyiha ishga tushgach, brauzeringizda quyidagi manzilni oching:
👉 **`http://localhost:8080`**

### Ma'lumotlar bazasi boshqaruvi:
- **H2 Web Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/smartedu_db`
- Foydalanuvchi: `sa`
- Parol: *(bo'sh qoldiring)*

---

## 📁 Loyiha Strukturasi

```
SmartEdu/
├── pom.xml                                    # Maven qaramliklari (Spring Boot, Security, POI, OpenPDF)
├── mvnw & mvnw.cmd                            # Maven Wrapper
├── run.bat                                    # 1-klikda ishga tushirish skripti
├── README.md                                  # Loyiha hujjati
└── src/main/
    ├── java/com/smartedu/
    │   ├── SmartEduApplication.java           # Asosiy kirish nuqtasi
    │   ├── config/
    │   │   ├── SecurityConfig.java            # Rollar va xavfsizlik filtrlari
    │   │   └── DataInitializer.java           # Boshlang'ich test ma'lumotlar
    │   ├── controller/
    │   │   ├── AuthController.java            # Kirish, ro'yxatdan o'tish, profil
    │   │   ├── DashboardController.java       # Rolga qarab yo'naltirish
    │   │   ├── AdminController.java           # Admin paneli (foydalanuvchilar, fanlar)
    │   │   ├── TeacherController.java         # O'qituvchi kabineti (darslar, vazifalar, baholash)
    │   │   ├── StudentController.java         # Talaba kabineti (darslar, topshiriqlar, testlar)
    │   │   ├── ReportController.java          # PDF va Excel hisobotlar
    │   │   └── MaterialController.java        # Fayllarni yuklab olish
    │   ├── model/                             # Entity ma'lumot modellari
    │   ├── repository/                        # Spring Data JPA repozitoriylari
    │   └── service/                           # Biznes mantiq, fayl saqlash va hisobotlar
    └── resources/
        ├── application.properties             # Tizim sozlamalari
        └── templates/                         # Thymeleaf HTML sahifalari
```
