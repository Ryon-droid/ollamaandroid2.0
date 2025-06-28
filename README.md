Ollama Android 项目介绍



一、项目概述



Ollama Android 是一个基于 Android 平台开发的聊天应用程序，主要用于与 Ollama AI 进行交互。该应用集成了用户管理功能，包括用户注册、登录、修改密码等操作，同时支持文件上传功能，方便用户在聊天过程中分享文件。


二、功能特性





1.  **用户管理**

*   **注册**：用户可以使用用户名和密码进行注册，系统会对密码进行加密存储。


*   **登录**：已注册用户可以使用用户名和密码登录系统。


*   **修改密码**：登录用户可以修改自己的密码。


1.  **聊天功能**

*   **消息发送**：用户可以在聊天界面输入消息并发送给 Ollama AI。


*   **文件上传**：用户可以选择本地文件进行上传，上传成功后会在聊天界面显示提示信息。


1.  **权限管理**

*   根据不同的 Android 版本，动态请求文件访问权限，确保应用可以正常访问用户选择的文件。


三、项目结构





```
ollamaandroid2.0/


├── .gitignore


├── .idea/


│   ├── .gitignore


│   ├── AndroidProjectSystem.xml


│   ├── compiler.xml


│   ├── deploymentTargetSelector.xml


│   ├── gradle.xml


│   ├── misc.xml


│   ├── runConfigurations.xml


│   └── vcs.xml


├── app/


│   ├── .gitignore


│   ├── build.gradle.kts


│   ├── src/


│       ├── main/


│           │── java/


│           │   └── com/example/ollamaandroid/


│           │       ├── ChatActivity.java


│           │       ├── ChatAdapter.java


│           │       ├── ChatMessage.java


│           │       ├── UserManager.java


│           │       └── ...


│           ├── res/


│           │   ├── layout/


│           │   │   ├── item\_bot\_message.xml


│           │   │   ├── item\_info\_message.xml


│           │   │   ├── item\_user\_message.xml


│           │   │   └── ...


│           │   ├── menu/


│           │   │   └── chat\_menu.xml


│           │   ├── values/


│           │   │   └── strings.xml


│           │   ├── xml/


│           │       ├── backup\_rules.xml


│           │       └── data\_extraction\_rules.xml


│           └── AndroidManifest.xml


│       └── test/


│           └── java/


│               └── com/example/ollamaandroid/


│                   └── ExampleUnitTest.java


├── build.gradle.kts


├── gradle/


│   ├── libs.versions.toml


│   └── wrapper/


│       └── gradle-wrapper.properties


├── gradlew.bat


├── gradle.properties


└── settings.gradle.kts
```

四、技术栈





1.  **编程语言**：Java


2.  **构建工具**：Gradle


3.  **依赖管理**：Kotlin DSL（build.gradle.kts）


4.  **主要依赖库**

*   **AndroidX**：提供 Android 开发的一系列支持库，如 AppCompatActivity、RecyclerView 等。


*   **Security Crypto**：用于加密存储用户信息。


*   **HttpClient5**：用于网络请求。


*   **Jackson Databind**：用于 JSON 数据的处理。


五、环境要求





1.  **开发环境**

*   Android Studio Arctic Fox 或更高版本


*   JDK 21


1.  **运行环境**

*   Android 11 (API level 30) 或更高版本


六、安装与运行





1.  **克隆项目**



```
git clone https://github.com/Ryon-droid/ollamaandroid2.0.git

cd ollamaandroid2.0
```



1.  **打开项目**

    使用 Android Studio 打开项目文件夹 `ollamaandroid2.0`。


2.  **配置 Gradle**

    项目使用 Gradle 进行构建，确保 Gradle 配置正确。可以在 `gradle/wrapper/gradle-wrapper.properties` 中查看 Gradle 版本。


3.  **运行项目**

    连接 Android 设备或启动模拟器，点击 Android Studio 中的运行按钮，选择目标设备运行应用。


七、注意事项





1.  **权限问题**

*   应用需要网络权限和文件访问权限，请确保在运行应用时授予相应权限。


*   不同 Android 版本对文件访问权限的要求不同，应用会根据版本动态请求权限。


1.  **加密存储**

*   应用使用加密存储用户信息，需要 Android 6.0 (API level 23) 或更高版本支持。如果设备版本过低，会回退到普通 SharedPreferences。


八、贡献



如果你对本项目感兴趣，可以通过以下方式进行贡献：




1.  提交 Bug 报告或功能建议。


2.  提交 Pull Request 改进代码。
