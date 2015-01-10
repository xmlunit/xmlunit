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
package org.xmlunit.builder;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xmlunit.transform.Transformation;
import org.w3c.dom.Document;

/**
 * Fluent API access to {@link Transformation}.
 */
public final class Transform {

    private Transform() { /* no instances */ }

    /**
     * Builder for XSLT transformations.
     */
    public interface Builder extends TransformationBuilderBase<Builder> {
        /**
         * Create the result of the transformation.
         */
        TransformationResult build();
    }

    /**
     * Provides different options to obtain the result of an XSLT transformation.
     */
    public interface TransformationResult {
        /**
         * Output the result to a TraX Result.
         */
        void to(Result r);
        /**
         * Output the result to a String.
         */
        String toString();
        /**
         * Output the result to a DOM Document.
         */
        Document toDocument();
    }

    private static class TransformationBuilder
        extends AbstractTransformationBuilder<Builder>
        implements Builder, TransformationResult {
        private TransformationBuilder(Source s) {
            super(s);
        }
        @Override
        public TransformationResult build() {
            return this;
        }
        @Override
        public String toString() {
            return getHelper().transformToString();
        }
        @Override
        public Document toDocument() {
            return getHelper().transformToDocument();
        }
        @Override
        public void to(Result r) {
            getHelper().transformTo(r);
        }
    }

    /**
     * Build a transformation for a source document.
     */
    public static Builder source(Source s) {
        return new TransformationBuilder(s);
    }
}
