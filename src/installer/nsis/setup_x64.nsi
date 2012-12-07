; Installer for GeoVisualizer - 03.12.2012
!define JRE_FILE "jre-7u9-windows-x64.exe"
!define NATIVEDIR "..\..\..\natives\x64"
!define PROGRAM_DIR "$PROGRAMFILES64"
!define LAUNCHER_DIR "..\..\..\target\launcher\x64"

!include setup_header.nsi
!include setup_sections.nsi
!include setup_functions.nsi
!include "util\CheckJRE.nsi"