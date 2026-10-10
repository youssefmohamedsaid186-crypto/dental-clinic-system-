@echo off
echo ========================================================
echo Starting Dental Clinic Socket Notification Server...
echo ========================================================

set FX_JARS=C:\Users\youss\.m2\repository\org\openjfx\javafx-controls\26\javafx-controls-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-controls\26\javafx-controls-26.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-graphics\26\javafx-graphics-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-graphics\26\javafx-graphics-26.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-base\26\javafx-base-26-win.jar;C:\Users\youss\.m2\repository\org\openjfx\javafx-base\26\javafx-base-26.jar
set MYSQL_JAR=C:\Users\youss\.m2\repository\com\mysql\mysql-connector-j\9.2.0\mysql-connector-j-9.2.0.jar
set CLASSPATH=target/classes;%MYSQL_JAR%;%FX_JARS%

if not exist "target\classes" mkdir "target\classes"

javac -cp "%CLASSPATH%" -d target/classes src/main/java/socket/NotificationMessage.java src/main/java/socket/SocketServer.java

if %errorlevel% neq 0 (
    echo Error compiling SocketServer!
    pause
    exit /b %errorlevel%
)

echo Starting SocketServer on Port 5000...
java -cp target/classes socket.SocketServer
