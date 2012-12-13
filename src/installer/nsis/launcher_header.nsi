;======================================================
; General Settings
RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

;======================================================
; Variables
Var VmArgs         # JVM arguments, e.g. -Xdebug -Xnoagent, etc.
Var ProgramArgs    # your program arguments (if you use them), e.g. -enablefoo, etc.
Var C0             # temporary variable used in testing for quoted executable path
Var X0             # temporary variable used in testing for VM arguments
Var BreakLoop      # essentially a boolean to indicate loop processing should finish
Var JavaExe        # the Java exe to use - either java.exe or javaw.exe
!define APP "${PROJECT_FINAL_NAME}.jar"
!define TRUE "true"
!define FALSE "false"
!define USE_CONSOLE_ARG "-console"
!define JAVA_CONSOLE_EXE "java.exe"
!define JAVA_WINDOWED_EXE "javaw.exe"

Function .onInit
; Splash Screen
  InitPluginsDir
  File /oname=$PLUGINSDIR\splash.bmp "resources\splash.bmp"
  advsplash::show 1000 600 400 -1 $PLUGINSDIR\splash
  Pop $0          ; $0 has '1' if the user closed the splash screen early,
                  ; '0' if everything closed normally, and '-1' if some error occurred.
  Delete $PLUGINSDIR\splash.bmp
FunctionEnd