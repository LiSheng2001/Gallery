# Fossify Gallery

<img alt="Logo" src="graphics/icon.webp" width="120" />

<a href='https://play.google.com/store/apps/details?id=org.fossify.gallery'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height=80/></a> <a href="https://f-droid.org/en/packages/org.fossify.gallery/"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on-en.svg" alt="Get it on F-Droid" height=80/></a> <a href="https://apt.izzysoft.de/fdroid/index/apk/org.fossify.gallery"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height=80/></a>

本仓库是[Gallery](https://github.com/FossifyOrg/Gallery)的分支版本，旨在通过由OCR模型和大语言模型构建起来的`Caption`来扩展图像搜索的边界。已完成和打算做的事如下：

- [x] 建立`Captions`表，并在图像视图下提供对文件名和`Caption`内容的综合查询。
- [x] 借助谷歌ML Kit的文字识别模块，离线为图库中的图像建立基于离线OCR模型的`Caption`，通过该实施完成对[#295](https://github.com/FossifyOrg/Gallery/issues/294)功能的添加。
- [x] 实现在搜索结果中左右滑动的效果，修复[#347](https://github.com/FossifyOrg/Gallery/issues/347)。
- [x] 完成对`Captions`表的导入和导出功能。
- [ ] 借助OpenAI的API规范实施对在线或者局域网下的在线`Caption`生成。这将允许使用多模态大语言模型为图像生成语义丰富的`Caption`，或者使用PC端或者服务器端更强大的OCR模型为图像生成更准确的文本`Caption`。目前因为OpenAI尚未实施基于Kotlin的调用库[#70](https://github.com/openai/openai-java/issues/70)，暂缓该功能的实施。如果后续实施，预计可以部分满足[#19](https://github.com/FossifyOrg/Gallery/issues/19)，尽管我认为在手机上运行多模态大语言模型尚存在困难。

Unleash memories, not personal data. Fossify Gallery is the ultimate photo and video app that's as powerful as it is private. No ads, no unnecessary permissions – just a seamless experience tailored for you.

**🖼️ PHOTO EDITING AT YOUR FINGERTIPS:**  
Enhance your photos with our basic yet powerful photo editor. Crop, resize, rotate, flip, draw, and apply stunning filters, all without compromising your privacy. Take control of your memories like never before.

**🌐 PRIVACY FIRST, ALWAYS:**  
Your privacy matters. Ditch the data-hungry giants. Fossify Gallery puts you in control. Strip away EXIF metadata like GPS coordinates and camera details, keeping your memories yours, and yours alone.

**🔒 SUPERIOR SECURITY:**  
Lock down your memories with pin, pattern, or fingerprint protection. Secure specific photos, videos, or the entire app – you decide who gets access. Peace of mind, guaranteed.

**🔄 RECOVER WITH EASE:**  
Breathe easy, accidents happen! Fossify Gallery's built-in recycle bin lets you recover deleted photos and videos in seconds. No more lost treasures, just pure relief.

**🎨 YOUR GALLERY, YOUR STYLE:**  
Customize the look, feel, and functionality to match your style. From UI themes to function buttons, Fossify Gallery gives you the creative freedom you crave.

**📷 UNIVERSAL FORMAT FREEDOM:**  
JPEG, JPEG XL, PNG, MP4, MKV, RAW, SVG, GIF, AVIF, videos, and more – we've got your memories covered, in any format you choose. No restrictions, just limitless possibilities.

**✨ MATERIAL DESIGN WITH DYNAMIC THEMES:**  
Experience the beauty of intuitive material design with dynamic themes. Want more? Dive into custom themes and make your gallery truly unique.

➡️ Explore more Fossify apps: https://www.fossify.org<br>
➡️ Open-Source Code: https://www.github.com/FossifyOrg<br>
➡️ Join the community on Reddit: https://www.reddit.com/r/Fossify<br>
➡️ Connect on Telegram: https://t.me/Fossify

<div align="center">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" width="30%">
</div>
