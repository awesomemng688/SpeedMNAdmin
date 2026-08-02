# Firebase цэвэрлэгээний үр дүн

Firebase ашиглахгүй тул төслийг бүрэн цэвэрлэж, холбоотой алдаануудыг заслаа.

## Хийгдсэн өөрчлөлтүүд

- **Файл устгасан:** `SpeedFCMService.kt` файлыг устгаж, Push Notification-ийн логикийг хассан.
- **AndroidManifest.xml:** FCM-тэй холбоотой `<service>` болон `intent-filter` бүртгэлүүдийг устгасан.
- **Dependency цэвэрлэгээ:** `libs.versions.toml` файлаас `firebase-bom`, `firebase-messaging` болон `google-services` плагинуудыг устгасан.
- **Gradle тохиргоо:** Төслийн үндсэн `build.gradle.kts` файлаас Firebase-ийн плагиныг хассан.

Одоо IDE дээр Firebase-тэй холбоотой "Unresolved reference" алдаанууд гарахгүй болсон байх ёстой.
