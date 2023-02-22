/*
 *
 *  * Copyright 2023 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */
package org.apache.kafka.streams.processor.internals;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.kafka.streams.StreamsSpansUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;

@Weave(originalName = "org.apache.kafka.streams.processor.internals.StreamThread")
public abstract class StreamThread_Instrumentation extends Thread {
    @NewField
    private String nrApplicationIdWithSuffix;

    // This method runs once per each event loop iteration
    @Trace(dispatcher = true)
    void runOnce() {
        if (this.nrApplicationIdWithSuffix == null) {
            this.nrApplicationIdWithSuffix = StreamsSpansUtil.getAppIdWithSuffix(this.getName());
        }
        StreamsSpansUtil.initTransaction(this.nrApplicationIdWithSuffix);
        try {
            Weaver.callOriginal();
        } catch (Throwable t) {
            NewRelic.noticeError(t);
            throw t;
        } finally {
            StreamsSpansUtil.endTransaction();
        }
    }

    @Trace
    private ConsumerRecords<byte[], byte[]> pollRequests(final Duration pollTime) {
        return Weaver.callOriginal();
    }

    @WeaveAllConstructors
    public StreamThread_Instrumentation() {
        this.nrApplicationIdWithSuffix = null;
    }
}
