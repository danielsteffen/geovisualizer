;======================================================
; Includes
!include MUI.nsh
!include Sections.nsh
!include ..\..\..\target\project.nsh
!include "FileFunc.nsh"
!include "WordFunc.nsh"

;Global config for geovisualizer installer

;======================================================
; Installer Information
Name "${PROJECT_NAME}"
SetCompressor /SOLID lzma
XPStyle on
CRCCheck on
InstallDir "${PROGRAM_DIR}\${PROJECT_ARTIFACT_ID}\"
AutoCloseWindow false
ShowInstDetails show
Icon "..\..\..\src\main\resources\icons\GeoVisualizer.ico"

;======================================================
; Variables
!define MUI_FILE "${PROJECT_ARTIFACT_ID}.exe"
!define JAR_DIR "..\..\..\target\nsis-make-assembly"
!define CLASS "${PROJECT_NAME}"
!define PRODUCT_NAME "${PROJECT_NAME}"
; use javaw.exe to avoid dosbox.
; use java.exe to keep stdout/stderr
!define JAVAEXE "javaw.exe"
!define INSTALLER_ICON "..\..\..\src\main\resources\icons\GeoVisualizer.ico"

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
; Modern Interface Configuration
!define MUI_HEADERIMAGE
!define MUI_ABORTWARNING
!define MUI_COMPONENTSPAGE_SMALLDESC
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
!define MUI_FINISHPAGE
!define MUI_FINISHPAGE_TEXT "Thank you for installing ${PROJECT_NAME}. \r\n\n\nYou can now run ${PROJECT_ARTIFACT_ID}."

;======================================================
; Modern Interface Pages
!define MUI_DIRECTORYPAGE_VERIFYONLEAVE
!insertmacro MUI_PAGE_LICENSE license.txt
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

;======================================================
; Macros
!insertmacro GetFileVersion
!insertmacro GetParameters
!insertmacro VersionCompare

;======================================================
; Languages
!insertmacro MUI_LANGUAGE "English"	