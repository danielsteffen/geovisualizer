!include LogicLib.nsh

; StrUpperCase
;   Converts the string on the stack to uppercase (in an ASCII sense)
; Usage:
;   Push <string>
;   Call StrUpperCase
;   Pop <STRING>
Function StrUpperCase
  Exch $0 ; Original string
  Push $1 ; Final string
  Push $2 ; Current character
  Push $3
  Push $4
  StrCpy $1 ""
Loop:
  StrCpy $2 $0 1 ; Get next character
  StrCmp $2 "" Done
  StrCpy $0 $0 "" 1
  StrCpy $3 65 ; 65 = ASCII code for A
  ${If} $1 == "Geo"
    Goto Loop2
  ${EndIf}
  ${If} $1 == ""
    Goto Loop2
  ${Else}
    Goto NoMatch
  ${EndIf}
Loop2:
  IntFmt $4 %c $3 ; Get character from current ASCII code
  StrCmp $2 $4 Match
  IntOp $3 $3 + 1
  StrCmp $3 91 NoMatch Loop2 ; 91 = ASCII code one beyond Z
Match:
  StrCpy $2 $4 ; It 'matches' (either case) so grab the uppercase version
NoMatch:
  StrCpy $1 $1$2 ; Append to the final string
  Goto Loop
Done:
  StrCpy $0 $1 ; Return the final string
  Pop $4
  Pop $3
  Pop $2
  Pop $1
  Exch $0
FunctionEnd