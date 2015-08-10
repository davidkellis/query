package com.github.davidkellis.emitters

import com.github.davidkellis.query._

// SqlEmitter implements the LIMIT .. OFFSET clause as understood and supported by MySQL, H2, HSQLDB, Postgres, and SQLite.
// Chose to use LIMIT .. OFFSET because jOOQ does too - http://www.jooq.org/doc/3.1/manual/sql-building/sql-statements/select-statement/limit-clause/
class SqlEmitter() {
  def emit(query: Query): String = {
    val sb = new scala.collection.mutable.StringBuilder()
    query.whereClause.foreach { pred => sb append "WHERE " append emit(pred) }
    query.orderByClause.foreach { orderBy => sb append emit(orderBy) }
    query.limitAndSkipClause.foreach { limitAndSkip => sb append emit(limitAndSkip) }
    sb.toString
  }

  def emit(p: Predicate): String = {
    val sb = new scala.collection.mutable.StringBuilder()
    p match {
      case Eq(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " = " append emit(rhs)
      case Neq(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " <> " append emit(rhs)
      case Gt(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " > " append emit(rhs)
      case Gte(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " >= " append emit(rhs)
      case Lt(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " < " append emit(rhs)
      case Lte(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " <= " append emit(rhs)
      case Between(originValue: Expr, min: Expr, max: Expr) => sb append emit(originValue) append " BETWEEN " append emit(min) append " AND " append emit(max)
      case In(originValue: Expr, values: Seq[Expr]) =>
        sb append emit(originValue) append " IN " append "("
        values.map { value => emit(value) }.mkString(",")
        sb append ")"
      case Or(lhs: Predicate, rhs: Predicate) => sb append "(" append emit(lhs) append ") OR (" append emit(rhs) append ")"
      case And(lhs: Predicate, rhs: Predicate) => sb append "(" append emit(lhs) append ") AND (" append emit(rhs) append ")"
      case All(predicates: Seq[Predicate]) => sb append predicates.map(p => "(" + emit(p) + ")").mkString(" AND ")
      case Any(predicates: Seq[Predicate]) => sb append predicates.map(p => "(" + emit(p) + ")").mkString(" OR ")
      case Not(pred: Predicate) => sb append "NOT " append emit(pred)
    }
    sb.toString
  }

  def emit(e: Expr): String = {
    val sb = new scala.collection.mutable.StringBuilder()
    e match {
      case FieldExpr(name: String) => sb append name
      case StringExpr(v: String) => sb append "'" append v append "'"
      case IntExpr(v: Int) => sb append v
      case LongExpr(v: Long) => sb append v
      case FloatExpr(v: Float) => sb append v
      case DoubleExpr(v: Double) => sb append v
      case BoolExpr(v: Boolean) => sb append (if (v) "TRUE" else "FALSE")   // this could be replaced with "1=1" else "1=0" - see http://stackoverflow.com/questions/6898358/sql-server-boolean-literals
      case AdditionExpr(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " + " append emit(rhs)
      case SubtractionExpr(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " - " append emit(rhs)
      case MultiplicationExpr(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " * " append emit(rhs)
      case DivisionExpr(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " / " append emit(rhs)
      case ModulusExpr(lhs: Expr, rhs: Expr) => sb append emit(lhs) append " % " append emit(rhs)
    }
    sb.toString
  }

  def emit(orderBy: OrderBy): String = {
    val sb = new scala.collection.mutable.StringBuilder()
    sb append "ORDER BY "
    orderBy.orderings.map { ordering => emit(ordering) }.mkString(", ")
    sb.toString
  }

  def emit(ordering: Ordering): String = {
    ordering match {
      case Asc(e: Expr) => emit(e) + " ASC"
      case Desc(e: Expr) => emit(e) + " DESC"
    }
  }

  def emit(limitAndSkip: LimitAndSkip): String = {
    val sb = new scala.collection.mutable.StringBuilder()
    if (limitAndSkip.limit > 0) {
      sb append "LIMIT " append limitAndSkip.limit
    }
    if (limitAndSkip.skip > 0) {
      sb append "OFFSET " append limitAndSkip.skip
    }
    sb.toString
  }
}
object SqlEmitter {
  def emit(query: Query): String = new SqlEmitter().emit(query)
}
