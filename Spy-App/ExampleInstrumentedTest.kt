package me.hawkshaw

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.test
import org.junit.runner.AndroidJUnit4

import org.junit.Assert.*

/* 
* Instrumented test, which will execute on an Android device.
*
*  Reference --> http://d.android.com/tools/testing OR https://developer.android.com/studio/test
*/

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test 
    fun useAppContext() {
        // Context of the Spy App under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("me.adobot", appContext.packageName)
    }
}