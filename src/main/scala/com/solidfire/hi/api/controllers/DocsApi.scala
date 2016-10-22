package com.solidfire.hi.api.controllers

import org.json4s._
import org.scalatra.{ Ok, ScalatraServlet }

import scala.io.Source

class DocsApi extends ScalatraServlet {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = "text/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/") {
    Ok(Source.fromURL(getClass.getResource("/swagger.json")).mkString)
  }

}