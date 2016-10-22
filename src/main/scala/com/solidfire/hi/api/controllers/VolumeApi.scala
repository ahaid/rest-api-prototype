package com.solidfire.hi.api.controllers

import java.lang.Long._

import com.fasterxml.jackson.databind.ObjectMapper
import com.solidfire.element.api.{CreateVolumeRequest, ListVolumesRequest, QoS, Volume}
import com.solidfire.hi.api.ClusterSvc
import com.solidfire.javautil.Optional
import org.json4s._
import org.scalatra.{ CorsSupport, NotFound, Ok, ScalatraServlet }

import scala.util.Try

class VolumeApi(implicit val objectMapper: ObjectMapper) extends ScalatraServlet with CorsSupport {
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
    val volumes =
      if (ClusterSvc.version.toFloat >= 8.0f) {

        ClusterSvc.element.listVolumes(new ListVolumesRequest(null, null, null, null, null)).getVolumes
      } else {
        ClusterSvc.element.listActiveVolumes(null, null).getVolumes ++ ClusterSvc.element.listDeletedVolumes().getVolumes
      }
    if (name.isDefined) {
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(volumes.filter(v => v.getName.indexOf(name.getOrElse(v.getName)) > -1)))
    } else {
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(volumes))
    }
  }

  post("/") {
    val body = objectMapper.readTree(request.body)
    val name = body.get("name").asText
    val accountID = body.get("accountID").asLong
    val totalSize = body.get("totalSizeGB").asLong
    val enable512E = body.get("enable512E").asBoolean

    val qos = if (body.has("QoS")) {
      val minIops = body.findValue("minIops").asLong
      val maxIops = body.findValue("maxIops").asLong
      val burstIops = body.findValue("burstIops").asLong
      Optional.of(new QoS(minIops, maxIops, burstIops, 1l))
    } else null

    val result = ClusterSvc.element.createVolume(new CreateVolumeRequest(name, accountID, totalSize * 1024 * 1024 * 1024, enable512E, qos, null))

    Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getVolume(result.getVolumeID)))
  }

  get("/:id/stats") {
    Try(valueOf(params("id").toLong)).map(id => {
      val stats = ClusterSvc.element.getVolumeStats(id).getVolumeStats
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(stats))
    }).getOrElse(NotFound(s"Error retrieving VolumeStats for VolumeID ${params("id")}."))
  }

  get("/:id") {
    Try(Optional.of(valueOf(params("id").toLong))).map(id => {
      Ok(objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(getVolume(id.get)))
    }).getOrElse(NotFound(s"Unable to use ${params("id")} as a Volume ID. Use a Long data type."))
  }

  private def getVolume(id: Long): Volume = {
    val volumes: Array[Volume] =
      if (ClusterSvc.version.toFloat >= 8.0f) {
        ClusterSvc.element.listVolumes(new ListVolumesRequest(Optional.of(id), Optional.of(1l), null, null, null)).getVolumes
      } else {
        ClusterSvc.element.listActiveVolumes(Optional.of(id), Optional.of(1l)).getVolumes ++ ClusterSvc.element.listDeletedVolumes().getVolumes
      }
    volumes.filter(v => v.getVolumeID == id).head
  }

}