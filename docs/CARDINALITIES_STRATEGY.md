# Strategies to achieve cardinalities in data generation

## ONE-TO-ONE

1. Check if connected columns are both required (not optional).
2. If yes, check if the tables have the same number of rows.

**TBD: rest**

## ONE-TO-MANY (MANY-TO-ONE)

- Generated together with the table on the "many" side while the table on the "one" side is already generated.

### UNIFORM strategy 

1. Check if both connected columns are required (not optional).
2. If yes, check if the table with the "one" side has fewer rows than the table with the "many" side. 
3. Compute the ratio of rows between the two tables. For example, if the "one" side has 10 rows and the "many" side has 100 rows, the ratio is 1:10. Record the remainer.
4. For each row in the "one" side, generate the corresponding number of rows in the "many" side based on the ratio. If there is a remainder, distribute it evenly among the rows in the "one" side.
   - Load batch of rows in the "one" side (the table should be already generated) and generate corresponding number of records for each.
