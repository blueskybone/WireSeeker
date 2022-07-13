package com.example.wireseeker.recognizer;
import static android.widget.Toast.makeText;

import android.app.Activity;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class Recognizer extends Activity implements RecognitionListener {
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "oh mighty computer";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private Decoder decoder;
    private HashMap<String, Integer> captions;

    private void test() throws IOException {
        Config config= Decoder.defaultConfig();
        config.setString("-hmm",( "cn-digits-semi"));
        decoder = new Decoder(config);
        decoder.startUtt();
        decoder.setRawdataSize(300000);
        FileInputStream ais = new FileInputStream(new File("../../test/data/goforward.raw"));
        byte[] b = new byte[4096];
        int nbytes;
        while ((nbytes = ais.read(b)) >= 0) {
            ByteBuffer bb = ByteBuffer.wrap(b, 0, nbytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            short[] s = new short[nbytes/2];
            bb.asShortBuffer().get(s);
            decoder.processRaw(s, nbytes/2, false, false);
        }
        decoder.endUtt();
        System.out.println(decoder.hyp().getHypstr());
    }
    public class runRecognizerSetup implements Runnable{
        Assets assets = null;
        File assetDir = null;
        @Override
        public void run(){
            try {
                assets = new Assets(Recognizer.this);
                assetDir = assets.syncAssets();
                setupRecognizer(assetDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "cn-digits-semi"))
                .setDictionary(new File(assetsDir, "wireseeker_db.dic"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */
        // Create language model search
        File languageModel = new File(assetsDir, "wireseeker_db.lm.DMP");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

//        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {

    }

    private void test2(){
        recognizer.stop();
    }
    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
}
