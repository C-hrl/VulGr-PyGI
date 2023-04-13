import subprocess
from pathlib import Path
import time
import csv

class timer:
    START_TIME = 0

    def start(self):
        timer.START_TIME = time.time()
        
    def end(self, message):
        if(timer.START_TIME == 0):
            print("call start() method first")
        else:
            print(message + " took " + str(time.time() - timer.START_TIME))
        
class vulnerable_file:
    def __init__(self):
        self.is_source = False
        self.any = False
        self.flowsource_is_flowsink = False
        self.is_flowsink = False
        self.not_getsourcegroup = False
        self.path_node_sink_group = False
        self.path_node_source_group = False
        self.path_succ = False
    
    def check_vulnerability_type(self, name):
        if(name == "is-source"):
            self.is_source = True
        elif(name == "flowsource-is-flowsink"):
            self.flowsource_is_flowsink = True
        elif(name == "is-flowsink"):
            self.is_flowsink = True
        elif(name == "not-getsourcegroup"):
            self.not_getsourcegroup = True
        elif(name == "path-node-source-group"):
            self.path_node_source_group = True
        elif(name == "path-succ"):
            self.path_succ = True
        
        # match name:
        #     case "is-source":
        #         self.is_source = True
        #     case "any":
        #         self.any = True
        #     case "flowsource-is-flowsink":
        #         self.flowsource_is_flowsink = True
        #     case "is-flowsink":
        #         self.is_flowsink = True
        #     case "not-getsourcegroup":
        #         self.not_getsourcegroup = True
        #     case "path-node-sink-group":
        #         self.path_node_sink_group = True
        #     case "path-node-source-group":
        #         self.path_node_source_group = True
        #     case "path-succ":
        #         self.path_succ = True
                
    def compute_fitness(self):
        FITNESS_VALUE = 0
        if (self.is_flowsink):
            FITNESS_VALUE += 0.35
        if (self.flowsource_is_flowsink or self.path_succ):
            FITNESS_VALUE += 0.35
        if (self.path_node_source_group):
            FITNESS_VALUE += 0.3
        else:
            if(self.is_source):
                FITNESS_VALUE += 0.15
            if(self.not_getsourcegroup):
                FITNESS_VALUE += 0.15
        
        return FITNESS_VALUE
        

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

# timer.start()
# db_creation_result = create_database()
# timer.end("database creation")
# if "failed" in db_creation_result:
#     print("failed")
# else:
#     timer.start()
#     print(subprocess.run(execute_all_queries(), shell=True, capture_output=True, text=True, cwd="/content").stderr)
#     timer.end("running all queries")
    # timer.start()
    # for query in query_list:
    #     print(subprocess.run(query_command(query), shell=True,
    #                 capture_output=True, text=True).stderr)
    # timer.end("individual queries")
    
    # FILENAME_COLUMN = 4
    # for file in Path("/content/results").glob('*'):
    #     with open(file, 'r') as file:
    #         csvreader = csv.reader(file)
    #         for row in csvreader:
    #             print(row[FILENAME_COLUMN].split('/')[-1])
                
# timer.start()
# print(subprocess.run(execute_all_queries(), shell=True, capture_output=True, text=True, cwd="/content").stderr)
# timer.end("running all queries")

FILENAME_COLUMN = 4
QUERY_NAME_COLUMN = 0
VULNERABLE_FILES = {}
VULNERABILITY_NAME = ""
for file in Path("/content/results").glob('*'):
    with open(file, 'r') as file:
        csvreader = csv.reader(file)
        for row in csvreader:
            filename = row[FILENAME_COLUMN].split('/')[-1]
            if filename in VULNERABLE_FILES:
                VULNERABILITY_NAME = row[QUERY_NAME_COLUMN]
                VULNERABLE_FILES[filename].check_vulnerability_type(VULNERABILITY_NAME)
            else:
                VULNERABLE_FILES[filename] = vulnerable_file
                
HIGHEST_FITNESS = 0
for file in VULNERABLE_FILES:
    FITNESS = file.compute_fitness()
    if FITNESS > HIGHEST_FITNESS:
        HIGHEST_FITNESS = FITNESS
print(HIGHEST_FITNESS)