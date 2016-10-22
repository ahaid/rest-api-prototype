package com.solidfire.hi.api.controllers

import com.solidfire.element.api.InternalSolidFireElement
import org.scalatra.ScalatraServlet
import sun.misc.BASE64Decoder

import scala.util.Try


trait ScalatraServletBase extends ScalatraServlet {
  var basicAuthHeader: String = ""

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
    basicAuthHeader = request.getHeader("Authentication")
  }

  val version = "7.0"

  lazy val element: InternalSolidFireElement = {
    Try {
      val creds = new BASE64Decoder().decodeBuffer(basicAuthHeader).toString.split(":")
      InternalSolidFireElement.create("192.168.139.165", version, creds(0), creds(1))
    }.getOrElse(throw new Exception("Cannot Auth!!"))
  }
}
