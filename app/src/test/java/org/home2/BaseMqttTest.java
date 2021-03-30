package org.home2;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static org.junit.Assert.assertEquals;

/**
 * Created by mtkachenko on 23/10/17.
 */

public class BaseMqttTest {
    private MqttUnderTest mqtt;
    private Function1<String, Unit> listener1, listener2, listener3;

    @Before
    public void setUp() {
        mqtt = new MqttUnderTest();
        listener1 = newListener();
        listener2 = newListener();
        listener3 = newListener();
    }

    @Test
    public void test_subscribeCalls() {
        mqtt.subscribe("a", listener1);
        mqtt.subscribe("a", listener2);
        mqtt.subscribe("b", listener3);
        mqtt.subscribe("a", listener2);

        List<String> expectedSubscribeTopics = Arrays.asList("a", "b");
        assertEquals(expectedSubscribeTopics, mqtt.subscribeTopics);
    }

    @Test
    public void test_unsubscribeCalls() {
        mqtt.unsubscribe("a", listener1);
        mqtt.subscribe("a", listener1);
        mqtt.subscribe("a", listener2);
        mqtt.unsubscribe("a", listener1);
        mqtt.unsubscribe("a", listener3);
        mqtt.unsubscribe("a", listener2);

        List<String> expectedUnsubscribeTopics = Arrays.asList("a");
        assertEquals(expectedUnsubscribeTopics, mqtt.subscribeTopics);
    }

    @Test
    public void test_subscribeAfterUnsubscribe() {
        mqtt.subscribe("a", listener1);
        mqtt.unsubscribe("a", listener1);
        mqtt.subscribe("a", listener2);

        List<String> expectedSubscribeTopics = Arrays.asList("a", "a");
        assertEquals(expectedSubscribeTopics, mqtt.subscribeTopics);
    }

    private Function1<String, Unit> newListener() {
        return new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                return null;
            }
        };
    }

    public static class MqttUnderTest extends BaseMqtt {
        List<String> subscribeTopics = new ArrayList<>();
        List<String> unsubscribeTopics = new ArrayList<>();

        @Override
        protected void subscribeInner(@NotNull String topic, @NotNull IMqttMessageListener listener) {
            subscribeTopics.add(topic);

        }

        @Override
        protected void unsubscribeInner(@NotNull String topic) {
            unsubscribeTopics.add(topic);
        }

        @Override
        public void connect(@NotNull IMqttActionListener listener) {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void publish(@NotNull String topic, @NotNull String message) {

        }
    }
}
