libPath=$(find lib -name "*.jar" | tr '\n' ':')

files=$(find src -name "*.java" ! -name "TarifFrame.java")

javac -d bin -cp "$libPath" $files

if [ $? -eq 0 ]; then
	java -cp "bin:$libPath" view.MainFrame
else
	java -cp "bin:$libPath" view.MainFrame
fi
