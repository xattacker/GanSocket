chcp 65001
ipconfig -all
java -Xss1024k -Xms128m -Xmx256m -Dfile.encoding=UTF-8 -classpath .\lib\* com.xattacker.gan.GanServer
pause