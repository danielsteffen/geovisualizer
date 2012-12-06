;Installer script for geovizualizer

;======================================================
;Installer Sections    
Section "-jre" 	
  Call GetJRE	
  SetOutPath "$INSTDIR\util"
  FILE "resources\detectJVM-1.0.jar"
SectionEnd
	
Section ${PROJECT_NAME}
;Add files
  SetOutPath "$INSTDIR"
  File "${JAR_DIR}\*.jar" 
  File "..\..\..\target\launcher\${MUI_FILE}"

;create desktop shortcut
  CreateShortCut "$DESKTOP\${PROJECT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" ""

;create start-menu items
  CreateDirectory "$SMPROGRAMS\${PROJECT_NAME}"
  CreateShortCut "$SMPROGRAMS\${PROJECT_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${PROJECT_NAME}\${PROJECT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" "" "$INSTDIR\${MUI_FILE}" 0

;write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "DisplayName" "${PROJECT_NAME} (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"

  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "-natives" 	
;Add files
  SetOutPath "$INSTDIR"
  File "${NATIVEDIR}\*.dll"	
SectionEnd

;--------------------------------    
;Uninstaller Section  
Section "Uninstall"
;Delete Files 
  RMDir /r "$INSTDIR\*.*"    

;Remove the installation directory
  RMDir "$INSTDIR"
;Delete Start Menu Shortcuts
  Delete "$DESKTOP\${PRODUCT_NAME}.lnk"
  Delete "$SMPROGRAMS\${PRODUCT_NAME}\*.*"
  RMDir  "$SMPROGRAMS\${PRODUCT_NAME}"
 
;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${PRODUCT_NAME}"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  
SectionEnd

Function .onInit
  InitPluginsDir
FunctionEnd

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
  CheckLocal:
    ClearErrors
    StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
    IfFileExists $R0 JreFound
 
; 2) Check for JAVA_HOME
  CheckJavaHome:
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