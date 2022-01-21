javac -source 1.8 -target 1.8 -sourcepath src -d build src/*.java
pause
cd build
mkdir assets
cd assets
copy "..\..\assets\Logo.png"
cd ..

java src.Main