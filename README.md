# Implementing the Simplex Method in Java for Educational Purposes

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

An amateur implementation of the [Simplex Method](https://en.m.wikipedia.org/wiki/Simplex_algorithm) for solving linearly constrained optimization problems. 

![Simplex Representation](https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Simplex-method-3-dimensions.png/240px-Simplex-method-3-dimensions.png)

## Abstract:
The following document presents a description of a personal project undertaken with the primary goal of understanding the fundamental principles of linear programming and honing Java programming skills. The project involves the implementation of the Simplex Method, a popular algorithm used to solve linear programming problems efficiently. It is essential to emphasize that this project was carried out **solely for educational purposes**, to deepen the knowledge of linear programming and to gain proficiency in Java.

### Introduction
The simplex method is a powerful mathematical technique widely employed to solve linear programming problems, which involve optimizing a linear objective function while subject to a set of linear constraints. As a keen learner in the fields of both mathematics and computer science, I decided to embark on this personal project to explore the intricacies of linear programming and its practical implementation in Java.

### Project Objectives
The main objectives of this project were as follows:
1. Gain a deeper understanding of the simplex method and its applications in linear programming.
2. Learn and practice Java programming, applying object-oriented principles and data structures.
3. Develop a working implementation of the simplex algorithm that can efficiently solve various linear programming problems.

Example of linearly contstrained problem:
$$\max_{x,y} 30x + 40y. \\ subject\ to: \\ 2x + y \leq 10; \\ x + 2y \leq 12; \\ x + y \leq 7; \\ x,y \geq 0.$$

### Methodology
The project followed these steps:
1. Comprehensive Research: Before beginning the implementation, I conducted thorough research on the simplex method, studying its theoretical foundations, constraints, and optimization procedures.
2. Java Language Proficiency: As my primary focus was to learn Java, I dedicated time to study the language's syntax, object-oriented concepts, and data structures.
3. Algorithm Design: After understanding the simplex method's logic, I devised a plan to translate the algorithm into a Java program. This involved designing classes to represent constraints, objective functions, and the simplex solver itself.
4. Implementation: The coding process involved converting the algorithm's steps into Java code, utilizing data structures and control flow statements to execute the simplex method effectively.
5. Testing and Debugging: I conducted rigorous testing with various linear programming problems to verify the correctness and efficiency of the implementation. Bugs and issues were addressed systematically during this phase.

### Outcomes
1. The software is able to solve any linear programming problem.
2. The implementation allows users to specify objective functions, constraints, and variable limits in the `Main.java` file. Initially also a GUI was planned but ultimately left unfinished.
3. Tableau Generation: The algorithm generates and displays the intermediate tableaus during each iteration, enabling users to visualize the optimization process.
4. Optimality and Unboundedness Detection: The implementation accurately identified optimal solutions and unbounded problems, providing appropriate feedback to the user.
5. The implementation isn't optimal for what regards big-O time complexity.

### Conclusion
In conclusion, this personal project served as an excellent learning experience for understanding the simplex method and enhancing my Java programming skills. By implementing the simplex algorithm from scratch, I gained valuable insights into the world of linear programming and algorithm design. Throughout the project, I consistently reminded myself that the primary objective was to learn and grow as a programmer and mathematician, and the resulting implementation serves as a testament to that effort.
