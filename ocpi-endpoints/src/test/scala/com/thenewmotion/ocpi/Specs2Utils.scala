package com.thenewmotion.ocpi

import akka.http.scaladsl.testkit.{RouteTest, TestFrameworkInterface}
import org.specs2.execute.{Failure, FailureException}
import org.specs2.specification.{Fragments, SpecificationStructure}
import org.specs2.specification.Step

trait Specs2TestFrameworkInterface extends TestFrameworkInterface {

  def cleanUp(): Unit

  def failTest(msg: String): Nothing
}

trait Specs2Interface extends SpecificationStructure with Specs2TestFrameworkInterface {

  override def failTest(msg: String): Nothing = {
    val trace = new Exception().getStackTrace.toList
    val fixedTrace = trace.drop(trace.indexWhere(_.getClassName.startsWith("org.specs2")) - 1)
    throw new FailureException(Failure(msg, stackTrace = fixedTrace))
  }

  override def map(fs: ⇒ Fragments) = super.map(fs).add(Step(cleanUp()))
}

trait Specs2RouteTest extends RouteTest with Specs2Interface
