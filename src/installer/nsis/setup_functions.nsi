Function .onInit
  !insertmacro MULTIUSER_INIT
  InitPluginsDir
  UserInfo::GetAccountType
  pop $0
  ${If} $0 != "admin" ;Require admin rights on NT4+
  MessageBox mb_iconstop "Administrator rights required!"
  SetErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
  Quit
  ${EndIf}
FunctionEnd

Function un.onInit
  !insertmacro MULTIUSER_UNINIT
FunctionEnd