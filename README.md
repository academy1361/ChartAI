# ChartAI — GitHub Ready

این پروژه برای ساخت APK بدون Android Studio آماده شده است.

## روش آسان با GitHub

1. در GitHub یک Repository جدید بساز.
2. تمام محتویات این پوشه را داخل Repository آپلود کن.
3. به تب **Actions** برو.
4. Workflow با نام **Build ChartAI APK** را انتخاب کن.
5. روی **Run workflow** بزن.
6. بعد از پایان Build، وارد اجرای Workflow شو.
7. در پایین صفحه، از بخش **Artifacts** فایل `ChartAI-debug-apk` را دانلود کن.
8. ZIP دانلودشده را باز کن و `app-debug.apk` را روی گوشی نصب کن.

Workflow به‌صورت خودکار JDK 17، Android SDK و Build پروژه را آماده می‌کند.

## بدون GitHub

اگر بخواهی روی ویندوز Build کنی، JDK 17 و Android SDK لازم است و سپس:
`gradlew.bat assembleDebug`

## وضعیت نسخه
این نسخه اسکلت اجرایی ChartAI است و موتور اولیه RSI/MACD/Ichimoku/PSAR دارد.
تصویر چارت فعلاً نمایش داده می‌شود و تحلیل عددی از داده نمونه انجام می‌شود.
