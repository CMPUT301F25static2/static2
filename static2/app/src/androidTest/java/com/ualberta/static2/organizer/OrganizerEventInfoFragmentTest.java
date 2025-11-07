package com.ualberta.static2.organizer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.static2.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventInfoFragmentTest {

    @Test
    public void testFragmentCreationWithArguments() {
        String testEventId = "62iaipMyuGtQInbGrXog";

        FragmentScenario<OrganizerEventInfoFragment> scenario =
                FragmentScenario.launchInContainer(
                        OrganizerEventInfoFragment.class,
                        OrganizerEventInfoFragment.newInstance(testEventId).getArguments(),
                        R.style.TestTheme
                );

        scenario.onFragment(fragment -> {
            assertNotNull("Fragment should have arguments", fragment.getArguments());
            assertTrue("Fragment should have event ID",
                    fragment.getArguments().containsKey("event_id"));
        });
    }

    @Test
    public void testFragmentInitialization() {
        String testEventId = "62iaipMyuGtQInbGrXog";

        FragmentScenario<OrganizerEventInfoFragment> scenario =
                FragmentScenario.launchInContainer(
                        OrganizerEventInfoFragment.class,
                        OrganizerEventInfoFragment.newInstance(testEventId).getArguments(),
                        R.style.TestTheme
                );

        scenario.onFragment(fragment -> {
            assertTrue("Fragment should be added", fragment.isAdded());
            assertTrue("Fragment should be visible", fragment.isVisible());

            assertNotNull("EventRepository should be initialized", fragment.eventRepository);
            assertNotNull("Event ID should be set", fragment.eventId);
            assertNotNull("DateFormat should be initialized", fragment.dateFormat);

            assertNotNull("Binding should not be null", fragment.binding);
            assertNotNull("ScrollView should exist", fragment.binding.scrollView);
        });
    }

    @Test
    public void testDataLoadingFlow() {
        String testEventId = "62iaipMyuGtQInbGrXog";

        FragmentScenario<OrganizerEventInfoFragment> scenario =
                FragmentScenario.launchInContainer(
                        OrganizerEventInfoFragment.class,
                        OrganizerEventInfoFragment.newInstance(testEventId).getArguments(),
                        R.style.TestTheme
                );

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.onFragment(fragment -> {
            assertNotNull("Current event should be loaded or handled", fragment.currentEvent);

            assertNotNull("Event title view should exist", fragment.binding.tvEventTitle);
            assertNotNull("Event description view should exist", fragment.binding.tvEventDescription);
            assertNotNull("Back button should exist", fragment.binding.btnBack);
            assertNotNull("Showcase button should exist", fragment.binding.btnEventShowcase);
            assertNotNull("QR code button should exist", fragment.binding.btnEventShowQrcode);

            assertTrue("ScrollView should be visible after data load", fragment.binding.scrollView.getVisibility() == android.view.View.VISIBLE);
        });
    }

    @Test
    public void testUIComponentsInitialization() {
        // 测试UI组件初始化
        String testEventId = "62iaipMyuGtQInbGrXog";

        FragmentScenario<OrganizerEventInfoFragment> scenario =
                FragmentScenario.launchInContainer(
                        OrganizerEventInfoFragment.class,
                        OrganizerEventInfoFragment.newInstance(testEventId).getArguments(),
                        R.style.TestTheme
                );

        scenario.onFragment(fragment -> {
            // 验证所有主要UI组件都存在
            assertNotNull("Title TextView exists", fragment.binding.tvEventTitle);
            assertNotNull("Description TextView exists", fragment.binding.tvEventDescription);
            assertNotNull("Location TextView exists", fragment.binding.tvEventLocation);
            assertNotNull("Update Title TextView exists", fragment.binding.tvEventUpdateTitle);
            assertNotNull("Update Description TextView exists", fragment.binding.tvEventUpdateDescription);
            assertNotNull("Update Status TextView exists", fragment.binding.tvEventUpdateStatus);
            assertNotNull("Update End Time TextView exists", fragment.binding.tvEventUpdateEndTime);
            assertNotNull("Update Registry End Time TextView exists", fragment.binding.tvEventUpdateRegistryEndTime);

            // 验证按钮存在
            assertNotNull("Back button exists", fragment.binding.btnBack);
            assertNotNull("Showcase button exists", fragment.binding.btnEventShowcase);
            assertNotNull("QR Code button exists", fragment.binding.btnEventShowQrcode);
            assertNotNull("Update Title button exists", fragment.binding.btnEventUpdateTitle);
            assertNotNull("Update Description button exists", fragment.binding.btnEventUpdateDescription);
            assertNotNull("Update End Time button exists", fragment.binding.btnEventUpdateEndTime);
            assertNotNull("Update Registry End Time button exists", fragment.binding.btnEventUpdateRegistryEndTime);
            assertNotNull("Update Location button exists", fragment.binding.btnEventUpdateLocation);
            assertNotNull("Update Status button exists", fragment.binding.btnEventUpdateStatus);

            // 验证图片视图存在
            assertNotNull("Poster ImageView exists", fragment.binding.ivEventPosterImg);
            assertNotNull("Location ImageView exists", fragment.binding.ivEventLocationImg);
        });
    }


}
