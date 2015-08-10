package com.github.davidkellis.query

// see https://github.com/antlr/grammars-v4/blob/master/sqlite/SQLite.g4:312
sealed trait Predicate {
  def or(op: Predicate): Predicate = Or(this, op)
  def and(op: Predicate): Predicate = And(this, op)
}
case class Eq(lhs: Expr, rhs: Expr) extends Predicate
case class Neq(lhs: Expr, rhs: Expr) extends Predicate
case class Gt(lhs: Expr, rhs: Expr) extends Predicate
case class Gte(lhs: Expr, rhs: Expr) extends Predicate
case class Lt(lhs: Expr, rhs: Expr) extends Predicate
case class Lte(lhs: Expr, rhs: Expr) extends Predicate
case class Between(originValue: Expr, min: Expr, max: Expr) extends Predicate
case class In(originValue: Expr, values: Seq[Expr]) extends Predicate
case class Or(lhs: Predicate, rhs: Predicate) extends Predicate
case class And(lhs: Predicate, rhs: Predicate) extends Predicate

case class All(predicates: Seq[Predicate]) extends Predicate
case class Any(predicates: Seq[Predicate]) extends Predicate
case class Not(p: Predicate) extends Predicate

sealed trait Expr {
  def eq(op: Expr): Predicate = Eq(this, op)
  def neq(op: Expr): Predicate = Neq(this, op)
  def gt(op: Expr): Predicate = Gt(this, op)
  def gte(op: Expr): Predicate = Gte(this, op)
  def lt(op: Expr): Predicate = Lt(this, op)
  def lte(op: Expr): Predicate = Lte(this, op)
  def between(bounds: (Expr, Expr)): Predicate = {
    val (min, max) = bounds
    Between(this, min, max)
  }
  def in(valueSet: Seq[Expr]): Predicate = In(this, valueSet)
  def +(op: Expr): Expr = AdditionExpr(this, op)
  def -(op: Expr): Expr = SubtractionExpr(this, op)
  def *(op: Expr): Expr = MultiplicationExpr(this, op)
  def /(op: Expr): Expr = DivisionExpr(this, op)
  def %(op: Expr): Expr = ModulusExpr(this, op)
}
case class FieldExpr(name: String) extends Expr
case class StringExpr(v: String) extends Expr
case class IntExpr(v: Int) extends Expr
case class LongExpr(v: Long) extends Expr
case class FloatExpr(v: Float) extends Expr
case class DoubleExpr(v: Double) extends Expr
case class BoolExpr(v: Boolean) extends Expr
case class AdditionExpr(lhs: Expr, rhs: Expr) extends Expr
case class SubtractionExpr(lhs: Expr, rhs: Expr) extends Expr
case class MultiplicationExpr(lhs: Expr, rhs: Expr) extends Expr
case class DivisionExpr(lhs: Expr, rhs: Expr) extends Expr
case class ModulusExpr(lhs: Expr, rhs: Expr) extends Expr

sealed trait Ordering
case class Asc(e: Expr) extends Ordering
case class Desc(e: Expr) extends Ordering

case class OrderBy(orderings: Seq[Ordering])

case class LimitAndSkip(limit: Int, skip: Int)

class Query() {
  var whereClause: Option[Predicate] = None
  var orderByClause: Option[OrderBy] = None
  var limitAndSkipClause: Option[LimitAndSkip] = None

  def where(p: Predicate): Query = { whereClause = Some(p); this }
  def orderBy(orderings: Seq[Ordering]) = { orderByClause = Some(OrderBy(orderings)); this }
  def limitAndSkip(limit: Int, skip: Int) = { limitAndSkipClause = Some(LimitAndSkip(limit, skip)); this }
}

object dsl {
  def where(p: Predicate): Query = new Query().where(p)
  def all(predicates: Predicate*): Predicate = All(predicates)
  def any(predicates: Predicate*): Predicate = Any(predicates)

  def field(name: String): Expr = FieldExpr(name)
  def v(v: String): Expr = StringExpr(v)
  def v(v: Int): Expr = IntExpr(v)
  def v(v: Long): Expr = LongExpr(v)
  def v(v: Float): Expr = FloatExpr(v)
  def v(v: Double): Expr = DoubleExpr(v)
  def v(v: Boolean): Expr = BoolExpr(v)

  def orderBy(orderings: Ordering*): Query = new Query().orderBy(orderings)

  def limit(limit: Int): Query = new Query().limitAndSkip(limit, 0)
  def limitAndSkip(limit: Int, skip: Int): Query = new Query().limitAndSkip(limit, skip)
}
