
#!/bin/bash
#By Thomas Varano

echo 'Prerequisites:'
echo 'Be in the correct folder. Have a jar titled in the format "<name>-<version>.jar"'
echo 'Have all resources in the folder'
echo
echo 'Type "q" to quit'
read -p 'Application Name: ' name
if [ $name = 'q' ]
then
    echo 'exit'
    exit
fi
read -p 'Version: ' version
read -p 'Icon name (must be .png, include suffix): ' icon
read -p 'Class with main method (if using packages, include full reference): ' class
echo
echo 'What type of output would you like? (type exactly as shown)'
echo 'pkg'
echo -e '\t an install file which will properly install the file directly into the Applications folder'
echo 'image'
echo -e '\t a direct .app file, can be put any where and executed and shared as such'
echo 'dmg' 
echo -e '\t a virtual disk installer that is used with proper applications. can still be installed in directories other than Applications'
echo
read -p 'Export type: ' type
read -p 'Continue? (y : n): ' cont
if [ $cont = 'n' ]
then
    echo 'exit'
    exit
fi

# Create the icon set
mkdir $name.iconset

#sips -z 128 128 $icon.png --out $name.iconset/icon_128x128.png
sips -z 16 16 $icon --out $name.iconset/icon_16x16.png
sips -z   32   32 $icon --out $name.iconset/icon_16x16@2x.png
sips -z   32   32 $icon --out $name.iconset/icon_32x32.png
sips -z  128  128 $icon --out $name.iconset/icon_32x32@2x.png
sips -z  128  128 $icon --out $name.iconset/icon_128x128.png
sips -z  256  256 $icon --out $name.iconset/icon_128x128@2x.png
sips -z  256  256 $icon --out $name.iconset/icon_256x256.png
sips -z  512  512 $icon --out $name.iconset/icon_256x256@2x.png
sips -z  512  512 $icon --out $name.iconset/icon_512x512.png
sips -z 1024 1024 $icon --out $name.iconset/icon_512x512@2x.png
iconutil --convert icns $name.iconset
mkdir -p package/macosx
cp $name.icns package/macosx
echo 'icon created'


# Export the application
jdk=$(/usr/libexec/java_home)
$jdk/bin/javapackager -deploy -native dmg -name $name \
-BappVersion=$version -Bicon=package/macosx/$name.icns \
-srcdir . -srcfiles $name-$version.jar -appclass $class \
-outdir out -v
suffix=type
if [ type = 'image' ]
then
    suffix=app
fi

echo 'DONE'
exit 0
