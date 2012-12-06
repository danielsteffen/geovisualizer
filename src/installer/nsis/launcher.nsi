# ------------------------------------------------------------------------------
# Advanced Java Launcher
# Author: Jmay
# Based on: Java Launcher
# 
# Thanks to bigmac666 for StrTok and all the other NSIS Java launcher
# developers, on whose work this is substantially based.
#
# Specify '-console' as argument to launch with java.exe, otherwise javaw.exe 
# will be used.
#
# All arguments starting '-X' will be passed to the JVM, all other arguments 
# will be passed to the Java application.
# ------------------------------------------------------------------------------
 
!include ..\..\..\target\project.nsh

# You want to change the next four lines
Name "${PROJECT_ARTIFACT_ID}"
Caption "${PROJECT_ARTIFACT_ID}"
Icon "..\..\..\src\main\resources\icons\GeoVisualizer.ico"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
# You want to change the next two lines too
!define APP "${PROJECT_FINAL_NAME}.jar"
!define DETECT_JVM "util/detectJVM-1.0.jar"
 
!define TRUE "true"
!define FALSE "false"

!define NATIVEDIR32 "..\..\..\natives\x86"
!define NATIVEDIR64 "..\..\..\natives\x64"
 
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
 
 
Section "1"
	StrCpy $BreakLoop ${FALSE}
 
	# set to javaw.exe by default
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
		MessageBox MB_OK "Could not determine JVM architecture ($0). Assuming 32-bit."
		Goto NotX64
	DoneDetect:
		
	IntCmp $0 64 X64 NotX64 NotX64
	X64:
		MessageBox MB_OK "Detected ($0 bit) JVM."
		SetOutPath "$EXEDIR"
		File "${NATIVEDIR64}\*.dll"	
		Goto DoneX64
	NotX64:
		MessageBox MB_OK "Detected ($0 bit) JVM."
		SetOutPath "$EXEDIR"
		File "${NATIVEDIR32}\*.dll"	
	DoneX64:
		;
	
	StrCpy $0 '"$R0" $VmArgs -jar "${APP}" $ProgramArgs'
 
	;MessageBox MB_OK|MB_ICONINFORMATION "VmArgs: $VmArgs"
	;MessageBox MB_OK|MB_ICONINFORMATION "ProgramArgs: $ProgramArgs"
	;MessageBox MB_OK|MB_ICONINFORMATION "Final cmdline: $0"
	  
	ExecWait $0
SectionEnd
 
Function ParseArgs
  # first argument is executable name; check for double quote at start, e.g. if
  # path contains spaces
  StrCpy $C0 $CMDLINE 1
  StrCmpS $C0 '"' quoted unquoted
 
  quoted:
    # tokenize using '"' as the delimiter; this fixes up the problem where paths
	# containing spaces break the later tokenization of arguments using ' ' as a delimiter
 
    ; MessageBox MB_OK|MB_ICONINFORMATION "Quoted string" 
    ; MessageBox MB_OK|MB_ICONINFORMATION "Cmdline: $CMDLINE" 
 
	# push $CMDLINE, tokenize and ignore as first token will be empty as string starts with delimiter
    Push $CMDLINE	
    Push '"'
    Call StrTok
 
    Pop $R0  # first token
    Pop $R1  # remainder of string
 
 
    # push remainder of string, tokenize and use first token as the initial argument in $ProgramArgs
    Push $R1
    Push '"'
    Call StrTok
 
    Pop $R0
    Pop $R1
 
    Strcpy $ProgramArgs '"$R0"'
    GoTo appendspace
 
  unquoted:
    # no special processing needed for unquoted path	
    ; MessageBox MB_OK|MB_ICONINFORMATION "Un-quoted string" 
 
  appendspace:
    # append space to create trailing delimiter so can get final token, 
	# otherwise StrTok will finish prematurely
    StrCpy $R1 "$R1 "
 
  # main processing loop, uses StrTok to extract each argument in turn
  loop:
    # check loop continue flag
    StrCmp $BreakLoop ${TRUE} finished
 
    Push $R1
    Push " "
    Call StrTok
 
    Pop $R0
    Pop $R1
 
	# if token is empty string then set flag to break out of loop
    StrCmp $R1 "" 0 moretodo
    StrCpy $BreakLoop ${TRUE}
 
  moretodo:
 
    ; MessageBox MB_OK|MB_ICONINFORMATION "loop - R0: $R0"
    ; MessageBox MB_OK|MB_ICONINFORMATION "loop - R1: $R1"
 
    # check whether user specified '-console'
    StrCmpS $R0 "-console" usingconsole vmorprogramarg
 
  usingconsole:
    # set to java.exe
    StrCpy $JavaExe ${JAVA_CONSOLE_EXE}
    GoTo loop
 
  vmorprogramarg:
    # check start of arg to see if is a VM arg or program arg
    StrCpy $X0 $R0 2
    StrCmpS $X0 "-X" isvmarg isprogramarg
 
  isvmarg:
    # is a VM arg; append to $VmArgs
    StrCpy $VmArgs "$VmArgs $R0"
    GoTo loop
 
  isprogramarg:
    # is a program arg; append to $ProgramArgs
    StrCpy $ProgramArgs "$ProgramArgs $R0"  
    GoTo loop
 
  finished:
    ClearErrors
 
FunctionEnd
 
# Find JRE (java(w).exe)
# 1 - in .\jre directory (JRE Installed with application)
# 2 - in JAVA_HOME environment variable
# 3 - in the registry
# 4 - assume java(w).exe in current dir or PATH
Function GetJRE
 
  Push $R0
  Push $R1
 
  ClearErrors
 
  StrCpy $R0 "$EXEDIR\jre\bin\$JavaExe"
  ; MessageBox MB_OK|MB_ICONINFORMATION "Looking in JRE for: $R0"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\$JavaExe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\$JavaExe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "$JavaExe"
 
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
 
# C-style StrTok function
# Author: bigmac666
Function StrTok
  Exch $R1
  Exch 1
  Exch $R0
  Push $R2
  Push $R3
  Push $R4
  Push $R5
 
  #R0 fullstring
  #R1 tokens
  #R2 len of fullstring
  #R3 len of tokens
  #R4 char from string
  #R5 testchar
 
  StrLen $R2 $R0
  IntOp $R2 $R2 + 1
 
  loop1:
    IntOp $R2 $R2 - 1
    IntCmp $R2 0 exit
 
    StrCpy $R4 $R0 1 -$R2
 
    StrLen $R3 $R1
    IntOp $R3 $R3 + 1
 
    loop2:
      IntOp $R3 $R3 - 1
      IntCmp $R3 0 loop1
 
      StrCpy $R5 $R1 1 -$R3
 
      StrCmp $R4 $R5 Found
    Goto loop2
  Goto loop1
 
  exit:
  # Not found!!!
  StrCpy $R1 ""
  StrCpy $R0 ""
  Goto Cleanup
 
  Found:
  StrLen $R3 $R0
  IntOp $R3 $R3 - $R2
  StrCpy $R1 $R0 $R3
 
  IntOp $R2 $R2 - 1
  IntOp $R3 $R3 + 1
  StrCpy $R0 $R0 $R2 $R3
 
  Cleanup:
  Pop $R5
  Pop $R4
  Pop $R3
  Pop $R2
  Exch $R0
  Exch 1
  Exch $R1
 
FunctionEnd