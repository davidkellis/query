import org.scalatest._

// import com.github.davidkellis.query._
import com.github.davidkellis.emitters._
import com.github.davidkellis.query.dsl._

class QuerySpec extends FlatSpec {

  "The SQL Emitter" should "emit SQL for a where condition" in {
    val query = where(
      all(
        field("height").gt(v(72)),
        field("age").between( (v(10), v(100)) )
      )
      or
      all(
        field("firstName").eq(v("Bob")),
        field("lastName").eq(v("Kazamakis"))
      )
    )

    val expectedSql = "WHERE ((height > 72) AND (age BETWEEN 10 AND 100)) OR ((firstName = 'Bob') AND (lastName = 'Kazamakis'))"
    assert(SqlEmitter.emit(query) === expectedSql)
  }
}
