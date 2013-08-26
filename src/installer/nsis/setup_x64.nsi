; Installer for GeoVisualizer - 10.12.2012
!define UNINST_SUFFIX "(x64)"
!define JRE_FILE "jre-7u17-windows-x64.exe"
!define NATIVEDIR "..\..\..\natives\windows-x64\2.0.0-20130815"
!define PROGRAM_DIR $PROGRAMFILES64
!define LAUNCHER_DIR "..\..\..\target\launcher\x64"

!include header.nsi
!include setup_header.nsi
!include setup_sections.nsi
!include setup_functions.nsi
!include "util\CheckJRE.nsi"