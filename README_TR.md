# 📸 SnapTile

<p align="center">
  <img src="assets/logo.png" width="96" height="96" alt="SnapTile Logo" />
</p>

<p align="center">
  <b>Yerel Android Hızlı Ayarlar (Quick Settings) & Yüzen Buton Ekran Görüntüsü Uygulaması</b><br>
  <i>Donanım tuşu gerektirmeyen, Hızlı Ayarlar ve Yüzen Buton üzerinden yerel ekran görüntüsü alma aracı.</i>
</p>

<p align="center">
  <a href="README.md">🇬🇧 English</a> | <b>🇹🇷 Türkçe</b>
</p>

<p align="center">
  <a href="https://github.com/akiffurkan/snaptile/releases"><img src="https://img.shields.io/github/v/release/akiffurkan/snaptile?color=blue&style=flat-square" alt="Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android_9.0%2B_(API_28%2B)-green?style=flat-square" alt="Platform">
  <img src="https://img.shields.io/badge/Root-Gerekmez_(No_Root)-brightgreen?style=flat-square" alt="No Root">
  <img src="https://img.shields.io/badge/License-MIT-orange?style=flat-square" alt="License">
</p>

---

## 🎯 Projenin Öncelikli Amacı

SnapTile'ın öncelikli geliştirilme amacı:
- **Eski sürüm Android telefonlarda** ve özellikle **güç (Power) veya ses tuşları arızalanmış, bozulmuş ya da sertleşmiş** cihazlarda hiçbir donanım tuşuna ihtiyaç duymadan tek dokunuşla yerel ekran görüntüsü alabilmektir.
- Çoğu eski cihazda veya arızalı donanıma sahip modellerde ekran görüntüsü almak imkansız hale gelir ya da karmaşık üçüncü taraf root/ADB araçları gerektirir.
- SnapTile; **Root gerektirmeden**, **ekran kaydı veya MediaProjection hilesi kullanmadan**, doğrudan Android işletim sisteminin yerel deklanşör aksiyonunu (`GLOBAL_ACTION_TAKE_SCREENSHOT`) tetikler.

---

## 📱 Cihaz Uyumluluğu & Test Durumu

### 🔬 Test Edilen ve Doğrulanan Cihazlar
- **Samsung Galaxy S10+** — **Android 12 (One UI 4.1)**  
  *(Fiziksel güç tuşu arızalı cihazda başarıyla test edilmiş, bildirim paneli kapanışı, yerel Samsung deklanşör animasyonu, akıllı yakalama paneli ve Galeriye kaydetme eksiksiz doğrulanmıştır.)*

### 📲 Desteklenen Android Sürümleri
SnapTile, Android 9.0 Pie ve üzerindeki tüm sistemlerle tam uyumludur:
- ✅ **Android 9.0 Pie** (API 28)
- ✅ **Android 10** (API 29) — One UI 2.x
- ✅ **Android 11** (API 30) — One UI 3.x
- ✅ **Android 12 & 12L** (API 31/32) — One UI 4.x
- ✅ **Android 13** (API 33) — One UI 5.x
- ✅ **Android 14** (API 34) — One UI 6.x
- ✅ **Android 15** (API 35)

### 🏢 Desteklenen Üreticiler ve Arayüzler
- Samsung (One UI)
- Xiaomi / Redmi / POCO (MIUI / HyperOS)
- Google Pixel (Stock Android)
- OPPO / Realme / OnePlus (ColorOS / OxygenOS)
- Vivo (Funtouch OS)
- Motorola, Huawei, ASUS ve diğer Android 9+ cihazlar.

---

## ⚡ Öne Çıkan Özellikler

1. **Hızlı Ayarlar (QS) Paneli Butonu:**
   - Bildirim panelinde **"Ekran Görüntüsü"** isimli özel kamera simgeli kutucuk.
   - Dokunulduğunda Android 12+ sistem komutu (`GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE`) ile bildirim panelini otomatik olarak kapatır ve alttaki aktif ekranı temizce yakalar.

2. **Taşınabilir Yüzen Buton (Floating Camera Button):**
   - Ekranda her an parmak ucunuzda duran şık kamera butonu.
   - **Her Yere Sürükleme:** Ekranda istediğiniz köşeye veya kenara serbestçe taşıyabilirsiniz.
   - **Akıllı Gizlenme (Fotoğrafta Çıkmaz):** Dokunduğunuz an buton 120 ms görünmez olur, ekran görüntüsü alınır ve buton geri gelir. Böylece çektiğiniz ekran görüntüsünde buton yer almaz.
   - **Basılı Tutunca Gizleme:** Butona basılı tuttuğunuzda yanında **"✕ Gizle"** seçeneği çıkar. Dokunarak ekrandan kaldırabilirsiniz.
   - **Uygulama İçinden Tekrar Açma:** SnapTile uygulamasındaki anahtardan dilediğiniz zaman tek tıkla butonu ekrana geri getirebilirsiniz.

3. **Orijinal Sistem Deneyimi:**
   - Cihazınızın yerel ekran görüntüsü kırpma, düzenleme, akıllı yakalama ve ses efektleri aynen çalışır.
   - Çekilen görüntüler doğrudan cihazın yerel **Galeri / DCIM / Screenshots** klasörüne kaydedilir.

4. **Tam Gizlilik ve Sıfır Ağ İzni:**
   - ❌ İnternet İzni YOK (`android.permission.INTERNET` bulunmaz).
   - ❌ Depolama Okuma İzni YOK.
   - ❌ Ekran Kaydı İzni YOK.
   - ❌ Telemetri, Analitik veya Reklam YOK.

---

## 📥 İndirme ve Kurulum

1. [**Releases**](https://github.com/akiffurkan/snaptile/releases) bölümünden en güncel `SnapTile-v1.0.0.apk` dosyasını indirin.
2. Telefonunuza yükleyin (Gerekirse bilinmeyen kaynaklara izin verin).
3. Uygulamayı açıp **"Erişilebilirliği Etkinleştir"** butonuna dokunun ve **SnapTile Ekran Görüntüsü Servisi**'ni açın.
4. İsteğe göre:
   - **Yüzen Buton:** Uygulama içindeki anahtarı açarak ekranda sürüklenebilir butonu kullanabilirsiniz.
   - **Hızlı Ayarlar Kutucuğu:** Bildirim panelini iki kez aşağı kaydırıp **Düzenle (Kalem / Tuş Sırası)** menüsünden **Ekran Görüntüsü** butonunu aktif butonların arasına sürükleyin.

---

## 🛠️ Kaynak Koddan Derleme

```bash
# Projeyi klonlayın
git clone https://github.com/akiffurkan/snaptile.git
cd snaptile

# Windows'ta derleyin
gradlew.bat assembleDebug

# Linux/macOS'ta derleyin
./gradlew assembleDebug
```

Derlenen APK: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) altında açık kaynak olarak sunulmaktadır.
