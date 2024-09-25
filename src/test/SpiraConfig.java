package src.test;

import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        rssToken = "{D3F132C0-B1C4-4F28-A4CC-246934D58A4A}",
        projectId = 258,
        // The following are OPTIONAL
        releaseId = 0,
        testSetId = 0
)
public class SpiraConfig {
    // This class is empty and only serves as a holder for the SpiraTestConfiguration
}