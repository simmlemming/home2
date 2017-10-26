package org.home2;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import org.home2.service.HomeService;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Created by mtkachenko on 26/10/17.
 */

public class HomeServiceTest {
    @Rule
    public ServiceTestRule rule = new ServiceTestRule();

    @Test
    public void testNotificationUpdatesOnConnectivityChanges() throws TimeoutException, InterruptedException {
        Intent service = new Intent(InstrumentationRegistry.getTargetContext(), HomeService.class);
        rule.startService(service);

        Thread.sleep(5000);
    }
}
