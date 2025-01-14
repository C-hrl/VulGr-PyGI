import os
from os.path import expanduser

# Configure the absolute path to directory of vul4j on your local machine
VUL4J_ROOT = "C:/Users/CKB/Documents/GitHub/VulGr-PyGI/vul4j-main"

# Configure the paths to Java homes for your local machine
JAVA7_HOME = os.environ.get("JAVA7_HOME",
                            expanduser("C:/Program Files/Java/jdk1.7.0_80"))
JAVA8_HOME = os.environ.get("JAVA8_HOME",
                            expanduser("C:/Program Files/Java/jdk1.8.0_221"))

DATASET_PATH = os.environ.get("DATASET_PATH", expanduser(VUL4J_ROOT + "/dataset/vul4j_dataset.csv"))
BENCHMARK_PATH = os.environ.get("BENCHMARK_PATH", expanduser(VUL4J_ROOT))
PROJECT_REPOS_ROOT_PATH = os.environ.get("PROJECT_REPOS_ROOT_PATH", expanduser(VUL4J_ROOT + "/project_repos"))
REPRODUCTION_DIR = os.environ.get("REPRODUCTION_DIR", expanduser(VUL4J_ROOT + "/reproduction"))

JAVA_ARGS = os.environ.get("JAVA_ARGS", "-Xmx4g -Xms1g -XX:MaxPermSize=512m")
MVN_OPTS = os.environ.get("MVN_OPTS", "-Xmx4g -Xms1g -XX:MaxPermSize=512m")

OUTPUT_FOLDER_NAME = "VUL4J"
ENABLE_EXECUTING_LOGS = os.environ.get("ENABLE_EXECUTING_LOGS", "1")