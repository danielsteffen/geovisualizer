!include MUI.nsh
!include ..\..\..\target\project.nsh
;======================================================
; General Information
!define PRODUCT_NAME "GeoVisualizer"
!define DETECT_JVM "util\detectJVM-1.0.jar"
!define ProductIcon "..\..\..\src\main\resources\icons\GeoVisualizer.ico"
!define HeaderImage "..\..\..\src\main\resources\icons\GeoVisualizer.png"
!define ProductDescription "The GeoVisualizer was developed within the sudplan3D (http://sudplan.kl.dfki.de) component."

;======================================================
; Version Tab information for Setup.exe properties
VIProductVersion 1.0.0.0
VIAddVersionKey ProductName "${PRODUCT_NAME}"
VIAddVersionKey ProductVersion "${PROJECT_VERSION}"
VIAddVersionKey CompanyName "${PROJECT_ORGANIZATION_NAME}"
VIAddVersionKey FileVersion "${PROJECT_VERSION}"
VIAddVersionKey FileDescription "${ProductDescription}"
VIAddVersionKey LegalCopyright ""

;======================================================
; Icon Settings
Icon ${ProductIcon}
!define MUI_ICON ${ProductIcon}
!define MUI_UNICON ${ProductIcon}

;======================================================
; General Settings
Name "${PRODUCT_NAME}"
Caption "${PRODUCT_NAME}"
SetCompressor /SOLID lzma
XPStyle on
CRCCheck on
AutoCloseWindow false
ShowInstDetails show