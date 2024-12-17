/*
 * Copyright 2024 OIDC Sweden
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
package se.oidc.oidfed.base.process.metadata.policyoperators;

import java.util.List;

import se.oidc.oidfed.base.process.metadata.PolicyMergeException;
import se.oidc.oidfed.base.process.metadata.PolicyProcessingException;
import se.oidc.oidfed.base.process.metadata.PolicyTranslationException;
import se.oidc.oidfed.base.utils.OidcUtils;

/**
 * Implementation of the "one_of" policy operator
 */
public class OneOfPolicyOperator extends AbstractPolicyOperator<Object> {

  public static final String OPERATOR_NAME = "one_of";

  public OneOfPolicyOperator(Object value, String valueType)
    throws PolicyTranslationException, PolicyProcessingException {
    super(value, valueType);
  }

  @Override protected String getPolicyValueType() throws PolicyProcessingException {
    return getArrayPolicyValueTypeFromValueTYpe();
  }

  @Override protected boolean isEmptyValueAllowed() {
    return false;
  }

  @Override protected List<String> deriveNormalizedValue() throws PolicyTranslationException {
    return OidcUtils.convertListToStringList(this.value, this.policyValueType);
  }

  @Override public String getName() {
    return OPERATOR_NAME;
  }

  @Override public PolicyOperator mergeWithSubordinate(PolicyOperator policyOperator)
    throws PolicyMergeException, PolicyTranslationException, PolicyProcessingException {
    checkPolicyOperatorClass(policyOperator, this.getClass());
    List<String> intersection = getIntersection(policyOperator.getNormalizedOperatorValue());
    return new OneOfPolicyOperator(OidcUtils.convertToValueObject(intersection, valueType), valueType);
  }

  @Override public List<String> getModifiedMetadataValues(List<String> metadataParameterValue) {
    // This is not a modifier. Returning the metadata unaltered.
    return metadataParameterValue;
  }

  @Override public boolean isMetadataValid(List<String> metadataParameterValue) {
    return metadataParameterValue.size() == 1 &&
      getNormalizedOperatorValue().contains(metadataParameterValue.get(0));
  }
}
