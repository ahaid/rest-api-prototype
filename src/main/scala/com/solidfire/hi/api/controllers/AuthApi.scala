package com.solidfire.hi.api.controllers

import java.net.URLDecoder

import org.apache.commons.codec.binary.Base64
import org.json4s._
import org.scalatra.{BadRequest, Ok, ScalatraServlet}

import scala.util.Try

class AuthApi extends ScalatraServlet {
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/encode") {
    val creds: String = params.getAs[String]("creds").getOrElse("")
    var triedString: Try[String] = Try(new String(Base64.encodeBase64(URLDecoder.decode(creds, "UTF-8").getBytes)))
    if (triedString.isSuccess) {
      Ok(triedString.get)
    }
    else{
      BadRequest("Unable to encode")
    }

  }

}