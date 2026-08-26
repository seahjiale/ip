# Bobby project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bobby/Bobby.java` file, right-click it, and choose `Run Bobby.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

This project uses the [Shadow Gradle plugin](https://gradleup.com/shadow/) to
package Bobby and its runtime dependencies into one executable fat JAR.

1. Open PowerShell in the project root.
1. Configure the terminal to use JDK 25. For example:

   ```powershell
   $env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.4'
   $env:Path = "$env:JAVA_HOME\bin;$env:Path"
   ```

1. Create the fat JAR:

   ```powershell
   .\gradlew.bat shadowJar
   ```

1. Gradle writes the executable JAR to:

   ```text
   build\libs\duke.jar
   ```

1. Run it from the project root:

   ```powershell
   java -jar .\build\libs\duke.jar
   ```

The JAR can also be copied elsewhere and run with a JDK 25 installation because
the application code and its runtime dependencies are packaged inside it.
