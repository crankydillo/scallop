package org.rogach.scallop

import scala.reflect.Manifest

class PropertyBackedConverter[A](
  name: String
  , underlying: ValueConverter[A]
  , appProps: java.util.Properties = new java.util.Properties
) extends ValueConverter[A] {
  override def parse(s: List[(String,List[String])]): Either[Unit,Option[A]] = {
    underlying.parse(s) match {
      case Right(Some(a)) => 
        a match {
          case l:List[A] if l.isEmpty => parseProps
          case _ => Right(Some(a))
        }
      case _ => parseProps
    }
  }

  private def parseProps: Either[Unit, Option[A]] = {
    val propVal = appProps.getProperty(name)
    if (propVal == null) 
      Right(None)
    else 
      underlying.parse(List((name, propVal.split(" ").map { _.trim }.toList)))
  }

  val manifest = underlying.manifest
  val argType = underlying.argType
}

/** Converter from list of plain strings to something meaningful. */
trait ValueConverter[A] { parent =>
  
  /** Takes a list of arguments to all option invocations:
    * for example, "-a 1 2 -a 3 4 5" would produce List(("a",List(1,2)),("a",List(3,4,5))).
    * <ul>
    * <li> parse returns Left, if there was an error while parsing. </li>
    * <li> if no option was found, it returns Right(None). </li>
    * <li> if option was found, it returns Right(...). </li>
    * </ul>
    */
  def parse(s: List[(String,List[String])]): Either[Unit,Option[A]]
  
  /** Manifest, holding the type for this builder. */
  val manifest: Manifest[A]
  
  /** Type of parsed argument list. */
  val argType: ArgType.V 
  
  /** Maps the converter to another value:
    *
    * {{{ 
    * intConverter.map(2 +) // and you get a "biased converter"
    * }}} 
    */
  def map[B](fn: A => B)(implicit m: Manifest[B]) = new ValueConverter[B] { child =>
    def parse(s: List[(String,List[String])]) = parent.parse(s).right.map(_.map(fn))
    val manifest = m
    val argType = parent.argType
  }
  
}

