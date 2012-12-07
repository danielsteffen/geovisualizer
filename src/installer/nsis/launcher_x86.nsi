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

!include "launcher_config.nsi"

Section "1"
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
    MessageBox MB_OK "Could not determine JVM architecture ($0). Trying to start ${PRODUCT_NAME}. If nothing happens please install Java JRE 32bit."
    Goto X86
  DoneDetect:
    ;
  IntCmp $0 32 X86 NotX86 NotX86
  X86:
    ;JVM is ok
	StrCpy $0 '"$R0" $VmArgs -jar "${APP}" $ProgramArgs'
	ExecWait $0
    Goto DoneX86
  NotX86:
    MessageBox MB_OK "Detected ($0 bit) JVM. Please install x64 version of ${PRODUCT_NAME} or x86 bit version of Java JRE."
  DoneX86:
    ;Proram terminated
	
  ;MessageBox MB_OK|MB_ICONINFORMATION "VmArgs: $VmArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "ProgramArgs: $ProgramArgs"
  ;MessageBox MB_OK|MB_ICONINFORMATION "Final cmdline: $0" 
SectionEnd

!include "launcher_functions.nsi"