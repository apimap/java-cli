echo Alter Resources

rc Resources.rc
"C:\Program Files (x86)\Resource Hacker\ResourceHacker.exe" -open build/executable/apicatalog.exe -save build/executable/apicatalog.exe -action addoverwrite -resource Resources.res
"C:\Program Files (x86)\Resource Hacker\ResourceHacker.exe" -open build/executable/apicatalog.exe -save build/executable/apicatalog.exe -action addoverwrite -resource icon.ico -mask ICONGROUP,MAINICON,

echo Finished


