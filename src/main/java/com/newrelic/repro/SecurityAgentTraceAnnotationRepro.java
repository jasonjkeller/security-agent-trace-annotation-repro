package com.newrelic.repro;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SecurityAgentTraceAnnotationRepro {
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    public static void main(String[] args) throws Exception {
        SecurityAgentTraceAnnotationRepro httpClientTest = new SecurityAgentTraceAnnotationRepro();
        httpClientTest.run();
    }

    public void run() throws Exception {
        executor.scheduleWithFixedDelay(this::startNewRelicTransaction, 1L, 2L, TimeUnit.SECONDS);
        executor.awaitTermination(666666, TimeUnit.DAYS);
    }

    @Trace(dispatcher = true, metricName = "start-newrelic-transaction")
    public void startNewRelicTransaction() {
        System.out.println("Transaction (startNewRelicTransaction): " + NewRelic.getAgent().getTransaction());
        childTrace();
    }

    @Trace
    public void childTrace() {
        System.out.println("Transaction (childTrace): " + NewRelic.getAgent().getTransaction() + "\n");
    }
}
