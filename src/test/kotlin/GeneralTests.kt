import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.ui.LineLauncher

@RunWith(MockitoJUnitRunner::class)
class GeneralTests {
        @Test
        fun lineLauncherTest(){
            val INPUT_FILE = "input.txt"
            val OUTPUT_DIR = "results"
            val TEST_FILE = "test.txt"
            val TIMEOUT = 180
            val SETTINGS = "settings.json"
            val PARAM_NAME = "param"
            val PARAM_VALUE = "value"

            val paramStrings = arrayOf("-i",INPUT_FILE,"-o",OUTPUT_DIR,
                "-t",TEST_FILE,"-tout",TIMEOUT.toString(),"-s", SETTINGS, "-p", PARAM_NAME, PARAM_VALUE)

            val inputParams = LineLauncher(paramStrings).getInputParams()

            Assert.assertEquals(INPUT_FILE, inputParams.inputSet)
            Assert.assertEquals(OUTPUT_DIR, inputParams.outputDict)
            Assert.assertEquals(TEST_FILE, inputParams.testSet)
            Assert.assertEquals(TIMEOUT, inputParams.timeout)
            Assert.assertEquals(SETTINGS, inputParams.settingsFile)
            Assert.assertEquals(PARAM_NAME, inputParams.paramPairs[0].first)
            Assert.assertEquals(PARAM_VALUE, inputParams.paramPairs[0].second)
        }
}