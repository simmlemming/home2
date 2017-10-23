package org.home2;

import android.support.test.runner.AndroidJUnit4;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mtkachenko on 23/10/17.
 */

@RunWith(AndroidJUnit4.class)
public class BaseMqttTest {
    private BaseMqtt mqtt;
    private Function1<String, Unit> listener1, listener2, listener3;

    @Before
    public void setUp() {
        mqtt = Mockito.spy(new MqttUnderTest());
        listener1 = newListener();
        listener2 = newListener();
        listener3 = newListener();
    }

    @Test
    public void test_subscribeCalls() {
        mqtt.subscribe("a", listener1);
        verify(mqtt, times(1)).subscribeInner(eq("a"), any(IMqttMessageListener.class));

        mqtt.subscribe("a", listener2);
        verify(mqtt, times(1)).subscribeInner(eq("a"), any(IMqttMessageListener.class));

        mqtt.subscribe("b", listener3);
        verify(mqtt, times(1)).subscribeInner(eq("b"), any(IMqttMessageListener.class));
    }

    @Test
    public void test_unsubscribeCalls() {
        mqtt.unsubscribe("a", listener1);
        verify(mqtt, never()).subscribeInner(anyString(), any(IMqttMessageListener.class));

        mqtt.subscribe("a", listener1);
        mqtt.subscribe("a", listener2);

        mqtt.unsubscribe("a", listener1);
        verify(mqtt, never()).unsubscribeInner(eq("a"));

        mqtt.unsubscribe("a", listener3);
        verify(mqtt, never()).unsubscribeInner(eq("a"));

        mqtt.unsubscribe("a", listener2);
        verify(mqtt, times(1)).unsubscribeInner(eq("a"));
    }

    @Test
    public void test_subscribeAfterUnsubscribe() {
        mqtt.subscribe("a", listener1);
        mqtt.unsubscribe("a", listener1);

        mqtt.subscribe("a", listener2);
        verify(mqtt, times(2)).subscribeInner(eq("a"), any(IMqttMessageListener.class));
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
        String subscribeTopic = null;
        String unsubscribeTopic = null;

        @Override
        protected void subscribeInner(@NotNull String topic, @NotNull IMqttMessageListener listener) {
            subscribeTopic = topic;

        }

        @Override
        protected void unsubscribeInner(@NotNull String topic) {
            unsubscribeTopic = topic;
        }

        @Override
        public void connect() {

        }

        @Override
        public void disconnect() {

        }
    }
}
