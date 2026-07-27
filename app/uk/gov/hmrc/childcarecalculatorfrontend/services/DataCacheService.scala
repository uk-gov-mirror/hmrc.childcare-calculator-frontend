/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.services

import com.google.inject.{ImplementedBy, Inject}
import play.api.libs.json.{Json, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts.CascadeUpsert
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.SessionIdProvider
import uk.gov.hmrc.childcarecalculatorfrontend.repositories.SessionRepository
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheKey, CacheMap}

import scala.concurrent.{ExecutionContext, Future}

class DataCacheServiceImpl @Inject() (val sessionRepository: SessionRepository, val cascadeUpsert: CascadeUpsert)(
    implicit ec: ExecutionContext
) extends DataCacheService {

  def save(
      cacheKey: CacheKey,
      value: cacheKey.CacheValue
  )(
      using writes: Writes[cacheKey.CacheValue],
      request: SessionIdProvider
  ): Future[CacheMap] = {
    val cacheId = request.sessionId

    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap =
        cascadeUpsert(cacheKey, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map(_ => updatedCacheMap)
    }
  }

  def remove(key: CacheKey)(using request: SessionIdProvider): Future[Boolean] =
    sessionRepository().get(request.sessionId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(Future(false)) { cacheMap =>
        val newCacheMap = cacheMap.removed(key)
        sessionRepository().upsert(newCacheMap)
      }
    }

  def fetch()(using request: SessionIdProvider): Future[Option[CacheMap]] =
    sessionRepository().get(request.sessionId)

  def getEntry(
      key: CacheKey
  )(implicit reads: Reads[key.CacheValue], request: SessionIdProvider): Future[Option[key.CacheValue]] =
    fetch().map(optionalCacheMap => optionalCacheMap.flatMap(cacheMap => cacheMap.getEntry(key)))

  def saveInMap[K, V](cacheKey: CacheKey, key: K, value: V)(
      implicit fmt: Writes[Map[K, V]],
      reads: Reads[cacheKey.CacheValue],
      request: SessionIdProvider,
      ev: cacheKey.CacheValue =:= Map[K, V]
  ): Future[CacheMap] = {
    val cacheId = request.sessionId

    sessionRepository().get(cacheId).flatMap {
      _.map { cacheMap =>
        val map: Map[K, V]  = cacheMap.getEntry(cacheKey).asInstanceOf[Option[Map[K, V]]].getOrElse(Map.empty)
        val updatedMap      = map + (key -> value)
        val updatedCacheMap = cacheMap.copy(data = cacheMap.data + (cacheKey.cacheKey -> Json.toJson(updatedMap)))
        sessionRepository().upsert(updatedCacheMap).map(_ => updatedCacheMap)
      }.getOrElse(throw new RuntimeException(s"Couldn't find document with key $cacheId"))
    }
  }

  def updateMap(data: CacheMap): Future[Boolean] =
    sessionRepository().upsert(data)

}

@ImplementedBy(classOf[DataCacheServiceImpl])
trait DataCacheService {

  def save(key: CacheKey, value: key.CacheValue)(
      using Writes[key.CacheValue],
      SessionIdProvider
  ): Future[CacheMap]

  def updateMap(data: CacheMap): Future[Boolean]

  def remove(key: CacheKey)(using SessionIdProvider): Future[Boolean]

  def fetch()(using SessionIdProvider): Future[Option[CacheMap]]

  def getEntry(
      key: CacheKey
  )(using Reads[key.CacheValue], SessionIdProvider): Future[Option[key.CacheValue]]

  def saveInMap[K, V](
      cacheKey: CacheKey,
      key: K,
      value: V
  )(
      using Writes[Map[K, V]],
      Reads[cacheKey.CacheValue],
      SessionIdProvider,
      cacheKey.CacheValue =:= Map[K, V]
  ): Future[CacheMap]

}
