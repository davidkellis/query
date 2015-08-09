# query

query is a database agnostic query object library written in Scala.

query represents database queries as an AST that can be traversed to emit database specific query strings.

Currently there is an included generic SQL query emitter. Other query emitters could be easily implemented.

## Example:

```
scala>   val testQuery = where(
     |     all(
     |       field("height").gt(v(72)),
     |       field("age").between( (v(10), v(100)) )
     |     )
     |     or
     |     all(
     |       field("firstName").eq(v("Bob")),
     |       field("lastName").eq(v("Kazamakis"))
     |     )
     |   )
testQuery: Query = Query@999b951

scala> emitSql(testQuery)
res0: String = WHERE ((height > 72) AND (age BETWEEN 10 AND 100)) OR ((firstName = 'Bob') AND (lastName = 'Kazamakis'))
```

```
scala> val testQuery2 = where(
     |     (
     |       field("height").gt(v(72))
     |       and
     |       field("age").between( (v(10), v(100)) )
     |     )
     |     or
     |     (
     |       field("firstName").eq(v("Bob"))
     |       and
     |       field("lastName").eq(v("Kazamakis"))
     |     )
     |   )
testQuery2: Query = Query@1903b5d

scala> emitSql(testQuery2)
res1: String = WHERE ((height > 72) AND (age BETWEEN 10 AND 100)) OR ((firstName = 'Bob') AND (lastName = 'Kazamakis'))
```
