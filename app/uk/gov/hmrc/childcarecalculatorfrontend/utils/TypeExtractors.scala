/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

object TypeExtractors {

  // Takes a Seq[B] and returns B
  type ExtractSeqElement[S] = S match {
    case Seq[b] => b
  }

  // Takes a Set[B] and returns B
  type ExtractSetElement[S] = S match {
    case Set[b] => b
  }

  // Takes a Map[K, V] and returns B
  type ExtractMapKey[M] = M match {
    case Map[k, _] => k
  }

  // Takes a Map[K, V] and returns V
  type ExtractMapValue[M] = M match {
    case Map[_, v] => v
  }

}
