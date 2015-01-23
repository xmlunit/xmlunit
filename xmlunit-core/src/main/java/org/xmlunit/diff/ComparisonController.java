/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.xmlunit.diff;

/**
 * Decides whether the difference engine should stop the whole
 * comparison process because of the current difference.
 */
public interface ComparisonController {
    /**
     * May instruct the difference engine to stop the whole comparison process.
     *
     * @param difference the Difference that is responsible for
     * stopping the comparison process
     * @return whether to stop the comparison process
     */
    boolean stopDiffing(Difference difference);
}
