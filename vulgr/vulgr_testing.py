import subprocess
from pathlib import Path
import csv


def query_command(query_name):
    """Returns a string corresponding to the provided query's name (made for subqueries)

    Args:
        query_name (String): Name of the query to execute

    Returns:
        command_to_execute (String): executable command for the 
    """
    codeql_exec = "/content/codeql/codeql"
    path_to_codedb = "/content/codeDB"
    result_path = "/content/results/VUL4J-34"
    cwe_path = "/content/VulGr-PyGI/codeql/java/ql/src/Security/CWE/CWE-079/Split"

    command_to_execute = f"{codeql_exec} database analyze --format=csv --output={result_path}_{query_name}.csv {path_to_codedb} {cwe_path}/{query_name}.ql --threads=16 --ram=12000"
    return command_to_execute


query_list = ["any",
              "flowsource-is-flowsink",
              "is-flowsink",
              "is-source",
              "not-getsinkgroup",
              "path-node-source-group",
              "path-node-sink-group",
              "path-succ"
              ]
subprocess.run("cd /content/VulGr-PyGI", shell=True, capture_output=True, text=True).stdout

result = ""
for query in query_list:
    result += subprocess.run(query_command(query), shell=True,
                   capture_output=True, text=True).stderr
NbSuccessfulQuery = result.count("Interpreting")
print(f"*****\n{NbSuccessfulQuery}/{len(query_list)} queries have been successfully executed!\n*****")

for file in Path("/content/results").glob('*'):
    with open(file, 'r') as file:
        csvreader = csv.reader(file)
        # for row in csvreader:
        #     print(row)
    print(file.name.split("/")[-1])