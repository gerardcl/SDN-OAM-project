echo "================ Installing DXAT plugin ===================="
echo "\tCopying code..."
cp -R ./dxathacks ../src/main/java/
echo "\tCopying configuration..."
cp floodlightdefault.properties ../src/main/resources/floodlightdefault.properties
cp net.floodlightcontroller.core.module.IFloodlightModule ../src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule
echo "\tCopying ant build file..."
cp build.xml ../
echo "\tCopying libraries..."
cp ./gson-2.2.4.jar ../lib
echo "\tPatch installed succesfully compile using the 'ant'\n\tcommand in the floodlight root directory"
echo "============================================================"