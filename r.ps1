#powershell script to re-build and reinstall code
./gradlew assembleDebug ; if($?) {.\gradlew installDebug;}