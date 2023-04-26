import subprocess

print(subprocess.check_call(['wsl', 'ls','-l']))
print(subprocess.check_call(['wsl', 'vul4j','checkout', '--id', 'VUL4J-34', '-d', 'C:/Users/CKB/Documents/GitHub/VulGr-PyGI']))