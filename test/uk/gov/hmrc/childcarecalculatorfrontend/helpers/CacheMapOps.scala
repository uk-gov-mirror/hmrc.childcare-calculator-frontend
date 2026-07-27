package uk.gov.hmrc.childcarecalculatorfrontend.helpers

import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

trait CacheMapOps {

  extension (cacheMap: CacheMap) {
    def overwritten(values: (String, JsValue)*): CacheMap = {
      cacheMap.copy(
        data = cacheMap.data ++ values.toMap
      )
    }
  }

  extension (cacheMapObject: CacheMap.type) {

    def of(values: (String, JsValue)*): CacheMap = {
      CacheMap(
        id = "id",
        data = values.toMap
      )
    }

  }

}
