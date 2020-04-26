setup\wget\wget http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-src.zip
tar --extract --file=forge-1.7.10-10.13.4.1614-1.7.10-src.zip gradle eclipse gradlew gradlew.bat
xcopy setup\build.gradle .
xcopy setup\gradle-wrapper.properties gradle\wrapper\ /y
del forge-1.7.10-10.13.4.1614-1.7.10-src.zip