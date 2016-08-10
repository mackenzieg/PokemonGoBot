Echo Off
CLS
mkdir library
git clone https://github.com/Grover-c13/PokeGOAPI-Java.git library 
cd library
git submodule update --init
./gradlew :library:build
cd..