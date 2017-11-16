package org.home2.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ServiceTestRule;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.home2.BaseMqtt;
import org.home2.DeviceInfo;
import org.home2.DeviceRepository;
import org.home2.HomeApplication;
import org.home2.NotificationController;
import org.home2.service.HomeService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
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
    private DeviceRepository deviceRepository;

    @Before
    public void setUp() {
        notificationController = Mockito.mock(NotificationController.class, Mockito.RETURNS_MOCKS);
        homeApplication = ((HomeTestRunner) InstrumentationRegistry.getInstrumentation()).homeApplication;
        mqtt = Mockito.spy(new MockedMqtt());

        homeApplication.setMockedNotificationController(notificationController);
        homeApplication.setMockedMqtt(mqtt);

        deviceRepository = new DeviceRepository();
        homeApplication.setMockedDeviceRepository(deviceRepository);
    }

    @Test
    public void testNotificationOnAlarm() throws Exception {
        deviceRepository.add(DeviceInfo.nameOnly("a", "b"));
        startService();

        JSONObject alarm = new JSONObject();
        alarm.put("name", "a");
        alarm.put("state", DeviceInfo.STATE_ALARM);

        mqtt.receiveMessage(alarm);

        loopMainThreadUntilIdle();
        verify(notificationController).notifyAlarm();
    }

    @Test
    public void testNotificationOnNotAlarm() throws Exception {
        deviceRepository.add(new DeviceInfo("b", "c", DeviceInfo.STATE_ALARM, 0, 0));
        startService();

        JSONObject ok = new JSONObject();
        ok.put("name", "b");
        ok.put("state", DeviceInfo.STATE_OK);

        mqtt.receiveMessage(ok);

        loopMainThreadUntilIdle();
        verify(notificationController).notifyOk();
    }

    @Test
    public void deviceInteractions() throws TimeoutException, JSONException {
        HomeService service = startService();

        service.device("abc").on();

        loopMainThreadUntilIdle();
        assertMessageSent("{name: abc, cmd: on}");

        service.device("qwe").state();

        loopMainThreadUntilIdle();
        assertMessageSent("{name: qwe, cmd: state}");
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

        void receiveMessage(JSONObject message) throws Exception {
            getSubscribeListener().messageArrived("home/out", new MqttMessage(message.toString().getBytes()));
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
