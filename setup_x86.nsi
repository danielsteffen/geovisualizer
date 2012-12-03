; Installer for GeoVisualizer - 03.12.2012

;======================================================
; Includes

  !include MUI.nsh
  !include Sections.nsh
  !include target\project.nsh

;======================================================
; Installer Information

  Name "${PROJECT_NAME}"

  SetCompressor /SOLID lzma
  XPStyle on
  CRCCheck on
  InstallDir "C:\Program Files\${PROJECT_ARTIFACT_ID}\"
  AutoCloseWindow false
  ShowInstDetails show
  Icon "src\main\resources\icons\GeoVisualizer.ico"

;======================================================
; Version Tab information for Setup.exe properties

  VIProductVersion 2008.3.22.0
  VIAddVersionKey ProductName "${PROJECT_NAME}"
  VIAddVersionKey ProductVersion "${PROJECT_VERSION}"
  VIAddVersionKey CompanyName "${PROJECT_ORGANIZATION_NAME}"
  VIAddVersionKey FileVersion "${PROJECT_VERSION}"
  VIAddVersionKey FileDescription ""
  VIAddVersionKey LegalCopyright ""

;======================================================
; Variables
!define MUI_FILE "${PROJECT_ARTIFACT_ID}.exe"
!define JAR_DIR "target\nsis-make-assembly"
!define CLASS "${PROJECT_NAME}"
!define PRODUCT_NAME "${PROJECT_NAME}"
!define NATIVEDIR "natives\x86" 
 
; Definitions for Java 7.0 local
!define JRE_VERSION "7.0"
!define JRE_URL "$INSTDIR\jre-7u9-windows-i586.exe"
; Definitions for Java 6.0
;!define JRE_VERSION "6.0"
;!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=24936&/jre-6u10-windows-i586-p.exe"
;!define JRE_VERSION "5.0"
;!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=22933&/jre-1_5_0_16-windows-i586-p.exe"
 
; use javaw.exe to avoid dosbox.
; use java.exe to keep stdout/stderr
!define JAVAEXE "javaw.exe"

!include "FileFunc.nsh"
!insertmacro GetFileVersion
!insertmacro GetParameters
!include "WordFunc.nsh"
!insertmacro VersionCompare

;======================================================
; Modern Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_ABORTWARNING
  !define MUI_COMPONENTSPAGE_SMALLDESC
  !define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
  !define MUI_FINISHPAGE
  !define MUI_FINISHPAGE_TEXT "Thank you for installing ${PROJECT_NAME}. \r\n\n\nYou can now run ${PROJECT_ARTIFACT_ID} from your command line."
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\orange-install.ico"

;======================================================
; Modern Interface Pages

  !define MUI_DIRECTORYPAGE_VERIFYONLEAVE
  !insertmacro MUI_PAGE_LICENSE license.txt
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

;======================================================
; Languages

  !insertmacro MUI_LANGUAGE "English"

;======================================================
;Installer Sections    
Section "-jre" 	
	Call GetJRE	
SectionEnd
	
Section ${PROJECT_NAME}
;Add files
  SetOutPath "$INSTDIR"
  File "${JAR_DIR}\*.jar" 
  File "target\launcher\${MUI_FILE}"
 
;create desktop shortcut
  CreateShortCut "$DESKTOP\${PROJECT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" ""
 
;create start-menu items
  CreateDirectory "$PROGRAMFILES\${PROJECT_NAME}"
  CreateShortCut "$PROGRAMFILES\${PROJECT_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  CreateShortCut "$PROGRAMFILES\${PROJECT_NAME}\${PROJECT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" "" "$INSTDIR\${MUI_FILE}" 0
 
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

;======================================================
;Uninstaller Section  
Section "Uninstall"
 
;Delete Files 
  RMDir /r "$INSTDIR\*.*"    
 
;Remove the installation directory
  RMDir "$INSTDIR"
 
;Delete Start Menu Shortcuts
  Delete "$DESKTOP\${PROJECT_NAME}.lnk"
  Delete "$SMPROGRAMS\${PROJECT_NAME}\*.*"
  RmDir  "$SMPROGRAMS\${PROJECT_NAME}"
 
;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${PROJECT_NAME}"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}"  
 
SectionEnd
;======================================================

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
	file "jre\jre-7u9-windows-i586.exe"
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

;======================================================================