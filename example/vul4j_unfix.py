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

class MyProgram(AbstractProgram):
    def compute_fitness(self, result, return_codde, stdout, stderr, elapsed_time):
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
        import re
        m = re.findall(" ", stdout)
        if len(m) > 0:
            runtime = m[0]
            failed = re.findall("([0-9]+) failed", stdout)
            pass_all = len(failed) == 0
            failed = int(failed[0]) if not pass_all else 0
            result.fitness = failed
        else:
            result.status = 'PARSE_ERROR'

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
        if fitness == 1:
            return True
        return False
    
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
        for file in Path("/content/VulGr-PyGI/vulgr/VUL4J-34/core/src/main/java/org/apache/struts2/components/").glob('*'):
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