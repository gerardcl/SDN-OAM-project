echo "================= Updating DXAT plugin Code ================"
PRG="$BASH_SOURCE"
echo $PRG
echo "\tSaving code into the DXAT plugin folder..."
cp -R ../src/main/java/dxathacks/ ./ 
echo "\tSaving configuration files..."
cp ../src/main/resources/floodlightdefault.properties ./
cp ../src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule ./
echo "\tSaving current ant build file..."
cp ../build.xml ./
echo "\tCopy libraries..."
 cp ../lib/gson-2.2.4.jar ./
echo "\tCode updated succesfully, you can push it to git"
echo "============================================================"   
