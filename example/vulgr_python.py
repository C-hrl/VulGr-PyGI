from pyggi.base import Patch, AbstractProgram

class MyProgram(AbstractProgram):
    def compute_fitness(self, result, return_codde, stdout, stderr, elapsed_time):
        import re
        m = re.findall("runtime: ([0-9.]+)", stdout)
        if len(m) > 0:
            runtime = m[0]
            failed = re.findall("([0-9]+) failed", stdout)
            pass_all = len(failed) == 0
            failed = int(failed[0]) if not pass_all else 0
            result.fitness = failed
        else:
            result.status = 'PARSE_ERROR'