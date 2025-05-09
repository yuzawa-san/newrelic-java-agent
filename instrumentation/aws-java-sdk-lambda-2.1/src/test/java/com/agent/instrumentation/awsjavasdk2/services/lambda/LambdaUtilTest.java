/*
 *
 *  * Copyright 2024 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.agent.instrumentation.awsjavasdk2.services.lambda;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.CloudApi;
import com.newrelic.agent.bridge.NoOpCloud;
import com.newrelic.api.agent.CloudAccountInfo;
import com.newrelic.api.agent.CloudParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.regions.Region;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LambdaUtilTest {

    @Test
    public void testGetCloudParamFunctionName() {
        FunctionRawData functionRawData = data("my-function", null);
        CloudParameters cloudParameters = LambdaUtil.getCloudParameters(functionRawData);
        assertNotNull(cloudParameters);
        assertEquals("aws_lambda", cloudParameters.getPlatform());
        assertNull(cloudParameters.getResourceId());
    }

    @Test
    public void testGetCloudParamPartialArn() {
        FunctionRawData functionRawData = data("123456789012:function:my-function", null);
        CloudParameters cloudParameters = LambdaUtil.getCloudParameters(functionRawData);
        assertNotNull(cloudParameters);
        assertEquals("aws_lambda", cloudParameters.getPlatform());
        assertEquals("arn:aws:lambda:us-east-1:123456789012:function:my-function", cloudParameters.getResourceId());
    }

    @Test
    public void testGetCloudParamArnQualifier() {
        FunctionRawData functionRawData = data("arn:aws:lambda:us-east-1:123456789012:function:my-function", "alias");
        CloudParameters cloudParameters = LambdaUtil.getCloudParameters(functionRawData);
        assertNotNull(cloudParameters);
        assertEquals("aws_lambda", cloudParameters.getPlatform());
        assertEquals("arn:aws:lambda:us-east-1:123456789012:function:my-function:alias", cloudParameters.getResourceId());
    }

   private FunctionRawData data(String functionRef, String number) {
        SdkClientConfiguration config = SdkClientConfiguration.builder()
                .option(AwsClientOption.AWS_REGION, Region.US_EAST_1)
                .build();
        return new FunctionRawData(functionRef, number, config, new Object());
    }

}