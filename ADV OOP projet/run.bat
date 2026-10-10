@echo off
echo ========================================================
echo Starting Dental Clinic Management System...
echo ========================================================

REM 1. Compile Java files
echo Compiling source code...
set FX_JARS=C:\Users\youss\.m2\repository\org\openjfx\javafx-controls\26\javafx-controls-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-controls\26\javafx-controls-26.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-graphics\26\javafx-graphics-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-graphics\26\javafx-graphics-26.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-base\26\javafx-base-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-base\26\javafx-base-26.jar
set MYSQL_JAR=C:\Users\youss\.m2\repository\com\mysql\mysql-connector-j\9.2.0\mysql-connector-j-9.2.0.jar
set CLASSPATH=target/classes;%MYSQL_JAR%;%FX_JARS%

if not exist "target\classes\css" mkdir "target\classes\css"
copy /Y "src\main\resources\css\style.css" "target\classes\css\style.css" >nul

javac -cp "%CLASSPATH%" -d target/classes src/main/java/model/*.java src/main/java/database/*.java src/main/java/controller/*.java src/main/java/socket/*.java src/main/java/view/*.java src/main/java/MainApp.java src/main/java/App.java

if %errorlevel% neq 0 (
    echo Error during compilation!
    pause
    exit /b %errorlevel%
)

echo Starting JavaFX Application...
java --module-path "%FX_JARS%" --add-modules javafx.controls -cp "%CLASSPATH%" MainApp
