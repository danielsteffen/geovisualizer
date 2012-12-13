;======================================================
; JRE
;  returns the full path of a valid java.exe
;  looks in:
;  1 - .\jre directory (JRE Installed with application)
;  2 - JAVA_HOME environment variable
;  3 - the registry
;  4 - hopes it is in current dir or PATH
Function GetJRE
  Push $R0
  Push $R1
  Push $2

; 1) Check local JRE
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
  IfFileExists $R0 JreFound

; 2) Check for JAVA_HOME
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors CheckRegistry
  IfFileExists $R0 0 CheckRegistry
  Call CheckJREVersion
  IfErrors CheckRegistry JreFound

; 3) Check for registry
  CheckRegistry:
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors DownloadJRE
  IfFileExists $R0 0 DownloadJRE
  Call CheckJREVersion
  IfErrors DownloadJRE JreFound

  DownloadJRE:
  MessageBox MB_ICONINFORMATION "${PRODUCT_NAME} uses Java Runtime Environment ${JRE_VERSION}, it will now be downloaded and installed."
  SetOutPath "$INSTDIR"
  file ${JRE_RES}
  ExecWait ${JRE_URL}
  Delete ${JRE_URL}

  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfFileExists $R0 0 GoodLuck
  Call CheckJREVersion
  IfErrors GoodLuck JreFound

; 4) wishing you good luck
  GoodLuck:
  StrCpy $R0 "${JAVAEXE}"
; MessageBox MB_ICONSTOP "Cannot find appropriate Java Runtime Environment."
; Abort

  JreFound:
  Pop $2
  Pop $R1
  Exch $R0
FunctionEnd

; Pass the "javaw.exe" path by $R0
Function CheckJREVersion
  Push $R1

; Get the file version of javaw.exe
  ${GetFileVersion} $R0 $R1
  ${VersionCompare} ${JRE_VERSION} $R1 $R1

; Check whether $R1 != "1"
  ClearErrors
  StrCmp $R1 "1" 0 CheckDone
  SetErrors

  CheckDone:
  Pop $R1
FunctionEnd