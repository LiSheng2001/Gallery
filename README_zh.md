(简体中文|[English](./README.md))
# Fossify Gallery

<img alt="Logo" src="graphics/icon.webp" width="120" />

本仓库是[Gallery](https://github.com/FossifyOrg/Gallery)的分支版本，旨在通过由OCR模型和大语言模型构建起来的`Caption`来扩展图像搜索的边界。已完成和打算做的事如下：

- [x] 建立`Captions`表，并在图像视图下提供对文件名和`Caption`内容的综合查询。
- [x] 借助谷歌ML Kit的文字识别模块，离线为图库中的图像建立基于离线OCR模型的`Caption`，通过该实施完成对[#295](https://github.com/FossifyOrg/Gallery/issues/294)功能的添加。
- [x] 实现在搜索结果中左右滑动的效果，修复[#347](https://github.com/FossifyOrg/Gallery/issues/347)。
- [x] 完成对`Captions`表的导入和导出功能。
- [ ] 借助OpenAI的API规范实施对在线或者局域网下的在线`Caption`生成。这将允许使用多模态大语言模型为图像生成语义丰富的`Caption`，或者使用PC端或者服务器端更强大的OCR模型为图像生成更准确的文本`Caption`。目前因为OpenAI尚未实施基于Kotlin的调用库[#70](https://github.com/openai/openai-java/issues/70)，暂缓该功能的实施。如果后续实施，预计可以部分满足[#19](https://github.com/FossifyOrg/Gallery/issues/19)，尽管我认为在手机上离线运行多模态大语言模型尚存在困难。
- [ ] 对`captions`表的`content`字段使用[FTS5](https://www.sqlite.org/fts5.html)技术进行搜索加速。
- [ ] 实现更加丰富地查询逻辑。比如`AND`和`OR`等逻辑运算符的支持。

释放回忆，而非泄露个人数据。Fossify Gallery 是一款功能强大且注重隐私的终极照片与视频应用。无广告，无多余权限——只为你提供流畅便捷的体验。

**🖼️ 轻松编辑照片：**  
使用我们简洁但强大的照片编辑器提升你的照片质量。裁剪、调整大小、旋转、翻转、绘图，或应用炫酷滤镜，一切操作都不影响你的隐私。从未如此掌控你的回忆。

**🌐 隐私至上，坚持到底：**  
你的隐私至关重要。告别那些贪婪的数据巨头。Fossify Gallery 让你掌控一切。删除 EXIF 元数据，比如 GPS 位置和相机信息，确保你的回忆只属于你。

**🔒 顶级安全保护：**  
通过 PIN 码、图案或指纹锁定你的回忆。你可以选择保护特定照片、视频，甚至整个应用——由你决定谁能访问。安心无忧，安全无虞。

**🔄 轻松恢复误删：**  
放心，意外总会发生！Fossify Gallery 内置回收站，让你可以在几秒内恢复误删的照片和视频。不再丢失珍贵记忆，只剩安心与满足。

**🎨 你的图库，你的风格：**  
根据你的喜好自定义外观、布局和功能。从界面主题到功能按钮，Fossify Gallery 给你所需的创意自由，打造属于你的独特图库。

**📷 全格式支持，自由无拘：**  
JPEG、JPEG XL、PNG、MP4、MKV、RAW、SVG、GIF、AVIF 等等——无论哪种格式，我们都支持，让你的回忆没有限制，只有无限可能。

**✨ Material Design 与动态主题：**  
体验直观流畅的 Material Design 设计搭配动态主题。还想更多个性化？探索自定义主题，让你的图库真正独一无二。

➡️ 了解更多 Fossify 应用：https://www.fossify.org<br>
➡️ 开源代码地址：https://www.github.com/FossifyOrg<br>
➡️ 加入 Reddit 社区：https://www.reddit.com/r/Fossify<br>
➡️ Telegram 交流频道：https://t.me/Fossify

<div align="center">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" width="30%">
</div>
