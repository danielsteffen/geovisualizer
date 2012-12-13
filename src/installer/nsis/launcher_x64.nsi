# ------------------------------------------------------------------------------
# Advanced Java Launcher
# Author: Tobias Zimmermann
# Based on: Java Launcher
# 
# '-console' as argument to launch with java.exe, otherwise javaw.exe 
# will be used.
#
# arguments starting '-X' will be passed to the JVM, all other arguments 
# will be passed to the Java application.
# ------------------------------------------------------------------------------

;======================================================
; Includes
!include header.nsi
!include launcher_header.nsi
!include launcher_functions.nsi

;======================================================
; Localisation
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "German"
LangString error1 ${LANG_GERMAN} "Java Virtual Machine konnte nicht bestimmt werden ($0).$\r$\n"
LangString error1 ${LANG_ENGLISH} "Could not determine Java Virtual Machine architecture ($0).$\r$\n"
LangString error2 ${LANG_GERMAN} "Versuche ${PRODUCT_NAME} zu starten.$\r$\n"
LangString error2 ${LANG_ENGLISH} "Trying to start ${PRODUCT_NAME}.$\r$\n"
LangString error3 ${LANG_GERMAN} "Wenn ${PRODUCT_NAME} nicht startet installieren Sie bitte ${PRODUCT_NAME} erneut, oder kontaktieren Sie Ihren System Administrator."
LangString error3 ${LANG_ENGLISH} "If nothing happens please reinstall ${PRODUCT_NAME} or contact your System Administrator."
LangString msg1 ${LANG_GERMAN} "$0 bit Java Virtual Machine erkannt.$\r$\n"
LangString msg1 ${LANG_ENGLISH} "Detected $0 bit Java Virtual Machine.$\r$\n"
LangString msg2 ${LANG_GERMAN} "Bitte installieren Sie die x64 Version von ${PRODUCT_NAME}, oder kontaktieren Sie Ihren System Administrator."
LangString msg2 ${LANG_ENGLISH} "Please install x64 version of ${PRODUCT_NAME}, or contact your System Administrator."

Section "-launch"  
  StrCpy $BreakLoop ${FALSE}
  SetOutPath "$EXEDIR"
 
; set to javaw.exe by default
  StrCpy $JavaExe ${JAVA_WINDOWED_EXE}

  Call ParseArgs
  Call GetJRE
  Pop $R0

  ClearErrors
  StrCpy $0 '"$R0" $VmArgs -jar "${DETECT_JVM}" $ProgramArgs'
 
  ;MessageBox MB_OK|MB_ICONINFORMATION "VmArgs: $VmArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "ProgramArgs: $ProgramArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "Final cmdline: $0"

  ExecWait $0 $0
  IfErrors DetectExecError
  IntCmp $0 0 DetectError DetectError DoneDetect
  DetectExecError:
    StrCpy $0 "exec error"
  DetectError:
	MessageBox MB_OK|MB_ICONEXCLAMATION "$(error1)$(error2)$(error3)"
    Goto X64
  DoneDetect:
    ;
  IntCmp $0 64 X64 NotX64 NotX64
  X64:
    ;JVM is ok
	StrCpy $0 '"$R0" $VmArgs -jar "${APP}" $ProgramArgs'
	ExecWait $0
    Goto DoneX64
  NotX64:
    MessageBox MB_OK|MB_ICONEXCLAMATION "$(msg1)$(msg2)"
  DoneX64:
    ;Proram terminated
	
  ;MessageBox MB_OK|MB_ICONINFORMATION "VmArgs: $VmArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "ProgramArgs: $ProgramArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "Final cmdline: $0" 
SectionEnd