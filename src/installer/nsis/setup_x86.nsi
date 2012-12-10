;x86 Installer for GeoVisualizer - 10.12.2012
!define UNINST_SUFFIX "(x32)"
!define JRE_FILE "jre-7u9-windows-i586.exe"
!define NATIVEDIR "..\..\..\natives\x86"
!define PROGRAM_DIR "$PROGRAMFILES"
!define LAUNCHER_DIR "..\..\..\target\launcher\x86"

!include setup_header.nsi
!include setup_sections.nsi
!include setup_functions.nsi
!include "util\CheckJRE.nsi"