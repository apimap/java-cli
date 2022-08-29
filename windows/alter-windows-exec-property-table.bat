echo Alter Resources

rc Resources.rc
"C:\Program Files (x86)\Resource Hacker\ResourceHacker.exe" -open build/executable/apimap.exe -save build/executable/apimap.exe -action addoverwrite -resource Resources.res
"C:\Program Files (x86)\Resource Hacker\ResourceHacker.exe" -open build/executable/apimap.exe -save build/executable/apimap.exe -action addoverwrite -resource icon.ico -mask ICONGROUP,MAINICON,

echo Finished


