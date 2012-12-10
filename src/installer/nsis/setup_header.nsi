InstallDir "${PROGRAM_DIR}\${PRODUCT_NAME}"

;======================================================
; Includes
!include MultiUser.nsh
!include Sections.nsh
!include FileFunc.nsh
!include WordFunc.nsh

RequestExecutionLevel highest
!define MULTIUSER_EXECUTIONLEVEL Highest

;======================================================
; Macros
!insertmacro GetFileVersion
!insertmacro GetParameters
!insertmacro VersionCompare

;======================================================
; Variables
!define MUI_FILE "${PROJECT_ARTIFACT_ID}.exe"
!define JAR_DIR "..\..\..\target\nsis-make-assembly"
!define ARP "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}${UNINST_SUFFIX}"
!define JRE_URL "$INSTDIR\${JRE_FILE}"
!define JAVAEXE "javaw.exe"
!define JRE_VERSION "7.0"
!define JRE_RES "resources\jre\${JRE_FILE}"

;======================================================
; Modern Interface Pages
!define MUI_DIRECTORYPAGE_VERIFYONLEAVE
;!insertmacro MUI_PAGE_LICENSE license.txt;
;!insertmacro MUI_PAGE_LICENSE license3rdparty.txt
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

;======================================================
; Languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "German"
; Language specific strings
LangString FINISHPAGE ${LANG_ENGLISH} "Thank you for installing ${PRODUCT_NAME}. \r\n\n\nYou can now run ${PRODUCT_NAME}."
LangString FINISHPAGE ${LANG_GERMAN} "Danke, dass Sie ${PRODUCT_NAME} installiert haben. \r\n\n\nSie können jetzt ${PRODUCT_NAME} starten."
LangString DESC_Section1 ${LANG_ENGLISH} "${PRODUCT_NAME} main application."
LangString DESC_Section2 ${LANG_ENGLISH} "Desktop shurtcut for ${PRODUCT_NAME}."
LangString DESC_Section3 ${LANG_ENGLISH} "Start menu entries for ${PRODUCT_NAME} and the uninstaller."
LangString DESC_Section1 ${LANG_GERMAN} "${PRODUCT_NAME} Anwendung"
LangString DESC_Section2 ${LANG_GERMAN} "Desktop Verknüpfung für ${PRODUCT_NAME}."
LangString DESC_Section3 ${LANG_GERMAN} "Startmenü Einträge für ${PRODUCT_NAME} und den Uninstaller."
LangString DesktopShortcut ${LANG_GERMAN} "Desktop Verknüpfung"
LangString DesktopShortcut ${LANG_ENGLISH} "Desktop shurtcut"
LangString StartmenuItems ${LANG_GERMAN} "Startmenü Einträge"
LangString StartmenuItems ${LANG_ENGLISH} "Startmenu items"

;======================================================
; Modern Interface Configuration
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP ${HeaderImage}
!define MUI_HEADERIMAGE_RIGHT
!define MUI_ABORTWARNING
!define MUI_COMPONENTSPAGE_SMALLDESC
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
!define MUI_FINISHPAGE_TEXT $(FINISHPAGE)