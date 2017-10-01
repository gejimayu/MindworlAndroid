# Mindworld

Mindworld adalah program multi-platform berbasis Android, Unity, dan Arduino di mana pengguna dapat menyimpan catatan, gambar, dan video (disebut *memory*) ke server dengan aplikasi Android. Kemudian, pengguna dapat melihat dan mengeksplorasi *memories* miliknya di dunia virtual 3D pada platform Unity. Platform Arduino berfungsi untuk mengenali lingkungan sekitar pengguna dan mengadaptasi program sesuai kondisi lingkungan, seperti gelap/terang.

### Panduan Instalasi Sistem

1. Clone repository ini `git clone http://gitlab.informatika.org/IF3111-2017-13/android.git`.
2. Buka project ini dari Android Studio.
3. Run 'app' dari Android Studio.
4. Pilih device/emulator tujuan instalasi app.

#### Penggunaan Sistem

- Lakukan *sign in* dengan Google Account pada tampilan awal Mindworld.
- Anda dapat mengunggah *memory* dengan menekan tombol '+' di kanan bawah.
- Anda dapat menulis dan mengunggah catatan dengan menekan tombol bergambar pensil di kanan bawah. (Dapat juga dilakukan dengan mencondongkan device Anda ke belakang.)
- Anda dapat mengunduh *memory* yang sudah diupload dengan menekan kartu *memory* selama beberapa saat.
- Anda dapat menghapus *memory* yang sudah diupload dengan menggeser kartu *memory* ke kanan atau kiri.
- Anda dapat membuka *headline news* hari ini dengan memilih "Read news" di menu kanan atas.
- Anda dapat melakukan *sign out* dengan memilih "Sign out" di menu kanan atas.

### Letak Deliverables

app/src/main/AndroidManifest.xml
app/src/main/java/com/mindworld/howtosurvive/mindworld/*.java
app/src/main/res/**/*.xml
