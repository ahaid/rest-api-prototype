package com.solidfire.hi.api.controllers

import com.solidfire.client.ElementFactory
import com.solidfire.element.api.SolidFireElement
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

  lazy val element: SolidFireElement = {
    Try {
      val creds = new BASE64Decoder().decodeBuffer(basicAuthHeader).toString.split(":")
      ElementFactory.create("10.117.61.42", creds(0), creds(1))
    }.getOrElse(throw new Exception("Cannot Auth!!"))
  }
}
