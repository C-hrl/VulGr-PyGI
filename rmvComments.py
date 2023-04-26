import pyparsing
from pathlib import Path

for file in Path("./VUL4J-34").rglob('*'):
    if(file.name.endswith(".java")):
        with open(file, 'r') as fileR:
            fileContent = fileR.read()
        with open(file, 'w') as fileW:
            commentFilter = pyparsing.javaStyleComment.suppress()
            fileContent = commentFilter.transformString(fileContent)
            fileW.write(fileContent)
            
print("Done")