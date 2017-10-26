package org.home2.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ServiceTestRule;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.home2.BaseMqtt;
import org.home2.HomeApplication;
import org.home2.NotificationController;
import org.home2.service.HomeService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.TimeoutException;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

/**
 * Created by mtkachenko on 26/10/17.
 */

public class HomeServiceTest {
    @Rule
    public ServiceTestRule rule = new ServiceTestRule();

    @Mock
    private NotificationController notificationController;
    private MockedMqtt mqtt;
    private HomeApplication homeApplication;

    @Before
    public void setUp() {
        notificationController = Mockito.mock(NotificationController.class, Mockito.RETURNS_MOCKS);
        homeApplication = ((HomeTestRunner) InstrumentationRegistry.getInstrumentation()).homeApplication;
        mqtt = Mockito.spy(new MockedMqtt());

        homeApplication.setMockedNotificationController(notificationController);
        homeApplication.setMockedMqtt(mqtt);
    }

    @Test
    public void deviceInteractions() throws TimeoutException, JSONException {
        HomeService service = startService();

        service.device("abc").on();

        loopMainThreadUntilIdle();
        assertMessageSent("{name: abc, cmd: on}");

        service.device("qwe").status();

        loopMainThreadUntilIdle();
        assertMessageSent("{name: qwe, cmd: status}");
    }

    @Test
    public void testNotificationUpdatesOnConnectivityChanges() throws TimeoutException, InterruptedException {
        startService();

        mqtt.breakConnection();
        loopMainThreadUntilIdle();
        verify(notificationController).notifyDisconnected();

        mqtt.restoreConnection();
        loopMainThreadUntilIdle();
        verify(notificationController).notifyConnected();
    }

    private void assertMessageSent(String expectedMessage) throws JSONException {
        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);

        verify(mqtt, atLeastOnce()).publish(eq("home/in"), message.capture());
        assertEquals(expectedMessage, message.getValue(), false);
    }

    @After
    public void tearDown() {
        homeApplication.cleanMockedDependencies();
    }

    private void loopMainThreadUntilIdle() {
        Espresso.onIdle();
    }

    private HomeService startService() throws TimeoutException {
        Intent service = new Intent(InstrumentationRegistry.getTargetContext(), HomeService.class);
        HomeService.HomeBinder homeBinder = (HomeService.HomeBinder) rule.bindService(service);


        assertNotNull(homeBinder);
        return homeBinder.getService();
    }

    public class MockedMqtt extends BaseMqtt {

        void breakConnection() {
            if (getConnectivityListener() != null) {
                getConnectivityListener().onDisconnected();
            }
        }

        void restoreConnection() {
            if (getConnectivityListener() != null) {
                getConnectivityListener().onConnected();
            }
        }

        @Override
        protected void subscribeInner(@NotNull String topic, @NotNull IMqttMessageListener listener) {

        }

        @Override
        protected void unsubscribeInner(@NotNull String topic) {

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
