import subprocess

#
codeql = "/content/codeql/codeql"
codedb = "/content/codeDB"
vulgr = "/content/VulGr-PyGI"


#listing all commands that will be executed
cmd_path = f"cd {vulgr}" #
cmd_sub_any = f"{codeql} database analyze --format=csv --output=/content/results/VUL4J-34_any.csv {codedb} {cmd_path}/codeql/java/ql/src/Security/CWE/CWE-079/Split/any.ql --threads=16 --ram=12000"
cmd_list = [cmd_path, cmd_sub_any]
result = subprocess.run(cmd_str, shell=True, capture_output=True, text=True)