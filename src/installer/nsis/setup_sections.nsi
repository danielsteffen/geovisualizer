;Installer Sections
Section "-jre"
  Call GetJRE
  SetOutPath "$INSTDIR\util"
  FILE ${DETECT_JVM}
SectionEnd

Section ${PRODUCT_NAME} SEC01
  SectionIn RO
;Add files
  SetOutPath "$INSTDIR"
  File "${JAR_DIR}\*.jar"
  File "${LAUNCHER_DIR}\${MUI_FILE}"
  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  IntFmt $0 "0x%08X" $0
;write uninstall information to the registry
  WriteRegStr HKLM "${ARP}" "DisplayName" "${PRODUCT_NAME} ${UNINST_SUFFIX} (remove only)"
  WriteRegStr HKLM "${ARP}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegDWORD HKLM "${ARP}" "EstimatedSize" "$0"
  WriteRegStr HKLM "${ARP}" "DisplayIcon" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "${ARP}" "DisplayVersion" "${PROJECT_VERSION}"
  WriteRegStr HKLM "${ARP}" "Comments" "${ProductDescription}"
  WriteRegStr HKLM "${ARP}" "Publisher" "${PROJECT_ORGANIZATION_NAME}"
  WriteRegStr HKLM "${ARP}" "URLInfoAbout" "${PROJECT_URL}"
  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section $(DesktopShortcut) SEC02
  CreateShortCut "$DESKTOP\${PRODUCT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" ""
SectionEnd

Section $(StartmenuItems) SEC03
  CreateDirectory "$SMPROGRAMS\${PRODUCT_NAME}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${PRODUCT_NAME}\${PRODUCT_NAME}.lnk" "$INSTDIR\${MUI_FILE}" ""
SectionEnd

Section "-natives"
  SetOutPath "$INSTDIR"
  File "${NATIVEDIR}\*.dll"
SectionEnd

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SEC01} $(DESC_Section1)
!insertmacro MUI_DESCRIPTION_TEXT ${SEC02} $(DESC_Section2)
!insertmacro MUI_DESCRIPTION_TEXT ${SEC03} $(DESC_Section3)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

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
  DeleteRegKey HKEY_LOCAL_MACHINE ${ARP}
SectionEnd