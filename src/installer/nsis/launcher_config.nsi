!include setup_header.nsi

RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define APP "${PROJECT_FINAL_NAME}.jar"
 
!define TRUE "true"
!define FALSE "false"
 
# defines for console or windowed usage
!define USE_CONSOLE_ARG "-console"
!define JAVA_CONSOLE_EXE "java.exe"
!define JAVA_WINDOWED_EXE "javaw.exe"
 
Var VmArgs         # JVM arguments, e.g. -Xdebug -Xnoagent, etc.
Var ProgramArgs    # your program arguments (if you use them), e.g. -enablefoo, etc.
Var C0             # temporary variable used in testing for quoted executable path
Var X0             # temporary variable used in testing for VM arguments
Var BreakLoop      # essentially a boolean to indicate loop processing should finish
Var JavaExe        # the Java exe to use - either java.exe or javaw.exe