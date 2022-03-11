; Taken from http://nsis.sourceforge.net/Simple_installer_with_JRE_check by weebib
; Use it as you desire.

; Credit given to so many people of the NSIS forum.

!define AppName "Don't Get Up!"
!define AppVersion "1.0"
!define ShortName "DontGetUp"
!define JRE_VERSION "1.8"
!define Vendor "Me and My Phone"

!include "MUI.nsh"
!include "Sections.nsh"
!include LogicLib.nsh
!include x64.nsh
!include explode.nsh

Var InstallJRE
Var JREPath
Var DETECTED_JAVA_STRING
Var DETECTED_JAVA_MAJOR
Var DETECTED_JAVA_MINOR
Var DETECTED_JAVA_BUILD
Var DETECTED_JAVA_HOME
Var MIN_JAVA_VERSION

;--------------------------------
;Configuration

;General
Name "${AppName}"
OutFile "setup.exe"

;Folder selection page
InstallDir "$PROGRAMFILES\${SHORTNAME}"

;Get install folder from registry if available
InstallDirRegKey HKLM "SOFTWARE\${Vendor}\${ShortName}" ""



; Installation types
;InstType "full"	; Uncomment if you want Installation types

;--------------------------------

!define MUI_HEADERIMAGE
!define MUI_ICON                     "${NSISDIR}\Contrib\Graphics\Icons\modern-install-blue.ico"
!define MUI_HEADERIMAGE_BITMAP       "${NSISDIR}\Contrib\Graphics\Header\orange-nsis.bmp"
!define MUI_WELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange-nsis.bmp"


;Pages

; License page
;!insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Contrib\Modern UI\License.txt"
!insertmacro MUI_PAGE_WELCOME
Page custom CheckInstalledJRE
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Java installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing Java runtime"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while we install the Java runtime"
!define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT "Java runtime installed successfully."
!insertmacro MUI_PAGE_INSTFILES
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while ${AppName} is being installed."
!insertmacro MUI_PAGE_COMPONENTS
!define MUI_PAGE_CUSTOMFUNCTION_PRE myPreInstfiles
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE RestoreSections
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!define MUI_FINISHPAGE_SHOWREADME
!define MUI_FINISHPAGE_SHOWREADME_TEXT "Show Release Notes"
!define MUI_FINISHPAGE_SHOWREADME_FUNCTION ShowReleaseNotes
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT "Launch Application"
!define MUI_FINISHPAGE_RUN_FUNCTION "LaunchApp"
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Modern UI Configuration

!define MUI_ABORTWARNING

;--------------------------------
;Languages

!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Language Strings

;Description
LangString DESC_SecAppFiles ${LANG_ENGLISH} "Application files copy"

;Header
LangString TEXT_JRE_TITLE ${LANG_ENGLISH} "Java Runtime Environment"
LangString TEXT_JRE_SUBTITLE ${LANG_ENGLISH} "Installation"
LangString TEXT_PRODVER_TITLE ${LANG_ENGLISH} "Installed version of ${AppName}"
LangString TEXT_PRODVER_SUBTITLE ${LANG_ENGLISH} "Installation cancelled"

;--------------------------------
;Reserve Files

;Only useful for BZIP2 compression


ReserveFile "jre.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

;--------------------------------
;Installer Sections

Section -installjre jre
    Push $0
    Push $1

    ;  MessageBox MB_OK "Inside JRE Section"
    Strcmp $InstallJRE "yes" InstallJRE JREPathStorage
    DetailPrint "Starting the JRE installation"
    InstallJRE:
    File /oname=$TEMP\jre_setup.exe j2re-setup.exe

    DetailPrint "Installing JRE"
    DetailPrint "Launching JRE setup"
    ;ExecWait "$TEMP\jre_setup.exe /S" $0
    ; The silent install /S does not work for installing the JRE, sun has documentation on the
    ; parameters needed.  I spent about 2 hours hammering my head against the table until it worked
    ExecWait "$TEMP\jre_setup.exe" $0
    DetailPrint "Setup finished"
    Delete "$TEMP\jre_setup.exe"
    StrCmp $0 "0" InstallVerif 0
    Push "The JRE setup has been abnormally interrupted."
    Goto ExitInstallJRE

    InstallVerif:
    DetailPrint "Checking the JRE Setup's outcome"
    ;  MessageBox MB_OK "Checking JRE outcome"
    Push "${JRE_VERSION}"
    Call DetectJRE
    Pop $0	  ; DetectJRE's return value
    StrCmp $0 "0" ExitInstallJRE 0
    StrCmp $0 "-1" ExitInstallJRE 0
    Goto JavaExeVerif
    Push "The JRE setup failed"
    Goto ExitInstallJRE

    JavaExeVerif:
    IfFileExists "$DETECTED_JAVA_HOME\bin\java.exe" JREPathStorage 0
    Push "The following file : $DETECTED_JAVA_HOME\bin\java.exe, cannot be found."
    Goto ExitInstallJRE

    JREPathStorage:
    ;  MessageBox MB_OK "Path Storage"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $1
    StrCpy $JREPath $0
    Goto End

    ExitInstallJRE:
    Pop $1
    MessageBox MB_OK "The setup is about to be interrupted for the following reason : $1"
    Pop $1 	; Restore $1
    Pop $0 	; Restore $0
    Abort
    End:
    Pop $1	; Restore $1
    Pop $0	; Restore $0

SectionEnd

Section "${AppName}" SecAppFiles
    SectionIn 1 RO	; Full install, cannot be unselected
                ; If you add more sections be sure to add them here as well
    SetOutPath $INSTDIR

    File /oname=${ShortName}.exe server.exe
    File servercpp.dll
    File release_notes.txt
    setFileAttributes "$INSTDIR\release_notes.txt" READONLY

    ;   Store install folder
    WriteRegStr HKLM "SOFTWARE\${Vendor}\${ShortName}" "" $INSTDIR

    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "DisplayName" "${AppName}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "UninstallString" '"$INSTDIR\uninstall.exe"'
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoModify" "1"
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoRepair" "1"

    WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd


Section "Start menu shortcuts" SecCreateShortcut
    SectionIn 1	; Can be unselected
    CreateDirectory "$SMPROGRAMS\${AppName}"
    CreateShortCut "$SMPROGRAMS\${AppName}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\${AppName}\${AppName}.lnk" "$INSTDIR\${ShortName}.exe" "" "$INSTDIR\${ShortName}.exe" 0
    ; Etc
SectionEnd

Section "Start ${AppName} automatically"
    SectionIn 1
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Run" "${AppName}" "$INSTDIR\${ShortName}.exe"
SectionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SecAppFiles} $(DESC_SecAppFiles)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Installer Functions

Function .onInit
    ;Extract InstallOptions INI Files
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jre.ini"
    Call SetupSections
FunctionEnd

Function myPreInstfiles
    Call RestoreSections
    SetAutoClose true
FunctionEnd

Function CheckInstalledJRE
    SetRegView 64

    DetailPrint "Checking Installed JRE Version"
    Push "${JRE_VERSION}"
    Call DetectJRE
    DetailPrint "Done checking JRE version"
    Exch $0	; Get return value from stack
    StrCmp $0 "0" NoFound
    StrCmp $0 "-1" FoundOld
    Goto JREAlreadyInstalled

    FoundOld:
    DetailPrint "Old JRE found"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" "${AppName} requires a more recent version of the Java Runtime Environment than the one found on your computer. The installation of JRE ${JRE_VERSION} will start."
    !insertmacro MUI_HEADER_TEXT "$(TEXT_JRE_TITLE)" "$(TEXT_JRE_SUBTITLE)"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
    Goto MustInstallJRE

    NoFound:
    DetailPrint "JRE not found"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" "No Java Runtime Environment could be found on your computer. The installation of JRE v${JRE_VERSION} will start."
    !insertmacro MUI_HEADER_TEXT "$(TEXT_JRE_TITLE)" "$(TEXT_JRE_SUBTITLE)"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
    Goto MustInstallJRE

    MustInstallJRE:
    Exch $0	; $0 now has the installoptions page return value
    ; Do something with return value here
    Pop $0	; Restore $0
    StrCpy $InstallJRE "yes"
    Return

    JREAlreadyInstalled:
    DetailPrint "JRE already installed"
    StrCpy $InstallJRE "no"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $JREPATH
    Pop $0		; Restore $0
    Return

FunctionEnd

; Returns: 0 - JRE not found. -1 - JRE found but too old. Otherwise - Path to JAVA EXE

; DetectJRE. Version requested is on the stack.
; Returns (on stack)	"0" on failure (java too old or not installed), otherwise path to java interpreter
; Stack value will be overwritten!

Function DetectJRE
    Exch $0	; Get version requested
    ; Now the previous value of $0 is on the stack, and the asked for version of JDK is in $0
    Push $1	; $1 = Java version string (ie 1.5.0)
    Push $2	; $2 = Javahome
    Push $3	; $3 and $4 are used for checking the major/minor version of java
    Push $4

    DetailPrint "Detecting JRE"
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\JavaSoft\Java Runtime Environment..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry2
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" JavaHome
    StrCmp $2 "" DetectTry2
    Goto GetJRE

    DetectTry2:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\JavaSoft\Java Development Kit" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\JavaSoft\Java Development Kit..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry3
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\JavaSoft\Java Development Kit\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry3
    Goto GetJRE

    DetectTry3:
    DetailPrint "Looking for Java in: SOFTWARE\JavaSoft\JRE..."
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\JavaSoft\JRE" CurrentVersion
    StrCmp $DETECTED_JAVA_STRING "" DetectTry4
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\JavaSoft\JRE\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry4
    Goto GetJRE

    DetectTry4:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\JavaSoft\JDK" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\JavaSoft\JDK..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry5
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\JavaSoft\JDK\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry5
    Goto GetJRE

    DetectTry5:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\WOW6432Node\JavaSoft\JRE" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\WOW6432Node\JavaSoft\JRE..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry6
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\WOW6432Node\JavaSoft\JRE\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry6
    Goto GetJRE

    DetectTry6:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\WOW6432Node\JavaSoft\JDK" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\WOW6432Node\JavaSoft\JDK..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry7
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\WOW6432Node\JavaSoft\JDK\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry7
    Goto GetJRE

    DetectTry7:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\WOW6432Node\JavaSoft\Java Runtime Environment" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\WOW6432Node\JavaSoft\Java Runtime Environment..."
    StrCmp $DETECTED_JAVA_STRING "" DetectTry8
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\WOW6432Node\JavaSoft\Java Runtime Environment\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" DetectTry8
    Goto GetJRE

    DetectTry8:
    ReadRegStr $DETECTED_JAVA_STRING HKLM "SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit" CurrentVersion
    DetailPrint "Looking for Java in: SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit..."
    StrCmp $DETECTED_JAVA_STRING "" NoFound
    ReadRegStr $DETECTED_JAVA_HOME HKLM "SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit\$DETECTED_JAVA_STRING" JavaHome
    StrCmp $DETECTED_JAVA_HOME "" NoFound
    Goto GetJRE

    GetJRE:
    DetailPrint "Getting JRE"
    IfFileExists "$DETECTED_JAVA_HOME\bin\java.exe" 0 NoFound

    ${Explode}  $0  "." "$DETECTED_JAVA_STRING"
    DetailPrint "Found Java version: $DETECTED_JAVA_STRING"
    ${For} $1 1 $0
        Pop $2
        ${If} $1 == 1
            StrCpy $DETECTED_JAVA_MAJOR $2
        ${ElseIf} $1 == 2
            StrCpy $DETECTED_JAVA_MINOR $2
        ${ElseIf} $1 == 3
            StrCpy $DETECTED_JAVA_BUILD $2
        ${EndIf}
    ${Next}

    ${If} $DETECTED_JAVA_MAJOR >= $MIN_JAVA_VERSION
        DetailPrint "Your Java is all right"
    Goto FoundNew
    ${Else}
        Goto FoundOld
    ${EndIf}

    NoFound:
    DetailPrint "JRE not found"
    Push "0"
    Goto DetectJREEnd

    FoundOld:
    DetailPrint "JRE too old: $3 is older than $4"
    ;    Push ${TEMP2}
    Push "-1"
    Goto DetectJREEnd
    FoundNew:
    DetailPrint "JRE is new: $3 is newer than $4"

    Push "$2\bin\java.exe"
    ;    Push "OK"
    ;    Return
    Goto DetectJREEnd
    DetectJREEnd:
        ; Top of stack is return value, then r4,r3,r2,r1
        Exch	; => r4,rv,r3,r2,r1,r0
        Pop $4	; => rv,r3,r2,r1r,r0
        Exch	; => r3,rv,r2,r1,r0
        Pop $3	; => rv,r2,r1,r0
        Exch 	; => r2,rv,r1,r0
        Pop $2	; => rv,r1,r0
        Exch	; => r1,rv,r0
        Pop $1	; => rv,r0
        Exch	; => r0,rv
        Pop $0	; => rv
FunctionEnd

Function RestoreSections
    !insertmacro UnselectSection ${jre}
    !insertmacro SelectSection ${SecAppFiles}
    !insertmacro SelectSection ${SecCreateShortcut}

FunctionEnd

Function SetupSections
    !insertmacro SelectSection ${jre}
    ;!insertmacro UnselectSection ${SecCreateShortcut}
FunctionEnd

;Uninstaller Section
Section "Uninstall"
    SetRegView 64

    DeleteExe:
    Delete "$INSTDIR\${ShortName}.exe"
    IfFileExists $INSTDIR\${ShortName}.exe FailedToDelete 0
    ExecDos::exec /ASYNC /DETAILED /TIMEOUT=6000 "$EXEDIR\consApp.exe"

    ; remove registry keys
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}"
    DeleteRegKey HKLM "SOFTWARE\${Vendor}\${ShortName}"
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Run\${AppName}"

    ; remove shortcuts, if any.
    Delete "$SMPROGRAMS\${AppName}\*.*"
    ; remove files
    RMDir /r "$INSTDIR"
    Goto End
    Abort:
    Abort
    FailedToDelete:
    MessageBox MB_RETRYCANCEL "${AppName} is running, please exit before continuing." IDRETRY DeleteExe IDCANCEL Abort

    End:
SectionEnd

Function LaunchApp
    ExecShell "" "$INSTDIR\${ShortName}.exe"
FunctionEnd

Function ShowReleaseNotes
    ExecShell "" "notepad" "$INSTDIR\release_notes.txt"
FunctionEnd