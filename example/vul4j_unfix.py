"""
Automated vulnerability injection ::
"""
from pathlib import Path
import random
import argparse
import os
from pyggi.base import Patch, AbstractProgram
from pyggi.line import LineProgram
from pyggi.line import LineReplacement, LineInsertion, LineDeletion
from pyggi.tree import TreeProgram, XmlEngine
from pyggi.tree import StmtReplacement, StmtInsertion, StmtDeletion
from pyggi.algorithms import LocalSearch
import subprocess
from pathlib import Path
import csv

def create_database():
    """Returns the resulting String after trying to create the CodeQL Database

    Returns:
        output (String) : String output from running the database creation command
    """
    output = subprocess.run("/content/codeql/codeql database create /content/codeDB --language=java --overwrite --command='vul4j compile -d /content/VUL4J-34-MODIFIED'", shell=True, capture_output=True, text=True, cwd="/content/VUL4J-34-MODIFIED").stdout
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

def run_queries():
    """
    Returns the resulting String after running the queries
    """
    subprocess.run(execute_all_queries(), shell=True,
                    capture_output=True, text=True, cwd="/content").stderr

    FILENAME_COLUMN = 4
    for file in Path("/content/results").glob('*'):
        with open(file, 'r') as file:
            csvreader = csv.reader(file)
            for row in csvreader:
                print(row[FILENAME_COLUMN].split('/')[-1])

class MyProgram(AbstractProgram):
    def compute_fitness(self, result, return_codde, stdout, stderr, elapsed_time):
        db_creation_result = create_database()
        if "failed" in db_creation_result:
            result.status = 'PARSE_ERROR'
        else:
            result.fitness = run_queries()
            
    @property
    def tmp_path(self):
        return "/content/VUL4J-34-MODIFIED/core/src/main/java/org/apache/struts2/components"

class MyLineProgram(LineProgram, MyProgram):
    pass


class MyXmlEngine(XmlEngine):
    @classmethod
    def process_tree(cls, tree):
        stmt_tags = ['if', 'decl_stmt', 'expr_stmt', 'return', 'try']
        cls.select_tags(tree, keep=stmt_tags)
        cls.rewrite_tags(tree, stmt_tags, 'stmt')
        cls.rotate_newlines(tree)

class MyTreeProgram(TreeProgram):
    def setup(self):
        if not os.path.exists(os.path.join(self.tmp_path, "Triangle.java.xml")):
            self.exec_cmd("srcml Triangle.java -o Triangle.java.xml")

    @classmethod
    def get_engine(cls, file_name):
        return MyXmlEngine

class MyTabuSearch(LocalSearch):
    def setup(self):
        self.tabu = []

    def get_neighbour(self, patch):
        while True:
            temp_patch = patch.clone()
            if len(temp_patch) > 0 and random.random() < 0.5:
                temp_patch.remove(random.randrange(0, len(temp_patch)))
            else:
                edit_operator = random.choice(self.operators)
                temp_patch.add(edit_operator.create(self.program, method="weighted"))
            if not any(item == temp_patch for item in self.tabu):
                self.tabu.append(temp_patch)
                break
        return temp_patch

    def is_better_than_the_best(self, fitness, best_fitness):
        return fitness > best_fitness

    def stopping_criterion(self, iter, fitness):
        return fitness == 1
    
class MyLocalSearch(LocalSearch):
    def get_neighbour(self, patch):
        if len(patch) > 0 and random.random() < 0.5:
            patch.remove(random.randrange(0, len(patch)))
        else:
            edit_operator = random.choice(self.operators)
            patch.add(edit_operator.create(self.program))
        return patch

    def stopping_criterion(self, iter, fitness):
        return fitness == 1

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='VulGr PyGGI')
    parser.add_argument('--project_path', type=str, default='../vulgr')
    parser.add_argument('--mode', type=str, default='line')
    parser.add_argument('--epoch', type=int, default=30,
        help='total epoch(default: 30)')
    parser.add_argument('--iter', type=int, default=10000,
        help='total iterations per epoch(default: 10000)')
    args = parser.parse_args()
    assert args.mode in ['line', 'tree']

    if args.mode == 'line': #TODO asap
        files_to_improve = []
        for file in Path("/content/VUL4J-34/core/src/main/java/org/apache/struts2/components/").glob('*'):
            if file.is_file():
                with open(file, 'r') as file:
                    files_to_improve.append(file.name.split("/")[-1])
        config = {
            "target_files": files_to_improve, #TODO add files in which the vulnerabilities were found
            "test_command": "python vulgr_testing.py" #TODO edit this run.sh
        }
        program = MyLineProgram(args.project_path, config=config)
        tabu_search = MyTabuSearch(program)
        tabu_search.operators = [LineReplacement, LineInsertion, LineDeletion]
    elif args.mode == 'tree': #TODO (after "line")
        config = {
            "target_files": ["Triangle.java.xml"], #TODO Seems like tree-based mutations require srcML
            "test_command": "python vulgr_testing.py"
        }
        program = MyTreeProgram(args.project_path, config=config)
        tabu_search = MyTabuSearch(program)
        tabu_search.operators = [StmtReplacement, StmtInsertion, StmtDeletion]

    result = tabu_search.run(warmup_reps=1, epoch=args.epoch, max_iter=args.iter)
    print("======================RESULT======================")
    print(result)
    program.remove_tmp_variant()