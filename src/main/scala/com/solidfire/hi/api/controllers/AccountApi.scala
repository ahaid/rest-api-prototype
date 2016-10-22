package com.solidfire.hi.api.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.solidfire.element.api._
import com.solidfire.hi.api.ClusterSvc
import org.json4s._
import org.scalatra._

import scala.util.{Success, Try}

class AccountApi(implicit val objectMapper: ObjectMapper) extends ScalatraServletBase with CorsSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/") {
    val accountsTry = listAccounts
    accountsTry.map(result => Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(result)))
      .getOrElse(NotFound(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(accountsTry.get)))
  }

  get("/:id") {
    params.getAs[Long]("id").map(id => {
      val accountTry: Try[Account] = getAccountById(id)
      accountTry.map(a =>
        Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(a)))
        .getOrElse(NotFound(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(accountTry.get)))
    }).getOrElse(halt(NotFound(s"Unable to use ${params("id")} as an Account ID. Use a Long data type.")))
  }

  post("/") {
    val body = objectMapper.readTree(request.body)
    Try(body.get("name").asText).map(name => {
      val accountTry: Try[AddAccountResult] = Try(
        ClusterSvc.element.addAccount(new AddAccountRequest(name, null, null, null)))
      accountTry.map(a => {
        Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getAccountById(a.getAccountID).get))
      }).getOrElse(BadRequest(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(accountTry.get)))
    }).getOrElse("Unable to find a valid Name for the new Account in the POST body.")
  }

  private def listAccounts: Try[Array[Account]] = {
    val accountTry: Try[ListAccountsResult] = Try(
      element.listAccounts(null, null))
    accountTry.map(a => Success(a.getAccounts)).get
  }

  private def getAccountById(id: Long): Try[Account] = {
    val accountTry: Try[GetAccountResult] = Try(
      element.getAccountByID(id))
    accountTry.map(a => Success(a.getAccount)).get
  }

}