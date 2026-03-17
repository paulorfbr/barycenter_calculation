@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET %%__MVNW_ARG0_NAME__%%=

@IF "%JAVA_HOME%"=="" (
  @FOR /F "tokens=*" %%i IN ('where java 2^>NUL') DO @SET JAVA_HOME=%%~dpi..
)

@SET MAVEN_WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar

@IF NOT EXIST "%MAVEN_WRAPPER_JAR%" (
  @ECHO Downloading Maven Wrapper...
  @curl -fsSL -o "%MAVEN_WRAPPER_JAR%" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
)

@"%JAVA_HOME%\bin\java" ^
  -jar "%MAVEN_WRAPPER_JAR%" ^
  -Dmaven.multiModuleProjectDirectory="%~dp0" ^
  %*
