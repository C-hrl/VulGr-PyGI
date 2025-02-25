# Number of classes
This metric measures the number of classes below this location in the tree. At a file level, this would just be the number of classes in the file.

There are both advantages and disadvantages to putting multiple classes in the same file, so some judgment needs to be applied when interpreting these results. That said, putting large numbers of classes, or unrelated classes, in the same file is a malpractice that makes the organization of the code harder to understand. There is a debate in the programming community as to whether it is necessary to go as far as having a 'one class per file' rule, but there is far more agreement over the principle that you should not bundle large numbers of unrelated classes into a single file. Indeed, Java already enforces a 'one *public* class per file' rule - this was done to make importing packages efficient (see \[Kabutz\]).

The disadvantages of putting multiple classes in the same file include:

* It causes problems with incremental compilation because changes to a class force all of the other classes in the same file to be recompiled, even if they don't actually need to be. Furthermore, files that contain multiple classes generally have higher coupling, which can also make for slower builds.
* It increases the likelihood of multiple developers working on the same file at once, and thereby the likelihood of merge conflicts.
* Projects whose files contain multiple classes can be less intuitive to navigate, even with tool support, because the logical organization of the code is obscured.
* It makes your files larger, which can increase network traffic if you're using a poor version control system.
* It makes it harder to look at the files changed in a commit and infer what has been changed at a code level.
* Some compilers generate error messages based on the file name rather than the class name, which are less helpful if there is not a one-to-one correspondence between files and classes.
There are a couple of advantages, however:

* It reduces the proliferation of files containing very few lines of code.
* It can be used positively to group logically-related classes together.

## Recommendation
Generally speaking, the goal is to ensure that only strongly logically-related classes are 'packaged' together in the same file. This usually militates in favor of having a separate file for each class. If your code currently puts lots of large, unrelated classes in the same file, the solution is to move them all into separate files.

As with any rule or guideline, however, there are exceptions. The primary one is that helper classes that are only used in the context of a file's main class should probably stay in the same file, e.g. an iterator class for a container can happily cohabit the container's source file. The same applies to enumerations.


## References
* H. Kabutz. [Java History 101: Once Upon an Oak](https://www.devx.com/java-zone/10686/). Published online.
