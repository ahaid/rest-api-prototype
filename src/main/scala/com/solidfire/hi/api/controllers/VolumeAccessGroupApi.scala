package com.solidfire.hi.api.controllers

import java.lang.Long._

import com.fasterxml.jackson.databind.ObjectMapper
import com.solidfire.element.api._
import com.solidfire.hi.api.ClusterSvc
import com.solidfire.jsvcgen.javautil.Optional
import org.json4s._
import org.scalatra._

import scala.util.{Success, Try}

class VolumeAccessGroupApi(implicit val objectMapper: ObjectMapper) extends ScalatraServlet with CorsSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  get("/") {
    val name = params.getAs[String]("name")
    val vags = ClusterSvc.element.listVolumeAccessGroups(new ListVolumeAccessGroupsRequest(null, null)).getVolumeAccessGroups
    if (name.isDefined) {
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(vags.filter(v => v.getName.indexOf(name.getOrElse(v.getName)) > -1)))
    } else {
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(vags))
    }
  }

  post("/") {
    val body = objectMapper.readTree(request.body)
    val name = body.get("name").asText
    val result = ClusterSvc.element.createVolumeAccessGroup(new CreateVolumeAccessGroupRequest(name, null, null, null))

    Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getVolumeAccessGroup(result.getVolumeAccessGroupID).get))
  }

  put("/:id/volume/:volumeID") {
    params.getAs[Long]("id").map(id => {
      params.getAs[Long]("volumeID").map(volumeID => {
        Try(ClusterSvc.element.addVolumesToVolumeAccessGroup(valueOf(id), List(valueOf(volumeID)).toArray))
        .map(r => {Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getVolumeAccessGroup(id).get))
        }).getOrElse(halt(BadRequest(s"There was a problem adding volume $volumeID to volumeAccessGroup $id")))
      }).getOrElse(halt(NotFound(s"Unable to use ${params("volumeID")} as a Volume ID. Use a Long data type.")))
    }).getOrElse(halt(NotFound(s"Unable to use ${params("id")} as a Volume Access Group ID. Use a Long data type.")))
  }

  delete("/:id/volume/:volumeID") {
    params.getAs[Long]("id").map(id => {
      params.getAs[Long]("volumeID").map(volumeID => {
        Try(ClusterSvc.element.removeVolumesFromVolumeAccessGroup(valueOf(id), List(valueOf(volumeID)).toArray))
          .map(r => {Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getVolumeAccessGroup(id).get))
        }).getOrElse(halt(BadRequest(s"There was a problem removing volume $volumeID from volumeAccessGroup $id")))
      }).getOrElse(halt(NotFound(s"Unable to use ${params("volumeID")} as a Volume ID. Use a Long data type.")))
    }).getOrElse(halt(NotFound(s"Unable to use ${params("id")} as a Volume Access Group ID. Use a Long data type.")))
  }

  get("/:id") {
    params.getAs[Long]("id").map(id => {
      val volumeAccessGroupsTry = getVolumeAccessGroup(id)
      volumeAccessGroupsTry.map(a =>
        Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(a)))
        .getOrElse(NotFound(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(volumeAccessGroupsTry.get)))
    }).getOrElse(halt(NotFound(s"Unable to use ${params("id")} as a Volume Access Group ID. Use a Long data type.")))
  }


  private def getVolumeAccessGroup(id: Long): Try[VolumeAccessGroup] = {
    val volumeAccessGroupsTry: Try[ListVolumeAccessGroupsResult] = Try(
      ClusterSvc.element.listVolumeAccessGroups(new ListVolumeAccessGroupsRequest(Optional.of(id), Optional.of(1l))))
    volumeAccessGroupsTry.map(a => Success(a.getVolumeAccessGroups.filter(v => v.getVolumeAccessGroupID == id).head))
      .get
  }

}