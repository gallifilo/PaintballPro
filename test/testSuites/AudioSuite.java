package testSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import audio.TestAudioManager;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        TestAudioManager.class
})

/**
 * Audio Test Suite.
 *
 * @author Jack Hughes
 */
public class AudioSuite {
}