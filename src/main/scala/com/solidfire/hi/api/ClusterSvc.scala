package com.solidfire.hi.api

import javax.net.ssl.{HostnameVerifier, HttpsURLConnection, SSLSession}

import com.solidfire.client.ElementFactory

object ClusterSvc {
  val element = ElementFactory.create("10.117.61.42", "admin", "admin")
  HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier {
    override def verify(s: String, sslSession: SSLSession): Boolean = true
  })
}
