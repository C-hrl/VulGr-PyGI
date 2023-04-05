import subprocess
from pathlib import Path
import time
import csv

class timer:
    START_TIME = 0

    def start():
        timer.START_TIME = time.time()
        
    def end(message):
        if(timer.START_TIME == 0):
            print("call start() method first")
        else:
            print(message + " took " + str(time.time() - timer.START_TIME))
        
        
def create_database():
    """Returns the resulting String after trying to create the CodeQL Database

    Returns:
        output (String) : String output from running the database creation command
    """
    output = subprocess.run("/content/codeql/codeql database create /content/codeDB --language=java --overwrite --command='vul4j compile -d /content/VUL4J-34-MODIFIED' --threads=16 --ram=12000", shell=True, capture_output=True, text=True, cwd="/content/VUL4J-34-MODIFIED").stdout
    print(output)
    return output

def execute_all_queries():
    """Returns a string corresponding to the provided query's name (made for subqueries)

    Returns:
        command_to_execute (String): executable command for the 
    """
    codeql_exec = "/content/codeql/codeql"
    path_to_codedb = "/content/codeDB"
    result_path = "/content/results/VUL4J-34"
    cwe_path = "/content/VulGr-PyGI/codeql/java/ql/src/Security/CWE/CWE-079/Split"

    command_to_execute = f"{codeql_exec} database analyze --format=csv --output={result_path}_result.csv {path_to_codedb} {cwe_path} --threads=16 --ram=12000"
    return command_to_execute

def query_command(query_name):
    """Returns a string corresponding to the provided query's name (made for subqueries)

        Args:
            query_name (String): Name of the query to execute

        Returns:
            command_to_execute (String): executable command
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

timer.start()
db_creation_result = create_database()
timer.end("database creation")
if "failed" in db_creation_result:
    print("failed")
else:
    timer.start()
    print(subprocess.run(execute_all_queries(), shell=True, capture_output=True, text=True, cwd="/content").stderr)
    timer.end("running all queries")
    timer.start()
    for query in query_list:
        print(subprocess.run(query_command(query), shell=True,
                    capture_output=True, text=True).stderr)
    timer.end("individual queries")

    # for file in Path("/content/results").glob('*'):
    #     with open(file, 'r') as file:
    #         csvreader = csv.reader(file)
    #         # for row in csvreader:
    #         #     print(row)
    #     print(file.name.split("/")[-1])