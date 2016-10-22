package com.solidfire.hi.api

import javax.net.ssl.{ SSLSession, HostnameVerifier, HttpsURLConnection }

import com.solidfire.element.api.InternalSolidFireElement

object ClusterSvc {
  val version = "7.0"
  val element = InternalSolidFireElement.create("192.168.139.165", version, "admin", "admin")
  HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier {
    override def verify(s: String, sslSession: SSLSession): Boolean = true
  })

}
