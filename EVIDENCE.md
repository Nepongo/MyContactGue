# Bukti Implementasi Checklist - My Contact Gue

Dokumen ini menjelaskan bukti kode dan fungsionalitas untuk setiap poin checklist yang telah terpenuhi pada aplikasi **My Contact Gue**.

---

### 1. Input Data (Create)
**Bukti Kode:** `CreateContactFragment.kt` -> fungsi `saveContact()`
```kotlin
val newContact = Contact(name = name, phoneNumber = phone, ...)
contactViewModel.insertContact(newContact)
```
**Penjelasan:** Pengguna dapat memasukkan data melalui form di `CreateContactFragment` dan menyimpannya ke database.

### 2. Validasi Form
**Bukti Kode:** `CreateContactFragment.kt`
```kotlin
if (name.isEmpty()) {
    binding.tilName.error = getString(R.string.error_empty_name)
    return
}
```
**Penjelasan:** Muncul pesan error pada `TextInputLayout` jika kolom Nama atau Nomor Telepon kosong.

### 3. Tampil Data (Read)
**Bukti Kode:** `ContactListFragment.kt`
```kotlin
contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
    contactAdapter.updateList(it)
}
```
**Penjelasan:** Data dari database Room ditampilkan secara real-time di halaman utama menggunakan LiveData.

### 4. Edit Data (Update)
**Bukti Kode:** `CreateContactFragment.kt` -> fungsi `saveContact()`
```kotlin
val updatedContact = contactToEdit!!.copy(name = name, ...)
contactViewModel.updateContact(updatedContact)
```
**Penjelasan:** Pengguna dapat mengubah informasi kontak yang sudah ada melalui layar Edit.

### 5. Hapus Data (Delete)
**Bukti Kode:** `ContactDetailFragment.kt`
```kotlin
contactViewModel.deleteContact(args.contact)
```
**Penjelasan:** Terdapat fitur hapus kontak di layar rincian kontak.

### 6. Dialog Konfirmasi
**Bukti Kode:** `ContactDetailFragment.kt`
```kotlin
MaterialAlertDialogBuilder(requireContext())
    .setTitle("Delete Contact")
    .setMessage("Are you sure?")
    .setPositiveButton("Delete") { ... }
```
**Penjelasan:** Dialog peringatan muncul untuk mencegah penghapusan data yang tidak disengaja.

### 7. Data Persisten
**Bukti Kode:** `ContactDatabase.kt`
```kotlin
@Database(entities = [Contact::class, CallLog::class, Profile::class], version = 3)
abstract class ContactDatabase : RoomDatabase() { ... }
```
**Penjelasan:** Menggunakan database Room sehingga data tetap tersimpan meskipun aplikasi ditutup total.

### 8. RecyclerView
**Bukti Kode:** `fragment_contact_list.xml`
```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rv_contacts" ... />
```
**Penjelasan:** Semua daftar (Kontak, Riwayat, SMS) ditampilkan menggunakan komponen RecyclerView.

### 9. Custom Adapter
**Bukti Kode:** `ContactAdapter.kt`, `CallLogAdapter.kt` (Dynamic Resolution), `SmsAdapter.kt`.
**Penjelasan:** Menggunakan adapter kustom yang mengimplementasikan `ViewHolder` untuk performa tinggi. `CallLogAdapter` secara dinamis mencocokkan nomor telepon dengan nama kontak.

### 10. Custom Item Layout
**Bukti Kode:** `item_contact.xml`
**Penjelasan:** Tampilan item kontak memiliki Gambar (ShapeableImageView), Nama (Title), dan Nomor (Body).

### 11. Scroll Lancar
**Penjelasan:** Penggunaan `ViewHolder` dan `DiffUtil`-like logic (updateList) memastikan scrolling daftar kontak sangat lancar tanpa lag.

### 12. Navigasi Standar
**Bukti Kode:** `activity_main.xml`
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_nav_view" ... />
```
**Penjelasan:** Menggunakan `BottomNavigationView` yang tertanam di `BottomAppBar` sesuai standar Material Design.

### 13. Fragment
**Bukti Kode:** Terdiri dari 7 fragment: `ContactListFragment`, `ContactDetailFragment`, `CreateContactFragment`, `CallLogFragment`, `SmsInboxFragment`, `ChatFragment`, dan `ProfileFragment`.

### 14. Explicit Intent
**Bukti Kode:** `ContactDetailFragment.kt`
```kotlin
val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
startActivity(intent)
```
**Penjelasan:** Menggunakan Intent eksplisit untuk melakukan panggilan telepon dan navigasi antar layar.

### 15. Kirim Data (PutExtra/SafeArgs)
**Bukti Kode:** `nav_graph.xml`
```xml
<argument android:name="contact" app:argType="com.fibonacci.mycontactgue.data.Contact" />
```
**Penjelasan:** Pengiriman data antar layar (misal: Detail ke Chat) menggunakan SafeArgs yang aman dan efisien.

### 16. ConstraintLayout
**Bukti Kode:** Digunakan di hampir seluruh file XML (`fragment_chat.xml`, `item_contact.xml`, dll).

### 17. Material Components
**Bukti Kode:** Menggunakan `MaterialCardView`, `FloatingActionButton`, `TextInputLayout` (OutlinedBox), dan `MaterialToolbar`.

### 18. Manajemen Resource
**Bukti Kode:**
- Warna: `res/values/colors.xml` (Modern Blue Palette)
- Teks: `res/values/strings.xml`
**Penjelasan:** Tidak ada hardcoded warna atau teks di dalam file layout atau Kotlin.

### 19. Feedback User
**Bukti Kode:** `Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show()`
**Penjelasan:** Memberikan respon visual melalui Toast saat aksi Simpan, Update, atau Hapus berhasil.

### 20. Penamaan Variabel
**Penjelasan:** Konsisten menggunakan `camelCase` untuk variabel Kotlin dan `snake_case` untuk ID XML.

### 21. Modular
**Penjelasan:** Kode dipisah secara rapi dalam package `ui` (Fragment, Adapter, ViewModel) dan `data` (Entity, DAO, Database, Repository).

### 22. Clean Project
**Penjelasan:** Seluruh file sampah dan referensi lama (seperti keyword "reminder") telah dihapus dan diganti dengan modul SMS yang terintegrasi.

### 23. Splash Screen
**Bukti Kode:** `AndroidManifest.xml` (Launcher Activity)
**Penjelasan:** Menggunakan System Splash Screen modern yang menampilkan logo secara instan.

### 24. Dark Mode
**Bukti Kode:** `res/values-night/themes.xml`
**Penjelasan:** Warna aplikasi menyesuaikan secara otomatis saat perangkat beralih ke mode gelap dengan skema warna yang tetap estetik.

### 25. Search Bar
**Bukti Kode:** `ContactListFragment.kt` -> `SearchView`
```kotlin
contactViewModel.setSearchQuery(newText.orEmpty())
```
**Penjelasan:** Fitur pencarian kontak berfungsi secara real-time di halaman utama.

### 26. Custom App Icon
**Bukti Kode:** `ic_launcher_foreground.xml` (Adaptive Icon)
**Penjelasan:** Menggunakan ikon kustom (Biru-Putih) yang seragam dengan identitas visual aplikasi.
