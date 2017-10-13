# NonLinear Planner With Regression

This planner will accept an input file with the following format:
```
MaxColumns=3
Blocks=A*,B**,C**,D***, F*;
InitialState=ON-TABLE(C),ON(B,C),ON(A,B),CLEAR(A);ON-TABLE(D),ON(F,D),CLEAR(D),EMPTY-ARM(L),EMPTY-ARM(R);
GoalState=ON-TABLE(B),ON(C,B),CLEAR(C),ON-TABLE(D),ON(A,D),ON(F,A),CLEAR(R),EMTPY-ARM(L),EMTPY-ARM(R);
```
It will run the NonLinear planner algorithm and return an output file like:
```
nn // number of operators of the plan
ii // number of states generated to solve the problem
op1, op2, op3, … // plan defined as a sequence of operators from initial to final state
-------------
// details of the states that were cancelled (not continued) reason with format:
p1,p2,p3 … //predicates that define the state
repeated state, contradictory predicates, … // reason for cancelling the exploration
------------- //a line will separate each state

```
