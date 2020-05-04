# java
small java projects

## Freecell Solver
### Compile
```shell
mkdir build && javac common/*.java freecell/*.java -d build
```
### Run
```shell
pushd build && java freecell.FreecellSolution --deal 1 && popd
```
### JAR File
```shell
pushd build
jar cvfe freecell-solver.jar freecell.FreecellSolution common/*.class freecell/*.class
popd
java -jar build/freecell-solver.jar --deal 1
```
