# Firebase-тэй холбоотой кодыг устгах төлөвлөгөө

Хэрэглэгч Firebase ашиглахгүй гэж мэдэгдсэн тул төслөөс Firebase-тэй холбоотой бүх код болон тохиргоог цэвэрлэх шаардлагатай байна. Одоогийн байдлаар `SpeedFCMService.kt` файл дээр Firebase-ийн сангууд олдохгүй байгаагаас болж алдаа зааж байна.

## Санал болгож буй өөрчлөлтүүд

### [App Component]

#### [DELETE] [SpeedFCMService.kt](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/app/src/main/java/mn/speed/admin/utils/SpeedFCMService.kt)
Firebase Cloud Messaging-ийг хариуцаж буй файлыг бүрмөсөн устгана.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/app/src/main/AndroidManifest.xml)
`<service>` хэсэгт байгаа `SpeedFCMService`-ийн бүртгэлийг устгана.

### [Build & Gradle]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/gradle/libs.versions.toml)
Firebase-тэй холбоотой хувилбарууд (versions), сангууд (libraries) болон плагин (plugins)-ийг устгана.

#### [MODIFY] [build.gradle.kts (Project level)](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/build.gradle.kts)
`google-services` плагиныг устгана.

## Шалгах төлөвлөгөө

### Автомат тестүүд
- Төслийг амжилттай build хийгдэж байгаа эсэхийг шалгана: `./gradlew assembleDebug`

### Гар аргаар шалгах
- `SpeedFCMService.kt` файл болон холбоотой алдаанууд IDE-ээс алга болсон эсэхийг нягтална.
- AndroidManifest.xml дээр илүү дутуу Firebase тохиргоо үлдсэн эсэхийг шалгана.
